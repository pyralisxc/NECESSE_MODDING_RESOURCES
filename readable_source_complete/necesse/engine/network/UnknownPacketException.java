/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network;

public class UnknownPacketException
extends Exception {
    public UnknownPacketException() {
    }

    public UnknownPacketException(String message) {
        super(message);
    }

    public UnknownPacketException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownPacketException(Throwable cause) {
        super(cause);
    }
}

