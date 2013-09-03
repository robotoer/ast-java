// File handling communication over a websocket.
part of ast_editor;

/** Handles communication with the Ast editor backend. */
class AstClient {
  final Logger logger = new Logger("AstClient");
  final WebSocket connection;

  AstClient(this.connection) {
    this.connection.onOpen.listen(onOpen);
    this.connection.onClose.listen(onClose);
    this.connection.onMessage.listen(onMessage);
    this.connection.onError.listen(onError);
  }
  AstClient.fromAddress(String wsAddress) : this(new WebSocket(wsAddress));

  void onOpen(Event event) {
    logger.info("Websocket connection opened!");
  }

  void onClose(CloseEvent event) {
    logger.info("[admin]:   Websocket connection closed! Reason: " + event.reason);
  }

  void onMessage(MessageEvent event) {
    logger.info("[message]: " + event.data);
  }

  void onError(Event event) {
    logger.info("[admin]:   Websocket connection error!");
  }
}