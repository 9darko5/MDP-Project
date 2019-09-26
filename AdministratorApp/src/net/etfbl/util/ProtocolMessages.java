package net.etfbl.util;

public enum ProtocolMessages {

    MESSAGE_SEPARATOR("#"),
    OK("AUTH OK"),
    NOT_OK("AUTH NOK"),
    SINGLE_CHAT_HISTORY("SINGLE_CHAT_HISTORY"),
    SINGLE_MESSAGE("SINGLE_MESSAGE"),
    MONITOR_RECEIVE("MONITOR_RECEIVE"),
    MONITOR_REQUEST("MONITOR_REQUEST"),
	ERROR("ERR");

    private final String value;

    private ProtocolMessages(String value) {
        this.value = value;
    }

    public String getMessage() {
        return value;
    }
}
