/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import necesse.engine.modLoader.DevModProvider;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModProvider;
import necesse.engine.modLoader.ModsFolderModProvider;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.platforms.Platform;
import necesse.engine.save.LoadDataException;

public class ModNetworkData {
    public final LoadedMod.SaveType type;
    public final String id;
    public final String name;
    public final String version;
    public final long steamWorkshopID;
    public final boolean clientside;

    public ModNetworkData(LoadedMod mod) {
        this.type = mod.getSaveType();
        this.id = mod.id;
        this.version = mod.version;
        this.name = mod.name;
        this.clientside = mod.clientside;
        this.steamWorkshopID = mod.getSteamWorkshopID();
    }

    public ModNetworkData(LoadedMod.SaveType type, PacketReader reader) {
        this.type = type;
        this.id = reader.getNextString();
        this.version = reader.getNextString();
        this.name = reader.getNextString();
        this.clientside = reader.getNextBoolean();
        this.steamWorkshopID = reader.getNextLong();
    }

    public static ModNetworkData fromPacketReader(PacketReader reader) {
        LoadedMod.SaveType type = reader.getNextEnum(LoadedMod.SaveType.class);
        ModNetworkData modNetworkData = Platform.getNetworkManager().tryGetModNetworkData(type, reader);
        if (modNetworkData == null) {
            throw new LoadDataException("Unknown mod type value: " + (Object)((Object)type));
        }
        return modNetworkData;
    }

    public void write(PacketWriter writer) {
        writer.putNextEnum(this.type);
        writer.putNextString(this.id);
        writer.putNextString(this.version);
        writer.putNextString(this.name);
        writer.putNextBoolean(this.clientside);
        writer.putNextLong(this.steamWorkshopID);
    }

    public ModProvider getModProvider() {
        for (ModProvider modProvider : Platform.getModProviders()) {
            if (this.type == LoadedMod.SaveType.FILE_MOD && modProvider instanceof ModsFolderModProvider) {
                return modProvider;
            }
            if (this.type != LoadedMod.SaveType.DEV_MOD || !(modProvider instanceof DevModProvider)) continue;
            return modProvider;
        }
        throw new RuntimeException("Could not find a ModProvider for mod of type " + (Object)((Object)this.type));
    }
}

