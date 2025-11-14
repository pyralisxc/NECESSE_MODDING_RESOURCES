/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.res;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import necesse.engine.GameLaunch;
import necesse.engine.GlobalData;
import necesse.engine.modLoader.GameFileEntry;
import necesse.engine.modLoader.LoadedMod;
import necesse.gfx.res.ResourceFile;
import necesse.gfx.res.ResourceFolder;

public class ResourceEncoder {
    public static final String resDataFile = "res.data";
    public static final String jarResourcePath = "resources/";
    public static final String previewImagePath = "resources/preview.png";
    public static final String[] fileExtensions = new String[]{"png", "ogg", "glsl", "ttf"};
    public static final String[] ignoreFiles = new String[]{"examplefolder/examplefile.png"};
    private static ResourceFolder resources = null;

    public static boolean isLoaded() {
        return resources != null;
    }

    public static void loadResourceFile() {
        try {
            resources = new ResourceFolder(ResourceEncoder.getFileResources());
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to load game resources", e);
        }
    }

    public static void loadEmptyResourceFile() {
        resources = new ResourceFolder();
    }

    private static List<GameFileEntry> getFileResources() {
        File file;
        boolean ignoreResData;
        ArrayList<GameFileEntry> out = new ArrayList<GameFileEntry>();
        boolean bl = ignoreResData = GameLaunch.launchOptions != null && GameLaunch.launchOptions.containsKey("ignoreresdata");
        if (!ignoreResData && (file = new File(GlobalData.rootPath() + resDataFile)).isFile()) {
            out.add(new GameFileEntry("resources/res.data", file));
        }
        return out;
    }

    public static void addModResources(LoadedMod mod) {
        if (resources == null) {
            throw new IllegalStateException("Resources not loaded yet.");
        }
        try {
            Iterator iterator = mod.jarFile.stream().map(e -> new GameFileEntry(mod.jarFile, (ZipEntry)e)).iterator();
            resources.addModResources(() -> iterator, mod);
        }
        catch (IOException e2) {
            System.err.println("Could not load mod " + mod.id + " resources");
            e2.printStackTrace();
        }
    }

    public static Set<Map.Entry<String, ResourceFile>> getAllFiles() {
        return ResourceEncoder.resources.files.entrySet();
    }

    public static byte[] getResourceBytes(String path) throws IOException {
        if (resources == null) {
            throw new IllegalStateException("Resources not loaded yet.");
        }
        ResourceFile file = ResourceEncoder.resources.files.get(path);
        if (file != null) {
            return file.loadBytes(true);
        }
        throw new FileNotFoundException("Could not find resource file \"" + path + "\"");
    }

    public static InputStream getResourceInputStream(String path) throws IOException {
        return new ByteArrayInputStream(ResourceEncoder.getResourceBytes(path));
    }
}

