/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import necesse.engine.GameLaunch;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.ItemCategoryExpandedSetting;
import necesse.engine.SceneColorSetting;
import necesse.engine.input.Control;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.modLoader.ModRuntimeException;
import necesse.engine.modLoader.ModSettings;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.EventVariable;
import necesse.engine.util.GameUtils;
import necesse.engine.window.DisplayMode;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.Renderer;
import necesse.gfx.forms.components.FormTravelContainerGrid;
import necesse.gfx.res.ResourceEncoder;
import necesse.gfx.ui.GameInterfaceStyle;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.recipe.RecipeFilter;

public class Settings {
    private static boolean loadedClientSettings;
    private static boolean firstTimeSavedSettings;
    public static float sceneSize;
    public static boolean adjustZoomOnHighResolution;
    public static boolean limitCameraToLevelBounds;
    public static boolean pauseOnFocusLoss;
    public static boolean savePerformanceOnFocusLoss;
    public static boolean alwaysSkipTutorial;
    public static boolean showSettlerHeadArmor;
    public static boolean useTileObjectHitboxes;
    public static boolean loadLevelBeforeSpawn;
    public static boolean instantLevelChange;
    public static boolean smartMining;
    public static EventVariable<Boolean> craftingUseNearby;
    public static boolean minimapHidden;
    public static int minimapZoomLevel;
    public static int islandMapZoomLevel;
    public static boolean menuCameraPan;
    public static boolean show1p0Notice;
    public static boolean showSteamDeckCursorNotice;
    public static int patchNotesOpenYear;
    public static int patchNotesOpenMonth;
    public static int patchNotesOpenDay;
    public static float cursorJoystickSensitivity;
    public static float interfaceSize;
    public static GameInterfaceStyle UI;
    public static boolean adjustInterfaceOnHighResolution;
    public static boolean sharpenInterface;
    public static boolean pixelFont;
    public static boolean showDebugInfo;
    public static boolean showQuestMarkers;
    public static boolean showTeammateMarkers;
    public static boolean showPickupText;
    public static boolean showDamageText;
    public static boolean showDoTText;
    public static boolean showMobHealthBars;
    public static boolean showBossHealthBars;
    public static boolean showControlTips;
    public static boolean showBasicTooltipBackground;
    public static boolean showItemTooltipBackground;
    public static boolean showLogicGateTooltips;
    public static boolean showIngredientsAvailable;
    public static boolean alwaysShowQuickbar;
    public static int tooltipTextSize;
    public static boolean drawCursorManually;
    public static Color cursorColor;
    public static int cursorSize;
    public static EventVariable<Boolean> trackNewQuests;
    public static boolean displayJournalNotifications;
    public static EventVariable<Boolean> hasCraftingFilterExpanded;
    public static EventVariable<Boolean> hasCraftingListExpanded;
    public static EventVariable<Boolean> craftingListShowHidden;
    public static EventVariable<Boolean> craftingListOnlyCraftable;
    public static EventVariable<Boolean> highlightNewRecipesToWorkstation;
    public static EventVariable<Boolean> hideSettlementStorage;
    public static EventVariable<Boolean> hideSettlementWorkstations;
    public static EventVariable<Boolean> hideSettlementForestryZones;
    public static EventVariable<Boolean> hideSettlementHusbandryZones;
    public static EventVariable<Boolean> hideSettlementFertilizeZones;
    public static FormTravelContainerGrid.CoordinateSetting mapCoordinates;
    public static String outputDevice;
    public static float masterVolume;
    public static float effectsVolume;
    public static float weatherVolume;
    public static float UIVolume;
    public static float musicVolume;
    public static boolean muteOnFocusLoss;
    public static DisplayMode displayMode;
    public static int monitor;
    public static Dimension displaySize;
    public static boolean vSyncEnabled;
    public static int maxFPS;
    public static boolean reduceUIFramerate;
    public static SceneColorSetting sceneColors;
    public static float brightness;
    public static boolean smoothLighting;
    public static LightSetting lights;
    public static ParticleSetting particles;
    public static boolean wavyGrass;
    public static boolean denseGrass;
    public static boolean windEffects;
    public static boolean cameraShake;
    public static boolean alwaysLight;
    public static boolean alwaysRain;
    public static final int DEFAULT_SERVER_PORT = 14159;
    public static int serverPort;
    public static int serverSlots;
    public static String serverPassword;
    public static int maxClientLatencySeconds;
    public static boolean pauseWhenEmpty;
    public static boolean strictServerAuthority;
    public static boolean serverLogging;
    public static String serverMOTD;
    public static String serverWorld;
    public static String serverOwnerName;
    public static long serverOwnerAuth;
    public static int unloadLevelsCooldown;
    public static int worldBorderSize;
    public static int droppedItemsLifeMinutes;
    public static boolean unloadSettlements;
    public static int maxSettlementsPerPlayer;
    public static int maxSettlersPerSettlement;
    public static boolean zipSaves;
    public static String language;
    public static ArrayList<String> banned;
    public static boolean serverPerspective;
    public static boolean hideUI;
    public static boolean hideCursor;
    public static boolean hideChat;
    protected static HashMap<Object, RecipeFilter> recipeFilters;
    protected static HashMap<Object, ItemCategoryExpandedSetting> itemCategoriesExpanded;
    public static final int[] lanPorts;
    public static String bindIP;

    public static String clientDir() {
        return GlobalData.cfgPath() + "settings.cfg";
    }

    public static String clientModDir(LoadedMod mod) {
        return GlobalData.cfgPath() + "/mods/" + mod.id + ".cfg";
    }

    public static String serverDir() {
        return GlobalData.cfgPath() + "server.cfg";
    }

    public static RecipeFilter getRecipeFilterSetting(Object keyObject) {
        return recipeFilters.compute(keyObject, (key, last) -> {
            if (last == null) {
                return new RecipeFilter();
            }
            return last;
        });
    }

    public static ItemCategoryExpandedSetting getItemCategoryExpandedSetting(Object keyObject) {
        return itemCategoriesExpanded.compute(keyObject, (key, last) -> {
            if (last == null) {
                return new ItemCategoryExpandedSetting(false);
            }
            return last;
        });
    }

    public static ItemCategoryExpandedSetting getItemCategoryExpandedSetting(Object keyObject, ItemCategory masterCategory, boolean defaultExpanded) {
        return itemCategoriesExpanded.compute(keyObject, (key, last) -> {
            if (last == null) {
                return new ItemCategoryExpandedSetting(masterCategory, defaultExpanded);
            }
            return last;
        });
    }

    private static SaveData getClientSaveData() {
        SaveData settings = new SaveData("SETTINGS");
        SaveData general = new SaveData("GENERAL");
        general.addFloat("sceneSize", sceneSize, "[" + GameWindow.minSceneSize + " - " + GameWindow.maxSceneSize + "] Higher = more zoomed in");
        general.addBoolean("adjustZoomOnHighResolution", adjustZoomOnHighResolution);
        general.addBoolean("limitCameraToLevelBounds", limitCameraToLevelBounds);
        general.addBoolean("pauseOnFocusLoss", pauseOnFocusLoss);
        general.addBoolean("savePerformanceOnFocusLoss", savePerformanceOnFocusLoss);
        general.addBoolean("alwaysSkipTutorial", alwaysSkipTutorial);
        general.addBoolean("showSettlerHeadArmor", showSettlerHeadArmor);
        general.addBoolean("useTileObjectHitboxes", useTileObjectHitboxes);
        general.addBoolean("loadLevelBeforeSpawn", loadLevelBeforeSpawn);
        general.addBoolean("smartMining", smartMining);
        general.addBoolean("craftingUseNearby", craftingUseNearby.get());
        general.addBoolean("minimapHidden", minimapHidden);
        general.addBoolean("menuCameraPan", menuCameraPan);
        general.addInt("minimapZoomLevel", minimapZoomLevel);
        general.addInt("islandMapZoomLevel", islandMapZoomLevel);
        general.addBoolean("show1p0Notice", show1p0Notice);
        general.addBoolean("showSteamDeckCursorNotice", showSteamDeckCursorNotice);
        general.addInt("patchNotesOpenYear", patchNotesOpenYear);
        general.addInt("patchNotesOpenMonth", patchNotesOpenMonth);
        general.addInt("patchNotesOpenDay", patchNotesOpenDay);
        general.addInt("unloadLevelsCooldown", unloadLevelsCooldown);
        general.addInt("worldBorderSize", worldBorderSize);
        general.addInt("droppedItemsLifeMinutes", droppedItemsLifeMinutes);
        general.addBoolean("unloadSettlements", unloadSettlements);
        general.addInt("maxSettlementsPerPlayer", maxSettlementsPerPlayer);
        general.addInt("maxSettlersPerSettlement", maxSettlersPerSettlement);
        general.addBoolean("zipSaves", zipSaves);
        general.addSafeString("language", language);
        settings.addSaveData(general);
        if (!Control.isControlsLoaded()) {
            Control.loadControls();
        }
        SaveData controls = new SaveData("CONTROLS");
        controls.addFloat("cursorJoystickSensitivity", cursorJoystickSensitivity);
        for (Control c : Control.getControls()) {
            if (c == null || c.mod != null) continue;
            controls.addInt(c.id, c.getKey());
        }
        SaveData controllerInputSaveData = new SaveData("CONTROLLER");
        ControllerInput.saveControllerBinds(controllerInputSaveData, null);
        if (!controllerInputSaveData.isEmpty()) {
            controls.addSaveData(controllerInputSaveData);
        }
        settings.addSaveData(controls);
        SaveData interf = new SaveData("INTERFACE");
        interf.addFloat("interfaceSize", interfaceSize, "[" + GameWindow.interfaceSizes[0] + " - " + GameWindow.interfaceSizes[GameWindow.interfaceSizes.length - 1] + "] Higher = bigger");
        interf.addSafeString("interfaceStyle", Settings.UI.texturesPath);
        interf.addBoolean("adjustInterfaceOnHighResolution", adjustInterfaceOnHighResolution);
        interf.addBoolean("sharpenInterface", sharpenInterface);
        interf.addBoolean("pixelFont", pixelFont);
        interf.addBoolean("showDebugInfo", showDebugInfo);
        interf.addBoolean("showQuestMarkers", showQuestMarkers);
        interf.addBoolean("showTeammateMarkers", showTeammateMarkers);
        interf.addBoolean("showPickupText", showPickupText);
        interf.addBoolean("showDamageText", showDamageText);
        interf.addBoolean("showDoTText", showDoTText);
        interf.addBoolean("showMobHealthBars", showMobHealthBars);
        interf.addBoolean("showBossHealthBars", showBossHealthBars);
        interf.addBoolean("showBasicTooltipBackground", showBasicTooltipBackground);
        interf.addBoolean("showItemTooltipBackground", showItemTooltipBackground);
        interf.addBoolean("showControlTips", showControlTips);
        interf.addBoolean("showLogicGateTooltips", showLogicGateTooltips);
        interf.addBoolean("showIngredientsAvailable", showIngredientsAvailable);
        interf.addBoolean("alwaysShowQuickbar", alwaysShowQuickbar);
        interf.addInt("tooltipTextSize", tooltipTextSize);
        interf.addBoolean("drawCursorManually", drawCursorManually);
        interf.addColor("cursorColor", cursorColor);
        interf.addInt("cursorSize", cursorSize);
        interf.addBoolean("trackNewQuests", trackNewQuests.get());
        interf.addBoolean("displayJournalNotifications", displayJournalNotifications);
        interf.addBoolean("craftingFilterExpanded", hasCraftingFilterExpanded.get());
        interf.addBoolean("craftingListExpanded", hasCraftingListExpanded.get());
        interf.addBoolean("craftingListShowHidden", craftingListShowHidden.get());
        interf.addBoolean("craftingListOnlyCraftable", craftingListOnlyCraftable.get());
        interf.addBoolean("highlightNewRecipesToWorkstation", highlightNewRecipesToWorkstation.get());
        interf.addBoolean("hideSettlementStorage", hideSettlementStorage.get());
        interf.addBoolean("hideSettlementWorkstations", hideSettlementWorkstations.get());
        interf.addBoolean("hideSettlementForestryZones", hideSettlementForestryZones.get());
        interf.addBoolean("hideSettlementHusbandryZones", hideSettlementHusbandryZones.get());
        interf.addBoolean("hideSettlementFertilizeZones", hideSettlementFertilizeZones.get());
        interf.addEnum("mapCoordinates", mapCoordinates);
        settings.addSaveData(interf);
        SaveData sound = new SaveData("SOUND");
        if (outputDevice != null) {
            sound.addSafeString("outputDevice", outputDevice);
        }
        sound.addInt("masterVolume", (int)(masterVolume * 100.0f), "[0 - 100]");
        sound.addInt("effectsVolume", (int)(effectsVolume * 100.0f), "[0 - 100]");
        sound.addInt("weatherVolume", (int)(weatherVolume * 100.0f), "[0 - 100]");
        sound.addInt("UIVolume", (int)(UIVolume * 100.0f), "[0 - 100]");
        sound.addInt("musicVolume", (int)(musicVolume * 100.0f), "[0 - 100]");
        sound.addBoolean("muteOnFocusLoss", muteOnFocusLoss);
        settings.addSaveData(sound);
        SaveData graphics = new SaveData("GRAPHICS");
        graphics.addEnum("displayMode", displayMode, Arrays.stream(DisplayMode.values()).map(Enum::toString).collect(Collectors.joining(", ")));
        if (displaySize != null) {
            graphics.addDimension("displaySize", displaySize);
        }
        graphics.addInt("monitor", monitor);
        graphics.addBoolean("vSyncEnabled", vSyncEnabled);
        graphics.addInt("maxFPS", maxFPS, "0 = uncapped");
        graphics.addBoolean("reduceUIFramerate", reduceUIFramerate);
        graphics.addEnum("sceneColors", sceneColors, Arrays.stream(SceneColorSetting.values()).map(Enum::toString).collect(Collectors.joining(", ")));
        graphics.addFloat("brightness", brightness);
        graphics.addBoolean("smoothLighting", smoothLighting);
        graphics.addEnum("lights", lights, Arrays.stream(LightSetting.values()).map(Enum::toString).collect(Collectors.joining(", ")));
        graphics.addEnum("particles", particles, Arrays.stream(ParticleSetting.values()).map(Enum::toString).collect(Collectors.joining(", ")));
        graphics.addBoolean("wavyGrass", wavyGrass);
        graphics.addBoolean("denseGrass", denseGrass);
        graphics.addBoolean("windeffects", windEffects);
        graphics.addBoolean("cameraShake", cameraShake);
        settings.addSaveData(graphics);
        return settings;
    }

    private static SaveData getServerSaveData() {
        SaveData server = new SaveData("SERVER");
        server.addInt("port", serverPort, "[0 - 65535] Server default port");
        server.addInt("slots", serverSlots, "[1 - 250] Server default slots");
        server.addSafeString("password", serverPassword, "Leave blank for no password");
        server.addInt("maxClientLatencySeconds", maxClientLatencySeconds);
        server.addBoolean("pauseWhenEmpty", pauseWhenEmpty);
        server.addBoolean("strictServerAuthority", strictServerAuthority, "If true, server will be much more strict about what clients can do. It is strongly recommended to ONLY have this enabled if absolutely necessary");
        server.addBoolean("logging", serverLogging, "If true, will create log files for each server start");
        server.addSafeString("language", language);
        server.addInt("unloadLevelsCooldown", unloadLevelsCooldown, "The number of seconds a level will stay loaded after the last player has left it");
        server.addInt("droppedItemsLifeMinutes", droppedItemsLifeMinutes, "Minutes that dropped items will stay in the world. 0 or less for indefinite");
        server.addBoolean("unloadSettlements", unloadSettlements, "If the server should unload player settlements or keep them loaded");
        server.addInt("maxSettlementsPerPlayer", maxSettlementsPerPlayer, "The maximum amount of settlements per player. -1 or less means infinite");
        server.addInt("maxSettlersPerSettlement", maxSettlersPerSettlement, "The maximum amount of settlers per settlement. -1 or less means infinite");
        server.addBoolean("zipSaves", zipSaves, "If true, will create new saves uncompressed");
        server.addSafeString("MOTD", serverMOTD, "Message of the day");
        return server;
    }

    private static SaveData getModSaveData(LoadedMod mod, ModSettings modSettings) {
        SaveData save = new SaveData("");
        if (modSettings != null) {
            SaveData settingsData = new SaveData("SETTINGS");
            modSettings.addSaveData(settingsData);
            save.addSaveData(settingsData);
        }
        SaveData controls = new SaveData("CONTROLS");
        for (Control c : Control.getControls()) {
            if (c == null || c.mod != mod) continue;
            controls.addInt(c.id, c.getKey());
        }
        SaveData controllerInputSaveData = new SaveData("CONTROLLER");
        ControllerInput.saveControllerBinds(controllerInputSaveData, mod);
        if (!controllerInputSaveData.isEmpty()) {
            controls.addSaveData(controllerInputSaveData);
        }
        if (!controls.isEmpty()) {
            save.addSaveData(controls);
        }
        return save;
    }

    private static void loadClientData(LoadData data) {
        LoadData controls;
        LoadData general = data.getFirstLoadDataByName("GENERAL");
        if (general != null) {
            sceneSize = general.getFloat("sceneSize", sceneSize, GameWindow.minSceneSize, GameWindow.maxSceneSize);
            adjustZoomOnHighResolution = general.hasLoadDataByName("dynamicSceneSize") ? general.getBoolean("dynamicSceneSize", adjustZoomOnHighResolution) : general.getBoolean("adjustZoomOnHighResolution", adjustZoomOnHighResolution);
            limitCameraToLevelBounds = general.getBoolean("limitCameraToLevelBounds", limitCameraToLevelBounds, false);
            pauseOnFocusLoss = general.getBoolean("pauseOnFocusLoss", pauseOnFocusLoss);
            savePerformanceOnFocusLoss = general.getBoolean("savePerformanceOnFocusLoss", savePerformanceOnFocusLoss);
            alwaysSkipTutorial = general.getBoolean("alwaysSkipTutorial", alwaysSkipTutorial);
            showSettlerHeadArmor = general.getBoolean("showSettlerHeadArmor", showSettlerHeadArmor);
            useTileObjectHitboxes = general.getBoolean("useTileObjectHitboxes", useTileObjectHitboxes, false);
            loadLevelBeforeSpawn = general.getBoolean("loadLevelBeforeSpawn", loadLevelBeforeSpawn, false);
            smartMining = general.getBoolean("smartMining", smartMining);
            craftingUseNearby.set(general.getBoolean("craftingUseNearby", craftingUseNearby.get()));
            minimapHidden = general.getBoolean("minimapHidden", minimapHidden);
            menuCameraPan = general.getBoolean("menuCameraPan", menuCameraPan);
            minimapZoomLevel = general.getInt("minimapZoomLevel", minimapZoomLevel);
            islandMapZoomLevel = general.getInt("islandMapZoomLevel", islandMapZoomLevel);
            show1p0Notice = general.getBoolean("show1p0Notice", show1p0Notice, false);
            showSteamDeckCursorNotice = general.getBoolean("showSteamDeckCursorNotice", showSteamDeckCursorNotice, false);
            patchNotesOpenYear = general.getInt("patchNotesOpenYear", patchNotesOpenYear, false);
            patchNotesOpenMonth = general.getInt("patchNotesOpenMonth", patchNotesOpenMonth, false);
            patchNotesOpenDay = general.getInt("patchNotesOpenDay", patchNotesOpenDay, false);
            unloadLevelsCooldown = general.getInt("unloadLevelsCooldown", unloadLevelsCooldown, 2, Integer.MAX_VALUE, false);
            worldBorderSize = general.getInt("worldBorderSize", worldBorderSize, -1, Integer.MAX_VALUE, false);
            droppedItemsLifeMinutes = general.getInt("droppedItemsLifeMinutes", droppedItemsLifeMinutes, false);
            unloadSettlements = general.getBoolean("unloadSettlements", unloadSettlements, false);
            maxSettlementsPerPlayer = general.getInt("maxSettlementsPerPlayer", maxSettlementsPerPlayer, false);
            maxSettlersPerSettlement = general.getInt("maxSettlersPerSettlement", maxSettlersPerSettlement, false);
            zipSaves = general.getBoolean("zipSaves", zipSaves);
            language = general.getSafeString("language", language);
            Language l = Localization.getLanguageStringID(language);
            if (l == null) {
                l = Localization.defaultLang;
            }
            l.setCurrent();
        } else {
            System.err.println("Could not load general settings.");
        }
        if (!Control.isControlsLoaded()) {
            Control.loadControls();
        }
        if ((controls = data.getFirstLoadDataByName("CONTROLS")) != null && Control.getControls() != null) {
            cursorJoystickSensitivity = controls.getFloat("cursorJoystickSensitivity", cursorJoystickSensitivity, false);
            for (Control c : Control.getControls()) {
                if (c == null || c.mod != null) continue;
                c.changeKey(controls.getInt(c.id, c.getKey()));
            }
            LoadData controllerInputLoadData = controls.getFirstLoadDataByName("CONTROLLER");
            if (controllerInputLoadData != null && !controllerInputLoadData.isEmpty()) {
                ControllerInput.loadControllerBinds(controllerInputLoadData, null);
            }
        } else if (controls == null) {
            System.err.println("Could not load controls settings.");
        }
        LoadData interf = data.getFirstLoadDataByName("INTERFACE");
        if (interf != null) {
            interfaceSize = interf.getFloat("interfaceSize", interfaceSize, 1.0f, 2.0f);
            String styleString = interf.getSafeString("interfaceStyle", null, false);
            if (styleString != null) {
                UI = GameInterfaceStyle.getStyle(styleString);
            }
            adjustInterfaceOnHighResolution = interf.hasLoadDataByName("dynamicInterfaceSize") ? interf.getBoolean("dynamicInterfaceSize", adjustInterfaceOnHighResolution) : interf.getBoolean("adjustInterfaceOnHighResolution", adjustInterfaceOnHighResolution);
            sharpenInterface = interf.getBoolean("sharpenInterface", sharpenInterface);
            pixelFont = interf.getBoolean("pixelFont", pixelFont);
            showDebugInfo = interf.getBoolean("showDebugInfo", showDebugInfo);
            showQuestMarkers = interf.getBoolean("showQuestMarkers", showQuestMarkers);
            showTeammateMarkers = interf.getBoolean("showTeammateMarkers", showTeammateMarkers);
            showPickupText = interf.getBoolean("showPickupText", showPickupText);
            showDamageText = interf.getBoolean("showDamageText", showDamageText);
            showDoTText = interf.getBoolean("showDoTText", showDoTText);
            showMobHealthBars = interf.getBoolean("showMobHealthBars", showMobHealthBars);
            showBossHealthBars = interf.getBoolean("showBossHealthBars", showBossHealthBars);
            showBasicTooltipBackground = interf.getBoolean("showBasicTooltipBackground", showBasicTooltipBackground);
            showItemTooltipBackground = interf.getBoolean("showItemTooltipBackground", showItemTooltipBackground);
            showControlTips = interf.getBoolean("showControlTips", showControlTips);
            showLogicGateTooltips = interf.getBoolean("showLogicGateTooltips", showLogicGateTooltips);
            showIngredientsAvailable = interf.getBoolean("showIngredientsAvailable", showIngredientsAvailable);
            alwaysShowQuickbar = interf.getBoolean("alwaysShowQuickbar", alwaysShowQuickbar);
            drawCursorManually = interf.getBoolean("drawCursorManually", drawCursorManually);
            tooltipTextSize = interf.getInt("tooltipTextSize", tooltipTextSize, 12, 32);
            cursorColor = interf.getColor("cursorColor", cursorColor);
            cursorSize = interf.getInt("cursorSize", cursorSize, -Renderer.cursorSizeOffset, Renderer.cursorSizes.length - Renderer.cursorSizeOffset - 1);
            trackNewQuests.set(interf.getBoolean("trackNewQuests", trackNewQuests.get()));
            displayJournalNotifications = interf.getBoolean("displayJournalNotifications", displayJournalNotifications, false);
            hasCraftingFilterExpanded.set(interf.getBoolean("craftingFilterExpanded", hasCraftingFilterExpanded.get()));
            hasCraftingListExpanded.set(interf.getBoolean("craftingListExpanded", hasCraftingListExpanded.get()));
            craftingListShowHidden.set(interf.getBoolean("craftingListShowHidden", craftingListShowHidden.get()));
            craftingListOnlyCraftable.set(interf.getBoolean("craftingListOnlyCraftable", craftingListOnlyCraftable.get()));
            highlightNewRecipesToWorkstation.set(interf.getBoolean("highlightNewRecipesToWorkstation", highlightNewRecipesToWorkstation.get()));
            hideSettlementStorage.set(interf.getBoolean("hideSettlementStorage", hideSettlementStorage.get(), false));
            hideSettlementWorkstations.set(interf.getBoolean("hideSettlementWorkstations", hideSettlementWorkstations.get(), false));
            hideSettlementForestryZones.set(interf.getBoolean("hideSettlementForestryZones", hideSettlementForestryZones.get(), false));
            hideSettlementHusbandryZones.set(interf.getBoolean("hideSettlementHusbandryZones", hideSettlementHusbandryZones.get(), false));
            hideSettlementFertilizeZones.set(interf.getBoolean("hideSettlementFertilizeZones", hideSettlementFertilizeZones.get(), false));
            mapCoordinates = interf.getEnum(FormTravelContainerGrid.CoordinateSetting.class, "mapCoordinates", mapCoordinates, false);
        } else {
            System.err.println("Could not load interface settings.");
        }
        LoadData sound = data.getFirstLoadDataByName("SOUND");
        if (sound != null) {
            outputDevice = sound.getSafeString("outputDevice", null, false);
            masterVolume = (float)sound.getInt("masterVolume", (int)(masterVolume * 100.0f), 0, 100) / 100.0f;
            effectsVolume = (float)sound.getInt("effectsVolume", (int)(effectsVolume * 100.0f), 0, 100) / 100.0f;
            weatherVolume = (float)sound.getInt("weatherVolume", (int)(weatherVolume * 100.0f), 0, 100) / 100.0f;
            UIVolume = (float)sound.getInt("UIVolume", (int)(UIVolume * 100.0f), 0, 100) / 100.0f;
            musicVolume = (float)sound.getInt("musicVolume", (int)(musicVolume * 100.0f), 0, 100) / 100.0f;
            muteOnFocusLoss = sound.getBoolean("muteOnFocusLoss", muteOnFocusLoss);
        } else {
            System.err.println("Could not load sound settings.");
        }
        LoadData graphics = data.getFirstLoadDataByName("GRAPHICS");
        if (graphics != null) {
            displayMode = graphics.getEnum(DisplayMode.class, "displayMode", displayMode);
            displaySize = graphics.getDimension("displaySize", displaySize, false);
            monitor = graphics.getInt("monitor", monitor, 0, Integer.MAX_VALUE);
            vSyncEnabled = graphics.getBoolean("vSyncEnabled", vSyncEnabled);
            maxFPS = graphics.getInt("maxFPS", maxFPS);
            reduceUIFramerate = graphics.getBoolean("reduceUIFramerate", reduceUIFramerate);
            sceneColors = graphics.getEnum(SceneColorSetting.class, "sceneColors", sceneColors);
            brightness = graphics.getFloat("brightness", brightness, false);
            smoothLighting = graphics.getBoolean("smoothLighting", smoothLighting);
            lights = graphics.getEnum(LightSetting.class, "lights", lights);
            particles = graphics.getEnum(ParticleSetting.class, "particles", particles);
            wavyGrass = graphics.getBoolean("wavyGrass", wavyGrass);
            denseGrass = graphics.getBoolean("denseGrass", denseGrass);
            windEffects = graphics.getBoolean("windeffects", windEffects);
            cameraShake = graphics.getBoolean("cameraShake", cameraShake);
        } else {
            System.err.println("Could not load graphics settings.");
        }
    }

    private static void loadServerData(LoadData data) {
        serverPort = data.getInt("port", serverPort, 0, 65535);
        serverSlots = data.getInt("slots", serverSlots, 1, 250);
        serverPassword = data.getSafeString("password", serverPassword);
        maxClientLatencySeconds = data.getInt("maxClientLatencySeconds", maxClientLatencySeconds);
        pauseWhenEmpty = data.getBoolean("pauseWhenEmpty", pauseWhenEmpty);
        strictServerAuthority = data.getBoolean("strictServerAuthority", strictServerAuthority, false);
        serverLogging = data.getBoolean("logging", serverLogging);
        unloadLevelsCooldown = data.getInt("unloadLevelsCooldown", unloadLevelsCooldown, 2, Integer.MAX_VALUE, false);
        worldBorderSize = data.getInt("worldBorderSize", worldBorderSize, -1, Integer.MAX_VALUE, false);
        droppedItemsLifeMinutes = data.getInt("droppedItemsLifeMinutes", droppedItemsLifeMinutes, false);
        unloadSettlements = data.getBoolean("unloadSettlements", unloadSettlements, false);
        maxSettlementsPerPlayer = data.getInt("maxSettlementsPerPlayer", maxSettlementsPerPlayer, false);
        maxSettlersPerSettlement = data.getInt("maxSettlersPerSettlement", maxSettlersPerSettlement, false);
        zipSaves = data.getBoolean("zipSaves", zipSaves);
        Language l = Localization.getLanguageStringID(language = data.getSafeString("language", language));
        if (l == null) {
            l = Localization.defaultLang;
        }
        l.setCurrent();
        serverMOTD = data.getSafeString("MOTD", serverMOTD, false);
        serverWorld = data.getSafeString("world", serverWorld, false);
    }

    private static void loadModSettings(LoadedMod mod, ModSettings modSettings, LoadData save) {
        LoadData controlsData;
        LoadData settingsData;
        if (modSettings != null && (settingsData = save.getFirstLoadDataByName("SETTINGS")) != null && settingsData.isArray()) {
            modSettings.applyLoadData(settingsData);
        }
        if ((controlsData = save.getFirstLoadDataByName("CONTROLS")) != null) {
            for (Control c : Control.getControls()) {
                if (c == null || c.mod != mod) continue;
                c.changeKey(controlsData.getInt(c.id, c.getKey()));
            }
            LoadData controllerInputLoadData = controlsData.getFirstLoadDataByName("CONTROLLER");
            if (controllerInputLoadData != null && !controllerInputLoadData.isEmpty()) {
                ControllerInput.loadControllerBinds(controllerInputLoadData, mod);
            }
        }
    }

    public static void loadClientSettings() {
        GameWindow window;
        File file = new File(Settings.clientDir());
        if (!file.exists()) {
            System.out.println("Could not load settings file, does not exist. Creating new default " + file.getName());
            firstTimeSavedSettings = true;
            Settings.saveClientSettings();
        }
        Color oldCursorColor = cursorColor;
        try {
            Settings.loadClientData(new LoadData(file));
        }
        catch (Exception e) {
            System.err.println("Error loading client settings, some settings may be reset");
            e.printStackTrace();
            Settings.saveClientSettings();
        }
        SoundManager.updateVolume();
        if (ResourceEncoder.isLoaded() && !cursorColor.equals(oldCursorColor)) {
            Renderer.setCursorColor(cursorColor);
        }
        if ((window = WindowManager.getWindow()) != null && window.isCreated()) {
            window.setVSync(vSyncEnabled);
            window.updateSceneSize();
            window.updateHudSize();
        }
        Settings.loadModSettings(true);
        loadedClientSettings = true;
        if (GameLaunch.launchOptions.containsKey("debugstartup")) {
            System.out.println("STARTUP: Loaded client settings");
        }
    }

    public static boolean hasLoadedClientSettings() {
        return loadedClientSettings;
    }

    public static boolean isFirstTimeSavedSettings() {
        return firstTimeSavedSettings;
    }

    public static boolean loadServerSettings(File file, boolean saveIfNotExists) {
        boolean load = file.exists();
        if (!load && saveIfNotExists) {
            System.out.println("Could not load server settings file, does not exist. Creating new default " + file.getName());
            Settings.saveServerSettings();
            load = true;
        }
        if (load) {
            Settings.loadServerData(new LoadData(file));
        }
        return load;
    }

    public static void loadServerSettings() {
        File file = new File(Settings.serverDir());
        Settings.loadServerSettings(file, true);
        Settings.loadModSettings(true);
    }

    public static void loadModSettings(boolean saveIfNotExists) {
        if (ModLoader.hasLoadedMods()) {
            for (LoadedMod mod : ModLoader.getEnabledMods()) {
                ModSettings settings = mod.getSettings();
                try {
                    File modFile = new File(Settings.clientModDir(mod));
                    boolean load = modFile.exists();
                    if (!load && saveIfNotExists) {
                        load = Settings.saveModSettings(mod);
                    }
                    if (!load) continue;
                    Settings.loadModSettings(mod, settings, new LoadData(modFile));
                }
                catch (Exception e) {
                    throw new ModRuntimeException(mod, "Error loading mod settings", e);
                }
            }
        }
    }

    public static void saveClientSettings() {
        File file = new File(Settings.clientDir());
        Settings.getClientSaveData().saveScript(file);
        Settings.saveModSettings();
    }

    public static void saveServerSettings() {
        File file = new File(Settings.serverDir());
        Settings.getServerSaveData().saveScript(file);
        Settings.saveModSettings();
    }

    private static void saveModSettings() {
        if (ModLoader.hasLoadedMods()) {
            for (LoadedMod mod : ModLoader.getEnabledMods()) {
                Settings.saveModSettings(mod);
            }
        }
    }

    private static boolean saveModSettings(LoadedMod mod) {
        ModSettings settings = mod.getSettings();
        try {
            File modFile = new File(Settings.clientModDir(mod));
            SaveData data = Settings.getModSaveData(mod, settings);
            if (!data.isEmpty()) {
                data.saveScript(modFile);
                return true;
            }
            return false;
        }
        catch (Exception e) {
            throw new ModRuntimeException(mod, "Error saving mod settings", e);
        }
    }

    public static void loadBanned() {
        banned = new ArrayList();
        File file = new File(GlobalData.cfgPath() + "banned.cfg");
        try {
            String admin;
            if (!file.exists()) {
                if (GameUtils.mkDirs(file)) {
                    FileOutputStream make = new FileOutputStream(file);
                    ((OutputStream)make).write(new byte[0]);
                    ((OutputStream)make).close();
                } else {
                    GameLog.warn.println("Could not create folders for file: " + file.getAbsolutePath());
                }
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((admin = reader.readLine()) != null) {
                banned.add(admin.trim().toLowerCase());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isBanned(String auth) {
        return banned.contains(auth.toLowerCase());
    }

    public static boolean isBanned(long auth) {
        return Settings.isBanned("" + auth);
    }

    public static void addBanned(String auth) {
        if (banned.contains(auth.trim().toLowerCase())) {
            return;
        }
        banned.add(auth.trim().toLowerCase());
        File file = new File(GlobalData.cfgPath() + "banned.cfg");
        try {
            FileOutputStream make = new FileOutputStream(file);
            for (String s : banned) {
                ((OutputStream)make).write((s + "\n").getBytes());
            }
            ((OutputStream)make).close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean removeBanned(String auth) {
        if (banned.contains(auth.trim().toLowerCase())) {
            banned.remove(auth.trim().toLowerCase());
            File file = new File(GlobalData.cfgPath() + "banned.cfg");
            try {
                FileOutputStream make = new FileOutputStream(file);
                for (String s : banned) {
                    ((OutputStream)make).write((s + "\n").getBytes());
                }
                ((OutputStream)make).close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    static {
        sceneSize = 1.25f;
        adjustZoomOnHighResolution = true;
        limitCameraToLevelBounds = true;
        pauseOnFocusLoss = true;
        savePerformanceOnFocusLoss = true;
        alwaysSkipTutorial = false;
        showSettlerHeadArmor = true;
        useTileObjectHitboxes = false;
        loadLevelBeforeSpawn = false;
        instantLevelChange = true;
        smartMining = false;
        craftingUseNearby = new EventVariable<Boolean>(true);
        minimapHidden = false;
        minimapZoomLevel = 3;
        islandMapZoomLevel = 3;
        menuCameraPan = true;
        show1p0Notice = true;
        showSteamDeckCursorNotice = true;
        patchNotesOpenYear = 2000;
        patchNotesOpenMonth = 1;
        patchNotesOpenDay = 1;
        cursorJoystickSensitivity = 1.0f;
        interfaceSize = 1.0f;
        UI = GameInterfaceStyle.styles.get(0);
        adjustInterfaceOnHighResolution = true;
        sharpenInterface = true;
        pixelFont = false;
        showDebugInfo = false;
        showQuestMarkers = true;
        showTeammateMarkers = true;
        showPickupText = true;
        showDamageText = true;
        showDoTText = true;
        showMobHealthBars = true;
        showBossHealthBars = true;
        showControlTips = true;
        showBasicTooltipBackground = false;
        showItemTooltipBackground = true;
        showLogicGateTooltips = false;
        showIngredientsAvailable = true;
        alwaysShowQuickbar = false;
        tooltipTextSize = 16;
        drawCursorManually = true;
        cursorColor = new Color(255, 255, 255);
        cursorSize = 0;
        trackNewQuests = new EventVariable<Boolean>(true);
        displayJournalNotifications = true;
        hasCraftingFilterExpanded = new EventVariable<Boolean>(false);
        hasCraftingListExpanded = new EventVariable<Boolean>(true);
        craftingListShowHidden = new EventVariable<Boolean>(false);
        craftingListOnlyCraftable = new EventVariable<Boolean>(false);
        highlightNewRecipesToWorkstation = new EventVariable<Boolean>(true);
        hideSettlementStorage = new EventVariable<Boolean>(false);
        hideSettlementWorkstations = new EventVariable<Boolean>(false);
        hideSettlementForestryZones = new EventVariable<Boolean>(false);
        hideSettlementHusbandryZones = new EventVariable<Boolean>(false);
        hideSettlementFertilizeZones = new EventVariable<Boolean>(false);
        mapCoordinates = FormTravelContainerGrid.CoordinateSetting.RELATIVE_SELF;
        outputDevice = null;
        masterVolume = 0.5f;
        effectsVolume = 1.0f;
        weatherVolume = 0.2f;
        UIVolume = 1.0f;
        musicVolume = 0.5f;
        muteOnFocusLoss = false;
        displayMode = DisplayMode.Borderless;
        monitor = 0;
        displaySize = null;
        vSyncEnabled = true;
        maxFPS = 0;
        reduceUIFramerate = true;
        sceneColors = SceneColorSetting.Vibrant;
        brightness = 1.0f;
        smoothLighting = true;
        lights = LightSetting.Color;
        particles = ParticleSetting.Maximum;
        wavyGrass = true;
        denseGrass = true;
        windEffects = true;
        cameraShake = true;
        alwaysLight = false;
        alwaysRain = false;
        serverPort = 14159;
        serverSlots = 10;
        serverPassword = "";
        maxClientLatencySeconds = 30;
        pauseWhenEmpty = true;
        strictServerAuthority = false;
        serverLogging = true;
        serverMOTD = "";
        serverWorld = "";
        serverOwnerName = null;
        serverOwnerAuth = -1L;
        unloadLevelsCooldown = 30;
        worldBorderSize = -1;
        droppedItemsLifeMinutes = 0;
        unloadSettlements = false;
        maxSettlementsPerPlayer = -1;
        maxSettlersPerSettlement = -1;
        zipSaves = true;
        language = Localization.English.stringID;
        serverPerspective = false;
        hideUI = false;
        hideCursor = false;
        hideChat = false;
        recipeFilters = new HashMap();
        itemCategoriesExpanded = new HashMap();
        lanPorts = new int[]{55169, 51868, 49745, 54198, 60631, 58737, 61410};
        bindIP = null;
    }

    public static enum LightSetting {
        Color(new LocalMessage("settingsui", "lightscolor")),
        White(new LocalMessage("settingsui", "lightswhite"));

        public final GameMessage displayName;

        private LightSetting(GameMessage displayName) {
            this.displayName = displayName;
        }
    }

    public static enum ParticleSetting {
        Minimal(new LocalMessage("settingsui", "particlesmin")),
        Decreased(new LocalMessage("settingsui", "particlesdec")),
        Maximum(new LocalMessage("settingsui", "particlesmax"));

        public final GameMessage displayName;

        private ParticleSetting(GameMessage displayName) {
            this.displayName = displayName;
        }
    }
}

