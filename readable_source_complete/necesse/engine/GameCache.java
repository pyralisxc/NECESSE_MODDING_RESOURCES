/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import necesse.engine.GameInfo;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.modLoader.InputStreamSupplier;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;

public class GameCache {
    public static final String suffix = ".cache";
    private static boolean gameUpdated = false;
    private static String prevGameVersion = null;

    public static String cachePath() {
        return GlobalData.appDataPath() + "cache/";
    }

    public static boolean gameUpdated() {
        return gameUpdated;
    }

    public static String getPreviousGameVersion() {
        return prevGameVersion;
    }

    public static boolean isFirstLaunch() {
        return GameCache.gameUpdated() && GameCache.getPreviousGameVersion() == null;
    }

    public static void checkCacheVersion() {
        gameUpdated = false;
        prevGameVersion = null;
        if (GameCache.cacheFileExists("version")) {
            try {
                prevGameVersion = new String(GameCache.getBytes("version"));
                gameUpdated = !prevGameVersion.equals(GameInfo.getFullVersionString());
            }
            catch (Exception e) {
                GameLog.warn.println("Could not read cache version file: " + e.getMessage());
                gameUpdated = true;
            }
        } else {
            gameUpdated = true;
            GameCache.cacheBytes(GameInfo.getFullVersionString().getBytes(), "version");
        }
        if (GameCache.gameUpdated()) {
            if (GameCache.isFirstLaunch()) {
                System.out.println("First time launched");
            } else {
                System.out.println("First time launch since version " + prevGameVersion);
            }
            GameCache.clearCacheFolder("version");
            System.out.println("Cache version was not correct, deleted version cache files.");
            GameCache.cacheBytes(GameInfo.getFullVersionString().getBytes(), "version");
        }
    }

    public static void clearCacheFolder(String subFolder) {
        File folder = new File(GameCache.cachePath() + (subFolder == null ? "" : subFolder + "/"));
        if (folder.exists() && folder.isDirectory()) {
            try {
                for (File f : folder.listFiles()) {
                    GameUtils.deleteFileOrFolder(f.getPath());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean cacheFileExists(String path) {
        return new File(GameCache.cachePath() + path + suffix).exists();
    }

    public static boolean removeCache(String path) {
        if (GameCache.cacheFileExists(path)) {
            File file = new File(GameCache.cachePath() + path + suffix);
            return file.delete();
        }
        return false;
    }

    public static void cacheObject(Object obj, String path) {
        try {
            GameCache.cacheObjectToPath(obj, new File(GameCache.cachePath() + path + suffix));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cacheObjectToPath(Object obj, File file) throws IOException {
        if (!GameUtils.mkDirs(file)) {
            System.out.println("Could not create folders for cache files");
            return;
        }
        FileOutputStream saveFile = new FileOutputStream(file);
        ObjectOutputStream save = new ObjectOutputStream(saveFile);
        save.writeObject(obj);
        save.close();
    }

    public static Object getObjectFromStream(InputStream inputStream) throws IOException, ClassNotFoundException {
        return GameCache.getObjectFromStream(inputStream, Object.class);
    }

    public static <C> C getObjectFromStream(InputStream inputStream, Class<? extends C> expectedClass) throws IOException, ClassNotFoundException {
        return GameCache.getObjectFromStream(inputStream, expectedClass, null);
    }

    public static <C> C getObjectFromStream(InputStream inputStream, Class<? extends C> expectedClass, C defaultObject) throws IOException, ClassNotFoundException {
        ObjectInputStream read = new ObjectInputStream(inputStream);
        Object obj = read.readObject();
        if (expectedClass.isAssignableFrom(obj.getClass())) {
            return expectedClass.cast(obj);
        }
        return defaultObject;
    }

    public static InputStreamSupplier getInputStreamFromObject(Object obj) {
        return () -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream save = new ObjectOutputStream(outputStream);
            save.writeObject(obj);
            byte[] array = outputStream.toByteArray();
            return new ByteArrayInputStream(array);
        };
    }

    public static Object getObject(String path) {
        return GameCache.getObject(path, Object.class);
    }

    public static <C> C getObject(String path, Class<? extends C> expectedClass) {
        return GameCache.getObject(path, expectedClass, null);
    }

    public static File getCacheFile(String path) {
        return new File(GameCache.cachePath() + path + suffix);
    }

    public static <C> C getObject(String path, Class<? extends C> expectedClass, C defaultObject) {
        File file = GameCache.getCacheFile(path);
        try {
            return GameCache.getObjectFromFile(file, expectedClass, defaultObject);
        }
        catch (Exception e) {
            if (!GameCache.gameUpdated()) {
                GameLog.warn.println("Could not read object from cache file " + file.getPath() + " : " + e.getMessage());
            }
            return defaultObject;
        }
    }

    public static Object getObjectFromFile(File file) throws IOException, ClassNotFoundException {
        return GameCache.getObjectFromFile(file, Object.class);
    }

    public static <C> C getObjectFromFile(File file, Class<? extends C> expectedClass) throws IOException, ClassNotFoundException {
        return GameCache.getObjectFromFile(file, expectedClass, null);
    }

    public static <C> C getObjectFromFile(File file, Class<? extends C> expectedClass, C defaultObject) throws IOException, ClassNotFoundException {
        if (!file.exists()) {
            return defaultObject;
        }
        FileInputStream readFile = new FileInputStream(file);
        ObjectInputStream read = new ObjectInputStream(readFile);
        Object obj = read.readObject();
        if (expectedClass.isAssignableFrom(obj.getClass())) {
            return expectedClass.cast(obj);
        }
        return defaultObject;
    }

    public static void cacheSave(SaveData save, String path) {
        File file = GameCache.getCacheFile(path);
        save.saveScript(file);
    }

    public static LoadData getSave(String path) {
        File file = GameCache.getCacheFile(path);
        if (!file.exists()) {
            return null;
        }
        return new LoadData(file);
    }

    public static void cacheBytes(byte[] data, String path) {
        try {
            GameUtils.saveByteFile(data, GameCache.cachePath() + path + suffix);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] getBytes(String path) {
        try {
            return GameUtils.loadByteFile(GameCache.cachePath() + path + suffix);
        }
        catch (Exception e) {
            System.err.println("Could load byte cache file: " + e.getMessage());
            return null;
        }
    }
}

