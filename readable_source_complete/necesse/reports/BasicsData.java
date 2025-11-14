/*
 * Decompiled with CFR 0.152.
 */
package necesse.reports;

import java.text.SimpleDateFormat;
import java.util.Date;
import necesse.engine.GameAuth;
import necesse.engine.GameInfo;
import necesse.engine.GameLaunch;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.platforms.PlatformManager;
import necesse.reports.RestData;

public class BasicsData
extends RestData {
    public BasicsData(String state) {
        this.data.put("generated_on", this.getString(() -> new SimpleDateFormat("yyyy-MM-dd HH'h'mm'm'ss's'").format(new Date())));
        this.data.put("authentication", this.getString(GameAuth::getAuthentication));
        this.data.put("platform_name", PlatformManager.getPlatform().getPlatformDebugString());
        this.data.put("platform_username", PlatformManager.getPlatform().getUserName());
        this.data.put("platform_build", this.getString(() -> PlatformManager.getPlatform().getPlatformAppBuild()));
        this.data.put("game_state", state);
        this.data.put("game_version", this.getString(GameInfo::getFullVersionString));
        this.data.put("game_language", this.getString(() -> Localization.getCurrentLang().stringID));
        this.data.put("launch_parameters", GameLaunch.fullLaunchParameters == null ? "" : GameLaunch.fullLaunchParameters);
        this.addList("total_loaded_mods", "loaded_mod", ModLoader::getEnabledMods, this.data, LoadedMod::getReportInfo);
        this.addList("total_found_mods", "found_mod", ModLoader::getNotEnabledMods, this.data, LoadedMod::getReportInfo);
    }
}

