/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.function.Supplier;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerUtils;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.packet.PacketChangeWorldTime;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.quest.QuestManager;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.WorldEntitySave;
import necesse.engine.team.TeamManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.LevelManager;
import necesse.engine.world.OneWorldMigration;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldFile;
import necesse.engine.world.WorldFileSystem;
import necesse.engine.world.WorldGenerator;
import necesse.engine.world.WorldSettings;
import necesse.entity.manager.LevelSavedEntityComponent;
import necesse.gfx.HumanLook;
import necesse.level.maps.Level;
import necesse.level.maps.TemporaryDummyLevel;

public class World {
    public final String displayName;
    public final File filePath;
    public WorldFileSystem fileSystem;
    public final LevelManager levelManager;
    public final Server server;
    public final WorldSettings settings;
    public WorldEntity worldEntity;
    public OneWorldMigration oneWorldMigration;

    private World(File worldPath, boolean isSimple) throws IOException, FileSystemClosedException {
        this.filePath = worldPath;
        if (this.filePath == null || this.filePath.getName().isEmpty()) {
            throw new IllegalArgumentException("Server name cannot be null or empty");
        }
        this.displayName = World.getWorldDisplayName(this.filePath.getName());
        this.server = null;
        this.reloadFileSystem();
        this.settings = new WorldSettings(this);
        this.settings.loadSettings(false);
        this.levelManager = new LevelManager(this);
        if (this.fileSystem.worldEntityFileExists()) {
            this.worldEntity = this.loadWorldEntity(isSimple);
        }
    }

    public World(Server server) throws IOException, FileSystemClosedException {
        this.filePath = server.getSettings().creationSettings.worldFilePath;
        if (this.filePath == null || this.filePath.getName().isEmpty()) {
            throw new IllegalArgumentException("Server name cannot be null or empty");
        }
        this.displayName = World.getWorldDisplayName(this.filePath.getName());
        this.server = server;
        this.reloadFileSystem();
        this.settings = new WorldSettings(this);
        this.levelManager = new LevelManager(this);
    }

    public static World getSaveDataWorld(File worldPath, boolean isSimple) throws IOException, FileSystemClosedException {
        return new World(worldPath, isSimple);
    }

    private static String encodedPath(String path) {
        return path.replace("%", "%25").replace("^", "%5E").replace("\u00b4", "%C2%B4").replace("`", "%60").replace("#", "%23").replace("!", "%21").replace("{", "%7B").replace("}", "%7D").replace("[", "%5B").replace("]", "%5D").replace(" ", "%20").replace("\t", "%09").replace("\u3000", "%E3%80%80");
    }

    public static WorldFileSystem getFileSystem(File worldPath, boolean createIfNoneExists) throws IOException, FileSystemClosedException {
        String absolutePath = worldPath.getAbsolutePath();
        String absolutePathEncoded = World.encodedPath(absolutePath);
        return new WorldFileSystem(absolutePath, absolutePathEncoded, createIfNoneExists, false);
    }

    public static String getWorldDisplayName(String fileName) {
        return GameUtils.removeFileExtension(fileName);
    }

    public static File getExistingWorldFilePath(String name) {
        for (String path : World.loadWorldsFromPaths()) {
            File directorySaveFile = new File(path + name + "/");
            if (directorySaveFile.exists() && directorySaveFile.isDirectory()) {
                return directorySaveFile;
            }
            File zipSaveFile = new File(path + name + ".zip");
            if (!zipSaveFile.exists() || zipSaveFile.isDirectory()) continue;
            return zipSaveFile;
        }
        return null;
    }

    public static boolean isWorldADirectory(File filePath) {
        if (!filePath.exists()) {
            return false;
        }
        if (filePath.getName().endsWith(".zip")) {
            return filePath.isDirectory();
        }
        return true;
    }

    public static boolean worldExists(File filePath) {
        if (!filePath.exists()) {
            return false;
        }
        if (filePath.isFile() && filePath.getName().endsWith(".zip")) {
            return true;
        }
        return filePath.isDirectory();
    }

    public static File worldExists(String name) {
        for (String path : World.loadWorldsFromPaths()) {
            File file = new File(path + name);
            if (!World.worldExists(file)) continue;
            return file;
        }
        return null;
    }

    public static File worldExistsWithName(String name) {
        File notArchived;
        if (name.endsWith(".zip")) {
            name = name.substring(0, name.length() - 4);
        }
        if ((notArchived = World.worldExists(name)) != null) {
            return notArchived;
        }
        return World.worldExists(name + ".zip");
    }

    public static void copyWorld(File filePath, File targetFilePath, boolean renameFolderInZip) throws IOException {
        if (!filePath.exists()) {
            throw new FileNotFoundException();
        }
        try {
            GameUtils.copyFileOrFolderReplaceExisting(filePath, targetFilePath);
        }
        catch (IOException e) {
            World.deleteWorld(targetFilePath);
            throw e;
        }
        if (filePath.isFile() && filePath.getName().endsWith(".zip") && renameFolderInZip) {
            try (WorldFileSystem fs = World.getFileSystem(targetFilePath, true);){
                fs.fixArchiveFirstFolder(World.getWorldDisplayName(targetFilePath.getName()));
            }
            catch (IOException e) {
                World.deleteWorld(targetFilePath);
                throw e;
            }
            catch (FileSystemClosedException e) {
                World.deleteWorld(targetFilePath);
                throw new IOException(e);
            }
        }
    }

    public static void moveWorld(File filePath, File targetFilePath) throws IOException {
        if (!filePath.exists()) {
            throw new FileNotFoundException();
        }
        GameUtils.moveFileOrFolderReplaceExisting(filePath, targetFilePath);
        if (filePath.isFile() && filePath.getName().endsWith(".zip")) {
            try (WorldFileSystem fs = World.getFileSystem(targetFilePath, true);){
                fs.fixArchiveFirstFolder(World.getWorldDisplayName(targetFilePath.getName()));
            }
            catch (FileSystemClosedException e) {
                throw new IOException(e);
            }
        }
    }

    public static boolean deleteWorld(File filePath) {
        if (!filePath.exists()) {
            return false;
        }
        return GameUtils.deleteFileOrFolder(filePath);
    }

    public void init() {
        this.settings.loadSettings();
        if (this.fileSystem.worldEntityFileExists()) {
            this.worldEntity = this.loadWorldEntity();
        } else {
            System.out.println("Creating save with name: " + this.filePath.getName());
        }
        if (this.worldEntity == null) {
            System.out.println("Could not find world file, creating new one: " + this.filePath.getName());
            this.worldEntity = WorldEntity.getServerWorldEntity(this, this.server.getSettings().creationSettings.worldSeed);
            this.worldEntity.initServer(this.server.getSettings().creationSettings.spawnGuide);
        }
        this.saveWorldEntity();
        if (this.worldEntity.isEarlierThanOneWorld()) {
            this.oneWorldMigration = new OneWorldMigration(this);
            this.oneWorldMigration.run();
            this.oneWorldMigration = null;
        }
    }

    public void serverTick() {
        this.levelManager.serverTick();
        this.worldEntity.serverTick();
    }

    public void frameTick(TickManager tickManager) {
        this.worldEntity.serverFrameTick(tickManager);
        this.levelManager.frameTick(tickManager);
    }

    public Level getLevel(ServerClient client) {
        return this.getLevel(client.getLevelIdentifier());
    }

    public Level getLevel(LevelIdentifier identifier) {
        return this.getLevel(identifier, null);
    }

    public Level getLevel(LevelIdentifier identifier, Supplier<Level> generator) {
        Level level = this.levelManager.getLevel(identifier);
        if (level == null || level instanceof TemporaryDummyLevel && generator != null) {
            if (level == null) {
                this.levelManager.loadLevel(identifier);
            }
            if ((level = this.levelManager.getLevel(identifier)) == null || level instanceof TemporaryDummyLevel && generator != null) {
                if (generator != null && (level = generator.get()) != null) {
                    level.overwriteIdentifier(identifier);
                    level.makeServerLevel(this.server);
                    level.entityManager.refreshSetLevel();
                    this.levelManager.overwriteLevel(level);
                    this.levelManager.loadLevel(identifier);
                }
                if (level == null) {
                    level = this.getGeneratedLevel(identifier);
                    level.overwriteIdentifier(identifier);
                    level.makeServerLevel(this.server);
                    this.levelManager.overwriteLevel(level);
                }
            }
        }
        return level;
    }

    public void generateNewLevel(LevelIdentifier levelIdentifier) {
        this.levelManager.overwriteLevel(this.getGeneratedLevel(levelIdentifier));
    }

    private Level getGeneratedLevel(LevelIdentifier levelIdentifier) {
        Level level = WorldGenerator.generateNewLevel(levelIdentifier, this.server, new GameBlackboard());
        level.overwriteIdentifier(levelIdentifier);
        level.makeServerLevel(this.server);
        level.setWorldEntity(this.server.world.worldEntity);
        level.entityManager.refreshSetLevel();
        return level;
    }

    public long getUniqueID() {
        return this.worldEntity.getUniqueID();
    }

    public long getTime() {
        return this.worldEntity.getTime();
    }

    public long getLocalTime() {
        return this.worldEntity.getLocalTime();
    }

    public long getWorldTime() {
        return this.worldEntity.getWorldTime();
    }

    public void addWorldTime(long time) {
        this.worldEntity.addWorldTime(time);
        this.server.network.sendToAllClients(new PacketChangeWorldTime(this.server));
    }

    public long getTimeToNextTimeOfDay(int dayTime) {
        long change = (long)(this.worldEntity.getDayTimeMax() - this.worldEntity.getDayTimeInt() + dayTime) * 1000L;
        if (this.worldEntity.getWorldTime() + change - (long)this.worldEntity.getDayTimeMax() * 1000L > this.worldEntity.getWorldTime()) {
            change -= (long)this.worldEntity.getDayTimeMax() * 1000L;
        }
        return change;
    }

    public void setDawn() {
        this.addWorldTime(this.getTimeToNextTimeOfDay(this.worldEntity.getDayDuration() + this.worldEntity.getDayToNightDuration() + this.worldEntity.getNightDuration()));
    }

    public void setMorning() {
        long change = (long)(this.worldEntity.getDayTimeMax() - this.worldEntity.getDayTimeInt()) * 1000L;
        this.addWorldTime(change);
    }

    public void setMidday() {
        this.addWorldTime(this.getTimeToNextTimeOfDay(this.worldEntity.getDayDuration() / 2));
    }

    public void setDusk() {
        this.addWorldTime(this.getTimeToNextTimeOfDay(this.worldEntity.getDayDuration()));
    }

    public void setNight() {
        this.addWorldTime(this.getTimeToNextTimeOfDay(this.worldEntity.getDayDuration() + this.worldEntity.getDayToNightDuration()));
    }

    public void setMidnight() {
        this.addWorldTime(this.getTimeToNextTimeOfDay(this.worldEntity.getDayDuration() + this.worldEntity.getDayToNightDuration() + this.worldEntity.getNightDuration() / 2));
    }

    public WorldEntity loadWorldEntity() {
        return this.loadWorldEntity(false);
    }

    public FileTime getLastModified() {
        try {
            return this.fileSystem.getLastModified();
        }
        catch (IOException e) {
            return FileTime.fromMillis(0L);
        }
    }

    public WorldEntity loadWorldEntity(boolean simple) {
        WorldFile file = this.fileSystem.getWorldEntityFile();
        if (!file.exists()) {
            System.err.println("Could not find WorldEntity file for " + this.filePath.getName());
            return null;
        }
        WorldEntity world = WorldEntitySave.loadSave(new LoadData(file), simple, this);
        if (world == null) {
            GameLog.warn.println("World file is corrupt for " + this.filePath.getName());
            return null;
        }
        return world;
    }

    public void simulateWorldTime(long timeIncrease, boolean sendChanges) {
        for (Level level : this.levelManager.getLoadedLevels()) {
            level.simulateWorldTime(timeIncrease, sendChanges);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Level loadLevel(WorldFile file) {
        LoadData script = this.loadLevelScript(file);
        if (script == null) {
            System.out.println("Could not find Level: " + file + " file for " + this.filePath.getName());
            return null;
        }
        Level level = LevelSave.loadSave(script, this.server);
        try {
            if (level == null) {
                GameLog.warn.println("Level file " + file + " is corrupt for " + this.filePath.getName());
                String corruptFilePath = "corruptlevels/" + GameUtils.removeFileExtension(file.getFileName().toString()) + " - Corrupt" + ".dat";
                GameLog.warn.println("Backing up corrupt level file to " + corruptFilePath);
                try {
                    file.copyTo(this.fileSystem.file(corruptFilePath), new CopyOption[0]);
                }
                catch (IOException e) {
                    System.err.println("Error copying corrupt file");
                    e.printStackTrace();
                }
                Level level2 = null;
                return level2;
            }
            Performance.recordConstant(LevelSave.debugLoadingPerformance, "finalize", () -> {
                Performance.recordConstant(LevelSave.debugLoadingPerformance, "repair", level::onLoadingComplete);
                Performance.recordConstant(LevelSave.debugLoadingPerformance, "simulate", () -> level.simulateSinceLastWorldTime(false));
            });
            Level level3 = level;
            return level3;
        }
        finally {
            if (LevelSave.debugLoadingPerformance != null) {
                PerformanceTimerUtils.printPerformanceTimer(LevelSave.debugLoadingPerformance.getCurrentRootPerformanceTimer());
            }
            LevelSave.debugLoadingPerformance = null;
        }
    }

    public Level loadLevel(LevelIdentifier identifier) {
        Level level = this.loadLevel(this.fileSystem.getLevelFile(identifier));
        if (level != null) {
            level.overwriteIdentifier(identifier);
        }
        return level;
    }

    public LoadData loadLevelScript(WorldFile file) {
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        return new LoadData(file);
    }

    public LoadData loadLevelScript(LevelIdentifier identifier) {
        return this.loadLevelScript(this.fileSystem.getLevelFile(identifier));
    }

    public LoadData loadClientScript(WorldFile playerFile) {
        if (!playerFile.exists() || playerFile.isDirectory()) {
            System.out.println("Could not find Player: " + playerFile + " in directory");
            return null;
        }
        return new LoadData(playerFile);
    }

    public LoadData loadClientScript(long authentication) {
        return this.loadClientScript(this.fileSystem.getPlayerFile(authentication));
    }

    public ServerClient loadClient(WorldFile playerFile, long sessionID, NetworkInfo networkInfo, int slot, long authentication) {
        LoadData script = this.loadClientScript(playerFile);
        if (script == null) {
            GameLog.warn.println("Player file is corrupt for " + playerFile);
            return null;
        }
        return new ServerClient(this.server, sessionID, networkInfo, slot, authentication, script);
    }

    public ServerClient loadClient(long sessionID, long authentication, NetworkInfo networkInfo, int slot) {
        return this.loadClient(this.fileSystem.getPlayerFile(authentication), sessionID, networkInfo, slot, authentication);
    }

    public String loadClientName(WorldFile playerFile) {
        LoadData script = this.loadClientScript(playerFile);
        if (script == null) {
            return "N/A";
        }
        return ServerClient.loadClientName(script);
    }

    public String loadClientName(long authentication) {
        return this.loadClientName(this.fileSystem.getPlayerFile(authentication));
    }

    public HumanLook loadClientLook(WorldFile playerFile) {
        LoadData script = this.loadClientScript(playerFile);
        if (script == null) {
            return new HumanLook();
        }
        return ServerClient.loadClientLook(script);
    }

    public HumanLook loadClientLook(long authentication) {
        return this.loadClientLook(this.fileSystem.getPlayerFile(authentication));
    }

    public PlayerStats loadClientStats(WorldFile playerFile) {
        LoadData script = this.loadClientScript(playerFile);
        if (script == null) {
            return new PlayerStats(false, EmptyStats.Mode.READ_ONLY);
        }
        return ServerClient.loadClientStats(script);
    }

    public PlayerStats loadClientStats(long authentication) {
        return this.loadClientStats(this.fileSystem.getPlayerFile(authentication));
    }

    public boolean hasClient(long authentication) {
        return this.fileSystem.playerFileExists(authentication);
    }

    public void saveWorldEntity() {
        WorldFile file = this.fileSystem.getWorldEntityFile();
        WorldEntitySave.getSave(this.worldEntity).saveScript(file);
    }

    public void saveLevel(LevelIdentifier identifier) {
        Level level = this.getLevel(identifier);
        if (level != null) {
            this.saveLevel(level);
        }
    }

    public void saveLevel(Level level) {
        if (level.shouldSave()) {
            LevelSave.getSave(level).saveScript(this.fileSystem.getLevelFile(level));
            level.regionManager.saveRegions();
            level.getWorldEntity().saveGeneratedPresets(level.getIdentifier());
        } else if (!level.isDisposed()) {
            level.regionManager.saveRegions();
        }
        level.streamAll(LevelSavedEntityComponent.class).forEach(LevelSavedEntityComponent::onLevelSaved);
    }

    public void savePlayer(ServerClient client) {
        client.getSave().saveScript(this.fileSystem.getPlayerFile(client));
    }

    public LoadData loadPlayerMap(ServerClient client) {
        WorldFile file = this.fileSystem.getMapPlayerFile(client);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        return new LoadData(file);
    }

    public boolean levelExists(LevelIdentifier identifier) {
        return this.levelManager.isLoaded(identifier) || this.fileSystem.levelFileExists(identifier);
    }

    public QuestManager getQuests() {
        return this.worldEntity.quests;
    }

    public TeamManager getTeams() {
        return this.worldEntity.teams;
    }

    public HashMap<Long, String> getUsedPlayerNames() {
        HashMap<Long, String> names = new HashMap<Long, String>();
        for (WorldFile file : this.fileSystem.getPlayerFiles()) {
            try {
                String authString = GameUtils.removeFileExtension(file.getFileName().toString());
                long auth = Long.parseLong(authString);
                String name = this.loadClientName(file);
                if (name == null || name.length() <= 0) continue;
                names.put(auth, name);
            }
            catch (Exception e) {
                GameLog.warn.println("Found invalid player file name: " + file);
            }
        }
        return names;
    }

    public void dispose() {
        this.levelManager.dispose();
        this.worldEntity.dispose();
        try {
            this.closeFileSystem();
        }
        catch (IOException e) {
            System.err.println("Error closing world file system");
            e.printStackTrace();
        }
    }

    public void reloadFileSystem() throws IOException, FileSystemClosedException {
        if (this.fileSystem != null && this.fileSystem.isOpen()) {
            this.fileSystem.close();
        }
        this.fileSystem = World.getFileSystem(this.filePath, true);
    }

    public void closeFileSystem() throws IOException {
        if (this.fileSystem != null && this.fileSystem.isOpen()) {
            this.fileSystem.close();
        }
    }

    public static String getSavesPath() {
        return GlobalData.appDataPath() + "saves/";
    }

    public static String getWorldsPath() {
        return World.getSavesPath() + "worlds/";
    }

    public static String[] loadWorldsFromPaths() {
        return new String[]{World.getWorldsPath(), World.getSavesPath()};
    }
}

