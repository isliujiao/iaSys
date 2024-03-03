package com.ruoyi.web;

/**
 */
public class AccessRuntimeException extends RuntimeException {
    private String errorCode;

    private String errorMsg;

    private Throwable throwable;
    public AccessRuntimeException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorMsg = message;
    }

    public AccessRuntimeException(String errorCode, String message, Throwable throwable) {
        super(message, throwable);
        this.errorCode = errorCode;
        this.errorMsg = message;
        this.throwable = throwable;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public Throwable getThrowable() {
        return throwable;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("【errorCode】:").append(this.errorCode)
                .append("【errorMsg】:").append(this.errorMsg)
                .append("【inner Caluse】:")
                .append(null == throwable ? "Inner Business Caluse" : throwable.toString());
        return sb.toString();
    }
}
