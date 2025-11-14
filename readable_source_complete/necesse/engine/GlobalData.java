/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.Platform
 */
package necesse.engine;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import necesse.engine.GameCache;
import necesse.engine.GameLaunch;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GameLog;
import necesse.engine.GameSystemInfo;
import necesse.engine.Settings;
import necesse.engine.achievements.AchievementManager;
import necesse.engine.commands.CommandsManager;
import necesse.engine.gameLoop.GameLoop;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.engine.registries.GNDRegistry;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.registries.IncursionBiomeRegistry;
import necesse.engine.registries.IncursionDataRegistry;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.engine.registries.JournalRegistry;
import necesse.engine.registries.LevelDataRegistry;
import necesse.engine.registries.LevelEventRegistry;
import necesse.engine.registries.LevelJobRegistry;
import necesse.engine.registries.LevelLayerRegistry;
import necesse.engine.registries.LevelRegistry;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.registries.MapIconRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.PacketRegistry;
import necesse.engine.registries.PickupRegistry;
import necesse.engine.registries.ProjectileModifierRegistry;
import necesse.engine.registries.ProjectileRegistry;
import necesse.engine.registries.QuestRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.registries.RegionLayerRegistry;
import necesse.engine.registries.SettlementStorageIndexRegistry;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.registries.UniqueIncursionModifierRegistry;
import necesse.engine.registries.UniqueIncursionRewardsRegistry;
import necesse.engine.registries.WorldDataRegistry;
import necesse.engine.registries.WorldEventRegistry;
import necesse.engine.registries.WorldPresetRegistry;
import necesse.engine.seasons.GameSeasons;
import necesse.engine.state.MainGame;
import necesse.engine.state.State;
import necesse.engine.world.WorldGenerator;
import necesse.entity.mobs.mobMovement.MobMovement;
import necesse.gfx.GameEyes;
import necesse.gfx.GameHair;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.ThemeColorRegistry;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.forms.components.lists.FormRecipeList;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.res.ResourceEncoder;
import necesse.gfx.ui.HUD;
import necesse.gfx.ui.debug.Debug;
import necesse.inventory.recipe.Recipes;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameObject.WallObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.levelData.settlementData.jobCondition.JobConditionRegistry;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationRegistry;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZoneRegistry;
import org.lwjgl.system.Platform;

public class GlobalData {
    private static boolean devMode;
    private static boolean isServer;
    private static boolean lowMemoryMode;
    private static GameLoop gameLoop;
    public static final String saveSuffix = ".dat";
    private static String rootPath;
    private static String appDataPath;
    private static AchievementManager achievements;
    private static PlayerStats stats;
    static State currentState;
    public static final List<FormRecipeList> craftingLists;

    public static void setDevMode() {
        devMode = true;
        GameLog.debug.println("Started development mode");
    }

    public static boolean isDevMode() {
        return devMode;
    }

    public static boolean isServer() {
        return isServer;
    }

    public static void setCurrentGameLoop(GameLoop gameLoop) {
        GlobalData.gameLoop = gameLoop;
    }

    public static GameLoop getCurrentGameLoop() {
        return gameLoop;
    }

    public static void setLowMemoryMode() {
        lowMemoryMode = true;
    }

    public static boolean isLowMemoryMode() {
        return lowMemoryMode;
    }

    public static void setup(String[] args) {
        URL sourceLocation;
        HashMap<String, String> opts = GameLaunch.parseLaunchOptions(args);
        String pathSeparator = System.getProperty("file.separator");
        if (pathSeparator == null || pathSeparator.isEmpty()) {
            pathSeparator = "/";
        }
        if ((sourceLocation = GlobalData.class.getProtectionDomain().getCodeSource().getLocation()) != null) {
            try {
                String path = Paths.get(sourceLocation.toURI()).toFile().getAbsolutePath();
                if (path.endsWith(".jar") && (rootPath = new File(path).getParent()) == null) {
                    rootPath = "";
                }
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        if (opts.containsKey("rootdir")) {
            rootPath = opts.get("rootdir");
        }
        if (!rootPath.isEmpty() && !rootPath.endsWith(pathSeparator)) {
            rootPath = rootPath + pathSeparator;
        }
        if (!opts.containsKey("localdir")) {
            try {
                switch (Platform.get()) {
                    case WINDOWS: {
                        appDataPath = System.getenv("APPDATA") + pathSeparator + "Necesse" + pathSeparator;
                        break;
                    }
                    case MACOSX: {
                        appDataPath = System.getProperty("user.home") + pathSeparator + "Library" + pathSeparator + "Application Support" + pathSeparator + "Necesse" + pathSeparator;
                        break;
                    }
                    case LINUX: {
                        appDataPath = System.getProperty("user.home") + pathSeparator + ".config" + pathSeparator + "Necesse" + pathSeparator;
                    }
                }
            }
            catch (Exception e) {
                appDataPath = "";
            }
        }
        if (!(!opts.containsKey("datadir") || (appDataPath = opts.get("datadir")).isEmpty() || appDataPath.endsWith("\\") && appDataPath.endsWith("/"))) {
            appDataPath = appDataPath + pathSeparator;
        }
        new File(GlobalData.appDataPath()).mkdirs();
        GameCache.checkCacheVersion();
    }

    private static String nativesPath(String osPath) {
        File file = new File("natives/" + osPath);
        if (!file.exists()) {
            throw new RuntimeException("Could not find " + osPath + " natives files.");
        }
        return file.getAbsolutePath();
    }

    public static String rootPath() {
        return rootPath;
    }

    public static String appDataPath() {
        return appDataPath;
    }

    public static String cfgPath() {
        return appDataPath + "cfg/";
    }

    public static AchievementManager achievements() {
        return achievements;
    }

    public static PlayerStats stats() {
        return stats;
    }

    public static void resetStatsAndAchievements() {
        stats = new PlayerStats(true, EmptyStats.Mode.READ_ONLY);
        AchievementManager oldManager = achievements;
        achievements = new AchievementManager(stats);
        achievements.loadTextures(oldManager);
    }

    public static void updateCraftable() {
        craftingLists.forEach(FormRecipeList::updateCraftable);
    }

    public static void updateRecipes() {
        craftingLists.forEach(FormRecipeList::updateRecipes);
    }

    public static void resetDebug() {
        Debug.reset();
        HUD.reset();
    }

    public static void loadAll(boolean isServer) throws ModLoadException {
        GlobalData.isServer = isServer;
        GameLoadingScreen.drawLoadingString("...");
        if (!isServer) {
            if (GameSystemInfo.getGraphicsCard().toLowerCase(Locale.ENGLISH).contains("intel")) {
                System.out.println("Detected Intel Graphics. Limited max vertex calls per draw call to try and prevent driver crash");
                SharedTextureDrawOptions.MAX_VERTEX_CALLS_PER_DRAW_CALL = 250;
            }
            ResourceEncoder.loadResourceFile();
            GameLoadingScreen.logo = GameTexture.fromFile("logo");
            GameLoadingScreen.keyArt = GameTexture.fromFile("keyart");
            Renderer.queryGLError("PostResourceFile");
            FontManager.loadFonts();
            Renderer.queryGLError("PostLoadFonts");
        }
        ModLoader.loadMods(isServer);
        Localization.loadModsLanguage();
        Settings.loadModSettings(false);
        if (!ModLoader.getEnabledMods().isEmpty()) {
            GameLoadingScreen.drawLoadingString(Localization.translate("loading", "preinitmods"));
            for (LoadedMod mod : ModLoader.getEnabledMods()) {
                try {
                    mod.preInit();
                }
                catch (Exception e) {
                    throw new ModLoadException(mod, "Error during ModEntry.preInit", e);
                }
            }
        }
        GameSeasons.loadSeasons();
        GameRegistry[] registries = new GameRegistry[]{GNDRegistry.instance, DamageTypeRegistry.instance, LevelRegistry.instance, LevelLayerRegistry.instance, RegionLayerRegistry.instance, ObjectLayerRegistry.instance, GlobalIngredientRegistry.instance, MusicRegistry.instance, TileRegistry.instance, ObjectRegistry.instance, LogicGateRegistry.instance, BiomeRegistry.instance, IncursionDataRegistry.instance, IncursionBiomeRegistry.instance, UniqueIncursionModifierRegistry.instance, ThemeColorRegistry.instance, BuffRegistry.instance, RecipeTechRegistry.instance, ItemRegistry.instance, EnchantmentRegistry.instance, MobRegistry.instance, MobMovement.registry, PickupRegistry.instance, ProjectileRegistry.instance, ProjectileModifierRegistry.instance, WorldEventRegistry.instance, LevelEventRegistry.instance, LevelDataRegistry.instance, WorldDataRegistry.instance, WorldPresetRegistry.instance, JobTypeRegistry.instance, LevelJobRegistry.instance, SettlerRegistry.instance, JobConditionRegistry.instance, ContainerRegistry.instance, PacketRegistry.instance, QuestRegistry.instance, JournalChallengeRegistry.instance, JournalRegistry.instance, IncursionPerksRegistry.instance, UniqueIncursionRewardsRegistry.instance, ExpeditionMissionRegistry.instance, SettlementNotificationRegistry.instance, SettlementWorkZoneRegistry.instance, SettlementStorageIndexRegistry.instance, MapIconRegistry.instance};
        for (GameRegistry gameRegistry : registries) {
            gameRegistry.registerCore();
        }
        CommandsManager.registerCoreCommands();
        if (!ModLoader.getEnabledMods().isEmpty()) {
            GameLoadingScreen.drawLoadingString(Localization.translate("loading", "initmods"));
            for (LoadedMod mod : ModLoader.getEnabledMods()) {
                try {
                    mod.init();
                }
                catch (Exception e) {
                    throw new ModLoadException(mod, "Error during ModEntry.init", e);
                }
            }
        }
        GameMessage.registry.closeRegistry();
        for (GameRegistry gameRegistry : registries) {
            gameRegistry.closeRegistry();
        }
        GameEyes.loadEyeTypes();
        GameHair.loadHairTypes();
        if (!isServer) {
            for (LoadedMod mod : ModLoader.getAllMods()) {
                try {
                    mod.loadPreviewImage();
                }
                catch (Exception e) {
                    System.err.println("Error loading mod " + mod.getModDebugString() + " preview image");
                    e.printStackTrace();
                }
            }
            for (LoadedMod mod : ModLoader.getEnabledMods()) {
                try {
                    ResourceEncoder.addModResources(mod);
                }
                catch (Exception e) {
                    throw new ModLoadException(mod, "Error during adding mod resources", e);
                }
            }
            stats = new PlayerStats(true, EmptyStats.Mode.READ_ONLY);
            stats.loadStatsFile();
            achievements = new AchievementManager(stats);
            achievements.loadAchievementsFile();
            GameLoadingScreen.drawLoadingSub("...");
            TrackedSidebarForm.loadTrackedAchievements();
            GameSeasons.loadResources();
            Renderer.queryGLError("PostGameSeasons");
            GameResources.loadShaders();
            Renderer.queryGLError("PostShaders");
            GameResources.loadCursors();
            Renderer.queryGLError("PostCursors");
            GameResources.loadTextures();
            Renderer.queryGLError("PostTextures");
            GameResources.startSoundLoading();
            Renderer.queryGLError("PostStartSound");
            Settings.loadClientSettings();
            Settings.saveClientSettings();
            if (!ModLoader.getEnabledMods().isEmpty()) {
                GameLoadingScreen.drawLoadingString(Localization.translate("loading", "initmodsres"));
                for (LoadedMod mod : ModLoader.getEnabledMods()) {
                    try {
                        mod.initResources();
                    }
                    catch (Exception e) {
                        throw new ModLoadException(mod, "Error during ModEntry.initResources", e);
                    }
                }
            }
            GameResources.finishLoadingSounds();
            Renderer.queryGLError("PostFinishSound");
            Biome.generateBiomeTextures();
            GameTile.generateTileTextures();
            GameLogicGate.generateLogicGateTextures();
            WallObject.generateWallTextures();
            GameResources.generatedParticlesTexture = GameResources.particlesTextureGenerator.generate();
            Renderer.queryGLError("PostGenerateTileTextures");
            GameTexture.finalizeLoadedTextures();
            Renderer.queryGLError("PostFinalizeLoadedTextures");
        }
        Settings.loadBanned();
        if (!ModLoader.getEnabledMods().isEmpty()) {
            GameLoadingScreen.drawLoadingString(Localization.translate("loading", "postinitmods"));
            for (LoadedMod mod : ModLoader.getEnabledMods()) {
                try {
                    mod.postInit();
                }
                catch (Exception e) {
                    throw new ModLoadException(mod, "Error during ModEntry.postInit", e);
                }
            }
        }
        Recipes.loadDefaultRecipes();
        Recipes.closeModRecipeRegistry();
        WorldGenerator.closeRegistry();
        CommandsManager.closeRegistry();
        ItemRegistry.calculateBrokerValues();
        if (GameSeasons.isAprilFools()) {
            System.out.println("It's April Fools' day!");
        }
        if (GameSeasons.isHalloween()) {
            System.out.println("Happy Halloween!");
        }
        if (GameSeasons.isChristmas()) {
            System.out.println("Merry Christmas!");
        }
        if (GameSeasons.isNewYear()) {
            System.out.println("Happy New Year!");
        }
    }

    public static void setCurrentState(State state) {
        if (currentState != null) {
            currentState.dispose();
        }
        currentState = state;
    }

    public static State getCurrentState() {
        return currentState;
    }

    public static boolean debugActive() {
        return Debug.isActive() || HUD.debugActive || MainGame.debugFormActive;
    }

    public static boolean debugCheatActive() {
        return MainGame.debugFormActive;
    }

    static {
        rootPath = "";
        appDataPath = "";
        currentState = null;
        craftingLists = Collections.synchronizedList(new LinkedList());
    }
}

