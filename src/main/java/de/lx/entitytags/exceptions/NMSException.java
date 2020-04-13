package de.lx.entitytags.exceptions;

public class NMSException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NMSException(String errorMessage) {
        super(errorMessage);
    }

    public NMSException(String errorMessage, Throwable child) {
        super(errorMessage, child);
    }
}