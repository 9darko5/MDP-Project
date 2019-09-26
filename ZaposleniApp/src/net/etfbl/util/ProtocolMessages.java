package net.etfbl.util;

public enum ProtocolMessages {

    MESSAGE_SEPARATOR("#"),
    OK("AUTH OK"),
    NOT_OK("AUTH NOK"),
    MULTI_MESSAGE("MULTI_MESSAGE"),
    MULTI_CHAT_HISTORY("MULTI_CHAT_HISTORY"),
    SINGLE_MESSAGE("SINGLE_MESSAGE"),
    SINGLE_CHAT_HISTORY("SINGLE_CHAT_HISTORY"),
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
