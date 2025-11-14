/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import necesse.engine.modLoader.GameFileEntry;
import necesse.engine.modLoader.ModInfoNotFoundException;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveComponent;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;

public class ModInfoFile {
    public static final String modInfoPath = "mod.info";
    public final String id;
    public final String name;
    public final String version;
    public final String gameVersion;
    public final String author;
    public final String description;
    public final boolean clientside;
    public final String[] depends;
    public final String[] optionalDepends;
    public final Map<String, String> extra;

    public ModInfoFile(JarFile jarFile) throws IOException, ModInfoNotFoundException {
        this(jarFile.getInputStream(jarFile.stream().filter(f -> f.getName().equals(modInfoPath)).findFirst().orElseThrow(ModInfoNotFoundException::new)));
    }

    public ModInfoFile(List<GameFileEntry> modFiles) throws IOException, ModInfoNotFoundException {
        this(modFiles.stream().filter(f -> f.getPath().equals(modInfoPath)).findFirst().orElseThrow(ModInfoNotFoundException::new).getFileInputStream());
    }

    public ModInfoFile(InputStream stream) throws IOException {
        this(new String(GameUtils.loadInputStream(stream)));
    }

    public ModInfoFile(String script) {
        this(new LoadData(script));
    }

    public ModInfoFile(LoadData save) {
        this.id = this.loadUnsafeString(save, "id");
        if (this.id.length() > 40) {
            throw new IllegalArgumentException("Mod id cannot be longer than 40 characters");
        }
        if (!this.id.matches("[a-z0-9.-]+")) {
            throw new IllegalArgumentException("Mod id includes illegal characters");
        }
        this.name = this.loadSafeString(save, "name");
        if (this.name.length() > 80) {
            throw new IllegalArgumentException("Mod name cannot be longer than 80 characters");
        }
        if (!this.name.matches("[ a-zA-Z0-9.!:-]+")) {
            throw new IllegalArgumentException("Mod name includes illegal characters");
        }
        this.version = this.loadUnsafeString(save, "version");
        if (this.version.length() > 20) {
            throw new IllegalArgumentException("Mod version cannot be longer than 20 characters");
        }
        if (!this.version.matches("\\d+((\\.[\\d]+)+)?")) {
            throw new IllegalArgumentException("Mod version uses illegal format");
        }
        this.gameVersion = this.loadUnsafeString(save, "gameVersion");
        this.author = this.loadSafeString(save, "author");
        this.description = this.loadSafeString(save, "description");
        this.clientside = save.getBoolean("clientside", false, false);
        this.depends = this.loadOptionalArray(save, "dependencies");
        this.optionalDepends = this.loadOptionalArray(save, "optionalDependencies");
        HashMap<String, String> extra = new HashMap<String, String>();
        for (LoadData sub : save.getLoadData()) {
            if (!sub.isData()) continue;
            String data = sub.getData();
            if (data.startsWith("\"") && data.endsWith("\"")) {
                data = data.substring(1, data.length() - 1);
            }
            data = SaveComponent.fromSafeData(data);
            extra.put(sub.getName(), data);
        }
        extra.remove("id");
        extra.remove("name");
        extra.remove("version");
        extra.remove("gameVersion");
        extra.remove("author");
        extra.remove("description");
        extra.remove("dependencies");
        extra.remove("optionalDependencies");
        extra.remove("clientside");
        this.extra = Collections.unmodifiableMap(extra);
    }

    public static void saveModInfoFile(File file, HashMap<String, String> map) {
        SaveData data = new SaveData("");
        String id = map.get("id");
        if (id != null && !id.isEmpty()) {
            data.addUnsafeString("id", id);
        }
        map.remove("id");
        String name = map.get("name");
        if (name != null && !name.isEmpty()) {
            data.addSafeString("name", name);
        }
        map.remove("name");
        String version = map.get("version");
        if (version != null && !version.isEmpty()) {
            data.addUnsafeString("version", version);
        }
        map.remove("version");
        String gameVersion = map.get("gameVersion");
        if (gameVersion != null && !gameVersion.isEmpty()) {
            data.addUnsafeString("gameVersion", gameVersion);
        }
        map.remove("gameVersion");
        String author = map.get("author");
        if (author != null && !author.isEmpty()) {
            data.addSafeString("author", author);
        }
        map.remove("author");
        String description = map.get("description");
        if (description != null && !description.isEmpty()) {
            data.addSafeString("description", description);
        }
        map.remove("description");
        String clientside = map.get("clientside");
        if (clientside != null && !clientside.isEmpty()) {
            data.addUnsafeString("clientside", clientside);
        }
        map.remove("clientside");
        String dependencies = map.get("dependencies");
        if (dependencies != null && !dependencies.isEmpty()) {
            data.addUnsafeString("dependencies", dependencies);
        }
        map.remove("dependencies");
        String optionalDependencies = map.get("optionalDependencies");
        if (optionalDependencies != null && !optionalDependencies.isEmpty()) {
            data.addUnsafeString("optionalDependencies", optionalDependencies);
        }
        map.remove("optionalDependencies");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.isEmpty()) continue;
            if (value.startsWith("unsafe:")) {
                data.addSafeString(key, value.substring("unsafe:".length()));
                continue;
            }
            data.addSafeString(key, value);
        }
        data.saveScript(file);
    }

    private String loadUnsafeString(LoadData save, String name) {
        try {
            String out = save.getUnsafeString(name).trim();
            if (out.isEmpty()) {
                throw new IllegalStateException();
            }
            return out;
        }
        catch (NullPointerException e) {
            throw new IllegalArgumentException("mod.info missing " + name + " data");
        }
        catch (IllegalStateException e) {
            throw new IllegalArgumentException("mod.info " + name + " cannot be empty");
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not load mod info " + name);
        }
    }

    private String loadSafeString(LoadData save, String name) {
        try {
            String out = save.getSafeString(name).trim();
            if (out.isEmpty()) {
                throw new IllegalStateException();
            }
            return out;
        }
        catch (NullPointerException e) {
            throw new IllegalArgumentException("mod.info missing " + name + " data");
        }
        catch (IllegalStateException e) {
            throw new IllegalArgumentException("mod.info " + name + " cannot be empty");
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Could not load mod info " + name);
        }
    }

    private String[] loadOptionalArray(LoadData save, String name) {
        String s = save.getUnsafeString(name, "[]", false);
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1);
            return this.removeEmptyStrings(s.split(","));
        }
        return new String[]{s.trim()};
    }

    private String[] removeEmptyStrings(String[] array) {
        LinkedList<String> result = new LinkedList<String>();
        for (String s : array) {
            if ((s = s.trim()).isEmpty()) continue;
            result.add(s);
        }
        return result.toArray(new String[0]);
    }
}

