/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world;

public class FileSystemClosedException
extends Exception {
    public FileSystemClosedException() {
    }

    public FileSystemClosedException(String message) {
        super(message);
    }

    public FileSystemClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileSystemClosedException(Throwable cause) {
        super(cause);
    }

    public FileSystemClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

