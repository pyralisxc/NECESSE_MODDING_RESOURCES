/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import necesse.engine.modLoader.InputStreamSupplier;

public class GameFileEntry {
    public final String path;
    public final String name;
    public final boolean isDirectory;
    public final InputStreamSupplier inputStreamSupplier;

    public GameFileEntry(String path, File file) {
        this.path = path.replace("\\", "/");
        this.name = new File(path).getName();
        this.isDirectory = file.isDirectory();
        this.inputStreamSupplier = () -> new FileInputStream(file);
    }

    public GameFileEntry(ZipFile file, ZipEntry entry) {
        this.path = entry.getName();
        this.name = new File(entry.getName()).getName();
        this.isDirectory = entry.isDirectory();
        this.inputStreamSupplier = () -> file.getInputStream(entry);
    }

    public String getPath() {
        return this.path;
    }

    public String getName() {
        return this.name;
    }

    public boolean isDirectory() {
        return this.isDirectory;
    }

    public InputStream getFileInputStream() throws IOException {
        return this.inputStreamSupplier.get();
    }

    public static void addFileEntries(File parentDir, String subDir, List<GameFileEntry> list) {
        File[] subFiles = parentDir.listFiles();
        if (subFiles == null) {
            return;
        }
        for (File subFile : subFiles) {
            if (subFile.getName().startsWith(".")) continue;
            String newSubPath = Paths.get(subDir, subFile.getName()).toString();
            if (subFile.isDirectory()) {
                list.add(new GameFileEntry(newSubPath, subFile));
                GameFileEntry.addFileEntries(subFile, newSubPath + '/', list);
                continue;
            }
            list.add(new GameFileEntry(newSubPath, subFile));
        }
    }
}

