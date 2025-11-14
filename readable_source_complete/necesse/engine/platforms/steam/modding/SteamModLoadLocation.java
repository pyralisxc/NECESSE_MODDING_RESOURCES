/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamPublishedFileID
 */
package necesse.engine.platforms.steam.modding;

import com.codedisaster.steamworks.SteamPublishedFileID;
import java.io.File;
import necesse.engine.modLoader.ModLoadLocation;
import necesse.engine.platforms.steam.modding.SteamModProvider;

public class SteamModLoadLocation
extends ModLoadLocation {
    public final SteamPublishedFileID workshopFileID;

    public SteamModLoadLocation(SteamModProvider modProvider, File path, SteamPublishedFileID workshopFileID) {
        super(modProvider, path);
        this.workshopFileID = workshopFileID;
    }
}

