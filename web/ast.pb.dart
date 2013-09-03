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

class AstGetRequest extends GeneratedMessage {
  static final BuilderInfo _i = new BuilderInfo('AstGetRequest')
    ..a(1, 'path', GeneratedMessage.OS)
    ..a(2, 'version', GeneratedMessage.OU6, () => makeLongInt(0))
    ..hasRequiredFields = false
  ;

  AstGetRequest() : super();
  AstGetRequest.fromBuffer(List<int> i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromBuffer(i, r);
  AstGetRequest.fromJson(String i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromJson(i, r);
  AstGetRequest clone() => new AstGetRequest()..mergeFromMessage(this);
  BuilderInfo get info_ => _i;

  String get path => getField(1);
  void set path(String v) { setField(1, v); }
  bool hasPath() => hasField(1);
  void clearPath() => clearField(1);

  ByteData get version => getField(2);
  void set version(ByteData v) { setField(2, v); }
  bool hasVersion() => hasField(2);
  void clearVersion() => clearField(2);
}

class AstGetResponse extends GeneratedMessage {
  static final BuilderInfo _i = new BuilderInfo('AstGetResponse')
    ..a(1, 'root', GeneratedMessage.OM, () => new AstNode(), () => new AstNode())
    ..a(2, 'errorMessage', GeneratedMessage.OS)
  ;

  AstGetResponse() : super();
  AstGetResponse.fromBuffer(List<int> i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromBuffer(i, r);
  AstGetResponse.fromJson(String i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromJson(i, r);
  AstGetResponse clone() => new AstGetResponse()..mergeFromMessage(this);
  BuilderInfo get info_ => _i;

  AstNode get root => getField(1);
  void set root(AstNode v) { setField(1, v); }
  bool hasRoot() => hasField(1);
  void clearRoot() => clearField(1);

  String get errorMessage => getField(2);
  void set errorMessage(String v) { setField(2, v); }
  bool hasErrorMessage() => hasField(2);
  void clearErrorMessage() => clearField(2);
}

class AstPutRequest extends GeneratedMessage {
  static final BuilderInfo _i = new BuilderInfo('AstPutRequest')
    ..a(1, 'path', GeneratedMessage.OS)
    ..a(2, 'root', GeneratedMessage.OM, () => new AstNode(), () => new AstNode())
  ;

  AstPutRequest() : super();
  AstPutRequest.fromBuffer(List<int> i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromBuffer(i, r);
  AstPutRequest.fromJson(String i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromJson(i, r);
  AstPutRequest clone() => new AstPutRequest()..mergeFromMessage(this);
  BuilderInfo get info_ => _i;

  String get path => getField(1);
  void set path(String v) { setField(1, v); }
  bool hasPath() => hasField(1);
  void clearPath() => clearField(1);

  AstNode get root => getField(2);
  void set root(AstNode v) { setField(2, v); }
  bool hasRoot() => hasField(2);
  void clearRoot() => clearField(2);
}

class AstPutResponse extends GeneratedMessage {
  static final BuilderInfo _i = new BuilderInfo('AstPutResponse')
    ..a(1, 'errorMessage', GeneratedMessage.OS)
    ..hasRequiredFields = false
  ;

  AstPutResponse() : super();
  AstPutResponse.fromBuffer(List<int> i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromBuffer(i, r);
  AstPutResponse.fromJson(String i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromJson(i, r);
  AstPutResponse clone() => new AstPutResponse()..mergeFromMessage(this);
  BuilderInfo get info_ => _i;

  String get errorMessage => getField(1);
  void set errorMessage(String v) { setField(1, v); }
  bool hasErrorMessage() => hasField(1);
  void clearErrorMessage() => clearField(1);
}

class AstRequest extends GeneratedMessage {
  static final BuilderInfo _i = new BuilderInfo('AstRequest')
    ..a(1, 'requestType', GeneratedMessage.QS)
    ..a(2, 'getRequest', GeneratedMessage.OM, () => new AstGetRequest(), () => new AstGetRequest())
    ..a(3, 'putRequest', GeneratedMessage.OM, () => new AstPutRequest(), () => new AstPutRequest())
  ;

  AstRequest() : super();
  AstRequest.fromBuffer(List<int> i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromBuffer(i, r);
  AstRequest.fromJson(String i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromJson(i, r);
  AstRequest clone() => new AstRequest()..mergeFromMessage(this);
  BuilderInfo get info_ => _i;

  String get requestType => getField(1);
  void set requestType(String v) { setField(1, v); }
  bool hasRequestType() => hasField(1);
  void clearRequestType() => clearField(1);

  AstGetRequest get getRequest => getField(2);
  void set getRequest(AstGetRequest v) { setField(2, v); }
  bool hasGetRequest() => hasField(2);
  void clearGetRequest() => clearField(2);

  AstPutRequest get putRequest => getField(3);
  void set putRequest(AstPutRequest v) { setField(3, v); }
  bool hasPutRequest() => hasField(3);
  void clearPutRequest() => clearField(3);
}

class AstResponse extends GeneratedMessage {
  static final BuilderInfo _i = new BuilderInfo('AstResponse')
    ..a(1, 'responseType', GeneratedMessage.QS)
    ..a(2, 'getResponse', GeneratedMessage.OM, () => new AstGetResponse(), () => new AstGetResponse())
    ..a(3, 'putResponse', GeneratedMessage.OM, () => new AstPutResponse(), () => new AstPutResponse())
  ;

  AstResponse() : super();
  AstResponse.fromBuffer(List<int> i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromBuffer(i, r);
  AstResponse.fromJson(String i, [ExtensionRegistry r = ExtensionRegistry.EMPTY]) : super.fromJson(i, r);
  AstResponse clone() => new AstResponse()..mergeFromMessage(this);
  BuilderInfo get info_ => _i;

  String get responseType => getField(1);
  void set responseType(String v) { setField(1, v); }
  bool hasResponseType() => hasField(1);
  void clearResponseType() => clearField(1);

  AstGetResponse get getResponse => getField(2);
  void set getResponse(AstGetResponse v) { setField(2, v); }
  bool hasGetResponse() => hasField(2);
  void clearGetResponse() => clearField(2);

  AstPutResponse get putResponse => getField(3);
  void set putResponse(AstPutResponse v) { setField(3, v); }
  bool hasPutResponse() => hasField(3);
  void clearPutResponse() => clearField(3);
}

