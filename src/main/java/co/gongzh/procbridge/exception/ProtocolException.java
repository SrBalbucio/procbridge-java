package co.gongzh.procbridge.exception;

public final class ProtocolException extends RuntimeException {

    public static final String UNRECOGNIZED_PROTOCOL = "unrecognized protocol";
    public static final String INCOMPATIBLE_VERSION = "incompatible protocol version";
    public static final String INCOMPLETE_DATA = "incomplete data";
    public static final String INVALID_STATUS_CODE = "invalid status code";
    public static final String INVALID_BODY = "invalid body";

    public ProtocolException(String message) {
        super(message);
    }

}
