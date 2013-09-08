// Implementation of an Abstract Syntax Tree for Json.
part of ast_editor;

abstract class JsonNode extends AstNode {
  /** HTML DOM node representing this AST / JSON node. */
  Element domNode = null;

  /** Parent node of this node. Null if this is a root node. */
  final JsonNode parent;

  /** Key associated with this node. */
  final dynamic key;

  /** Constructor for JSONNode. */
  JsonNode(JsonNode this.parent, dynamic this.key);

  /** Factory method for JSON nodes. */
  factory JsonNode.fromJson(dynamic json, JsonNode parent, dynamic key) {
    if (json == null) {
      return new JsonNull(parent, key);
    } else if (json is bool) {
      return new JsonBoolean(json, parent, key);
    } else if (json is num) {
      return new JsonNumber(json, parent, key);
    } else if (json is String) {
      return new JsonString(json, parent, key);
    } else if (json is List) {
      return new JsonList(json, parent, key);
    } else if (json is Map) {
      return new JsonMap(json, parent, key);
    } else {
      throw new ArgumentError("Invalid JSON object: '${json}'.");
    }
  }

  factory JsonNode.fromJsonRoot(Object json) {
    return new JsonNode.fromJson(json, null, null);
  }

  factory JsonNode.jNull(JsonNode parent, dynamic key) {
    return new JsonNull(parent, key);
  }

  Iterable<Entry<Object, JsonNode>> get children => new List();

  JsonNode getChild(dynamic key) { return null; }
  JsonNode removeChild(dynamic key) { return null; }

  // Default values for boolean fields:
  bool get isList => false;
  bool get isMap => false;
  bool get isPrimitive => false;
}

// -------------------------------------------------------------------------------------------------

class JsonNull extends JsonNode {
  JsonNull(JsonNode parent, dynamic key) : super(parent, key) {
    this.domNode = new DivElement();
    this.domNode.classes.add("json_null");
    this.domNode.style.display = "inline-block";
    this.domNode.appendText("null");
  }

  dynamic get value => null;

  bool get isPrimitive => true;
}

// -------------------------------------------------------------------------------------------------

class JsonBoolean extends JsonNode {
  bool value;

  JsonBoolean(bool this.value, JsonNode parent, Object key) : super(parent, key) {
    this.domNode = new DivElement();
    this.domNode.classes.add("json_boolean");
    this.domNode.style.display = "inline-block";
    this.domNode.appendText("${this.value}");
  }

  bool get isPrimitive => true;
}

// -------------------------------------------------------------------------------------------------

class JsonNumber extends JsonNode {
  /** JSON value, here a number (int, double, other?). */
  num value;

  JsonNumber(num this.value, JsonNode parent, Object key) : super(parent, key) {
    this.domNode = new DivElement();
    this.domNode.classes.add("json_number");
    this.domNode.style.display = "inline-block";
    this.domNode.appendText(value.toString());
  }

  bool get isPrimitive => true;
}

// -------------------------------------------------------------------------------------------------

class JsonString extends JsonNode {
  /** JSON value, here a String. */
  String value;

  JsonString(
      String this.value,
      JsonNode parent,
      Object key) : super(parent, key) {
    this.domNode = new DivElement();
    this.domNode.classes.add("json_string");
    this.domNode.style.display = "inline-block";
    // TODO: No escaping/quoting should eventually be needed if we render it correctly:
    this.domNode.appendText("\"${value}\"");
  }

  bool get isPrimitive => true;
}

// -------------------------------------------------------------------------------------------------

class JsonList extends JsonNode {
  /** JSON value, here a List. */
  List<dynamic> value;

  /** Children AST nodes. */
  final List<JsonNode> _list = new List();

  JsonList(
      List<dynamic> this.value,
      JsonNode parent,
      dynamic key) : super(parent, key) {
    this.domNode = new DivElement();
    this.domNode.classes.add("json_list");
    this.domNode.style.display = "block";
    this._FillDivNode();
  }

  void _FillDivNode() {
    this.domNode.appendText("[");

    int i = 0;
    for (Object childValue in this.value) {
      final JsonNode childNode = new JsonNode.fromJson(childValue, this, i);
      _list.add(childNode);

      final DivElement element = new DivElement();
      element.style.display = "block";
      element.classes.add("json_list_item");

      element.append(childNode.domNode);

      this.domNode.append(element);

      i++;
    }
    this.domNode.appendText("]");
  }

  JsonNode getChild(int key) {
    if (!(key is int))
      throw new ArgumentError("JSONList key must be an integer, got '${key}'.");

    return this._list[key];
  }

  JsonNode removeChild(int key) {
    if (!(key is int))
      throw new ArgumentError("JSONList key must be an integer, got '${key}'.");

    final int index = key;
    final JsonNode removed = this._list.removeAt(index);
    if (removed != null) {
      this._list.removeAt(index);
      // This is suboptimal but simple:
      this.domNode.children.clear();
      this._FillDivNode();
    }
    return removed;
  }

  Iterable<Entry<int, JsonNode>> get children {
    int i = 0;
    return _list.map((elem) {
      var entry = new Entry(i, elem);
      i++;
      return entry;
    });
  }

  bool get isList => true;
}

// -------------------------------------------------------------------------------------------------

class JsonMap extends JsonNode {
  /** JSON value. */
  Map<String, dynamic> value;

  /** Children AST node map. */
  final Map<String, JsonNode> _map = new Map();

  JsonMap(
      Map<String, dynamic> this.value,
      JsonNode parent,
      dynamic key) : super(parent, key) {
    this.domNode = new DivElement();
    this.domNode.classes.add("json_object");
    this.domNode.style.display = "block";
    this._FillDivNode();
  }

  void _FillDivNode() {
    this.domNode.appendText("{");
    bool first = true;
    void renderEntry(Object key, Object value) {
      if (!(key is String))
        throw new ArgumentError("JSONMap key must be a string, got '${key}'.");
      final JsonNode childNode = new JsonNode.fromJson(value, this, key);
      this._map[key] = childNode;

      final DivElement element = new DivElement();
      element.style.display = "block";
      element.classes.add("json_object_entry");

      element.appendText(key);
      element.appendText(":");

      final DivElement valElement = new DivElement();
      valElement.classes.add("json_object_value");
      valElement.append(childNode.domNode);
      element.append(valElement);

      this.domNode.append(element);
    }
    this.value.forEach(renderEntry);
    this.domNode.appendText("}");
  }

  JsonNode getChild(String key) {
    if (!(key is String))
      throw new ArgumentError("JSONMap key must be a string, got '${key}'.");

    return this._map[key];
  }

  JsonNode removeChild(String key) {
    if (!(key is String))
      throw new ArgumentError("JSONMap key must be a string, got '${key}'.");

    final JsonNode removed = this._map.remove(key);
    if (removed != null) {
      this.value.remove(key);
      // This is suboptimal but simple:
      this.domNode.children.clear();
      this._FillDivNode();
    }
    return removed;
  }

  // TODO: This does not produce a stable ordering. Fix this.
  Iterable<Entry<String, JsonNode>> get children {
    return this._map.keys.map((key) => new Entry(key, _map[key]));
  }

  bool get isMap => true;
}
