// Manipulation and display of ASTs (Abstract Syntax Trees).
part of ast_editor;

/** Entry mapping one [key] to a [value]. */
class Entry<K, V> {
  final K key;
  final V value;

  /** Creates an entry with the specified [key] and the specified [value]. */
  Entry(this.key, this.value);

  /** Debugging representation of the entry. */
  String toString() {
    return "${key} -> ${value}";
  }
}

// -------------------------------------------------------------------------------------------------

abstract class AstNode {
  AstNode();

  /** Gets this node's parent node. If this is a root node, returns null. */
  AstNode get parent;

  /**
   * Gets the key associated with this node. A root node's key is null.
   *
   * Property: this == this.getParent().getChild(this.getKey());
   */
  dynamic get key;

  /** Gets the children of this ASTNode. */
  Iterable<Entry<dynamic, AstNode>> get children;

  /** Returns true if this node contains a list. */
  bool get isList;

  /** Returns true if this node contains a map. */
  bool get isMap;

  /** Returns true if this node contains a primitive. */
  bool get isPrimitive;

  /**
   * Gets the child node associated with the provided [key]. This key must be either a
   * String or an Integer.
   *
   * Note: Returns null if key does not exist.
   */
  AstNode getChild(dynamic key);

  /**
   * Removes the child node associated with the provided [key]. This key must be either a
   * String or an Integer. Returns the removed node.
   *
   * Note: Returns null if key does not exist.
   */
  AstNode removeChild(dynamic key);
}
