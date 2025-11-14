/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedDevMod;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModInfoFile;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModLoadLocation;
import necesse.engine.modLoader.ModProvider;
import necesse.engine.modLoader.classes.DevModLoadLocation;
import necesse.gfx.ui.HoverStateTextures;

public class DevModProvider
extends ModProvider {
    public static String devMod = null;
    private final GameMessage gameMessage = new LocalMessage("ui", "modfromdev");

    @Override
    public List<ModLoadLocation> locateMods(boolean isServer) throws ModLoadException {
        ArrayList<ModLoadLocation> modLoadLocations = new ArrayList<ModLoadLocation>();
        if (devMod != null) {
            File path = new File(devMod);
            System.out.println("Loading dev mod from " + path.getAbsolutePath());
            File jarFile = LoadedDevMod.validateDevFolderAndReturnJar(path);
            if (jarFile != null) {
                modLoadLocations.add(new DevModLoadLocation(this, jarFile, path));
            } else {
                GameLog.warn.println(Localization.translate("ui", "moduploadfolderinvalid"));
            }
        }
        return modLoadLocations;
    }

    @Override
    public HoverStateTextures getIcon() {
        return Settings.UI.config_icon;
    }

    @Override
    public GameMessage getGameMessage() {
        return this.gameMessage;
    }

    @Override
    protected LoadedMod createLoadedMod(JarFile jarFile, ModInfoFile modInfoFile, ModLoadLocation loadLocation) {
        return new LoadedDevMod(jarFile, modInfoFile, (DevModLoadLocation)loadLocation);
    }
}

