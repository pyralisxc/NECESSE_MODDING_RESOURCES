/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import java.io.IOException;
import java.io.Serializable;
import java.lang.instrument.Instrumentation;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import necesse.engine.GameLog;
import necesse.engine.modLoader.LoadedDevMod;
import necesse.engine.modLoader.ModClassSignature;
import necesse.engine.modLoader.ModClasses;
import necesse.engine.modLoader.ModInfoFile;
import necesse.engine.modLoader.ModListData;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModLoadLocation;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.engine.modLoader.ModSettings;
import necesse.engine.modLoader.classes.ModClass;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameUtils;
import necesse.gfx.gameTexture.GameTexture;

public class LoadedMod {
    private static final HashMap<String, LoadedClassEntry> loadedClasses = new HashMap();
    static LoadedMod runningMod = null;
    public final String id;
    public final String name;
    public final String version;
    public final String gameVersion;
    public final boolean clientside;
    public final String[] depends;
    public final String[] optionalDepends;
    public final String author;
    public final String description;
    public final Map<String, String> modInfo;
    private ModClasses classes;
    public final JarFile jarFile;
    public final ModLoadLocation loadLocation;
    private boolean hasExampleModPackageClasses;
    private boolean hasLoaded;
    private ModSettings settings;
    public GameTexture preview;
    public boolean initError;
    public boolean runError;
    private final HashSet<ModClassSignature> editedSignatures = new HashSet();
    ModListData listData;

    public static LoadedMod getRunningMod() {
        return runningMod;
    }

    public static boolean isRunningModClientSide() {
        return runningMod != null && LoadedMod.runningMod.clientside;
    }

    public LoadedMod(JarFile jarFile, ModInfoFile file, ModLoadLocation loadLocation) {
        this.id = file.id;
        this.name = file.name;
        this.version = file.version;
        this.gameVersion = file.gameVersion;
        this.clientside = file.clientside;
        this.author = file.author;
        this.description = file.description;
        this.depends = file.depends;
        this.optionalDepends = file.optionalDepends;
        this.modInfo = file.extra;
        this.jarFile = jarFile;
        this.loadLocation = loadLocation;
        this.listData = new ModListData(this);
    }

    public boolean hasExampleModPackageClasses() {
        return this.hasExampleModPackageClasses;
    }

    public void loadClasses(ComputedValue<Instrumentation> instrumentation) throws ModLoadException {
        try {
            boolean containsExampleModClasses = false;
            boolean containsNecessePackageClasses = false;
            boolean containsClassesWithNoPackage = false;
            instrumentation.get().appendToSystemClassLoaderSearch(this.jarFile);
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            Enumeration<JarEntry> entries = this.jarFile.entries();
            this.classes = new ModClasses();
            while (entries.hasMoreElements()) {
                LoadedClassEntry last;
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entry.isDirectory() || !entryName.endsWith(".class")) continue;
                String className = entryName.substring(0, entryName.length() - 6);
                if ((className = className.replace("/", ".")).startsWith("examplemod")) {
                    containsExampleModClasses = true;
                    if (this instanceof LoadedDevMod) {
                        this.hasExampleModPackageClasses = true;
                    }
                }
                if (className.startsWith("necesse")) {
                    containsNecessePackageClasses = true;
                }
                if (!className.contains(".")) {
                    containsClassesWithNoPackage = true;
                }
                if ((last = loadedClasses.get(className)) != null && !last.isSame(entry)) {
                    throw new ModLoadException(this, "Another mod (" + last.mod.id + ") has already loaded a class from " + className + ". Contact the mod author to get this fixed.");
                }
                Class<?> c = cl.loadClass(className);
                loadedClasses.put(className, new LoadedClassEntry(this, entry));
                this.editedSignatures.add(new ModClassSignature(c, null, null));
                for (ModClass modClass : this.classes.getAllClasses()) {
                    if (!modClass.shouldRegisterModClass(c)) continue;
                    modClass.registerModClass(this, c);
                }
            }
            for (ModClass modClass : this.classes.getAllClasses()) {
                modClass.finalizeLoading(this);
            }
            if (containsExampleModClasses) {
                if (this instanceof LoadedDevMod) {
                    GameLog.warn.println("Your mod \"" + this.name + "\" contains classes inside examplemod package. To upload your mod, you have to move them to a different and unique package.");
                } else {
                    GameLog.warn.println(this.name + " contains classes inside examplemod package. This will likely not be allowed in future versions.");
                }
            }
            if (containsNecessePackageClasses) {
                GameLog.warn.println(this.name + " contains classes inside necesse package. This will likely not be allowed in future versions.");
            }
            if (containsClassesWithNoPackage) {
                GameLog.warn.println(this.name + " contains classes not inside any package. This will likely not be allowed in future versions.");
            }
            this.hasLoaded = true;
        }
        catch (ModLoadException e) {
            throw e;
        }
        catch (ClassNotFoundException e) {
            throw new ModLoadException(this, "Could not load mod " + this.id + " class", e);
        }
        catch (LinkageError e) {
            throw new ModLoadException(this, "Linkage error for " + this.id + " mod", e);
        }
        catch (Exception e) {
            throw new ModLoadException(this, "Unknown error loading " + this.id + " mod", e);
        }
    }

    public void applyPatches(ComputedValue<Instrumentation> instrumentation) throws ModLoadException {
        this.classes.patchClasses.applyPatches(instrumentation, this.editedSignatures);
    }

    public void loadPreviewImage() {
        ZipEntry previewEntry = this.jarFile.getEntry("resources/preview.png");
        if (previewEntry != null) {
            try {
                byte[] previewBytes = GameUtils.loadInputStream(this.jarFile.getInputStream(previewEntry));
                this.preview = new GameTexture(this.id + " preview", previewBytes);
                this.preview.makeFinal();
            }
            catch (IllegalArgumentException e) {
                System.err.println("Failed loading of mod " + this.id + " preview image: " + e.getMessage());
            }
            catch (IOException e) {
                System.err.println("IOException: Could not load mod " + this.id + " preview image");
                e.printStackTrace();
            }
        }
    }

    public void preInit() {
        this.classes.entry.preInit();
    }

    public void init() {
        this.classes.entry.init();
    }

    public void initResources() {
        this.classes.entry.initResources();
    }

    public void postInit() {
        this.classes.entry.postInit();
    }

    public void initSettings() {
        this.settings = this.classes.entry.initSettings();
    }

    public ModSettings getSettings() {
        return this.settings;
    }

    public void dispose() {
        if (this.preview != null) {
            this.preview.delete();
        }
        if (this.classes != null) {
            this.classes.entry.dispose();
        }
    }

    public boolean dependsOn(LoadedMod mod) {
        return this.arrayContains(this.depends, mod.id);
    }

    public boolean optionalDependsOn(LoadedMod mod) {
        return this.arrayContains(this.optionalDepends, mod.id);
    }

    private boolean arrayContains(String[] array, String obj) {
        for (String s : array) {
            if (!s.equals(obj)) continue;
            return true;
        }
        return false;
    }

    public boolean isEnabled() {
        return this.listData.enabled;
    }

    public boolean hasLoaded() {
        return this.hasLoaded;
    }

    public String getModDebugString() {
        return this.id + " (v. " + this.version + ")";
    }

    public String getModNameString() {
        return this.name + " (v. " + this.version + ")";
    }

    public boolean isResponsibleForError(Throwable error) {
        if (error == null) {
            return false;
        }
        for (StackTraceElement e : error.getStackTrace()) {
            if (!this.hasEdited(e)) continue;
            return true;
        }
        int n = 0;
        Serializable[] serializableArray = error.getSuppressed();
        int n2 = serializableArray.length;
        if (n < n2) {
            Serializable suppressedError = serializableArray[n];
            return this.isResponsibleForError((Throwable)suppressedError);
        }
        return this.isResponsibleForError(error.getCause());
    }

    public boolean hasEdited(StackTraceElement e) {
        for (ModClassSignature signature : this.editedSignatures) {
            if (!signature.matches(e)) continue;
            return true;
        }
        return false;
    }

    public String getReportInfo() {
        return this.id + ", v" + this.version + " - " + this.name;
    }

    public SaveType getSaveType() {
        return SaveType.FILE_MOD;
    }

    public ModSaveInfo getModSaveInfo() {
        return new ModSaveInfo(this);
    }

    public long getSteamWorkshopID() {
        return -1L;
    }

    private static class LoadedClassEntry {
        public final LoadedMod mod;
        public long size;

        public LoadedClassEntry(LoadedMod mod, JarEntry entry) {
            this.mod = mod;
            this.size = entry.getSize();
        }

        public boolean isSame(JarEntry other) {
            return this.size == other.getSize();
        }
    }

    public static enum SaveType {
        FILE_MOD,
        DEV_MOD,
        STEAM_MOD;

    }
}

