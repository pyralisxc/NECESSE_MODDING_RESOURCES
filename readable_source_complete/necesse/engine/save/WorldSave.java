/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.zip.ZipError;
import necesse.engine.Settings;
import necesse.engine.network.server.ServerCreationSettings;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameUtils;
import necesse.engine.util.ObjectValue;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.World;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldFileSystem;
import necesse.engine.world.WorldSettings;

public class WorldSave
implements Comparable<WorldSave> {
    public static String LATEST_BACKUP_NAME = "LATEST_BACKUP";
    public static String CREATIVE_BACKUP_SUFFIX = " backup";
    public static String PRE_MIGRATION_BACKUP_SUFFIX = " pre-migration backup";
    public static Pattern BACKUP_PATTERN = Pattern.compile(LATEST_BACKUP_NAME + "(\\d+)?");
    public static int MAX_LATEST_BACKUPS = 5;
    public static ArrayList<Pattern> ignoredFiles = new ArrayList();
    public final String displayName;
    public final File filePath;
    public final String archiveFolderName;
    private String dateModified;
    private long timeModified;
    private final World world;
    public ServerCreationSettings creationSettings;

    public static boolean isLatestBackup(String saveFileName) {
        String fileNameWithoutExtension = GameUtils.removeFileExtension(saveFileName);
        return BACKUP_PATTERN.matcher(fileNameWithoutExtension).matches();
    }

    public static File getNextBackupPath(boolean isDirectory) {
        String best = LATEST_BACKUP_NAME + (isDirectory ? "" : ".zip");
        long bestTimeModified = Long.MAX_VALUE;
        if (MAX_LATEST_BACKUPS <= 1) {
            return new File(World.getWorldsPath() + best);
        }
        for (int i = 0; i < MAX_LATEST_BACKUPS; ++i) {
            File worldFile;
            String targetName = LATEST_BACKUP_NAME + (i + 1) + (isDirectory ? "" : ".zip");
            File file = new File(World.getWorldsPath() + targetName);
            if (!file.exists()) {
                return new File(World.getWorldsPath() + targetName);
            }
            long timeModified = file.lastModified();
            if (file.isDirectory() && (worldFile = new File(World.getWorldsPath() + targetName + "/world" + ".dat")).exists()) {
                timeModified = worldFile.lastModified();
            }
            if (timeModified >= bestTimeModified) continue;
            best = targetName;
            bestTimeModified = timeModified;
        }
        return new File(World.getWorldsPath() + best);
    }

    public static File getCreativeBackupPath(File worldFilePath) {
        boolean isDirectory = World.isWorldADirectory(worldFilePath);
        String fileName = GameUtils.removeFileExtension(worldFilePath.getName());
        String newPath = fileName + CREATIVE_BACKUP_SUFFIX + (isDirectory ? "" : ".zip");
        return new File(World.getWorldsPath() + newPath);
    }

    public static File getPreMigrationBackupPath(File worldFilePath) {
        boolean isDirectory = World.isWorldADirectory(worldFilePath);
        String fileName = GameUtils.removeFileExtension(worldFilePath.getName());
        String newPath = WorldSave.addNumberSuffixIfExists(fileName + PRE_MIGRATION_BACKUP_SUFFIX) + (isDirectory ? "" : ".zip");
        return new File(World.getWorldsPath() + newPath);
    }

    public static String addNumberSuffixIfExists(String name) {
        String saveName;
        int prefix = 1;
        while (World.worldExistsWithName(saveName = name + (prefix == 1 ? "" : "" + prefix)) != null) {
            ++prefix;
        }
        return saveName;
    }

    public WorldSave(ServerCreationSettings creationSettings) throws IOException, FileSystemClosedException {
        this.filePath = creationSettings.worldFilePath;
        this.displayName = World.getWorldDisplayName(creationSettings.worldFilePath.getName());
        this.world = World.getSaveDataWorld(creationSettings.worldFilePath, false);
        this.archiveFolderName = this.world.fileSystem.archiveFolderName;
        this.creationSettings = creationSettings;
        this.world.closeFileSystem();
    }

    public WorldSave(File filePath, boolean loadWorld, boolean isSimple, boolean closeFileSystem) throws IOException, ZipError, FileSystemClosedException {
        this.filePath = filePath;
        this.displayName = World.getWorldDisplayName(filePath.getName());
        this.dateModified = "N/A";
        if (loadWorld) {
            this.world = World.getSaveDataWorld(filePath, isSimple);
            this.archiveFolderName = this.world.fileSystem.archiveFolderName;
            if (this.world.worldEntity != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                this.timeModified = this.world.getLastModified().toMillis();
                this.dateModified = sdf.format(this.timeModified);
            }
            if (closeFileSystem) {
                this.closeWorldFileSystem();
            }
        } else {
            this.world = null;
            String archiveFolderName = filePath.getName();
            try (WorldFileSystem fs = World.getFileSystem(filePath, false);){
                archiveFolderName = fs.archiveFolderName;
                if (fs.worldEntityFileExists()) {
                    this.timeModified = fs.getLastModified().toMillis();
                }
            }
            catch (IOException e) {
                this.timeModified = 0L;
            }
            this.archiveFolderName = archiveFolderName;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.dateModified = sdf.format(this.timeModified);
        }
    }

    public WorldSettings worldSettings() {
        return this.world.settings;
    }

    public boolean isValid() {
        return this.world.worldEntity != null;
    }

    public String getDate() {
        return this.dateModified;
    }

    public int getWorldDay() {
        if (this.world.worldEntity != null) {
            return this.world.worldEntity.getDay();
        }
        return 0;
    }

    public int getWorldTime() {
        if (this.world.worldEntity != null) {
            return this.world.worldEntity.getDayTimeInt();
        }
        return 0;
    }

    public String getWorldTimeReadable() {
        if (this.world.worldEntity != null) {
            return this.world.worldEntity.getDayTimeReadable();
        }
        return "00:00";
    }

    public World getWorld() {
        return this.world;
    }

    public WorldEntity getWorldEntity() {
        return this.world.worldEntity;
    }

    public String getGameVersion() {
        if (this.world.worldEntity != null) {
            return this.world.worldEntity.loadedGameVersion;
        }
        return null;
    }

    public boolean isEarlierThanOneWorld() {
        return this.world.worldEntity != null && this.world.worldEntity.isEarlierThanOneWorld();
    }

    public void closeWorldFileSystem() throws IOException {
        if (this.world != null) {
            this.world.closeFileSystem();
        }
    }

    @Override
    public int compareTo(WorldSave other) {
        return Long.compare(other.timeModified, this.timeModified);
    }

    public static void loadSaves(Consumer<WorldSave> saveConsumer, Supplier<Boolean> isInterrupted, Consumer<Boolean> onDone, int limit) {
        WorldSave.loadSaves(true, saveConsumer, isInterrupted, onDone, limit);
    }

    public static void loadSaves(boolean loadWorlds, Consumer<WorldSave> saveConsumer, Supplier<Boolean> isInterrupted, Consumer<Boolean> onDone, int limit) {
        if (isInterrupted != null && isInterrupted.get().booleanValue()) {
            if (onDone != null) {
                onDone.accept(true);
            }
            return;
        }
        ArrayList<ObjectValue<File, ComparableSequence<Long>>> list = WorldSave.getSavesList(true, true);
        int loaded = 0;
        for (ObjectValue<File, ComparableSequence<Long>> ov : list) {
            if (isInterrupted != null && isInterrupted.get().booleanValue()) {
                if (onDone != null) {
                    onDone.accept(true);
                }
                return;
            }
            if (limit >= 0 && loaded >= limit) break;
            String validError = WorldSave.isValidExistingWorld((File)ov.object);
            if (validError != null) {
                System.err.println(validError);
                continue;
            }
            try {
                WorldSave worldSave = new WorldSave((File)ov.object, loadWorlds, true, true);
                if (isInterrupted != null && isInterrupted.get().booleanValue()) {
                    if (onDone != null) {
                        onDone.accept(true);
                    }
                    return;
                }
                saveConsumer.accept(worldSave);
                ++loaded;
            }
            catch (Exception e) {
                System.err.println("Could not load save file " + ov.object);
                e.printStackTrace();
            }
        }
        if (onDone != null) {
            onDone.accept(false);
        }
    }

    public static ArrayList<ObjectValue<File, ComparableSequence<Long>>> getSavesList(boolean sorted, boolean includeBackups) {
        ArrayList<ObjectValue<File, ComparableSequence<Long>>> list = new ArrayList<ObjectValue<File, ComparableSequence<Long>>>();
        for (String path : World.loadWorldsFromPaths()) {
            File[] files = new File(path).listFiles();
            if (files == null) {
                files = new File[]{};
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    File worldFile = GameUtils.resolveFile(file, "world.dat");
                    if (!worldFile.exists()) continue;
                    if (WorldSave.isLatestBackup(file.getName())) {
                        if (!includeBackups) continue;
                        list.add(new ObjectValue<File, ComparableSequence<Long>>(file, new ComparableSequence<Long>(-100000L).thenBy(worldFile.lastModified())));
                        continue;
                    }
                    list.add(new ObjectValue<File, ComparableSequence<Long>>(file, new ComparableSequence<Long>(worldFile.lastModified())));
                    continue;
                }
                if (!file.isFile()) continue;
                boolean ignored = false;
                for (Pattern pattern : ignoredFiles) {
                    if (!pattern.matcher(file.getName()).matches()) continue;
                    ignored = true;
                    break;
                }
                if (ignored) continue;
                if (WorldSave.isLatestBackup(file.getName())) {
                    if (!includeBackups) continue;
                    list.add(new ObjectValue<File, ComparableSequence<Long>>(file, new ComparableSequence<Long>(-100000L).thenBy(file.lastModified())));
                    continue;
                }
                list.add(new ObjectValue<File, ComparableSequence<Long>>(file, new ComparableSequence<Long>(file.lastModified())));
            }
        }
        if (sorted) {
            Comparator<ObjectValue> comparator = Comparator.comparing(f -> (ComparableSequence)f.value);
            list.sort(comparator.reversed());
        }
        return list;
    }

    public static WorldSave getMostRecentSave(boolean loadWorld) {
        ArrayList<ObjectValue<File, ComparableSequence<Long>>> list = WorldSave.getSavesList(true, false);
        for (ObjectValue<File, ComparableSequence<Long>> ov : list) {
            String validError = WorldSave.isValidExistingWorld((File)ov.object);
            if (validError != null) {
                System.err.println(validError);
                continue;
            }
            try {
                return new WorldSave((File)ov.object, loadWorld, true, true);
            }
            catch (Exception e) {
                System.err.println("Could not load save file " + ov.object);
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static String isValidExistingWorld(File file) {
        try (WorldFileSystem fs = World.getFileSystem(file, true);){
            if (fs.worldEntityFileExists()) {
                String string2 = null;
                return string2;
            }
            String string = "Error loading world because world entity file does not exist: " + file;
            return string;
        }
        catch (IOException | ZipError e) {
            e.printStackTrace();
            return "Error loading world: " + file;
        }
        catch (FileSystemClosedException e) {
            return "Error loading world because file system is not closed: " + file;
        }
    }

    public static WorldSave findOrCreateNewWorld(String worldName) throws FileSystemClosedException, IOException {
        File file = new File(World.getWorldsPath() + worldName + (Settings.zipSaves ? ".zip" : ""));
        if (WorldSave.isValidExistingWorld(file) == null) {
            return new WorldSave(file, true, true, true);
        }
        file = new File(World.getWorldsPath() + worldName + (Settings.zipSaves ? "" : ".zip"));
        if (WorldSave.isValidExistingWorld(file) == null) {
            return new WorldSave(file, true, true, true);
        }
        return new WorldSave(new ServerCreationSettings(new File(World.getWorldsPath() + worldName + (Settings.zipSaves ? ".zip" : ""))));
    }

    static {
        ignoredFiles.add(Pattern.compile(".+\\.vdf"));
    }
}

