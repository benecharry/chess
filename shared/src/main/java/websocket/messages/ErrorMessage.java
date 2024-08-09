package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        if (!errorMessage.toLowerCase().contains("error")) {
            this.errorMessage = "Error: " + errorMessage;
        } else {
            this.errorMessage = errorMessage;
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return errorMessage;
    }
}
