package com.bonree.dataview.plugin.exception;

public class PluginException extends RuntimeException {
    private static final long serialVersionUID = 6576381361684687237L;

    public PluginException() {}

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginException(Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
