/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamApps
 */
package necesse.engine.platforms.steam.dlc;

import com.codedisaster.steamworks.SteamApps;
import necesse.engine.dlc.DLC;
import necesse.engine.dlc.DLCProvider;
import necesse.engine.platforms.steam.SteamData;

public class SteamDLCRegistry
extends DLCProvider {
    @Override
    protected DLCProvider.DLCStatus checkDLCStatus(DLC dlc) {
        SteamApps apps = SteamData.getApps();
        apps.installDLC(dlc.steamAppID);
        if (apps.isDlcInstalled(dlc.steamAppID)) {
            return DLCProvider.DLCStatus.INSTALLED;
        }
        return DLCProvider.DLCStatus.NOT_OWNED;
    }
}

