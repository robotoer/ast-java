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

import japa.parser.ast.BlockComment
import japa.parser.ast.Comment
import japa.parser.ast.CompilationUnit
import japa.parser.ast.ImportDeclaration
import japa.parser.ast.LineComment
import japa.parser.ast.Node
import japa.parser.ast.PackageDeclaration
import japa.parser.ast.TypeParameter
import japa.parser.ast.body.AnnotationDeclaration
import japa.parser.ast.body.AnnotationMemberDeclaration
import japa.parser.ast.body.BodyDeclaration
import japa.parser.ast.body.ClassOrInterfaceDeclaration
import japa.parser.ast.body.ConstructorDeclaration
import japa.parser.ast.body.EmptyMemberDeclaration
import japa.parser.ast.body.EmptyTypeDeclaration
import japa.parser.ast.body.EnumConstantDeclaration
import japa.parser.ast.body.EnumDeclaration
import japa.parser.ast.body.FieldDeclaration
import japa.parser.ast.body.InitializerDeclaration
import japa.parser.ast.body.JavadocComment
import japa.parser.ast.body.MethodDeclaration
import japa.parser.ast.body.Parameter
import japa.parser.ast.body.TypeDeclaration
import japa.parser.ast.body.VariableDeclarator
import japa.parser.ast.body.VariableDeclaratorId
import japa.parser.ast.expr._
import japa.parser.ast.stmt.AssertStmt
import japa.parser.ast.stmt.BlockStmt
import japa.parser.ast.stmt.BreakStmt
import japa.parser.ast.stmt.CatchClause
import japa.parser.ast.stmt.ContinueStmt
import japa.parser.ast.stmt.DoStmt
import japa.parser.ast.stmt.EmptyStmt
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt
import japa.parser.ast.stmt.ExpressionStmt
import japa.parser.ast.stmt.ForStmt
import japa.parser.ast.stmt.ForeachStmt
import japa.parser.ast.stmt.IfStmt
import japa.parser.ast.stmt.LabeledStmt
import japa.parser.ast.stmt.ReturnStmt
import japa.parser.ast.stmt.Statement
import japa.parser.ast.stmt.SwitchEntryStmt
import japa.parser.ast.stmt.SwitchStmt
import japa.parser.ast.stmt.SynchronizedStmt
import japa.parser.ast.stmt.ThrowStmt
import japa.parser.ast.stmt.TryStmt
import japa.parser.ast.stmt.TypeDeclarationStmt
import japa.parser.ast.stmt.WhileStmt
import japa.parser.ast.`type`.ClassOrInterfaceType
import japa.parser.ast.`type`.PrimitiveType
import japa.parser.ast.`type`.ReferenceType
import japa.parser.ast.`type`.Type
import japa.parser.ast.`type`.VoidType
import japa.parser.ast.`type`.WildcardType
import japa.parser.ast.visitor.GenericVisitor
import japa.parser.ast.`type`.PrimitiveType.Primitive
import japa.parser.ast.expr.UnaryExpr.Operator

class JavaAstVisitor extends GenericVisitor[AstNode, Null] {
  private[this] def wrapJavaList[T](list: java.util.List[T]): Seq[T] = {
    if (list != null) {
      list.asScala
    } else {
      Seq()
    }
  }

  private[this] def buildAndAcceptSeq[T <: Node](
      nodeType: String,
      components: Seq[T]): ListNode = {
    ListNode(
      nodeType = nodeType,
      children = components.map { _.accept(this, null) })
  }

  private[this] def buildAndAcceptOption[T <: Node](component: Option[T]): AstNode = {
    component
        .map { _.accept(this, null) }
        .getOrElse { NullNode }
  }

  // Compilation Unit

  override def visit(n: CompilationUnit, arg: Null): AstNode = {
    // Get components of this node.
    val packageDeclaration: Option[PackageDeclaration] = Option(n.getPackage)
    val imports: Seq[ImportDeclaration] = wrapJavaList(n.getImports)
    val types: Seq[TypeDeclaration] = wrapJavaList(n.getTypes)
    val comments: Seq[Comment] = wrapJavaList(n.getComments)

    // Convert components to their AST counterparts.
    val packageDeclarationNode: AstNode = packageDeclaration
        .map { _.accept(this, arg) }
        .getOrElse { NullNode }
    val importsNode: AstNode = buildAndAcceptSeq("ImportsList", imports)
    val typesNode: AstNode = buildAndAcceptSeq("TypesList", types)
    val commentsNode: AstNode = buildAndAcceptSeq("CommentsList", comments)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "CompilationUnit",
        children = Map(
            "package" -> packageDeclarationNode,
            "imports" -> importsNode,
            "types" -> typesNode,
            "comments" -> commentsNode))
  }

  override def visit(n: PackageDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val packageName: NameExpr = n.getName
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)

    // Convert components to their AST counterparts.
    val packageNameNode: AstNode = packageName.accept(this, arg)
    val annotationsNode: AstNode = buildAndAcceptSeq("AnnotationsList", annotations)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "PackageDeclaration",
        children = Map(
            "packageName" -> packageNameNode,
            "annotations" -> annotationsNode))
  }

  override def visit(n: ImportDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val importName: NameExpr = n.getName
    val isAsterisk: Boolean = n.isAsterisk
    val isStatic: Boolean = n.isStatic

    // Convert components to their AST counterparts.
    val importNameNode: AstNode = importName.accept(this, arg)
    val isAsteriskNode: AstNode = BooleanNode(isAsterisk)
    val isStaticNode: AstNode = BooleanNode(isStatic)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ImportDeclaration",
        children = Map(
            "importName" -> importNameNode,
            "isAsterisk" -> isAsteriskNode,
            "isStatic" -> isStaticNode))
  }

  override def visit(n: TypeParameter, arg: Null): AstNode = {
    // Get components of this node.
    val typeParameter: String = n.getName
    val typeBounds: Seq[ClassOrInterfaceType] = wrapJavaList(n.getTypeBound)

    // Convert components to their AST counterparts.
    val typeParameterNode: AstNode = StringNode(typeParameter)
    val typeBoundsNode: AstNode = buildAndAcceptSeq("TypeBoundsList", typeBounds)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "TypeParameter",
        children = Map(
            "typeParameter" -> typeParameterNode,
            "typeBounds" -> typeBoundsNode))
  }

  override def visit(n: LineComment, arg: Null): AstNode = {
    // Get components of this node.
    val content: String = n.getContent

    // Convert components to their AST counterparts.
    val contentNode: AstNode = StringNode(content)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "LineComment",
        children = Map(
            "content" -> contentNode))
  }

  override def visit(n: BlockComment, arg: Null): AstNode = {
    // Get components of this node.
    val content: String = n.getContent

    // Convert components to their AST counterparts.
    val contentNode: AstNode = StringNode(content)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "LineComment",
        children = Map(
            "content" -> contentNode))
  }

  // Body

  override def visit(n: ClassOrInterfaceDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val extended: Seq[ClassOrInterfaceType] = wrapJavaList(n.getExtends)
    val implemented: Seq[ClassOrInterfaceType] = wrapJavaList(n.getImplements)
    val typeParameters: Seq[TypeParameter] = wrapJavaList(n.getTypeParameters)
    val members: Seq[BodyDeclaration] = wrapJavaList(n.getMembers)
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val javadoc: Option[JavadocComment] = Option(n.getJavaDoc)
    val modifiers: Int = n.getModifiers
    val name: String = n.getName
    val isInterface: Boolean = n.isInterface

    // Convert components to their AST counterparts.
    val extendedNode: AstNode = buildAndAcceptSeq("ExtendsList", extended)
    val implementedNode: AstNode = buildAndAcceptSeq("ImplementsList", implemented)
    val typeParametersNode: AstNode = buildAndAcceptSeq("TypeParameterList", typeParameters)
    val membersNode: AstNode = buildAndAcceptSeq("MembersList", members)
    val annotationsNode: AstNode = buildAndAcceptSeq("AnnotationsList", annotations)
    val javadocNode: AstNode = buildAndAcceptOption(javadoc)
    val modifiersNode: AstNode = IntegerNode(modifiers)

    val nameNode: AstNode = StringNode(name)
    val isInterfaceNode: AstNode = BooleanNode(isInterface)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ClassOrInterfaceDeclaration",
        children = Map(
            "extended" -> extendedNode,
            "implemented" -> implementedNode,
            "typeParameters" -> typeParametersNode,
            "members" -> membersNode,
            "annotations" -> annotationsNode,
            "javadoc" -> javadocNode,
            "modifiers" -> modifiersNode,
            "name" -> nameNode,
            "isInterface" -> isInterfaceNode))
  }

  override def visit(n: EnumDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val entries: Seq[EnumConstantDeclaration] = wrapJavaList(n.getEntries)
    val implemented: Seq[ClassOrInterfaceType] = wrapJavaList(n.getImplements)
    val members: Seq[BodyDeclaration] = wrapJavaList(n.getMembers)
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val javadoc: Option[JavadocComment] = Option(n.getJavaDoc)
    val modifiers: Int = n.getModifiers
    val name: String = n.getName

    // Convert components to their AST counterparts.
    val entriesNode: AstNode = buildAndAcceptSeq("EntriesList", entries)
    val implementedNode: AstNode = buildAndAcceptSeq("ImplementsList", implemented)
    val membersNode: AstNode = buildAndAcceptSeq("MembersList", members)
    val annotationsNode: AstNode = buildAndAcceptSeq("AnnotationsList", annotations)
    val javadocNode: AstNode = buildAndAcceptOption(javadoc)
    val modifiersNode: AstNode = IntegerNode(modifiers)
    val nameNode: AstNode = StringNode(name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "EnumDeclaration",
        children = Map(
            "entries" -> entriesNode,
            "implemented" -> implementedNode,
            "members" -> membersNode,
            "annotations" -> annotationsNode,
            "javadoc" -> javadocNode,
            "modifiers" -> modifiersNode,
            "name" -> nameNode))
  }

  override def visit(n: EmptyTypeDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val members: Seq[BodyDeclaration] = wrapJavaList(n.getMembers)
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val javadoc: Option[JavadocComment] = Option(n.getJavaDoc)
    val modifiers: Int = n.getModifiers
    val name: String = n.getName

    // Convert components to their AST counterparts.
    val membersNode: AstNode = buildAndAcceptSeq("MembersList", members)
    val annotationsNode: AstNode = buildAndAcceptSeq("AnnotationsList", annotations)
    val javadocNode: AstNode = buildAndAcceptOption(javadoc)
    val modifiersNode: AstNode = IntegerNode(modifiers)
    val nameNode: AstNode = StringNode(name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "EmptyTypeDeclaration",
        children = Map(
            "members" -> membersNode,
            "annotations" -> annotationsNode,
            "javadoc" -> javadocNode,
            "modifiers" -> modifiersNode,
            "name" -> nameNode))
  }

  override def visit(n: EnumConstantDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val args: Seq[Expression] = wrapJavaList(n.getArgs)
    val classBody: Seq[BodyDeclaration] = wrapJavaList(n.getClassBody)
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val javadoc: Option[JavadocComment] = Option(n.getJavaDoc)
    val name: String = n.getName

    // Convert components to their AST counterparts.
    val argsNode: AstNode = buildAndAcceptSeq("ArgsList", args)
    val classBodyNode: AstNode = buildAndAcceptSeq("ClassBodyList", classBody)
    val annotationsNode: AstNode = buildAndAcceptSeq("AnnotationsList", annotations)
    val javadocNode: AstNode = buildAndAcceptOption(javadoc)
    val nameNode: AstNode = StringNode(name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "EnumConstantDeclaration",
        children = Map(
            "args" -> argsNode,
            "classBody" -> classBodyNode,
            "annotations" -> annotationsNode,
            "javadoc" -> javadocNode,
            "name" -> nameNode))
  }

  override def visit(n: AnnotationDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val members: Seq[BodyDeclaration] = wrapJavaList(n.getMembers)
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val javadoc: Option[JavadocComment] = Option(n.getJavaDoc)
    val modifiers: Int = n.getModifiers
    val name: String = n.getName

    // Convert components to their AST counterparts.
    val membersNode: AstNode = buildAndAcceptSeq("MembersList", members)
    val annotationsNode: AstNode = buildAndAcceptSeq("AnnotationsList", annotations)
    val javadocNode: AstNode = buildAndAcceptOption(javadoc)
    val modifiersNode: AstNode = IntegerNode(modifiers)
    val nameNode: AstNode = StringNode(name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "AnnotationDeclaration",
        children = Map(
            "members" -> membersNode,
            "annotations" -> annotationsNode,
            "javadoc" -> javadocNode,
            "modifiers" -> modifiersNode,
            "name" -> nameNode))
  }

  override def visit(n: AnnotationMemberDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val defaultValue: Option[Expression] = Option(n.getDefaultValue)
    val memberType: Type = n.getType
    val javadoc: Option[JavadocComment] = Option(n.getJavaDoc)
    val modifiers: Int = n.getModifiers
    val name: String = n.getName

    // Convert components to their AST counterparts.
    val annotationsNode: AstNode = buildAndAcceptSeq("AnnotationsList", annotations)
    val javadocNode: AstNode = buildAndAcceptOption(javadoc)
    val defaultValueNode: AstNode = buildAndAcceptOption(defaultValue)
    val memberTypeNode: AstNode = memberType.accept(this, arg)
    val modifiersNode: AstNode = IntegerNode(modifiers)
    val nameNode: AstNode = StringNode(name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "AnnotationMemberDeclaration",
        children = Map(
            "annotations" -> annotationsNode,
            "javadoc" -> javadocNode,
            "defaultValue" -> defaultValueNode,
            "type" -> memberTypeNode,
            "modifiers" -> modifiersNode,
            "name" -> nameNode))
  }

  override def visit(n: FieldDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val variables: Seq[VariableDeclarator] = wrapJavaList(n.getVariables)
    val memberType: Type = n.getType
    val javadoc: Option[JavadocComment] = Option(n.getJavaDoc)
    val modifiers: Int = n.getModifiers

    // Convert components to their AST counterparts.
    val annotationsNode: AstNode = buildAndAcceptSeq("FieldDeclaration", annotations)
    val variablesNode: AstNode = buildAndAcceptSeq("VariablesList", variables)
    val javadocNode: AstNode = buildAndAcceptOption(javadoc)
    val memberTypeNode: AstNode = memberType.accept(this, arg)
    val modifiersNode: AstNode = IntegerNode(modifiers)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "FieldDeclaration",
        children = Map(
            "annotations" -> annotationsNode,
            "variables" -> variablesNode,
            "javadoc" -> javadocNode,
            "type" -> memberTypeNode,
            "modifiers" -> modifiersNode))
  }

  override def visit(n: VariableDeclarator, arg: Null): AstNode = {
    // Get components of this node.
    val id: VariableDeclaratorId = n.getId
    val init: Option[Expression] = Option(n.getInit)

    // Convert components to their AST counterparts.
    val idNode: AstNode = id.accept(this, arg)
    val initNode: AstNode = buildAndAcceptOption(init)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "VariableDeclarator",
        children = Map(
            "id" -> idNode,
            "init" -> initNode))
  }

  override def visit(n: VariableDeclaratorId, arg: Null): AstNode = {
    // Get components of this node.
    val arrayCount: Int = n.getArrayCount
    val name: String = n.getName

    // Convert components to their AST counterparts.
    val arrayCountNode: AstNode = IntegerNode(arrayCount)
    val nameNode: AstNode = StringNode(name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "VariableDeclaratorId",
        children = Map(
            "arrayCount" -> arrayCountNode,
            "name" -> nameNode))
  }

  override def visit(n: ConstructorDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val parameters: Seq[Parameter] = wrapJavaList(n.getParameters)
    val typeParameters: Seq[TypeParameter] = wrapJavaList(n.getTypeParameters)
    val throws: Seq[NameExpr] = wrapJavaList(n.getThrows)
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val javadoc: Option[JavadocComment] = Option(n.getJavaDoc)
    val modifiers: Int = n.getModifiers
    val block: BlockStmt = n.getBlock
    val name: String = n.getName

    // Convert components to their AST counterparts.
    val parametersNode: AstNode = buildAndAcceptSeq("ParametersList", parameters)
    val typeParametersNode: AstNode = buildAndAcceptSeq("TypeParametersList", typeParameters)
    val throwsNode: AstNode = buildAndAcceptSeq("ThrowsList", throws)
    val annotationsNode: AstNode = buildAndAcceptSeq("FieldDeclaration", annotations)
    val javadocNode: AstNode = buildAndAcceptOption(javadoc)
    val modifiersNode: AstNode = IntegerNode(modifiers)
    val blockNode: AstNode = block.accept(this, arg)
    val nameNode: AstNode = StringNode(name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ConstructorDeclaration",
        children = Map(
            "parameters" -> parametersNode,
            "typeParameters" -> typeParametersNode,
            "throws" -> throwsNode,
            "annotations" -> annotationsNode,
            "javadoc" -> javadocNode,
            "modifiers" -> modifiersNode,
            "block" -> blockNode,
            "name" -> nameNode))
  }

  override def visit(n: MethodDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val parameters: Seq[Parameter] = wrapJavaList(n.getParameters)
    val typeParameters: Seq[TypeParameter] = wrapJavaList(n.getTypeParameters)
    val throws: Seq[NameExpr] = wrapJavaList(n.getThrows)
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val javadoc: Option[JavadocComment] = Option(n.getJavaDoc)
    val modifiers: Int = n.getModifiers
    val methodType: Type = n.getType
    val name: String = n.getName
    val arrayCount: Int = n.getArrayCount
    val body: Option[BlockStmt] = Option(n.getBody)

    // Convert components to their AST counterparts.
    val parametersNode: AstNode = buildAndAcceptSeq("ParametersList", parameters)
    val typeParametersNode: AstNode = buildAndAcceptSeq("TypeParametersList", typeParameters)
    val throwsNode: AstNode = buildAndAcceptSeq("ThrowsList", throws)
    val annotationsNode: AstNode = buildAndAcceptSeq("FieldDeclaration", annotations)
    val javadocNode: AstNode = buildAndAcceptOption(javadoc)
    val modifiersNode: AstNode = IntegerNode(modifiers)
    val methodTypeNode: AstNode = methodType.accept(this, arg)
    val nameNode: AstNode = StringNode(name)
    val arrayCountNode: AstNode = IntegerNode(arrayCount)
    val bodyNode: AstNode = buildAndAcceptOption(body)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "MethodDeclaration",
        children = Map(
            "parameters" -> parametersNode,
            "typeParameters" -> typeParametersNode,
            "throws" -> throwsNode,
            "annotations" -> annotationsNode,
            "javadoc" -> javadocNode,
            "modifiers" -> modifiersNode,
            "type" -> methodTypeNode,
            "name" -> nameNode,
            "arrayCount" -> arrayCountNode,
            "body" -> bodyNode))
  }

  override def visit(n: Parameter, arg: Null): AstNode = {
    // Get components of this node.
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val modifiers: Int = n.getModifiers
    val parameterType: Type = n.getType
    val isVarArgs: Boolean = n.isVarArgs
    val id: VariableDeclaratorId = n.getId

    // Convert components to their AST counterparts.
    val annotationsNode: AstNode = buildAndAcceptSeq("AnnotationsList", annotations)
    val modifiersNode: AstNode = IntegerNode(modifiers)
    val parameterTypeNode: AstNode = parameterType.accept(this, arg)
    val isVarArgsNode: AstNode = BooleanNode(isVarArgs)
    val idNode: AstNode = id.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "Parameter",
        children = Map(
            "annotations" -> annotationsNode,
            "modifiers" -> modifiersNode,
            "parameterType" -> parameterTypeNode,
            "isVarArgs" -> isVarArgsNode,
            "id" -> idNode))
  }

  override def visit(n: EmptyMemberDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val javadoc: Option[JavadocComment] = Option(n.getJavaDoc)

    // Convert components to their AST counterparts.
    val annotationsNode: AstNode = buildAndAcceptSeq("FieldDeclaration", annotations)
    val javadocNode: AstNode = buildAndAcceptOption(javadoc)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "EmptyMemberDeclaration",
        children = Map(
            "annotations" -> annotationsNode,
            "javadoc" -> javadocNode))
  }

  override def visit(n: InitializerDeclaration, arg: Null): AstNode = {
    // Get components of this node.
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val javadoc: Option[JavadocComment] = Option(n.getJavaDoc)
    val isStatic: Boolean = n.isStatic
    val block: BlockStmt = n.getBlock

    // Convert components to their AST counterparts.
    val annotationsNode: AstNode = buildAndAcceptSeq("FieldDeclaration", annotations)
    val javadocNode: AstNode = buildAndAcceptOption(javadoc)
    val isStaticNode: AstNode = BooleanNode(isStatic)
    val blockNode: AstNode = block.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "InitializerDeclaration",
        children = Map(
            "annotations" -> annotationsNode,
            "javadoc" -> javadocNode,
            "isStatic" -> isStaticNode,
            "block" -> blockNode))
  }

  override def visit(n: JavadocComment, arg: Null): AstNode = {
    // Get components of this node.
    val content: String = n.getContent

    // Convert components to their AST counterparts.
    val contentNode: AstNode = StringNode(content)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "JavadocComment",
        children = Map(
            "content" -> contentNode))
  }

  // Type

  override def visit(n: ClassOrInterfaceType, arg: Null): AstNode = {
    // Get components of this node.
    val typeArgs: Seq[Type] = wrapJavaList(n.getTypeArgs)
    val scope: Option[ClassOrInterfaceType] = Option(n.getScope)
    val name: String = n.getName

    // Convert components to their AST counterparts.
    val typeArgsNode: AstNode = buildAndAcceptSeq("typeArgs", typeArgs)
    val scopeNode: AstNode = buildAndAcceptOption(scope)
    val nameNode: AstNode = StringNode(name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ClassOrInterfaceType",
        children = Map(
            "typeArgs" -> typeArgsNode,
            "scope" -> scopeNode,
            "name" -> nameNode))
  }

  override def visit(n: PrimitiveType, arg: Null): AstNode = {
    // Get components of this node.
    val primitiveType: Primitive = n.getType

    // Convert components to their AST counterparts.
    val primitiveTypeNode: AstNode = StringNode(primitiveType.name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "PrimitiveType",
        children = Map(
            "type" -> primitiveTypeNode))
  }

  override def visit(n: ReferenceType, arg: Null): AstNode = {
    // Get components of this node.
    val arrayCount: Int = n.getArrayCount
    val referenceType: Type = n.getType

    // Convert components to their AST counterparts.
    val arrayCountNode: AstNode = IntegerNode(arrayCount)
    val referenceTypeNode: AstNode = referenceType.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ReferenceType",
        children = Map(
            "arrayCount" -> arrayCountNode,
            "type" -> referenceTypeNode))
  }

  override def visit(n: VoidType, arg: Null): AstNode = {
    // Construct and return a map-type node.
    MapNode(
        nodeType = "VoidType",
        children = Map())
  }

  override def visit(n: WildcardType, arg: Null): AstNode = {
    // Get components of this node.
    val extendsBound: Option[ReferenceType] = Option(n.getExtends)
    val superBound: Option[ReferenceType] = Option(n.getSuper)

    // Convert components to their AST counterparts.
    val extendsBoundNode: AstNode = buildAndAcceptOption(extendsBound)
    val superBoundNode: AstNode = buildAndAcceptOption(superBound)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "WildcardType",
        children = Map(
            "extends" -> extendsBoundNode,
            "super" -> superBoundNode))
  }

  // Expression

  override def visit(n: ArrayAccessExpr, arg: Null): AstNode = {
    // Get components of this node.
    val name: Expression = n.getName
    val index: Expression = n.getIndex

    // Convert components to their AST counterparts.
    val nameNode: AstNode = name.accept(this, arg)
    val indexNode: AstNode = index.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ArrayAccessExpr",
        children = Map(
            "name" -> nameNode,
            "index" -> indexNode))
  }

  override def visit(n: ArrayCreationExpr, arg: Null): AstNode = {
    // Get components of this node.
    val arrayType: Type = n.getType
    val arrayCount: Int = n.getArrayCount
    val initializer: Option[ArrayInitializerExpr] = Option(n.getInitializer)
    val dimensions: Seq[Expression] = wrapJavaList(n.getDimensions)

    // Convert components to their AST counterparts.
    val arrayTypeNode: AstNode = arrayType.accept(this, arg)
    val arrayCountNode: AstNode = IntegerNode(arrayCount)
    val initializerNode: AstNode = buildAndAcceptOption(initializer)
    val dimensionsNode: AstNode = buildAndAcceptSeq("DimensionsList", dimensions)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ArrayCreationExpr",
        children = Map(
            "type" -> arrayTypeNode,
            "arrayCount" -> arrayCountNode,
            "initializer" -> initializerNode,
            "dimensions" -> dimensionsNode))
  }

  override def visit(n: ArrayInitializerExpr, arg: Null): AstNode = {
    // Get components of this node.
    val values: Seq[Expression] = wrapJavaList(n.getValues)

    // Convert components to their AST counterparts.
    val valuesNode: AstNode = buildAndAcceptSeq("ValuesList", values)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ArrayInitializerExpr",
        children = Map(
            "values" -> valuesNode))
  }

  override def visit(n: AssignExpr, arg: Null): AstNode = {
    // Get components of this node.
    val target: Expression = n.getTarget
    val value: Expression = n.getValue
    val operation: AssignExpr.Operator = n.getOperator

    // Convert components to their AST counterparts.
    val targetNode: AstNode = target.accept(this, arg)
    val valueNode: AstNode = value.accept(this, arg)
    val operationNode: AstNode = StringNode(operation.name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "AssignExpr",
        children = Map(
            "target" -> targetNode,
            "value" -> valueNode,
            "operation" -> operationNode))
  }

  override def visit(n: BinaryExpr, arg: Null): AstNode = {
    // Get components of this node.
    val left: Expression = n.getLeft
    val right: Expression = n.getRight
    val operation: BinaryExpr.Operator = n.getOperator

    // Convert components to their AST counterparts.
    val leftNode: AstNode = left.accept(this, arg)
    val rightNode: AstNode = right.accept(this, arg)
    val operationNode: AstNode = StringNode(operation.name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "BinaryExpr",
        children = Map(
            "left" -> leftNode,
            "right" -> rightNode,
            "operation" -> operationNode))
  }

  override def visit(n: CastExpr, arg: Null): AstNode = {
    // Get components of this node.
    val expression: Expression = n.getExpr
    val castType: Type = n.getType

    // Convert components to their AST counterparts.
    val expressionNode: AstNode = expression.accept(this, arg)
    val castTypeNode: AstNode = castType.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "CastExpr",
        children = Map(
            "expression" -> expressionNode,
            "type" -> castTypeNode))
  }

  override def visit(n: ClassExpr, arg: Null): AstNode = {
    // Get components of this node.
    val classType: Type = n.getType

    // Convert components to their AST counterparts.
    val classTypeNode: AstNode = classType.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ClassExpr",
        children = Map(
            "type" -> classTypeNode))
  }

  override def visit(n: ConditionalExpr, arg: Null): AstNode = {
    // Get components of this node.
    val condition: Expression = n.getCondition
    val thenExpr: Option[Expression] = Option(n.getThenExpr)
    val elseExpr: Option[Expression] = Option(n.getElseExpr)

    // Convert components to their AST counterparts.
    val conditionNode: AstNode = condition.accept(this, arg)
    val thenExprNode: AstNode = buildAndAcceptOption(thenExpr)
    val elseExprNode: AstNode = buildAndAcceptOption(elseExpr)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ConditionalExpr",
        children = Map(
            "condition" -> conditionNode,
            "then" -> thenExprNode,
            "else" -> elseExprNode))
  }

  override def visit(n: EnclosedExpr, arg: Null): AstNode = {
    // Get components of this node.
    val inner: Expression = n.getInner

    // Convert components to their AST counterparts.
    val innerNode: AstNode = inner.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "EnclosedExpr",
        children = Map(
            "inner" -> innerNode))
  }

  override def visit(n: FieldAccessExpr, arg: Null): AstNode = {
    // Get components of this node.
    val scope: Expression = n.getScope
    val typeArgs: Seq[Type] = wrapJavaList(n.getTypeArgs)
    val field: String = n.getField

    // Convert components to their AST counterparts.
    val scopeNode: AstNode = scope.accept(this, arg)
    val typeArgsNode: AstNode = buildAndAcceptSeq("TypeArgsList", typeArgs)
    val fieldNode: AstNode = StringNode(field)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "FieldAccessExpr",
        children = Map(
            "scope" -> scopeNode,
            "typeArgs" -> typeArgsNode,
            "field" -> fieldNode))
  }

  override def visit(n: InstanceOfExpr, arg: Null): AstNode = {
    // Get components of this node.
    val expression: Expression = n.getExpr
    val instanceOfType: Type = n.getType

    // Convert components to their AST counterparts.
    val expressionNode: AstNode = expression.accept(this, arg)
    val instanceOfTypeNode: AstNode = instanceOfType.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "InstanceOfExpr",
        children = Map(
            "expression" -> expressionNode,
            "type" -> instanceOfTypeNode))
  }

  override def visit(n: StringLiteralExpr, arg: Null): AstNode = {
    MapNode(
        nodeType = "StringLiteral",
        children = Map(
            "value" -> StringNode(n.getValue)))
  }

  override def visit(n: IntegerLiteralExpr, arg: Null): AstNode = {
    MapNode(
        nodeType = "IntegerLiteral",
        children = Map(
            "value" -> IntegerNode(n.getValue.toInt)))
  }

  override def visit(n: LongLiteralExpr, arg: Null): AstNode = {
    MapNode(
        nodeType = "LongLiteral",
        children = Map(
            "value" -> IntegerNode(n.getValue.toLong)))
  }

  override def visit(n: IntegerLiteralMinValueExpr, arg: Null): AstNode = {
    MapNode(
        nodeType = "IntegerLiteralMinValue",
        children = Map())
  }

  override def visit(n: LongLiteralMinValueExpr, arg: Null): AstNode = {
    MapNode(
        nodeType = "LongLiteralMinValue",
        children = Map())
  }

  override def visit(n: CharLiteralExpr, arg: Null): AstNode = {
    MapNode(
        nodeType = "CharLiteral",
        children = Map(
            "value" -> StringNode(n.getValue)))
  }

  override def visit(n: DoubleLiteralExpr, arg: Null): AstNode = {
    MapNode(
        nodeType = "DoubleLiteral",
        children = Map(
            "value" -> DecimalNode(n.getValue.toDouble)))
  }

  override def visit(n: BooleanLiteralExpr, arg: Null): AstNode = {
    MapNode(
        nodeType = "BooleanLiteral",
        children = Map(
            "value" -> BooleanNode(n.getValue)))
  }

  override def visit(n: NullLiteralExpr, arg: Null): AstNode = {
    MapNode(
        nodeType = "NullLiteral",
        children = Map())
  }

  override def visit(n: MethodCallExpr, arg: Null): AstNode = {
    // Get components of this node.
    val typeArgs: Seq[Type] = wrapJavaList(n.getTypeArgs)
    val args: Seq[Expression] = wrapJavaList(n.getArgs)
    val scope: Option[Expression] = Option(n.getScope)
    val name: String = n.getName

    // Convert components to their AST counterparts.
    val typeArgsNode: AstNode = buildAndAcceptSeq("TypeArgsList", typeArgs)
    val argsNode: AstNode = buildAndAcceptSeq("ArgsList", args)
    val scopeNode: AstNode = buildAndAcceptOption(scope)
    val nameNode: AstNode = StringNode(name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "MethodCallExpr",
        children = Map(
            "typeArgs" -> typeArgsNode,
            "args" -> argsNode,
            "scope" -> scopeNode,
            "name" -> nameNode))
  }

  override def visit(n: NameExpr, arg: Null): AstNode = {
    // Get components of this node.
    val name: String = n.getName

    // Convert components to their AST counterparts.
    val nameNode: AstNode = StringNode(name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "NameExpr",
        children = Map(
            "name" -> nameNode))
  }

  override def visit(n: ObjectCreationExpr, arg: Null): AstNode = {
    // Get components of this node.
    val typeArgs: Seq[Type] = wrapJavaList(n.getTypeArgs)
    val args: Seq[Expression] = wrapJavaList(n.getArgs)
    val anonymousClassBody: Seq[BodyDeclaration] = wrapJavaList(n.getAnonymousClassBody)
    val scope: Expression = n.getScope
    val objectType: ClassOrInterfaceType = n.getType

    // Convert components to their AST counterparts.
    val typeArgsNode: AstNode = buildAndAcceptSeq("TypeArgsList", typeArgs)
    val argsNode: AstNode = buildAndAcceptSeq("ArgsList", args)
    val anonymousClassBodyNode: AstNode = buildAndAcceptSeq(
        "AnonymousClassBodyList", anonymousClassBody)
    val scopeNode: AstNode = scope.accept(this, arg)
    val objectTypeNode: AstNode = objectType.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ObjectCreationExpr",
        children = Map(
            "typeArgs" -> typeArgsNode,
            "args" -> argsNode,
            "anonymousClassBody" -> anonymousClassBodyNode,
            "scope" -> scopeNode,
            "type" -> objectTypeNode))
  }

  override def visit(n: QualifiedNameExpr, arg: Null): AstNode = {
    // Get components of this node.
    val name: String = n.getName
    val qualifier: NameExpr = n.getQualifier

    // Convert components to their AST counterparts.
    val nameNode: AstNode = StringNode(name)
    val qualifierNode: AstNode = qualifier.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "QualifiedNameExpr",
        children = Map(
            "name" -> nameNode,
            "qualifier" -> qualifierNode))
  }

  override def visit(n: ThisExpr, arg: Null): AstNode = {
    // Get components of this node.
    val classExpr: Option[Expression] = Option(n.getClassExpr)

    // Convert components to their AST counterparts.
    val classExprNode: AstNode = buildAndAcceptOption(classExpr)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ThisExpr",
        children = Map(
            "classExpression" -> classExprNode))
  }

  override def visit(n: SuperExpr, arg: Null): AstNode = {
    // Get components of this node.
    val classExpr: Option[Expression] = Option(n.getClassExpr)

    // Convert components to their AST counterparts.
    val classExprNode: AstNode = buildAndAcceptOption(classExpr)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "SuperExpr",
        children = Map(
            "classExpression" -> classExprNode))
  }

  override def visit(n: UnaryExpr, arg: Null): AstNode = {
    // Get components of this node.
    val expression: Expression = n.getExpr
    val operation: Operator = n.getOperator

    // Convert components to their AST counterparts.
    val expressionNode: AstNode = expression.accept(this, arg)
    val operationNode: AstNode = StringNode(operation.name)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "UnaryExpr",
        children = Map(
            "expression" -> expressionNode,
            "operation" -> operationNode))
  }

  override def visit(n: VariableDeclarationExpr, arg: Null): AstNode = {
    // Get components of this node.
    val modifiers: Int = n.getModifiers
    val annotations: Seq[AnnotationExpr] = wrapJavaList(n.getAnnotations)
    val variableType: Type = n.getType
    val variables: Seq[VariableDeclarator] = wrapJavaList(n.getVars)

    // Convert components to their AST counterparts.
    val modifiersNode: AstNode = IntegerNode(modifiers)
    val annotationsNode: AstNode = buildAndAcceptSeq("AnnotationsList", annotations)
    val variableTypeNode: AstNode = variableType.accept(this, arg)
    val variablesNode: AstNode = buildAndAcceptSeq("VariablesList", variables)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "VariableDeclarationExpr",
        children = Map(
            "modifiers" -> modifiersNode,
            "annotations" -> annotationsNode,
            "type" -> variableTypeNode,
            "variables" -> variablesNode))
  }

  override def visit(n: MarkerAnnotationExpr, arg: Null): AstNode = {
    // Get components of this node.
    val name: NameExpr = n.getName

    // Convert components to their AST counterparts.
    val nameNode: AstNode = name.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "MarkerAnnotationExpr",
        children = Map(
            "name" -> nameNode))
  }

  override def visit(n: SingleMemberAnnotationExpr, arg: Null): AstNode = {
    // Get components of this node.
    val memberValue: Expression = n.getMemberValue
    val name: NameExpr = n.getName

    // Convert components to their AST counterparts.
    val memberValueNode: AstNode = memberValue.accept(this, arg)
    val nameNode: AstNode = name.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "SingleMemberAnnotationExpr",
        children = Map(
            "memberValue" -> memberValueNode,
            "name" -> nameNode))
  }

  override def visit(n: NormalAnnotationExpr, arg: Null): AstNode = {
    // Get components of this node.
    val pairs: Seq[MemberValuePair] = wrapJavaList(n.getPairs)
    val name: NameExpr = n.getName

    // Convert components to their AST counterparts.
    val pairsNode: AstNode = buildAndAcceptSeq("PairsList", pairs)
    val nameNode: AstNode = name.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "NormalAnnotationExpr",
        children = Map(
            "pairs" -> pairsNode,
            "name" -> nameNode))
  }

  override def visit(n: MemberValuePair, arg: Null): AstNode = {
    // Get components of this node.
    val name: String = n.getName
    val value: Expression = n.getValue

    // Convert components to their AST counterparts.
    val nameNode: AstNode = StringNode(name)
    val valueNode: AstNode = value.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "NormalAnnotationExpr",
        children = Map(
            "name" -> nameNode,
            "value" -> valueNode))
  }

  // Statements

  override def visit(n: ExplicitConstructorInvocationStmt, arg: Null): AstNode = {
    // Get components of this node.
    val args: Seq[Expression] = wrapJavaList(n.getArgs)
    val typeArgs: Seq[Type] = wrapJavaList(n.getTypeArgs)
    val expression: Expression = n.getExpr
    val isThis: Boolean = n.isThis

    // Convert components to their AST counterparts.
    val argsNode: AstNode = buildAndAcceptSeq("ArgsList", args)
    val typeArgsNode: AstNode = buildAndAcceptSeq("typeArgs", typeArgs)
    val expressionNode: AstNode = expression.accept(this, arg)
    val isThisNode: AstNode = BooleanNode(isThis)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ExplicitConstructorInvocationStmt",
        children = Map(
            "args" -> argsNode,
            "typeArgs" -> typeArgsNode,
            "expression" -> expressionNode,
            "isThis" -> isThisNode))
  }

  override def visit(n: TypeDeclarationStmt, arg: Null): AstNode = {
    // Get components of this node.
    val typeDeclaration: TypeDeclaration = n.getTypeDeclaration

    // Convert components to their AST counterparts.
    val typeDeclarationNode: AstNode = typeDeclaration.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "TypeDeclarationStmt",
        children = Map(
            "typeDeclaration" -> typeDeclarationNode))
  }

  override def visit(n: AssertStmt, arg: Null): AstNode = {
    // Get components of this node.
    val check: Expression = n.getCheck
    val message: Option[Expression] = Option(n.getMessage)

    // Convert components to their AST counterparts.
    val checkNode: AstNode = check.accept(this, arg)
    val messageNode: AstNode = buildAndAcceptOption(message)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "TypeDeclarationStmt",
        children = Map(
            "check" -> checkNode,
            "message" -> messageNode))
  }

  override def visit(n: BlockStmt, arg: Null): AstNode = {
    // Get components of this node.
    val statements: Seq[Statement] = wrapJavaList(n.getStmts)

    // Convert components to their AST counterparts.
    val statementsNode: AstNode = buildAndAcceptSeq("StatementsList", statements)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "BlockStmt",
        children = Map(
            "statements" -> statementsNode))
  }

  override def visit(n: LabeledStmt, arg: Null): AstNode = {
    // Get components of this node.
    val label: String = n.getLabel
    val statement: Statement = n.getStmt

    // Convert components to their AST counterparts.
    val labelNode: AstNode = StringNode(label)
    val statementNode: AstNode = statement.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "BlockStmt",
        children = Map(
            "label" -> labelNode,
            "statement" -> statementNode))
  }

  override def visit(n: EmptyStmt, arg: Null): AstNode = {
    MapNode(
        nodeType = "EmptyStmt",
        children = Map())
  }

  override def visit(n: ExpressionStmt, arg: Null): AstNode = {
    // Get components of this node.
    val expression: Expression = n.getExpression

    // Convert components to their AST counterparts.
    val expressionNode: AstNode = expression.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ExpressionStmt",
        children = Map(
            "expression" -> expressionNode))
  }

  override def visit(n: SwitchStmt, arg: Null): AstNode = {
    // Get components of this node.
    val selector: Expression = n.getSelector
    val entries: Seq[SwitchEntryStmt] = wrapJavaList(n.getEntries)

    // Convert components to their AST counterparts.
    val selectorNode: AstNode = selector.accept(this, arg)
    val entriesNode: AstNode = buildAndAcceptSeq("EntriesList", entries)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "SwitchStmt",
        children = Map(
            "selector" -> selectorNode,
            "entries" -> entriesNode))
  }

  override def visit(n: SwitchEntryStmt, arg: Null): AstNode = {
    // Get components of this node.
    val label: Expression = n.getLabel
    val statements: Seq[Statement] = wrapJavaList(n.getStmts)

    // Convert components to their AST counterparts.
    val labelNode: AstNode = label.accept(this, arg)
    val statementsNode: AstNode = buildAndAcceptSeq("StatementsList", statements)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "SwitchEntryStmt",
        children = Map(
            "label" -> labelNode,
            "statements" -> statementsNode))
  }

  override def visit(n: BreakStmt, arg: Null): AstNode = {
    // Get components of this node.
    val id: String = n.getId

    // Convert components to their AST counterparts.
    val idNode: AstNode = StringNode(id)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "BreakStmt",
        children = Map(
            "id" -> idNode))
  }

  override def visit(n: ReturnStmt, arg: Null): AstNode = {
    // Get components of this node.
    val expression: Expression = n.getExpr

    // Convert components to their AST counterparts.
    val expressionNode: AstNode = expression.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ReturnStmt",
        children = Map(
            "expression" -> expressionNode))
  }

  override def visit(n: IfStmt, arg: Null): AstNode = {
    // Get components of this node.
    val condition: Expression = n.getCondition
    val thenStmt: Statement = n.getThenStmt
    val elseStmt: Option[Statement] = Option(n.getElseStmt)

    // Convert components to their AST counterparts.
    val conditionNode: AstNode = condition.accept(this, arg)
    val thenStmtNode: AstNode = thenStmt.accept(this, arg)
    val elseStmtNode: AstNode = buildAndAcceptOption(elseStmt)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "IfStmt",
        children = Map(
            "condition" -> conditionNode,
            "thenStmt" -> thenStmtNode,
            "elseStmt" -> elseStmtNode))
  }

  override def visit(n: WhileStmt, arg: Null): AstNode = {
    // Get components of this node.
    val condition: Expression = n.getCondition
    val body: Option[Statement] = Option(n.getBody)

    // Convert components to their AST counterparts.
    val conditionNode: AstNode = condition.accept(this, arg)
    val bodyNode: AstNode = buildAndAcceptOption(body)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "WhileStmt",
        children = Map(
            "condition" -> conditionNode,
            "body" -> bodyNode))
  }

  override def visit(n: ContinueStmt, arg: Null): AstNode = {
    // Get components of this node.
    val id: Option[String] = Option(n.getId)

    // Convert components to their AST counterparts.
    val idNode: AstNode = id
        .map { StringNode }
        .getOrElse { NullNode }

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ContinueStmt",
        children = Map(
            "id" -> idNode))
  }

  override def visit(n: DoStmt, arg: Null): AstNode = {
    // Get components of this node.
    val condition: Expression = n.getCondition
    val body: Statement = n.getBody

    // Convert components to their AST counterparts.
    val conditionNode: AstNode = condition.accept(this, arg)
    val bodyNode: AstNode = body.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "DoStmt",
        children = Map(
            "condition" -> conditionNode,
            "body" -> bodyNode))
  }

  override def visit(n: ForeachStmt, arg: Null): AstNode = {
    // Get components of this node.
    val variable: VariableDeclarationExpr = n.getVariable
    val iterable: Expression = n.getIterable
    val body: Option[Statement] = Option(n.getBody)

    // Convert components to their AST counterparts.
    val variableNode: AstNode = variable.accept(this, arg)
    val iterableNode: AstNode = iterable.accept(this, arg)
    val bodyNode: AstNode = buildAndAcceptOption(body)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ForeachStmt",
        children = Map(
            "variable" -> variableNode,
            "iterable" -> iterableNode,
            "body" -> bodyNode))
  }

  override def visit(n: ForStmt, arg: Null): AstNode = {
    // Get components of this node.
    val init: Seq[Expression] = wrapJavaList(n.getInit)
    val update: Seq[Expression] = wrapJavaList(n.getUpdate)
    val body: Option[Statement] = Option(n.getBody)
    val compare: Expression = n.getCompare

    // Convert components to their AST counterparts.
    val initNode: AstNode = buildAndAcceptSeq("InitList", init)
    val updateNode: AstNode = buildAndAcceptSeq("UpdateList", update)
    val bodyNode: AstNode = buildAndAcceptOption(body)
    val compareNode: AstNode = compare.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ForStmt",
        children = Map(
            "init" -> initNode,
            "update" -> updateNode,
            "body" -> bodyNode,
            "compare" -> compareNode))
  }

  override def visit(n: ThrowStmt, arg: Null): AstNode = {
    // Get components of this node.
    val expression: Expression = n.getExpr

    // Convert components to their AST counterparts.
    val expressionNode: AstNode = expression.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "ThrowStmt",
        children = Map(
            "expression" -> expressionNode))
  }

  override def visit(n: SynchronizedStmt, arg: Null): AstNode = {
    // Get components of this node.
    val expression: Expression = n.getExpr
    val block: BlockStmt = n.getBlock

    // Convert components to their AST counterparts.
    val expressionNode: AstNode = expression.accept(this, arg)
    val blockNode: AstNode = block.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "SynchronizedStmt",
        children = Map(
            "expression" -> expressionNode,
            "block" -> blockNode))
  }

  override def visit(n: TryStmt, arg: Null): AstNode = {
    // Get components of this node.
    val tryBlock: BlockStmt = n.getTryBlock
    val catches: Seq[CatchClause] = wrapJavaList(n.getCatchs)
    val finallyBlock: Option[BlockStmt] = Option(n.getFinallyBlock)

    // Convert components to their AST counterparts.
    val tryBlockNode: AstNode = tryBlock.accept(this, arg)
    val catchesNode: AstNode = buildAndAcceptSeq("CatchesList", catches)
    val finallyBlockNode: AstNode = buildAndAcceptOption(finallyBlock)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "TryStmt",
        children = Map(
            "try" -> tryBlockNode,
            "catches" -> catchesNode,
            "finally" -> finallyBlockNode))
  }

  override def visit(n: CatchClause, arg: Null): AstNode = {
    // Get components of this node.
    val exception: Parameter = n.getExcept
    val catchBlock: BlockStmt = n.getCatchBlock

    // Convert components to their AST counterparts.
    val exceptionNode: AstNode = exception.accept(this, arg)
    val catchBlockNode: AstNode = catchBlock.accept(this, arg)

    // Construct and return a map-type node.
    MapNode(
        nodeType = "CatchClause",
        children = Map(
            "exception" -> exceptionNode,
            "catchBlock" -> catchBlockNode))
  }
}
