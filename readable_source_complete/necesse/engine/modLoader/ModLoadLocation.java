/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import java.io.File;
import necesse.engine.modLoader.ModProvider;

public class ModLoadLocation {
    public final ModProvider modProvider;
    public final File path;

    public ModLoadLocation(ModProvider modProvider, File path) {
        this.modProvider = modProvider;
        this.path = path;
    }
}

