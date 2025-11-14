/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import java.util.ArrayList;
import java.util.List;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadLocation;
import necesse.engine.modLoader.ModNetworkData;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;

public class ModListData {
    public final String id;
    public final String name;
    public boolean enabled;
    public final LoadedMod.SaveType type;

    public ModListData(LoadedMod mod) {
        this.id = mod.id;
        this.name = mod.name;
        this.type = mod.getSaveType();
        this.enabled = mod.listData == null || mod.listData.enabled;
    }

    public ModListData(LoadData save) {
        this.id = save.getUnsafeString("id", null, false);
        if (this.id == null) {
            throw new LoadDataException("Missing mod id");
        }
        this.name = save.getUnsafeString("name", this.id, false);
        LoadedMod.SaveType foundType = save.getEnum(LoadedMod.SaveType.class, "type", null, false);
        if (foundType == null) {
            legacy_loadLocation loadLocation = save.getEnum(legacy_loadLocation.class, "loadLocation", null, false);
            if (loadLocation == null) {
                throw new LoadDataException("Missing legacy loadLocation");
            }
            switch (loadLocation) {
                case DEVELOPMENT_FOLDER: {
                    foundType = LoadedMod.SaveType.DEV_MOD;
                    break;
                }
                case MODS_FOLDER: {
                    foundType = LoadedMod.SaveType.FILE_MOD;
                    break;
                }
                case STEAM_WORKSHOP: {
                    foundType = LoadedMod.SaveType.STEAM_MOD;
                }
            }
        }
        this.type = foundType;
        if (this.type == null) {
            throw new LoadDataException("Missing mod type");
        }
        this.enabled = save.getBoolean("enabled");
    }

    public ModListData(ModNetworkData mod) {
        this.id = mod.id;
        this.name = mod.name;
        this.type = mod.type;
        this.enabled = true;
    }

    public ModListData(ModSaveInfo mod, ModLoadLocation loadLocation) {
        this.id = mod.id;
        this.name = mod.name;
        this.type = mod.type;
        this.enabled = true;
    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("id", this.id);
        save.addUnsafeString("name", this.name);
        save.addEnum("type", this.type);
        save.addBoolean("enabled", this.enabled);
    }

    public boolean matchesMod(LoadedMod mod) {
        return this.id.equals(mod.id) && this.type == mod.getSaveType();
    }

    public static List<ModListData> loadList(LoadData save) {
        ArrayList<ModListData> out = new ArrayList<ModListData>();
        for (LoadData listSave : save.getLoadData()) {
            try {
                out.add(new ModListData(listSave));
            }
            catch (LoadDataException e) {
                System.err.println("Could not load mod list component: " + e.getMessage());
            }
            catch (Exception e) {
                System.err.println("Could not load mod list component");
            }
        }
        return out;
    }

    public static SaveData getSaveList(List<ModListData> list) {
        SaveData out = new SaveData("");
        for (ModListData data : list) {
            SaveData listSave = new SaveData("");
            data.addSaveData(listSave);
            out.addSaveData(listSave);
        }
        return out;
    }

    public static enum legacy_loadLocation {
        DEVELOPMENT_FOLDER,
        MODS_FOLDER,
        STEAM_WORKSHOP;

    }
}

