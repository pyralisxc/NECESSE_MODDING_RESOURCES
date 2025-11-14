/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.WorldFile;
import necesse.level.maps.Level;

public class WorldFileSystem
implements AutoCloseable {
    private final FileSystem fs;
    public final String fileName;
    private final File archiveFile;
    public final String archiveFolderName;
    public final boolean isArchive;
    private String pathPrepend;

    public WorldFileSystem(String path, boolean createIfNotExists, boolean relativePath) throws IOException, FileSystemClosedException {
        this(path, path, createIfNotExists, relativePath);
    }

    public WorldFileSystem(String path, String encodedPath, boolean createIfNotExists, boolean relativePath) throws IOException, FileSystemClosedException {
        if (path.endsWith(".zip") || path.endsWith(".rar")) {
            FileSystem fs;
            URI uri;
            path = path.replace("\\", "/");
            encodedPath = encodedPath.replace("\\", "/");
            this.fileName = Paths.get(path, new String[0]).getFileName().toString();
            if (relativePath) {
                Path parent;
                String currentDir = System.getProperty("user.dir").replace("\\", "/");
                if (createIfNotExists && (parent = FileSystems.getDefault().getPath(currentDir + "/" + path, new String[0]).getParent()) != null && !Files.exists(parent, new LinkOption[0]) && !Files.isDirectory(parent, new LinkOption[0])) {
                    Files.createDirectories(parent, new FileAttribute[0]);
                }
                uri = URI.create("jar:file:" + (currentDir.startsWith("/") ? "" : "/") + currentDir + "/" + encodedPath.replace(" ", "%20"));
                this.archiveFile = new File(currentDir + "/" + path);
            } else {
                Path parent;
                if (createIfNotExists && (parent = FileSystems.getDefault().getPath(path, new String[0]).getParent()) != null && !Files.exists(parent, new LinkOption[0]) && !Files.isDirectory(parent, new LinkOption[0])) {
                    Files.createDirectories(parent, new FileAttribute[0]);
                }
                uri = URI.create("jar:file:" + (encodedPath.startsWith("/") ? "" : "/") + encodedPath.replace(" ", "%20"));
                this.archiveFile = new File("/" + path);
            }
            HashMap<String, String> properties = new HashMap<String, String>();
            if (createIfNotExists) {
                properties.put("create", "true");
            }
            try {
                fs = FileSystems.getFileSystem(uri);
            }
            catch (FileSystemNotFoundException e) {
                fs = FileSystems.newFileSystem(uri, properties, null);
            }
            if (!fs.isOpen()) {
                throw new FileSystemClosedException("File system has been closed");
            }
            this.fs = fs;
            this.isArchive = true;
            String worldSubFolderName = this.fileName.substring(0, this.fileName.length() - 4);
            this.pathPrepend = worldSubFolderName + "/";
            if (!this.worldEntityFileExists()) {
                String newFolderName = worldSubFolderName;
                this.pathPrepend = "/";
                boolean found = false;
                for (WorldFile worldFile : this.getPathsInDirectory("")) {
                    if (!worldFile.isDirectory()) continue;
                    this.pathPrepend = worldFile.getFileName().toString() + "/";
                    if (!this.worldEntityFileExists()) continue;
                    newFolderName = worldFile.getFileName().toString();
                    found = true;
                    break;
                }
                if (!found) {
                    this.pathPrepend = worldSubFolderName + "/";
                }
                this.archiveFolderName = newFolderName;
            } else {
                this.archiveFolderName = worldSubFolderName;
            }
        } else {
            this.archiveFile = null;
            this.fileName = Paths.get(path, new String[0]).getFileName().toString();
            if (relativePath) {
                Path folder;
                String currentDir = System.getProperty("user.dir").replace("\\", "/");
                if (createIfNotExists && !Files.exists(folder = FileSystems.getDefault().getPath(currentDir + "/" + path, new String[0]), new LinkOption[0]) && !Files.isDirectory(folder, new LinkOption[0])) {
                    Files.createDirectories(folder, new FileAttribute[0]);
                }
                this.pathPrepend = currentDir + "/" + this.fileName + "/";
            } else {
                Path folder;
                if (createIfNotExists && !Files.exists(folder = FileSystems.getDefault().getPath(path, new String[0]), new LinkOption[0]) && !Files.isDirectory(folder, new LinkOption[0])) {
                    Files.createDirectories(folder, new FileAttribute[0]);
                }
                this.pathPrepend = path + "/";
            }
            this.archiveFolderName = this.fileName;
            this.fs = FileSystems.getDefault();
            this.isArchive = false;
        }
    }

    public WorldFileSystem(String path, boolean createIfNotExists) throws IOException, FileSystemClosedException {
        this(path, createIfNotExists, true);
    }

    public void fixArchiveFirstFolder(String lastName, String newName) throws IOException {
        if (!this.isArchive) {
            throw new IllegalStateException("Should only be used on archive saves");
        }
        new WorldFile(this.fs.getPath(lastName, new String[0])).moveTo(this.fs.getPath(newName, new String[0]), StandardCopyOption.REPLACE_EXISTING);
    }

    public void fixArchiveFirstFolder(String newName) throws IOException {
        this.fixArchiveFirstFolder(this.archiveFolderName, newName);
    }

    public WorldFile file(String path) {
        return new WorldFile(this.fs.getPath(this.pathPrepend + path, new String[0]));
    }

    public boolean exists(String path) {
        return this.file(path).exists();
    }

    public boolean isDirectory(String path) {
        return this.file(path).isDirectory();
    }

    public Iterator<String> iterateFilesInDirectory(String path) throws IOException {
        return this.file(path).iterateFilesInDirectory();
    }

    public Iterable<String> getFilesInDirectory(String path) throws IOException {
        return this.file(path).getFilesInDirectory();
    }

    public Iterator<WorldFile> iteratePathsInDirectory(String path) throws IOException {
        return this.file(path).iteratePathsInDirectory();
    }

    public Iterable<WorldFile> getPathsInDirectory(String path) throws IOException {
        return this.file(path).getPathsInDirectory();
    }

    public BufferedWriter fileWriter(String path, Charset charset, boolean allowOverride) throws IOException {
        return this.file(path).writer(charset, allowOverride);
    }

    public BufferedWriter fileWriter(String path, boolean allowOverride) throws IOException {
        return this.file(path).writer(allowOverride);
    }

    public void writeFile(String path, byte[] data, boolean allowOverride) throws IOException {
        this.file(path).write(data, allowOverride);
    }

    public void writeFile(String path, byte[] data) throws IOException {
        this.file(path).write(data);
    }

    public byte[] readFile(String path) throws IOException {
        return this.file(path).read();
    }

    public BufferedReader fileReader(String path, Charset charset) throws IOException {
        return this.file(path).reader(charset);
    }

    public BufferedReader fileReader(String path) throws IOException {
        return this.file(path).reader();
    }

    public InputStream inputStream(String path, OpenOption ... openOptions) throws IOException {
        return this.file(path).inputStream(openOptions);
    }

    public OutputStream outputStream(String path, OpenOption ... openOptions) throws IOException {
        return this.file(path).outputStream(openOptions);
    }

    public boolean deleteFile(String path, boolean deleteDirectoryContent) throws IOException {
        return this.file(path).delete(deleteDirectoryContent);
    }

    public FileTime getLastModified() throws IOException {
        if (this.archiveFile != null) {
            return FileTime.fromMillis(this.archiveFile.lastModified());
        }
        if (this.worldEntityFileExists()) {
            return Files.getLastModifiedTime(this.getWorldEntityFile().path, new LinkOption[0]);
        }
        return FileTime.fromMillis(0L);
    }

    public boolean isOpen() {
        return this.fs.isOpen();
    }

    @Override
    public void close() throws IOException {
        if (this.isArchive) {
            this.fs.close();
        }
    }

    public boolean worldEntityFileExists() {
        WorldFile file = this.getWorldEntityFile();
        return file.exists() && !file.isDirectory();
    }

    public WorldFile getWorldEntityFile() {
        return this.file("world.dat");
    }

    public boolean worldSettingsFileExists() {
        WorldFile file = this.getWorldSettingsFile();
        return file.exists() && !file.isDirectory();
    }

    public WorldFile getWorldSettingsFile() {
        return this.file("worldSettings.cfg");
    }

    public WorldFile getBackupWorldRegionFile(LevelIdentifier identifier, int worldRegionX, int worldRegionY) {
        return this.file("levels/regions/" + identifier.stringID + "/" + worldRegionX + "x" + worldRegionY + "_backup" + ".dat");
    }

    public WorldFile getWorldRegionFile(LevelIdentifier identifier, int worldRegionX, int worldRegionY) {
        return this.file("levels/regions/" + identifier.stringID + "/" + worldRegionX + "x" + worldRegionY + ".dat");
    }

    public WorldFile getLevelRegionsFolder(LevelIdentifier identifier) {
        return this.file("levels/regions/" + identifier.stringID);
    }

    public boolean deleteLevelRegionsFolder(LevelIdentifier identifier) throws IOException {
        WorldFile file = this.getLevelRegionsFolder(identifier);
        if (file.exists()) {
            return file.delete(true);
        }
        return true;
    }

    public WorldFile getPresetRegionFile(LevelIdentifier identifier, int presetRegionX, int presetRegionY) {
        return this.file("levels/presets/" + identifier.stringID + "/" + presetRegionX + "x" + presetRegionY + ".dat");
    }

    public WorldFile getLevelPresetsFolder(LevelIdentifier identifier) {
        return this.file("levels/presets/" + identifier.stringID);
    }

    public boolean deleteLevelPresetsFolder(LevelIdentifier identifier) throws IOException {
        WorldFile file = this.getLevelPresetsFolder(identifier);
        if (file.exists()) {
            return file.delete(true);
        }
        return true;
    }

    public boolean worldRegionFileExists(LevelIdentifier identifier, int worldRegionX, int worldRegionY) {
        WorldFile file = this.getWorldRegionFile(identifier, worldRegionX, worldRegionY);
        return file.exists() && !file.isDirectory();
    }

    public WorldFile getSettlementFile(int settlementUniqueID) {
        return this.file("levels/settlements/" + settlementUniqueID + ".dat");
    }

    public WorldFile getSettlementsFolder() {
        return this.file("levels/settlements");
    }

    public boolean settlementFileExists(int settlementUniqueID) {
        WorldFile file = this.getSettlementFile(settlementUniqueID);
        return file.exists() && !file.isDirectory();
    }

    public boolean levelFileExists(Level level) {
        WorldFile file = this.getLevelFile(level);
        return file.exists() && !file.isDirectory();
    }

    public WorldFile getLevelFile(Level level) {
        return this.getLevelFile(level.getIdentifier());
    }

    public boolean levelFileExists(LevelIdentifier identifier) {
        WorldFile file = this.getLevelFile(identifier);
        return file.exists() && !file.isDirectory();
    }

    public WorldFile getLevelFile(LevelIdentifier identifier) {
        return this.file("levels/" + identifier.stringID + ".dat");
    }

    public boolean deleteLevelFile(LevelIdentifier identifier) throws IOException {
        WorldFile file = this.getLevelFile(identifier);
        if (file.exists()) {
            return file.delete(false);
        }
        return true;
    }

    public boolean deleteAllLevelFiles(LevelIdentifier identifier) throws IOException {
        return this.deleteLevelRegionsFolder(identifier) & this.deleteLevelPresetsFolder(identifier) & this.deleteLevelFile(identifier);
    }

    public boolean playerFileExists(ServerClient client) {
        WorldFile file = this.getPlayerFile(client);
        return file.exists() && !file.isDirectory();
    }

    public WorldFile getPlayerFile(ServerClient client) {
        return this.getPlayerFile(client.authentication);
    }

    public boolean playerFileExists(long authentication) {
        WorldFile file = this.getPlayerFile(authentication);
        return file.exists() && !file.isDirectory();
    }

    public WorldFile getPlayerFile(long authentication) {
        return this.file("players/" + authentication + ".dat");
    }

    public boolean playerMapFileExists(ServerClient client) {
        WorldFile file = this.getMapPlayerFile(client);
        return file.exists() && !file.isDirectory();
    }

    public WorldFile getMapPlayerFile(ServerClient client) {
        return this.getMapPlayerFile(client.authentication);
    }

    public boolean playerMapFileExists(long authentication) {
        WorldFile file = this.getMapPlayerFile(authentication);
        return file.exists() && !file.isDirectory();
    }

    public WorldFile getMapPlayerFile(long authentication) {
        return this.file("players/" + authentication + ".dat" + "map");
    }

    public LinkedList<WorldFile> getLevelFiles() {
        LinkedList<WorldFile> out = new LinkedList<WorldFile>();
        try {
            for (WorldFile file : this.getPathsInDirectory("levels")) {
                if (!file.getFileName().toString().endsWith(".dat")) continue;
                out.add(file);
            }
        }
        catch (IOException e) {
            System.err.println("Error getting level files");
            e.printStackTrace();
        }
        return out;
    }

    public LinkedList<WorldFile> getPlayerFiles() {
        LinkedList<WorldFile> out = new LinkedList<WorldFile>();
        try {
            for (WorldFile file : this.getPathsInDirectory("players")) {
                if (!file.getFileName().toString().endsWith(".dat")) continue;
                out.add(file);
            }
        }
        catch (IOException e) {
            System.err.println("Error getting player files");
            e.printStackTrace();
        }
        return out;
    }
}

