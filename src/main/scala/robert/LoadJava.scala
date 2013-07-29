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

import java.io.FileInputStream

import japa.parser.JavaParser
import japa.parser.ast.CompilationUnit

import org.json4s.jackson.JsonMethods._

object LoadJava {
  def main(args: Array[String]) {
    val pathIn: String = "/home/robert/src/ast-java/Kiji.java"
    val in: FileInputStream = new FileInputStream(pathIn)

    val compilationUnit: CompilationUnit =
        try {
          JavaParser.parse(in)
        } finally {
          in.close()
        }

    val ast: AstNode = (new JavaAstVisitor).visit(compilationUnit, null)
    val json = (new JsonAstSerDe).serialize(ast)
    println(pretty(render(json)))
  }
}
