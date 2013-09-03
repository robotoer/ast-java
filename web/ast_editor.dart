library ast_editor;

import 'dart:html';

import 'package:logging/logging.dart';
import 'package:web_ui/web_ui.dart';
//import 'package:dart_web_toolkit/ui.dart' as ui;

part "ast_client.dart";
part "ast_layout.dart";
part "ast_shortcuts.dart";

@observable
String currentChord = "";

@observable
String currentCommand = "";

class AstEditor {
  final Logger logger = new Logger("AstEditor");
  final Element editorElement;
  final AstShortcutTrie keybindings;
  final AstShortcutState keybindingState;

  /** Constructs a new AstEditor instance. */
  AstEditor(
      Element this.editorElement,
      AstShortcutTrie this.keybindings,
      AstShortcutState this.keybindingState) {
    editorElement.onKeyDown.listen(editorKeyDown);

    this.editorElement;

//    Current keyboard shortcut chord: {{currentChord}}<br/>
//    Current keyboard shortcut command: {{currentCommand}}
  }

  /** Constructs a new AstEditor instance with no keys pressed by default. */
  AstEditor.fresh(
      Element elem,
      AstShortcutTrie keys) : this(elem, keys, new AstShortcutState(keys));

  /** Handles keypresses directed at the editor node. */
  void editorKeyDown(KeyboardEvent event) {
    keybindingState.pressKey(event);
    currentChord = keybindingState.currentKeys;
  }
}

void main() {
  final Logger logger = new Logger("jsoneditor_main");
  logger.info("Starting 'AST Editor'");

  final Element editorElement = queryAll("body").first;
  final AstShortcutTrie keybindings = new AstShortcutTrie.binding({
    [KeyCode.I, KeyCode.N, KeyCode.J]: () => currentCommand = "insert null below",
    [KeyCode.I, KeyCode.N, KeyCode.K]: () => currentCommand = "insert null above",
    [KeyCode.I, KeyCode.N, KeyCode.H]: () => currentCommand = "insert null left",
    [KeyCode.I, KeyCode.N, KeyCode.L]: () => currentCommand = "insert null right"
  });
  final AstEditor editor = new AstEditor.fresh(editorElement, keybindings);

  final AstClient client = new AstClient.fromAddress("ws://localhost:8125");
}