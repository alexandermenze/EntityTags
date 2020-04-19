package de.lx.entitytags.exceptions;

public class PacketServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PacketServiceException(String errorMessage) {
        super(errorMessage);
    }

    public PacketServiceException(String errorMessage, Throwable child) {
        super(errorMessage, child);
    }
}