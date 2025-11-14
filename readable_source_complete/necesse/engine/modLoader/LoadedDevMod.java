/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import java.io.File;
import java.util.jar.JarFile;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModInfoFile;
import necesse.engine.modLoader.classes.DevModLoadLocation;

public class LoadedDevMod
extends LoadedMod {
    public final File devModFolder;

    public LoadedDevMod(JarFile jarFile, ModInfoFile file, DevModLoadLocation loadLocation) {
        super(jarFile, file, loadLocation);
        this.devModFolder = loadLocation.devModFolder;
    }

    public boolean validateDevFolder() {
        return LoadedDevMod.validateDevFolderAndReturnJar(this.devModFolder) != null;
    }

    public static File validateDevFolderAndReturnJar(File devModFolder) {
        if (devModFolder.isDirectory()) {
            File[] files = devModFolder.listFiles();
            if (files == null) {
                return null;
            }
            File jarFile = null;
            for (File file : files) {
                if (file.getName().equals("preview.png")) continue;
                if (file.isDirectory()) {
                    return null;
                }
                if (!file.isFile()) continue;
                if (jarFile != null) {
                    return null;
                }
                if (!file.getName().endsWith(".jar")) continue;
                jarFile = file;
            }
            return jarFile;
        }
        return null;
    }

    @Override
    public LoadedMod.SaveType getSaveType() {
        return LoadedMod.SaveType.DEV_MOD;
    }
}

