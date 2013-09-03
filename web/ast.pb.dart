///
//  Generated code. Do not modify.
///
library Ast.pb;

import 'dart:typed_data';

import 'package:protobuf/protobuf.dart';

class AstNode extends GeneratedMessage {
  static final BuilderInfo _i = new BuilderInfo('AstNode')
    ..a(1, 'nodeType', GeneratedMessage.QS)
    ..a(2, 'booleanNode', GeneratedMessage.OB)
    ..a(3, 'decimalNode', GeneratedMessage.OD)
    ..a(4, 'integerNode', GeneratedMessage.OS6, () => makeLongInt(0))
    ..a(5, 'stringNode', GeneratedMessage.OS)
    ..a(6, 'listNode', GeneratedMessage.OM, () => new ListNode(), () => new ListNode())
    ..a(7, 'mapNode', GeneratedMessage.OM, () => new MapNode(), () => new MapNode())
  ;

  AstNode() : super();
  AstNode.fromBuffer(List<int> i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromBuffer(i, r);
  AstNode.fromJson(String i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromJson(i, r);
  AstNode clone() => new AstNode()..mergeFromMessage(this);
  BuilderInfo get info_ => _i;

  String get nodeType => getField(1);
  void set nodeType(String v) { setField(1, v); }
  bool hasNodeType() => hasField(1);
  void clearNodeType() => clearField(1);

  bool get booleanNode => getField(2);
  void set booleanNode(bool v) { setField(2, v); }
  bool hasBooleanNode() => hasField(2);
  void clearBooleanNode() => clearField(2);

  double get decimalNode => getField(3);
  void set decimalNode(double v) { setField(3, v); }
  bool hasDecimalNode() => hasField(3);
  void clearDecimalNode() => clearField(3);

  ByteData get integerNode => getField(4);
  void set integerNode(ByteData v) { setField(4, v); }
  bool hasIntegerNode() => hasField(4);
  void clearIntegerNode() => clearField(4);

  String get stringNode => getField(5);
  void set stringNode(String v) { setField(5, v); }
  bool hasStringNode() => hasField(5);
  void clearStringNode() => clearField(5);

  ListNode get listNode => getField(6);
  void set listNode(ListNode v) { setField(6, v); }
  bool hasListNode() => hasField(6);
  void clearListNode() => clearField(6);

  MapNode get mapNode => getField(7);
  void set mapNode(MapNode v) { setField(7, v); }
  bool hasMapNode() => hasField(7);
  void clearMapNode() => clearField(7);
}

class ListNode extends GeneratedMessage {
  static final BuilderInfo _i = new BuilderInfo('ListNode')
    ..a(1, 'nodeType', GeneratedMessage.OS)
    ..m(2, 'children', () => new AstNode(), () => new PbList<AstNode>())
  ;

  ListNode() : super();
  ListNode.fromBuffer(List<int> i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromBuffer(i, r);
  ListNode.fromJson(String i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromJson(i, r);
  ListNode clone() => new ListNode()..mergeFromMessage(this);
  BuilderInfo get info_ => _i;

  String get nodeType => getField(1);
  void set nodeType(String v) { setField(1, v); }
  bool hasNodeType() => hasField(1);
  void clearNodeType() => clearField(1);

  List<AstNode> get children => getField(2);
}

class MapNode_Entry extends GeneratedMessage {
  static final BuilderInfo _i = new BuilderInfo('MapNode_Entry')
    ..a(1, 'key', GeneratedMessage.QS)
    ..a(2, 'value', GeneratedMessage.QM, () => new AstNode(), () => new AstNode())
  ;

  MapNode_Entry() : super();
  MapNode_Entry.fromBuffer(List<int> i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromBuffer(i, r);
  MapNode_Entry.fromJson(String i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromJson(i, r);
  MapNode_Entry clone() => new MapNode_Entry()..mergeFromMessage(this);
  BuilderInfo get info_ => _i;

  String get key => getField(1);
  void set key(String v) { setField(1, v); }
  bool hasKey() => hasField(1);
  void clearKey() => clearField(1);

  AstNode get value => getField(2);
  void set value(AstNode v) { setField(2, v); }
  bool hasValue() => hasField(2);
  void clearValue() => clearField(2);
}

class MapNode extends GeneratedMessage {
  static final BuilderInfo _i = new BuilderInfo('MapNode')
    ..a(1, 'nodeType', GeneratedMessage.OS)
    ..m(2, 'children', () => new MapNode_Entry(), () => new PbList<MapNode_Entry>())
  ;

  MapNode() : super();
  MapNode.fromBuffer(List<int> i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromBuffer(i, r);
  MapNode.fromJson(String i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromJson(i, r);
  MapNode clone() => new MapNode()..mergeFromMessage(this);
  BuilderInfo get info_ => _i;

  String get nodeType => getField(1);
  void set nodeType(String v) { setField(1, v); }
  bool hasNodeType() => hasField(1);
  void clearNodeType() => clearField(1);

  List<MapNode_Entry> get children => getField(2);
}

