/*
 * Decompiled with CFR 0.152.
 */
package necesse.reports;

import necesse.engine.GameAuth;
import necesse.engine.GameInfo;
import necesse.engine.GameSystemInfo;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoader;
import necesse.engine.platforms.PlatformManager;
import necesse.reports.ReportUtils;
import necesse.reports.RestData;

public class SessionData
extends RestData {
    public SessionData(String state, long loadingTime) {
        this.data.put("authentication", this.getString(GameAuth::getAuthentication));
        this.data.put("platform_name", PlatformManager.getPlatform().getPlatformDebugString());
        this.data.put("platform_build", this.getString(() -> PlatformManager.getPlatform().getPlatformAppBuild()));
        this.data.put("session_id", this.getString(ReportUtils::getSessionID));
        this.data.put("session_state", state);
        if (loadingTime != 0L) {
            this.data.put("loading_time", this.getString(() -> loadingTime));
        }
        this.data.put("system_total_memory", this.getString(GameSystemInfo::getTotalMemory, (Throwable e) -> 0L));
        this.data.put("system_used_memory", this.getString(GameSystemInfo::getUsedMemory, (Throwable e) -> 0L));
        this.data.put("jvm_max_memory", this.getString(() -> Runtime.getRuntime().maxMemory(), (Throwable e) -> 0L));
        this.data.put("jvm_total_memory", this.getString(() -> Runtime.getRuntime().totalMemory(), (Throwable e) -> 0L));
        this.data.put("jvm_used_memory", this.getString(() -> Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(), (Throwable e) -> 0L));
        this.data.put("game_version", this.getString(GameInfo::getFullVersionString));
        this.data.put("game_language", this.getString(() -> Localization.getCurrentLang().stringID));
        this.addList("total_loaded_mods", "loaded_mod", ModLoader::getEnabledMods, this.data, LoadedMod::getReportInfo);
        this.addList("total_found_mods", "found_mod", ModLoader::getNotEnabledMods, this.data, LoadedMod::getReportInfo);
    }
}

