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

import scala.collection.JavaConverters._

import robert.protobuf.AstProtos

object ProtoBufAstSerDe extends AstSerDe[AstProtos.AstNode] {
  def serialize(node: AstNode): AstProtos.AstNode = {
    node match {
      case NullNode => AstProtos.AstNode.newBuilder()
          .setNodeType("null")
          .build()
      case BooleanNode(value) => AstProtos.AstNode.newBuilder()
          .setNodeType("boolean")
          .setBooleanNode(value)
          .build()
      case DecimalNode(value) => AstProtos.AstNode.newBuilder()
          .setNodeType("decimal")
          .setDecimalNode(value)
          .build()
      case IntegerNode(value) => AstProtos.AstNode.newBuilder()
          .setNodeType("integer")
          .setIntegerNode(value)
          .build()
      case StringNode(value) => AstProtos.AstNode.newBuilder()
          .setNodeType("string")
          .setStringNode(value)
          .build()
      case ListNode(nodeType, children) => {
        val listNode = AstProtos.ListNode.newBuilder()
            .setNodeType(nodeType)
            .addAllChildren(children.map { serialize } .asJava)

        AstProtos.AstNode.newBuilder()
            .setNodeType("list")
            .setListNode(listNode)
            .build()
      }
      case MapNode(nodeType, children) => {
        val childMap = children
            .map { entry: (String, AstNode) =>
              val (field, value) = entry

              AstProtos.MapNode.Entry.newBuilder()
                  .setField(field)
                  .setValue(serialize(value))
                  .build()
            }
        val mapNode = AstProtos.MapNode.newBuilder()
            .setNodeType(nodeType)
            .addAllChildren(childMap.asJava)

        AstProtos.AstNode.newBuilder()
            .setNodeType("map")
            .setMapNode(mapNode)
            .build()
      }
    }
  }

  def deserialize(serialized: AstProtos.AstNode): AstNode = {
    serialized.getNodeType match {
      case "null" => NullNode
      case "boolean" => BooleanNode(serialized.getBooleanNode)
      case "decimal" => DecimalNode(serialized.getDecimalNode)
      case "integer" => IntegerNode(serialized.getIntegerNode)
      case "string" => StringNode(serialized.getStringNode)
      case "list" => {
        val listNode = serialized.getListNode

        ListNode(
            nodeType = listNode.getNodeType,
            children = listNode.getChildrenList.asScala.map { deserialize })
      }
      case "map" => {
        val mapNode = serialized.getMapNode
        val children = mapNode
            .getChildrenList
            .asScala
            .map { entry => (entry.getField, deserialize(entry.getValue)) }
            .toMap

        MapNode(
            nodeType = mapNode.getNodeType,
            children = children)
      }
    }
  }
}
