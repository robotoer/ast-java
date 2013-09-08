// File describing the layout of the Ast editor.
part of ast_editor;

/**
 * Base class that identifies the length of one of the dimensions of an Ast editor split.
 */
abstract class AstSplitLength {
  int getLength(int maxLength);
}

/**
 * Class that identifies the length of one of the dimensions of an Ast editor split by storing a
 * weight ranging from 0.0 to 1.0. This weight represents a percentage of the available space to
 * fill with this split. All relative length splits that share the same space must have weights
 * that add up to 1.0.
 */
class AstSplitLengthRelative
    extends AstSplitLength {
  double weight;

  AstSplitLengthRelative(double this.weight);

  int getLength(int maxLength) {
    return (maxLength * weight).toInt();
  }
}

/**
 * Class that identifies the length of one of the dimensions of an Ast editor split by storing a
 * length in pixels that this split should be. This length should be less than the total available
 * space.
 */
class AstSplitLengthAbsolute
    extends AstSplitLength {
  int length;

  AstSplitLengthAbsolute(int this.length);

  int getLength(int maxLength) {
    return length;
  }
}

/**
 * Abstract class representing a split.
 */
abstract class AstSplit {
  AstSplitLength splitSize;

  AstSplit(this.splitSize);
}

/**
 * Split node representing the contents of a split.
 */
class AstSplitLeaf
    extends AstSplit {
  // Everything necessary to define a panel.

  AstSplitLeaf(
      AstSplitLength splitSize)
        : super(splitSize);
}

/**
 * Split node representing a split containing sub-splits.
 */
class AstSplitNode
    extends AstSplit {
  static const bool VERTICAL = true;
  static const bool HORIZONTAL = false;

  bool direction;
  List<AstSplit> children;

  /**
   * Default constructor for the split panel node.
   *
   * [children] nodes.
   * [direction] of the split.
   * [splitSize] of this split.
   */
  AstSplitNode({
      bool this.direction,
      List<AstSplitNode> this.children,
      AstSplitLength splitSize})
        : super(splitSize);
  AstSplitNode.vertical({
      List<AstSplitNode> children,
      AstSplitLength splitSize})
        : this(direction: VERTICAL, children: children, splitSize: splitSize);
  AstSplitNode.horizontal({
      List<AstSplitNode> children,
      AstSplitLength splitSize})
        : this(direction: HORIZONTAL, children: children, splitSize: splitSize);
}

/**
 * Class representing the contents of an Ast editor split.
 */
abstract class AstPanel { }

class TreePanel
    extends AstPanel {
  // Root Ast node.

  // Current view coordinates: bounding box
}

class StatusPanel
    extends AstPanel {
  // Status information.
}

class AstLayout {
  // Tree of splits where each depth level alternates between horizontal/vertical splits.
}