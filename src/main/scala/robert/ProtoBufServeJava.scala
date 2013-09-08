/**
 * (c) Copyright 2013 Robert Chu.
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package robert

import scala.collection.mutable

import java.io.FileInputStream
import java.io.PrintWriter

import io.backchat.hookup.BinaryMessage
import io.backchat.hookup.Connected
import io.backchat.hookup.HookupClient
import io.backchat.hookup.HookupServer
import io.backchat.hookup.HookupServer.HookupServerClient
import io.backchat.hookup.TextMessage

import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import robert.protobuf.AstProtos

object ProtoBufServeJava {
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]) {
    // Setup the server client.
    val server = HookupServer(8125) { new AstServerClient() }

    // Start the server.
    server.start
  }
}

class AstServerClient extends HookupServerClient {
  import AstServerClient._

  def onConnect() {
    logger.info("Connection opened!")
  }

  def onMessage(text: String) {
    // Parse the text.
    val request: AstProtos.AstRequest = base64DecodeRequest(text)

    request.getRequestType match {
      case "get" => {
        // Extract the get request.
        val getRequest: AstProtos.AstGetRequest = {
          assume(
            request.getGetRequest != null,
            "A request of type 'get' should have a populated get_request field")
          assume(
            request.getGetRequest.getPath != null,
            "A get request should have a path specified")
          logger.info(s"Received get request: ${request.getGetRequest.toString}")

          request.getGetRequest
        }

        val getResponse = get(getRequest)
        val response = AstProtos.AstResponse
            .newBuilder()
            .setGetResponse(getResponse)
            .build()

        // Send the response.
        val base64: String = base64EncodeResponse(response)
        logger.debug(s"Sending $base64")
        send(base64)
      }
      case "put" => {
        // Extract the put request.
        val putRequest: AstProtos.AstPutRequest = {
          assume(
            request.getPutRequest != null,
            "A request of type 'put' should have a populated put_request field")
          assume(
            request.getPutRequest.getPath != null,
            "A put request should have a path specified")
          logger.info(s"Received put request: ${request.getPutRequest.toString}")

          request.getPutRequest
        }

        val putResponse = put(putRequest)
        val response = AstProtos.AstResponse
            .newBuilder()
            .setPutResponse(putResponse)
            .build()

        // Send the result.
        val base64: String = base64EncodeResponse(response)
        logger.debug(s"Sending $base64")
        send(base64)
      }
    }
  }

  override def receive: HookupClient.Receive = {
    case Connected => onConnect()
    case TextMessage(text) => onMessage(text)
    case BinaryMessage(content) =>
  }
}

//final case class Memoize[-KeyType, +CachedType](
//    function: (KeyType => CachedType),
//    cache: mutable.Map[KeyType, CachedType] = mutable.Map())
//    extends (KeyType => CachedType) {
//  override def apply(key: KeyType): CachedType = {
//    if (cache.contains(key)) {
//      cache(key)
//    } else {
//      val value = function(key)
//      cache += (key -> value)
//      value
//    }
//  }
//}

object AstServerClient {
  val logger: Logger = LoggerFactory.getLogger(classOf[AstServerClient])

  /**
   * Encodes a protocol buffer into a base64 string.
   *
   * @param response message to encode.
   * @return the base64 string encoded message.
   */
  def base64EncodeResponse(response: AstProtos.AstResponse): String = {
    new sun.misc.BASE64Encoder().encode(response.toByteArray)
  }

//  val base64EncodeResponseCache: AstProtos.AstResponse => String = Memoize(base64EncodeResponse)
//
//  /**
//   * Encodes a protocol buffer into a base64 string or uses a cached encoding. Caches all encode
//   * operations for later use.
//   *
//   * @param response message to encode.
//   * @return the base64 string encoded message.
//   */
//  def base64EncodeResponseCached(response: AstProtos.AstResponse): String =
//      base64EncodeResponseCache(response)

  /**
   * Decodes a protocol buffer encoded into a base64 string containing a request message from a
   * websocket client.
   *
   * @param request string to decode.
   * @return the request message from a websocket client.
   */
  def base64DecodeRequest(request: String): AstProtos.AstRequest = {
    val requestBytes: Array[Byte] = new sun.misc.BASE64Decoder().decodeBuffer(request)

    AstProtos.AstRequest.parseFrom(requestBytes)
  }

//  val base64DecodeRequestCache: String => AstProtos.AstRequest = Memoize(base64DecodeRequest)
//
//  /**
//   * Decodes a protocol buffer encoded into a base64 string containing a request message from a
//   * websocket client or uses a cached decoding. Caches all decode operations for later use.
//   *
//   * @param request string to decode.
//   * @return the request message from a websocket client.
//   */
//  def base64DecodeRequestCached(request: String): AstProtos.AstRequest =
//      base64DecodeRequestCache(request)

  /**
   * Parses java code from a file.
   *
   * @param path to a java file to parse.
   * @return the ast of the java source code.
   */
  def parseJavaFile(path: String): CompilationUnit = {
    ResourceUtils.doAndClose(new FileInputStream(path)) { JavaParser.parse }
  }

  /**
   * Writes an AST to a file.
   *
   * @param path to write to.
   * @param ast to write to a file.
   */
  def writeJavaFile(path: String, ast: CompilationUnit) {
    ResourceUtils.doAndClose(new PrintWriter(path)) { _.println(ast.toString) }
  }

  /**
   * Handles 'get' requests from websocket clients. Returns the requested AST section.
   *
   * @param request to handle.
   * @return the response to respond with.
   */
  def get(request: AstProtos.AstGetRequest): AstProtos.AstGetResponse = {
    // Read an AST from the provided coordinates.
    val ast: CompilationUnit = {
      val path: String = request.getPath
      // val version: Option[Long] = Option(request.getVersion) // Ignored for now.

      parseJavaFile(path)
    }

    // Return the requested AST.
    logger.debug(s"Encoding ${ast.toString}")
    val node: AstNode = (new JavaAstVisitor).visit(ast, null)
    val pbNode: AstProtos.AstNode = ProtoBufAstSerDe.serialize(node)
    AstProtos.AstGetResponse
        .newBuilder()
        .setRoot(pbNode)
        .build()
  }

  /**
   * Handles 'put' requests from websocket clients. Writes the specified AST to the specified
   * location. Returns an error code if an error occurred.
   *
   * @param request to handle.
   * @return the response to respond with.
   */
  def put(request: AstProtos.AstPutRequest): AstProtos.AstPutResponse = {
    // Write the specified AST to the provided coordinates.
    val ast: CompilationUnit = {
      // TODO: Convert this AstNode to its CompilationUnit counterpart.
      // val node: AstNode = ProtoBufAstSerDe.deserialize(request.getRoot)
      null
    }
    val path: String = request.getPath
    writeJavaFile(path, ast)

    // Return the result.
    logger.debug(s"Encoding ${ast.toString}")
    AstProtos.AstPutResponse
        .newBuilder()
        .build()
  }
}
