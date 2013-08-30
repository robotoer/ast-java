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
import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit

import io.backchat.hookup.InboundMessage
import io.backchat.hookup.HookupServer
import io.backchat.hookup.TextMessage
import io.backchat.hookup.HookupServer.HookupServerClient

import org.json4s.jackson.JsonMethods._

object JsonServeJava {
  def main(args: Array[String]) {
    HookupServer(8125) {
      new HookupServerClient {
        override def receive: PartialFunction[InboundMessage, Unit] = {
          case TextMessage(text) => {
            // Parse and return as JSON.
            val in: InputStream = new ByteArrayInputStream(text.getBytes("UTF-8"))
            val compilationUnit: CompilationUnit =
                try {
                  JavaParser.parse(in)
                } finally {
                  in.close()
                }
            val ast: AstNode = (new JavaAstVisitor).visit(compilationUnit, null)
            val json = JsonAstSerDe.serialize(ast)
            send(compact(render(json)))
          }
        }
      }
    }.start
  }
}
