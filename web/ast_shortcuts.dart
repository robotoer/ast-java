// File handling keyboard shortcuts.
part of ast_editor;

typedef Callback();

String keyCodeToString(int keyCode) {
  switch (keyCode) {
    case KeyCode.A: return 'a';
    case KeyCode.B: return 'b';
    case KeyCode.C: return 'c';
    case KeyCode.D: return 'd';
    case KeyCode.E: return 'e';
    case KeyCode.F: return 'f';
    case KeyCode.G: return 'g';
    case KeyCode.H: return 'h';
    case KeyCode.I: return 'i';
    case KeyCode.J: return 'j';
    case KeyCode.K: return 'k';
    case KeyCode.L: return 'l';
    case KeyCode.M: return 'm';
    case KeyCode.N: return 'n';
    case KeyCode.O: return 'o';
    case KeyCode.P: return 'p';
    case KeyCode.Q: return 'q';
    case KeyCode.R: return 'r';
    case KeyCode.S: return 's';
    case KeyCode.T: return 't';
    case KeyCode.U: return 'u';
    case KeyCode.V: return 'v';
    case KeyCode.W: return 'w';
    case KeyCode.X: return 'x';
    case KeyCode.Y: return 'y';
    case KeyCode.Z: return 'z';
    case KeyCode.ESC: return 'esc';
    case KeyCode.ONE: return '1';
    case KeyCode.TWO: return '2';
    case KeyCode.THREE: return '3';
    case KeyCode.FOUR: return '4';
    case KeyCode.FIVE: return '5';
    case KeyCode.SIX: return '6';
    case KeyCode.SEVEN: return '7';
    case KeyCode.EIGHT: return '8';
    case KeyCode.NINE: return '9';
    case KeyCode.ZERO: return '0';
    default: return "<" + keyCode.toString() + ">";
  }
}

class AstShortcutTrie {
  final Map<int, AstShortcutTrie> children;
  final Callback callback;

  /** Constructs a new AstShortcutManager node. */
  const AstShortcutTrie(
      Map<int, AstShortcutTrie> this.children,
      Callback this.callback);

  /** Creates a new AstShortcutManager tree from a keybinding [Map]. */
  factory AstShortcutTrie.binding(Map<List<int>, Callback> bindingMap) {
    Map<int, AstShortcutTrie> shortcutMap = new Map();
    bindingMap
        .forEach((List<int> sequence, Callback action) =>
            AstShortcutTrie.addKeySequence(shortcutMap, sequence, action)
        );

    return new AstShortcutTrie(shortcutMap, null);
  }

  /** Adds a keybinding to the provided binding tree. */
  static void addKeySequence(
      Map<int, AstShortcutTrie> tree,
      List<int> sequence,
      Callback action) {
    // An empty key sequence is invalid.
    assert(!sequence.isEmpty);

    // Add all necessary parent nodes.
    Map<int, AstShortcutTrie> current = tree;
    for (int key in sequence.getRange(0, sequence.length - 1)) {
      // Add a new node if this key doesn't exist already in the trie.
      if (!current.containsKey(key)) {
        current[key] = new AstShortcutTrie(new Map(), null);
      }

      // Advance and add all remaining portions of the key sequence.
      current = current[key].children;
    }

    // Add leaf node.
    current[sequence.last] = new AstShortcutTrie(new Map(), action);
  }

  /** Returns true if this node defines an action or not. */
  bool isLeaf() => callback != null;

  String toString() {
    if (children.isEmpty) {
      // Base case, print the current leaf node.
      return "(leaf)";
    } else {
      // Print node and recursively print its children.
      String repr = "{ ";
      for (int key in children.keys) {
        repr += key.toString() + ": " + children[key].toString();
      }
      repr += " }";
      return repr;
    }
  }
}

class AstShortcutState {
  final Logger logger = new Logger("AstShortcutCursor");
  final AstShortcutTrie root;

  AstShortcutTrie current;
  String currentKeys = "";

  /** Constructs a new AstShortcutCursor. */
  AstShortcutState(this.root) {
    this.current = root;
  }

  /** Advances the cursor by one key. */
  void pressKey(KeyboardEvent event) {
    if (current.children.containsKey(event.keyCode)) {
      // See if the pressed key is an option.
      current = current.children[event.keyCode];
      currentKeys += " " + keyCodeToString(event.keyCode);

      // Run the callback associated with this key sequence if we're at a leaf node.
      if (current.isLeaf()) {
        try {
          current.callback();
        } on ArgumentError catch(err) { logger.warning(err.toString()); }
        this.reset();
      }
    } else {
      this.reset();
    }
  }

  /** Resets the current state of this cursor. */
  void reset() {
    this.currentKeys = "";
    this.current = this.root;
  }
}