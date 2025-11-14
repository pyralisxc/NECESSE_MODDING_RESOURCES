/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.state;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipError;
import necesse.engine.AbstractMusicList;
import necesse.engine.GameCache;
import necesse.engine.GameCrashLog;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GlobalData;
import necesse.engine.MusicList;
import necesse.engine.Settings;
import necesse.engine.gameLoop.GameLoop;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.ServerCreationSettings;
import necesse.engine.network.server.ServerHostSettings;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.platforms.Platform;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.WorldSave;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.state.State;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.ObjectValue;
import necesse.engine.util.PointHashSet;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.World;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.Mob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.camera.PanningCamera;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.MainMenuFormManager;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.ModSaveListMismatchForm;
import necesse.gfx.forms.presets.NoticeForm;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.level.maps.Level;
import necesse.level.maps.SurfaceLevel;
import necesse.level.maps.biomes.MobChance;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.regionSystem.Region;
import necesse.reports.ReportUtils;

public class MainMenu
extends State {
    private final Object lock = new Object();
    private Client client;
    private MainMenuTickThread tickThread;
    private Level level;
    private WorldEntity worldEntity;
    public ConnectFrom connectedFrom;
    private PanningCamera camera;
    private InputEvent cameraInput;
    private boolean preloadInCameraDirection = true;
    private boolean preloadLargeArea = true;
    private final PointHashSet preloadedRegionPositions = new PointHashSet();
    private MainMenuFormManager formManager;
    private NoticeForm waitingForServerShutdown;
    private long completedFirstLevelDrawTime;

    public MainMenu(String notice) {
        this(notice, null);
    }

    public MainMenu(String notice, Client client) {
        this(notice == null ? null : new StaticMessage(notice), client);
    }

    public MainMenu(GameMessage notice) {
        this(notice, null);
    }

    public MainMenu(GameMessage notice, Client client) {
        this.client = client;
        this.connectedFrom = null;
        Settings.hideUI = false;
        Settings.hideCursor = false;
        this.init();
        if (notice != null) {
            this.addNotice(notice);
        }
    }

    public MainMenu(ContinueComponent continueForm) {
        this(continueForm, null);
    }

    public MainMenu(ContinueComponent continueForm, Client client) {
        this.client = client;
        this.connectedFrom = null;
        Settings.hideUI = false;
        Settings.hideCursor = false;
        this.init();
        if (continueForm != null) {
            this.addContinueForm(continueForm);
        }
    }

    public MainMenu(Client client) {
        this.client = client;
        this.init();
        this.changeLevel(client);
    }

    public void init() {
        this.setupFormManager();
        Platform.getStatsProvider().storeStatsAndAchievements();
        this.connectedFrom = null;
        this.worldEntity = WorldEntity.getDebugWorldEntity();
        this.worldEntity.keepPresetGeneratedRegionsLoaded = true;
        ControllerInput.enableLayer(ControllerInput.MENU_SET_LAYER);
        ControllerInput.setMoveAsMenuNavigation(true);
        FormManager.cleanUpLastControllerFocuses();
        if (this.client != null && !this.client.loading.isDone()) {
            this.formManager.startConnection(this.client);
        }
        this.isInitialized = true;
    }

    @Override
    public void frameTick(TickManager tickManager, GameWindow window) {
        ControllerEvent controllerEvent;
        if (this.level == null) {
            this.initLevelCameraAndThread();
        }
        if (this.client != null) {
            if (!this.client.hasDisconnected()) {
                if (tickManager.isGameTick()) {
                    Performance.record((PerformanceTimerManager)tickManager, "clientTick", this.client::tick);
                }
                Performance.record((PerformanceTimerManager)tickManager, "clientMove", () -> {
                    if (this.client != null) {
                        this.client.frameTick(tickManager);
                    }
                });
                if (this.isDisposed()) {
                    return;
                }
            }
            if (this.client.isDisconnected() && this.client.getLocalServer() != null) {
                if (this.client.getLocalServer().serverThread.isAlive()) {
                    if (this.waitingForServerShutdown == null) {
                        this.waitingForServerShutdown = new NoticeForm("waitserver");
                        this.waitingForServerShutdown.setupNotice(new LocalMessage("ui", "waitserver"));
                        this.waitingForServerShutdown.setButtonCooldown(-2);
                        this.formManager.addContinueForm("waitserver", this.waitingForServerShutdown);
                    }
                } else {
                    if (this.waitingForServerShutdown != null) {
                        this.waitingForServerShutdown.applyContinue();
                        this.waitingForServerShutdown = null;
                    }
                    if (this.client.hasDisconnected()) {
                        if (this.client.getLocalServer().stopErrors != null) {
                            for (ContinueComponent stopError : this.client.getLocalServer().stopErrors) {
                                this.addContinueForm(stopError);
                            }
                        }
                        this.client = null;
                    }
                }
            } else if (this.client.hasDisconnected() && this.client.getLocalServer() == null) {
                this.client = null;
            }
        }
        Input input = WindowManager.getWindow().getInput();
        if (GlobalData.isDevMode()) {
            InputEvent f8 = input.getEvent(297);
            InputEvent f9 = input.getEvent(298);
            if (f8 != null && f8.state || f9 != null && f9.state) {
                float[] modifiers = new float[]{0.5f, 1.0f, 2.0f, 4.0f, 8.0f, 16.0f, 32.0f};
                int index = 0;
                for (int i = 0; i < modifiers.length; ++i) {
                    if (TickManager.globalTimeMod != modifiers[i]) continue;
                    index = i;
                    break;
                }
                if (f8 != null) {
                    TickManager.globalTimeMod = modifiers[Math.floorMod(index - 1, modifiers.length)];
                }
                if (f9 != null) {
                    TickManager.globalTimeMod = modifiers[(index + 1) % modifiers.length];
                }
                TickManager.skipDrawIfBehind = TickManager.globalTimeMod > 1.0f;
                System.out.println("Time modifier: x" + TickManager.globalTimeMod);
            }
        }
        InputEvent leftClick = input.getEvent(-100);
        try {
            input.getEvents().forEach(event -> this.formManager.submitInputEvent((InputEvent)event, tickManager, null));
            ControllerInput.getEvents().forEach(e -> this.formManager.submitControllerEvent((ControllerEvent)e, tickManager, null));
        }
        catch (ConcurrentModificationException e2) {
            System.err.println("ConcurrentModificationException likely caused by opening of url/file");
            e2.printStackTrace();
        }
        if (leftClick != null) {
            if (leftClick.state) {
                if (!leftClick.isUsed()) {
                    leftClick.use();
                    this.cameraInput = leftClick;
                }
            } else if (this.cameraInput != null) {
                leftClick.use();
                this.cameraInput = null;
            }
        }
        if (this.cameraInput != null) {
            int deltaX = input.mousePos().windowX - this.cameraInput.pos.windowX;
            int deltaY = input.mousePos().windowY - this.cameraInput.pos.windowY;
            float speed = Math.min((float)new Point(deltaX, deltaY).distance(0.0, 0.0) / 10.0f, 50.0f);
            this.camera.setDirection(deltaX, deltaY);
            this.camera.setSpeed(speed);
        } else if (this.preloadedRegionPositions.isEmpty()) {
            this.preloadInCameraDirection = true;
        }
        InputEvent escapeEvent = input.getEvent(256);
        if (escapeEvent == null && (controllerEvent = ControllerInput.getEvent(ControllerInput.MENU_BACK)) != null) {
            escapeEvent = InputEvent.ControllerButtonEvent(controllerEvent, tickManager);
        }
        if (escapeEvent == null && (controllerEvent = ControllerInput.getEvent(ControllerInput.MAIN_MENU)) != null) {
            escapeEvent = InputEvent.ControllerButtonEvent(controllerEvent, tickManager);
        }
        if (escapeEvent != null && !escapeEvent.isUsed() && escapeEvent.state) {
            this.formManager.submitEscapeEvent(escapeEvent);
        }
        this.formManager.frameTick(tickManager);
        if (this.isDisposed()) {
            return;
        }
        if (Control.SCREENSHOT.isPressed()) {
            Renderer.takeScreenshot(this.client == null ? null : this.client.chat);
        }
        if (tickManager.isGameTick() && this.client == null) {
            SoundManager.setMusic((AbstractMusicList)new MusicList(MusicRegistry.AdventureBegins), SoundManager.MusicPriority.BIOME);
        }
    }

    private void refreshLevelLoading(Level level) {
        double secondsToLoad = this.preloadLargeArea ? 120.0 : 10.0;
        double distanceToLoad = this.camera.getSpeed() > 0.0f ? (double)this.camera.getSpeed() * secondsToLoad * 32.0 / 250.0 : 1.0;
        double distanceLoaded = 0.0;
        this.worldEntity.refreshWorldPresetCache(level.getIdentifier(), this.camera.getStartTileX(), this.camera.getStartTileY());
        this.worldEntity.refreshWorldPresetCache(level.getIdentifier(), (int)((double)this.camera.getStartTileX() + (double)this.camera.getXDir() * distanceToLoad * 2.0), (int)((double)this.camera.getStartTileY() + (double)this.camera.getYDir() * distanceToLoad * 2.0));
        double regionTileSize = 512.0;
        double cameraX = (double)this.camera.getX() - regionTileSize * 0.5;
        double cameraY = (double)this.camera.getY() - regionTileSize * 0.5;
        double cameraXMod = cameraX % regionTileSize;
        double cameraYMod = cameraY % regionTileSize;
        int cameraRegionWidth = GameMath.ceil((double)this.camera.getWidth() / 32.0 / 16.0) + 2;
        int cameraRegionHeight = GameMath.ceil((double)this.camera.getHeight() / 32.0 / 16.0) + 2;
        Iterator iterator = GameUtils.streamRegionCoordinatesBetweenPoints(level, new Point2D.Double(cameraXMod, cameraYMod), new Point2D.Double(cameraXMod + (double)this.camera.getXDir() * 100000.0, cameraYMod + (double)this.camera.getYDir() * 100000.0)).iterator();
        block0: while (true) {
            double d = this.preloadInCameraDirection ? distanceToLoad : 1.0;
            if (!(distanceLoaded <= d) || !iterator.hasNext()) break;
            Point point = (Point)iterator.next();
            distanceLoaded = point.distance(new Point(0, 0)) * 16.0;
            int x = 0;
            while (true) {
                if (x >= cameraRegionWidth) continue block0;
                for (int y = 0; y < cameraRegionHeight; ++y) {
                    int regionY;
                    int regionX = x + point.x + GameMath.getRegionCoordByTile(GameMath.getTileCoordinate(cameraX));
                    boolean isNewlyGeneratedRegion = !level.regionManager.isRegionGenerated(regionX, regionY = y + point.y + GameMath.getRegionCoordByTile(GameMath.getTileCoordinate(cameraY)));
                    Region region = level.regionManager.getRegion(regionX, regionY, true);
                    region.unloadRegionBuffer.keepLoaded();
                    if (this.preloadInCameraDirection) {
                        this.preloadedRegionPositions.add(regionX, regionY);
                    }
                    if (!isNewlyGeneratedRegion) continue;
                    if (!this.preloadInCameraDirection && !this.preloadedRegionPositions.isEmpty()) {
                        this.preloadedRegionPositions.clear();
                    }
                    int critters = GameRandom.globalRandom.getIntBetween(-4, 2);
                    for (int i = 0; i < critters; ++i) {
                        Collection<Mob> spawnedMobs;
                        MobChance randomMob;
                        Point spawnTile = new Point(region.tileXOffset + GameRandom.globalRandom.getIntBetween(0, region.tileWidth - 1), region.tileYOffset + GameRandom.globalRandom.getIntBetween(0, region.tileHeight - 1));
                        MobSpawnTable critterSpawnTable = level.getBiome(spawnTile.x, spawnTile.y).getCritterSpawnTable(level);
                        while ((randomMob = critterSpawnTable.getRandomMob(level, null, new Point(spawnTile), GameRandom.globalRandom, "mainmenu")) != null && (spawnedMobs = randomMob.spawnMob(level, null, spawnTile, null, mob -> {
                            mob.canDespawn = false;
                        }, "mainmenu")) == null) {
                            critterSpawnTable = critterSpawnTable.withoutRandomMob(randomMob);
                        }
                    }
                }
                ++x;
            }
            break;
        }
        LinkedList<Region> regionsToUnload = level.regionManager.tickUnloadRegions(30);
        for (Region region : regionsToUnload) {
            if (this.preloadedRegionPositions.contains(region.regionX, region.regionY)) continue;
            level.regionManager.unloadRegion(region.regionX, region.regionY);
        }
        this.preloadInCameraDirection = false;
        this.preloadLargeArea = false;
    }

    @Override
    public void secondTick(TickManager tickManager) {
        ReportUtils.updateSessionSeconds(null);
    }

    public float getLoadingAlpha() {
        if (this.completedFirstLevelDrawTime == 0L) {
            return 0.0f;
        }
        long timeSinceLoaded = System.currentTimeMillis() - this.completedFirstLevelDrawTime;
        if (timeSinceLoaded < 2000L) {
            return GameMath.limit((float)timeSinceLoaded / 2000.0f, 0.0f, 1.0f);
        }
        return 1.0f;
    }

    @Override
    public void drawScene(TickManager tickManager, boolean sceneUpdated) {
        Level level = this.level;
        if (level == null || !level.isLoadingComplete()) {
            return;
        }
        level.runGLContextRunnables();
        this.camera.updateToSceneDimensions();
        this.camera.tickMovement(tickManager);
        level.draw(this.camera, null, tickManager, sceneUpdated);
        if (this.completedFirstLevelDrawTime == 0L) {
            this.completedFirstLevelDrawTime = System.currentTimeMillis();
        }
    }

    @Override
    public void drawSceneOverlay(TickManager tickManager) {
        Level level = this.level;
        if (level == null || !level.isLoadingComplete()) {
            return;
        }
        level.drawHud(this.camera, null, tickManager);
    }

    @Override
    public void drawHud(TickManager tickManager) {
        float loadingAlpha = this.getLoadingAlpha();
        GameLoadingScreen.drawKeyArt(WindowManager.getWindow(), 1.0f - loadingAlpha);
        this.formManager.draw(tickManager, null);
        GameTooltipManager.addControllerGlyph(Localization.translate("controls", "navigatetip"), ControllerInput.MENU_UP, ControllerInput.MENU_RIGHT, ControllerInput.MENU_DOWN, ControllerInput.MENU_LEFT);
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "backbutton"), ControllerInput.MENU_BACK);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onWindowResized(GameWindow window) {
        if (this.formManager != null) {
            this.formManager.onWindowResized(window);
        }
        Object object = this.lock;
        synchronized (object) {
            this.preloadInCameraDirection = true;
            this.preloadLargeArea = true;
        }
    }

    public void setupFormManager() {
        this.formManager = new MainMenuFormManager(this);
        this.formManager.setup();
    }

    @Override
    public MainMenuFormManager getFormManager() {
        return this.formManager;
    }

    @Override
    public void reloadInterfaceFromSettings(boolean makeInterfaceCurrent) {
        this.formManager = new MainMenuFormManager(this);
        this.formManager.setup();
        this.formManager.mainForm.makeCurrent(this.formManager.mainForm.settings);
        if (makeInterfaceCurrent) {
            this.formManager.mainForm.settings.makeInterfaceCurrent();
            this.formManager.mainForm.settings.setSaveActive(true);
            this.formManager.mainForm.settings.reloadedInterface = true;
        }
    }

    public void toggleCameraPanSetting() {
        this.initCamera();
    }

    @Override
    public GameCamera getCamera() {
        return this.camera;
    }

    protected boolean setupLoadSaveConfirmations(WorldSave worldSave, final HashSet<String> confirms, final Runnable confirmed) {
        if (worldSave != null && confirms != null) {
            if (!confirms.contains("modmismatch") && !ModLoader.matchesCurrentMods(worldSave)) {
                ModSaveListMismatchForm mismatchForm = new ModSaveListMismatchForm(){

                    @Override
                    public void loadAnywayPressed() {
                        confirms.add("modmismatch");
                        ((MainMenu)MainMenu.this).formManager.mainForm.makeCurrent(((MainMenu)MainMenu.this).formManager.mainForm.main);
                        confirmed.run();
                    }

                    @Override
                    public void backPressed() {
                        ((MainMenu)MainMenu.this).formManager.mainForm.setConnectedFromCurrent();
                    }
                };
                this.formManager.mainForm.addComponent(mismatchForm, (form, active) -> {
                    if (!active.booleanValue()) {
                        this.formManager.mainForm.removeComponent(form);
                    }
                });
                mismatchForm.setup(worldSave.getWorldEntity().lastMods);
                mismatchForm.onWindowResized(WindowManager.getWindow());
                this.formManager.mainForm.makeCurrent(mismatchForm);
                return true;
            }
            if (worldSave.isEarlierThanOneWorld()) {
                if (!confirms.contains("convertearlyaccessworld1")) {
                    ConfirmationForm confirmationForm = new ConfirmationForm("convertearlyaccessworld", 500, 600);
                    GameMessageBuilder builder = new GameMessageBuilder().append("misc", "converteaworld1").append("\n").append("misc", "converteaworld2");
                    confirmationForm.setupConfirmation(builder, (GameMessage)new LocalMessage("misc", "converteaworldreadmore"), (GameMessage)new LocalMessage("ui", "backbutton"), () -> {
                        confirms.add("convertearlyaccessworld1");
                        this.formManager.mainForm.makeCurrent(this.formManager.mainForm.main);
                        confirmed.run();
                    }, () -> this.formManager.mainForm.setConnectedFromCurrent());
                    this.formManager.mainForm.addComponent(confirmationForm, (form, active) -> {
                        if (!active.booleanValue()) {
                            this.formManager.mainForm.removeComponent(form);
                        }
                    });
                    confirmationForm.onWindowResized(WindowManager.getWindow());
                    this.formManager.mainForm.makeCurrent(confirmationForm);
                    return true;
                }
                if (!confirms.contains("convertearlyaccessworld2")) {
                    ConfirmationForm confirmationForm = new ConfirmationForm("convertearlyaccessworld", 500, 600);
                    GameMessageBuilder builder = new GameMessageBuilder().append("misc", "converteaworld3").append("\n\n").append("misc", "converteaworld4").append("\n\n").append("misc", "converteaworld5").append("\n\n").append("misc", "converteaworld6").append("\n\n").append("misc", "converteaworld7");
                    confirmationForm.setupConfirmation(builder, (GameMessage)new LocalMessage("misc", "converteaworldaccept"), (GameMessage)new LocalMessage("ui", "backbutton"), () -> {
                        confirms.add("convertearlyaccessworld2");
                        this.formManager.mainForm.makeCurrent(this.formManager.mainForm.main);
                        confirms.add("createmigrationbackup");
                        confirmed.run();
                    }, () -> this.formManager.mainForm.setConnectedFromCurrent());
                    this.formManager.mainForm.addComponent(confirmationForm, (form, active) -> {
                        if (!active.booleanValue()) {
                            this.formManager.mainForm.removeComponent(form);
                        }
                    });
                    confirmationForm.onWindowResized(WindowManager.getWindow());
                    this.formManager.mainForm.makeCurrent(confirmationForm);
                    return true;
                }
            }
        }
        return false;
    }

    public void startSingleplayer(WorldSave worldSave, ServerCreationSettings serverCreationSettings, ConnectFrom connectedFrom) {
        this.startSingleplayer(worldSave, serverCreationSettings, connectedFrom, new HashSet<String>());
    }

    private void startSingleplayer(WorldSave worldSave, ServerCreationSettings serverCreationSettings, ConnectFrom connectedFrom, HashSet<String> confirms) {
        this.connectedFrom = connectedFrom;
        if (this.setupLoadSaveConfirmations(worldSave, confirms, () -> this.startSingleplayer(worldSave, serverCreationSettings, connectedFrom, confirms))) {
            return;
        }
        if (confirms.contains("createmigrationbackup")) {
            File targetPath = WorldSave.getPreMigrationBackupPath(worldSave.filePath);
            try {
                World.copyWorld(worldSave.filePath, targetPath, false);
            }
            catch (IOException e) {
                this.addNotice("Failed to create a backup of the world before migration: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
        try {
            this.startConnection(Platform.getNetworkManager().startSingleplayerClient(serverCreationSettings), connectedFrom);
            SaveData continueSave = MainMenu.getContinueCacheSaveBase(ContinueMode.OPEN);
            continueSave.addSafeString("worldFilePath", serverCreationSettings.worldFilePath.getAbsolutePath());
            GameCache.cacheSave(continueSave, "continueLast");
        }
        catch (IOException | ZipError ex) {
            this.addNotice(Localization.translate("misc", "loadworldfailed") + "\n\n\"" + ex.getMessage() + "\"\n\n" + Localization.translate("misc", "restorebackup"));
            ex.printStackTrace();
        }
        catch (FileSystemClosedException ex) {
            this.addNotice(Localization.translate("misc", "loadworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
            ex.printStackTrace();
        }
    }

    public void host(WorldSave worldSave, ServerSettings serverSettings, ServerHostSettings serverHostSettings, ConnectFrom connectedFrom) {
        this.host(worldSave, serverSettings, serverHostSettings, connectedFrom, new HashSet<String>());
    }

    private void host(WorldSave worldSave, ServerSettings serverSettings, ServerHostSettings serverHostSettings, ConnectFrom connectedFrom, HashSet<String> confirms) {
        this.connectedFrom = connectedFrom;
        if (this.setupLoadSaveConfirmations(worldSave, confirms, () -> this.host(worldSave, serverSettings, serverHostSettings, connectedFrom, confirms))) {
            return;
        }
        if (confirms.contains("createmigrationbackup")) {
            File targetPath = WorldSave.getPreMigrationBackupPath(worldSave.filePath);
            try {
                World.copyWorld(worldSave.filePath, targetPath, false);
            }
            catch (IOException e) {
                this.addNotice("Failed to create a backup of the world before migration: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
        try {
            this.startConnection(Platform.getNetworkManager().startHostClient(serverSettings, serverHostSettings), connectedFrom);
            SaveData continueSave = MainMenu.getContinueCacheSaveBase(ContinueMode.HOST);
            continueSave.addSafeString("worldFilePath", serverSettings.creationSettings.worldFilePath.getAbsolutePath());
            SaveData hostSave = new SaveData("host");
            serverSettings.addSaveData(hostSave);
            continueSave.addSaveData(hostSave);
            GameCache.cacheSave(continueSave, "continueLast");
        }
        catch (IOException | ZipError ex) {
            this.addNotice(Localization.translate("misc", "loadworldfailed") + "\n\n\"" + ex.getMessage() + "\"\n\n" + Localization.translate("misc", "restorebackup"));
            ex.printStackTrace();
        }
        catch (FileSystemClosedException ex) {
            this.addNotice(Localization.translate("misc", "loadworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
            ex.printStackTrace();
        }
    }

    public ObjectValue<GameMessage, Runnable> loadContinueCacheSave() {
        block10: {
            try {
                ContinueMode mode;
                LoadData continueSave = GameCache.getSave("continueLast");
                if (continueSave == null || (mode = (ContinueMode)continueSave.getEnum(ContinueMode.class, "mode", null)) == null) break block10;
                switch (mode) {
                    case OPEN: {
                        String worldFilePath = continueSave.getSafeString("worldFilePath", continueSave.getSafeString("value", null, false));
                        if (worldFilePath == null) break;
                        File filePath = new File(worldFilePath);
                        if (!World.worldExists(filePath)) {
                            return null;
                        }
                        return new ObjectValue<GameMessage, Runnable>(new LocalMessage("ui", "continuetip", "world", World.getWorldDisplayName(filePath.getName())), () -> {
                            try {
                                WorldSave worldSave = new WorldSave(filePath, true, true, true);
                                this.startSingleplayer(worldSave, new ServerCreationSettings(filePath), null);
                            }
                            catch (IOException | ZipError ex) {
                                this.addNotice(Localization.translate("misc", "loadworldfailed") + "\n\n\"" + ex.getMessage() + "\"\n\n" + Localization.translate("misc", "restorebackup"));
                                ex.printStackTrace();
                            }
                            catch (FileSystemClosedException ex) {
                                this.addNotice(Localization.translate("misc", "loadworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
                                ex.printStackTrace();
                            }
                        });
                    }
                    case HOST: {
                        String worldFilePath = continueSave.getSafeString("worldFilePath", continueSave.getSafeString("value", null, false));
                        if (worldFilePath == null) break;
                        File filePath = new File(worldFilePath);
                        if (!World.worldExists(filePath)) {
                            return null;
                        }
                        LoadData hostSave = continueSave.getFirstLoadDataByName("host");
                        if (hostSave != null) {
                            ServerSettings settings = Platform.getNetworkManager().getServerSettingsFromSave(filePath, hostSave);
                            return new ObjectValue<GameMessage, Runnable>(new LocalMessage("ui", "continuehost", "world", World.getWorldDisplayName(filePath.getName())), () -> {
                                try {
                                    WorldSave worldSave = new WorldSave(filePath, true, true, true);
                                    this.host(worldSave, settings, null, null);
                                }
                                catch (IOException | ZipError ex) {
                                    this.addNotice(Localization.translate("misc", "loadworldfailed") + "\n\n\"" + ex.getMessage() + "\"\n\n" + Localization.translate("misc", "restorebackup"));
                                    ex.printStackTrace();
                                }
                                catch (FileSystemClosedException ex) {
                                    this.addNotice(Localization.translate("misc", "loadworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
                                    ex.printStackTrace();
                                }
                            });
                        }
                        break;
                    }
                    case JOIN: {
                        return Platform.getNetworkManager().getMainMenuContinueButtonForJoining(continueSave, this);
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    public static SaveData getContinueCacheSaveBase(ContinueMode mode) {
        SaveData save = new SaveData("");
        save.addEnum("mode", mode);
        return save;
    }

    public void startConnection(Client client, ConnectFrom connectedFrom) {
        if (this.client != null) {
            client.instantDisconnect("Client error");
        }
        this.client = client;
        this.connectedFrom = connectedFrom;
        GlobalData.resetDebug();
        this.formManager.startConnection(client);
    }

    public void changeLevel(Client client) {
        client.reset();
        this.formManager.startConnection(client);
    }

    public Client getClient() {
        return this.client;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void initLevelCameraAndThread() {
        Object object = this.lock;
        synchronized (object) {
            if (this.level != null && !this.level.isDisposed()) {
                this.level.dispose();
                this.level.runGLContextRunnables();
            }
            this.level = new SurfaceLevel(LevelIdentifier.SURFACE_IDENTIFIER, 0, 0, this.worldEntity);
            this.level.lightManager.ambientLightOverride = this.level.lightManager.newLight(150.0f);
            this.initCamera();
        }
        if (this.tickThread == null) {
            this.tickThread = new MainMenuTickThread("main-menu-level", 60);
            this.tickThread.start();
        }
    }

    public void initCamera() {
        PanningCamera lastCamera = this.camera;
        this.camera = new PanningCamera();
        if (lastCamera != null) {
            this.camera.setPosition(lastCamera.getX(), lastCamera.getY());
            this.camera.setDirection(lastCamera.getXDir(), lastCamera.getYDir());
        } else {
            int dirX = GameRandom.globalRandom.getOneOf(-1, 1);
            int dirY = GameRandom.globalRandom.getOneOf(-1, 1);
            this.camera.setDirection(dirX, dirY);
            this.camera.centerCamera(16384, 16384);
        }
        if (lastCamera != null) {
            boolean bl = Settings.menuCameraPan = lastCamera.getSpeed() == 0.0f;
        }
        if (Settings.menuCameraPan) {
            this.camera.setSpeed(10.0f);
        } else {
            this.camera.setSpeed(0.0f);
        }
    }

    public void addNotice(String notice) {
        this.addNotice(notice == null ? null : new StaticMessage(notice));
    }

    public void addNotice(GameMessage notice) {
        NoticeForm noticeForm = new NoticeForm("notice");
        noticeForm.setupNotice(notice);
        this.addContinueForm(null, noticeForm);
    }

    public void addContinueForm(String key, ContinueComponent form) {
        if (form != null) {
            this.formManager.addContinueForm(key, form);
        }
    }

    public void addContinueForm(ContinueComponent form) {
        if (form != null) {
            this.formManager.addContinueForm(null, form);
        }
    }

    public void cancelConnection() {
        this.formManager.cancelConnection();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispose() {
        super.dispose();
        Level level = this.level;
        this.level = null;
        Object object = this.lock;
        synchronized (object) {
            this.formManager.dispose();
            if (level != null) {
                level.dispose();
                level.runGLContextRunnables();
            }
            if (this.worldEntity != null) {
                this.worldEntity.dispose();
            }
            this.worldEntity = null;
            if (this.tickThread != null) {
                this.tickThread.gameLoop.stopMainGameLoop();
            }
            this.tickThread = null;
        }
    }

    @Override
    public void onClose() {
        if (this.client != null) {
            this.client.saveAndClose("Closed client", PacketDisconnect.Code.SERVER_STOPPED);
        }
    }

    @Override
    public void onCrash(List<Throwable> errors) {
        GameCrashLog.printCrashLog(errors, this.client, this.client == null ? null : this.client.getLocalServer(), "MainMenu", this.client == null);
        if (this.client != null) {
            this.client.error(Localization.translate("disconnect", "clienterror"), true);
        } else {
            GlobalData.getCurrentGameLoop().stopMainGameLoop();
        }
    }

    @Override
    public SoundEmitter getALListener() {
        return SoundPlayer.SimpleEmitter(0.0f, 0.0f);
    }

    public static enum ConnectFrom {
        SingleplayerMenu,
        SinglePlayerLoadWorld,
        MultiplayerMenu,
        MultiplayerHostWorld,
        MultiplayerJoinFriend,
        MultiplayerJoinServer;

    }

    public static enum ContinueMode {
        OPEN,
        HOST,
        JOIN;

    }

    public class MainMenuTickThread
    extends Thread {
        public MainMenuGameLoop gameLoop;

        public MainMenuTickThread(String name, int maxFPS) {
            super(name);
            this.gameLoop = new MainMenuGameLoop(name, maxFPS);
        }

        @Override
        public void run() {
            this.gameLoop.runMainGameLoop();
        }
    }

    public class MainMenuGameLoop
    extends GameLoop {
        public MainMenuGameLoop(String name, int maxFPS) {
            super(name, maxFPS);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void update() {
            super.update();
            if (MainMenu.this.isDisposed()) {
                return;
            }
            Level level = MainMenu.this.level;
            PanningCamera camera = MainMenu.this.camera;
            if (level == null || camera == null) {
                return;
            }
            Object object = MainMenu.this.lock;
            synchronized (object) {
                Performance.recordConstant((PerformanceTimerManager)this, "refreshLevelLoading", () -> MainMenu.this.refreshLevelLoading(level));
                if (!level.isLoadingComplete()) {
                    level.onLoadingComplete();
                }
                MainMenu.this.worldEntity.serverFrameTick(this);
                if (this.isGameTick()) {
                    MainMenu.this.worldEntity.serverTick();
                    level.clientTick();
                    level.serverTick();
                }
                level.frameTick(this);
                if (this.isGameTick()) {
                    Performance.recordConstant((PerformanceTimerManager)this, "levelEffectTick", () -> level.tickEffect(camera, null));
                }
            }
        }
    }
}

