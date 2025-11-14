/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import java.util.Arrays;
import necesse.engine.modLoader.classes.EntryClass;
import necesse.engine.modLoader.classes.ModClass;
import necesse.engine.modLoader.classes.ModPatchClasses;

public class ModClasses {
    public final EntryClass entry;
    public final ModPatchClasses patchClasses;
    private final ModClass[] classes;

    public ModClasses() {
        ModClass[] modClassArray = new ModClass[2];
        this.entry = new EntryClass();
        modClassArray[0] = this.entry;
        this.patchClasses = new ModPatchClasses();
        modClassArray[1] = this.patchClasses;
        this.classes = modClassArray;
    }

    public Iterable<ModClass> getAllClasses() {
        return Arrays.asList(this.classes);
    }
}

