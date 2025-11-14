/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader.classes;

import java.io.File;
import necesse.engine.modLoader.ModLoadLocation;
import necesse.engine.modLoader.ModProvider;

public class DevModLoadLocation
extends ModLoadLocation {
    public final File devModFolder;

    public DevModLoadLocation(ModProvider modProvider, File path, File devModFolder) {
        super(modProvider, path);
        this.devModFolder = devModFolder;
    }
}

