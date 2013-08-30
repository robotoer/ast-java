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

/// Things to do:
// - Create a protocol buffer based rpc system for communicating with Dart.
//   - Is there a dart proto-buf library?
//   - Add a way to store changes made/diff system.
// - Create a library for translating one type of AST into another with type-class like
//   conversion trait(?).
//   - Ordering can be achieved with a DAG type dependency description. Each conversion trait(?)
//     node(?) can have things it should be scheduled to be before(?).
// - Use AST libraries to autogenerate code handling of dart and java/scala interfacing code.

// AST ---------------------------------------------------------------------------------------------

sealed trait AstNode {
  def nodeType: String
}

// Primitives (leaf nodes).
case object NullNode
    extends AstNode {
  override val nodeType: String = "NullNode"
}
case class BooleanNode(
    value: Boolean)
    extends AstNode {
  override val nodeType: String = "BooleanNode"
}
case class DecimalNode(
    value: Double)
    extends AstNode {
  override val nodeType: String = "DecimalNode"
}
case class IntegerNode(
    value: Long)
    extends AstNode {
  override val nodeType: String = "IntegerNode"
}
case class StringNode(
    value: String)
    extends AstNode {
  override val nodeType: String = "StringNode"
}

// Compounds (subtrees).
case class ListNode(
    override val nodeType: String,
    children: Seq[AstNode])
    extends AstNode
case class MapNode(
    override val nodeType: String,
    children: Map[String, AstNode])
    extends AstNode
