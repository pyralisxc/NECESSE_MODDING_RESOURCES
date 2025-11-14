/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Objects;

public class CriticalGameException
extends RuntimeException {
    public CriticalGameException(Error cause) {
        super(cause);
        Objects.requireNonNull(cause);
    }

    @Override
    public String getMessage() {
        return this.getCause().getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return this.getCause().getLocalizedMessage();
    }

    @Override
    public String toString() {
        return this.getCause().toString();
    }

    @Override
    public void printStackTrace() {
        this.getCause().printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        this.getCause().printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        this.getCause().printStackTrace(s);
    }
}

