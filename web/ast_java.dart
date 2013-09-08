// Implementation of an Abstract Syntax Tree for Java.
part of ast_editor;

abstract class ProtoBufNode
    extends AstNode {
  ProtoBufNode parent;
  dynamic key;

  ProtoBufNode(ProtoBufNode this.parent, dynamic this.key);

  Iterable<Entry<dynamic, AstNode>> get children => null;
  bool get isList => false;
  bool get isMap => false;
  bool get isPrimitive => false;

  AstNode getChild(dynamic key) => null;
  AstNode removeChild(dynamic key) => null;
}

class ProtoBufNull
    extends ProtoBufNode {
  ProtoBufNull(dynamic parent, dynamic key) : super(parent, key);

  dynamic get value => null;
  bool get isPrimitive => true;
}

class ProtoBufBoolean
    extends ProtoBufNode {
  bool value;

  ProtoBufBoolean(dynamic parent, dynamic key, bool this.value) : super(parent, key);

  bool get isPrimitive => true;
}

class ProtoBufDecimal
    extends ProtoBufNode {
  double value;

  ProtoBufDecimal(dynamic parent, dynamic key, double this.value) : super(parent, key);

  bool get isPrimitive => true;
}

class ProtoBufInteger
    extends ProtoBufNode {
  int value;

  ProtoBufInteger(dynamic parent, dynamic key, int this.value) : super(parent, key);

  bool get isPrimitive => true;
}

class ProtoBufString
    extends ProtoBufNode {
  String value;

  ProtoBufString(dynamic parent, dynamic key, String this.value) : super(parent, key);

  bool get isPrimitive => true;
}

class ProtoBufList
    extends ProtoBufNode {
  String nodeType;
  List<ProtoBufNode> _children;

  ProtoBufList(
      dynamic parent,
      dynamic key,
      String this.nodeType,
      List<ProtoBufNode> this._children) : super(parent, key);

  Iterable<Entry<int, ProtoBufNode>> get children {
    return _children.fold([], (List<Entry<int, ProtoBufNode>> accumulator, ProtoBufNode node) {
      return accumulator..add(new Entry(accumulator.length, node));
    });
  }
  bool get isList => true;
}

class ProtoBufMap
    extends ProtoBufNode {
  String nodeType;
  Map<String, ProtoBufNode> _children;

  ProtoBufMap(
      dynamic parent,
      dynamic key,
      String this.nodeType,
      Map<String, ProtoBufNode> this._children) : super(parent, key);

  Iterable<Entry<String, ProtoBufNode>> get children {
    return _children.keys.map((String key) {
      return new Entry(key, _children[key]);
    });
  }
}

// TODO: Add a AstMutator/AstWalker/AstCursor class for walking and modifying a tree.
//       Could make a builder-like specific variety.
