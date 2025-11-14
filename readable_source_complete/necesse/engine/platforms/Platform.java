/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.platforms;

import java.util.ArrayList;
import necesse.engine.dlc.DLCProvider;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputManager;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModProvider;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.engine.network.NetworkManager;
import necesse.engine.playerStats.StatsProvider;
import necesse.engine.save.LoadData;
import necesse.engine.server.ServerWindow;
import necesse.engine.sound.SoundManager;
import necesse.engine.window.WindowManager;

public abstract class Platform {
    protected static InputManager inputManager;
    protected static NetworkManager networkManager;
    protected static WindowManager windowManager;
    protected static SoundManager soundManager;
    protected static StatsProvider statsProvider;
    protected static ArrayList<ModProvider> modProviders;
    protected static DLCProvider dlcProvider;

    public abstract boolean initialize() throws Exception;

    public abstract void tick(TickManager var1);

    public abstract void dispose();

    public static InputManager getInputManager() {
        return inputManager;
    }

    public abstract void updateLanguage();

    public abstract long getUniqueUserID();

    public static ArrayList<ModProvider> getModProviders() {
        return modProviders;
    }

    public static NetworkManager getNetworkManager() {
        return networkManager;
    }

    public abstract String getPlatformDebugString();

    public abstract int getPlatformAppBuild();

    public abstract ModSaveInfo tryGetModSaveInfo(LoadedMod.SaveType var1, LoadData var2);

    public static StatsProvider getStatsProvider() {
        return statsProvider;
    }

    public abstract boolean isRequestingPause();

    public abstract String getUserName();

    public abstract void registerPlatformCommands();

    public static WindowManager getWindowManager() {
        return windowManager;
    }

    public static SoundManager getSoundManager() {
        return soundManager;
    }

    public static DLCProvider getDLCProvider() {
        return dlcProvider;
    }

    public abstract ServerWindow getStandaloneServerGUI();

    public abstract OperatingSystemFamily getOperatingSystemFamily();

    public boolean canOpenURLs() {
        return this.getOperatingSystemFamily() == OperatingSystemFamily.Windows;
    }

    public boolean isSteamDeck() {
        return false;
    }

    public static enum OperatingSystemFamily {
        Windows,
        Mac,
        Linux;

    }
}

