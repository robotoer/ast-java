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

import org.json4s.JsonAST.JArray
import org.json4s.JsonAST.JBool
import org.json4s.JsonAST.JDouble
import org.json4s.JsonAST.JInt
import org.json4s.JsonAST.JNull
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JString
import org.json4s.JsonAST.JValue


class JsonAstSerDe extends AstSerDe[JValue] {
  def serialize(node: AstNode): JValue = {
    node match {
      case NullNode => JNull
      case BooleanNode(value) => JBool(value)
      case DecimalNode(value) => JDouble(value)
      case IntegerNode(value) => JInt(value)
      case StringNode(value) => JString(value)
      case ListNode(nodeType, children) => {
        val arr: Seq[JValue] = Seq(JString(nodeType)) ++ children.map { serialize }

        JArray(arr.toList)
      }
      case MapNode(nodeType, children) => {
        val childMap: Seq[(String, JValue)] = children
            .mapValues { serialize }
            .toSeq
        val componentMap: Seq[(String, JValue)] = childMap ++ Seq("nodeType" -> JString(nodeType))

        JObject(componentMap.toList)
      }
    }
  }

  def deserialize(serialized: JValue): AstNode = null
}
