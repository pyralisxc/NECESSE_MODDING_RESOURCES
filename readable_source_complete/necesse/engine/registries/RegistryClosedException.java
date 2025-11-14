/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

public class RegistryClosedException
extends RuntimeException {
    public RegistryClosedException() {
    }

    public RegistryClosedException(String message) {
        super(message);
    }

    public RegistryClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistryClosedException(Throwable cause) {
        super(cause);
    }

    public RegistryClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

