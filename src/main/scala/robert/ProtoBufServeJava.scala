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

import java.io.ByteArrayInputStream
import java.io.InputStream

import io.backchat.hookup.Connected
import io.backchat.hookup.InboundMessage
import io.backchat.hookup.HookupServer
import io.backchat.hookup.TextMessage
import io.backchat.hookup.HookupServer.HookupServerClient

import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit

import org.slf4j.LoggerFactory

object ProtoBufServeJava {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]) {
    // Setup the server client.
    val server = HookupServer(8125) {
      new HookupServerClient {
        override def receive: PartialFunction[InboundMessage, Unit] = {
          case Connected => logger.info("Connection opened!")
          case TextMessage(text) => {
            // Parse the text.
            val in: InputStream = new ByteArrayInputStream(text.getBytes("UTF-8"))
            val compilationUnit: CompilationUnit =
                try {
                  JavaParser.parse(in)
                } finally {
                  in.close()
                }
            val ast: AstNode = (new JavaAstVisitor).visit(compilationUnit, null)

            // Serialize and base64 encode the resulting byte array.
            val serialized = ProtoBufAstSerDe.serialize(ast)
            logger.debug(s"Encoding ${serialized.toString}")
            val messageBytes: Array[Byte] = serialized.toByteArray
            val base64string: String = new sun.misc.BASE64Encoder().encode(messageBytes)
            logger.debug(s"Sending ${base64string}")

            // Send the response.
            send(base64string)
          }
        }
      }
    }

    // Start the server.
    server.start
  }
}
