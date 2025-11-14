/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamNativeHandle
 */
package necesse.engine.platforms.steam.modding;

import com.codedisaster.steamworks.SteamNativeHandle;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModNetworkData;
import necesse.engine.modLoader.ModProvider;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.platforms.Platform;
import necesse.engine.platforms.steam.modding.LoadedSteamMod;
import necesse.engine.platforms.steam.modding.SteamModProvider;

public class SteamModNetworkData
extends ModNetworkData {
    public final long workshopID;

    public SteamModNetworkData(LoadedSteamMod mod) {
        super(mod);
        this.workshopID = SteamNativeHandle.getNativeHandle((SteamNativeHandle)mod.workshopFileID);
    }

    public SteamModNetworkData(LoadedMod.SaveType type, PacketReader reader) {
        super(type, reader);
        this.workshopID = reader.getNextLong();
    }

    @Override
    public void write(PacketWriter writer) {
        super.write(writer);
        writer.putNextLong(this.workshopID);
    }

    @Override
    public ModProvider getModProvider() {
        for (ModProvider modProvider : Platform.getModProviders()) {
            if (!(modProvider instanceof SteamModProvider)) continue;
            return modProvider;
        }
        throw new RuntimeException("Could not find a ModProvider for mod of type " + (Object)((Object)this.type));
    }
}

