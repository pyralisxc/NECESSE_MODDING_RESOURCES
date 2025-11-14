/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface InputStreamSupplier {
    public InputStream get() throws IOException;
}

