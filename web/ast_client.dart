// File handling communication over a websocket.
part of ast_editor;

/** Handles communication with the Ast editor backend. */
class AstClient {
  final Logger logger = new Logger("AstClient");
  final WebSocket connection;

  String response;
  bool receivedResponse = false;

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
    receivedResponse = true;
  }

  void onError(Event event) {
    logger.info("[admin]:   Websocket connection error!");
  }

  String encodeProtoBuf(GeneratedMessage message) {
    return CryptoUtils.bytesToBase64(message.writeToBuffer());
  }

  ast_pb.AstResponse decodeProtoBuf(String encodedMessage) {
    return new ast_pb.AstResponse.fromBuffer(
        CryptoUtils.base64StringToBytes(encodedMessage));
  }

  // TODO: Add support for requesting specific versions of the Ast.
  AstNode get(String path) {
    // Build a get request.
    ast_pb.AstGetRequest getRequest = new ast_pb.AstGetRequest()
        ..path = path;
    ast_pb.AstRequest request = new ast_pb.AstRequest()
        ..getRequest = getRequest;

    // Send a get request.
    this.connection.send(encodeProtoBuf(request));

    // Wait for the response.
    // TODO: Do something better than this or at least add a timeout...
    while(!this.receivedResponse);
    String received = this.response;
    this.receivedResponse = false;

    // Convert the protobuf Ast to the native dart one.
    ast_pb.AstNode rootNode = decodeProtoBuf(received)
        .getResponse
        .root;

  }
}