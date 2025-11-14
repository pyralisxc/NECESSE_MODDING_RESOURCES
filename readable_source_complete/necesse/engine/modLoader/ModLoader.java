/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.agent.ByteBuddyAgent
 */
package necesse.engine.modLoader;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.GameCrashLog;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModClassSignature;
import necesse.engine.modLoader.ModListData;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.ModLoadLocation;
import necesse.engine.modLoader.ModNextListData;
import necesse.engine.modLoader.ModProvider;
import necesse.engine.modLoader.ModSaveInfo;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketConnectRequest;
import necesse.engine.network.packet.PacketPing;
import necesse.engine.network.packet.PacketServerStatus;
import necesse.engine.network.packet.PacketServerStatusRequest;
import necesse.engine.platforms.Platform;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.WorldSave;
import necesse.engine.util.ComputedValue;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.WorldSettings;
import necesse.gfx.forms.presets.FeedbackForm;
import necesse.reports.BasicsData;
import necesse.reports.CrashJFrame;
import necesse.reports.CrashReportData;
import necesse.reports.GeneralModdingCrashJFrame;
import necesse.reports.ModCrashJFrame;
import necesse.reports.ReportUtils;
import net.bytebuddy.agent.ByteBuddyAgent;

public class ModLoader {
    private static final HashSet<ModClassSignature> specificIllegalModPatches = new HashSet();
    private static final ArrayList<ModClassSignature> abstractIllegalModPatches = new ArrayList();
    public static boolean disableMods;
    private static List<LoadedMod> enabledMods;
    private static List<LoadedMod> notEnabledMods;
    private static int modsHash;
    private static boolean hasLoadedMods;
    private static List<LoadedMod> allMods;
    public static List<ModListData> currentList;
    private static boolean loadedMods;
    public static final HashMap<Class<? extends Annotation>, HashSet<LoadedMod>> modErrors;

    public static boolean isIllegalModPatch(ModClassSignature signature) {
        if (specificIllegalModPatches.contains(signature)) {
            return true;
        }
        for (ModClassSignature patch : abstractIllegalModPatches) {
            if (!patch.matches(signature)) continue;
            return true;
        }
        return false;
    }

    private static String modsListFile() {
        return GlobalData.appDataPath() + "mods/modlist.data";
    }

    public static void loadMods(boolean isServer) throws ModLoadException {
        if (loadedMods) {
            throw new IllegalStateException("Can only load mods once");
        }
        loadedMods = true;
        enabledMods = new ArrayList<LoadedMod>();
        notEnabledMods = new ArrayList<LoadedMod>();
        allMods = new ArrayList<LoadedMod>();
        ArrayList<ModLoadLocation> modLoadLocations = new ArrayList<ModLoadLocation>();
        ArrayList<ModProvider> modProviders = Platform.getModProviders();
        for (ModProvider modProvider : modProviders) {
            modLoadLocations.addAll(modProvider.locateMods(isServer));
        }
        for (ModLoadLocation modLoadLocation : modLoadLocations) {
            LoadedMod mod = modLoadLocation.modProvider.loadMod(modLoadLocation);
            if (mod == null) continue;
            ModLoader.loadMod(mod);
        }
        if (!allMods.isEmpty()) {
            GameLoadingScreen.addLog(Localization.translate("loading", "foundmods", "mods", (Object)allMods.size()));
            if (disableMods) {
                System.out.println("Disabling all mods because of launch option: -disablemods");
                for (LoadedMod mod : allMods) {
                    mod.listData.enabled = false;
                }
            } else {
                File file = new File(ModLoader.modsListFile());
                if (file.exists()) {
                    ArrayList<LoadedMod> modsToAdd = new ArrayList<LoadedMod>(allMods);
                    allMods = new ArrayList<LoadedMod>();
                    try {
                        LoadData save = new LoadData(file);
                        currentList = ModListData.loadList(save);
                        block5: for (ModListData data : currentList) {
                            for (int i = 0; i < modsToAdd.size(); ++i) {
                                LoadedMod modToAdd = (LoadedMod)modsToAdd.get(i);
                                if (!data.matchesMod(modToAdd)) continue;
                                allMods.add(modToAdd);
                                modToAdd.listData.enabled = data.enabled;
                                modsToAdd.remove(i);
                                continue block5;
                            }
                        }
                    }
                    catch (Exception e2) {
                        System.err.println("Could not load mod list file");
                        e2.printStackTrace();
                    }
                    for (LoadedMod mod : modsToAdd) {
                        ModLoader.addSortedMod(allMods, mod);
                    }
                }
                HashSet<String> enabledModIDs = new HashSet<String>();
                for (LoadedMod mod : allMods) {
                    if (mod.isEnabled()) {
                        if (enabledModIDs.contains(mod.id)) {
                            mod.listData.enabled = false;
                            GameLog.warn.println("Disabled duplicate mod " + mod.id + " from " + mod.loadLocation.modProvider.getClass().getSimpleName() + " as one is already enabled");
                            continue;
                        }
                        enabledModIDs.add(mod.id);
                        enabledMods.add(mod);
                        continue;
                    }
                    notEnabledMods.add(mod);
                }
            }
            ModLoader.saveModListSettings(allMods.stream().map(e -> e.listData).collect(Collectors.toList()));
            GameLoadingScreen.addLog(Localization.translate("loading", "loadedmods", "mods", (Object)enabledMods.size()));
        }
        enabledMods = Collections.unmodifiableList(enabledMods);
        notEnabledMods = Collections.unmodifiableList(notEnabledMods);
        allMods = Collections.unmodifiableList(allMods);
        ModLoader.generateModsHash();
        if (!ModLoader.getEnabledMods().isEmpty()) {
            ComputedValue<Instrumentation> instrumentation = new ComputedValue<Instrumentation>(ByteBuddyAgent::install);
            GameLoadingScreen.drawLoadingString(Localization.translate("loading", "loadingmods"));
            for (LoadedMod mod : ModLoader.getEnabledMods()) {
                mod.loadClasses(instrumentation);
            }
            for (LoadedMod mod : ModLoader.getEnabledMods()) {
                mod.initSettings();
            }
            GameLoadingScreen.drawLoadingString(Localization.translate("loading", "patchmods"));
            for (LoadedMod mod : ModLoader.getEnabledMods()) {
                mod.applyPatches(instrumentation);
            }
        }
        hasLoadedMods = true;
    }

    private static void loadMod(LoadedMod mod) {
        if (allMods.stream().anyMatch(m -> m.id.equals(mod.id))) {
            GameLog.warn.println("Could not add mod " + mod.id + " from " + mod.loadLocation + " because another one with the same ID already exists");
            return;
        }
        System.out.println("Found mod: " + mod.name + " (" + mod.id + ", " + mod.version + ") from " + mod.loadLocation.modProvider.getClass().getSimpleName());
        ModLoader.addSortedMod(allMods, mod);
    }

    public static void addSortedMod(List<LoadedMod> list, LoadedMod mod) {
        int minIndex = list.size();
        for (int i = list.size() - 1; i >= 0; --i) {
            if (!list.get(i).dependsOn(mod) && !list.get(i).optionalDependsOn(mod)) continue;
            minIndex = i;
        }
        list.add(minIndex, mod);
    }

    public static void addSortedNextMod(List<ModNextListData> list, LoadedMod mod) {
        int minIndex = list.size();
        for (int i = list.size() - 1; i >= 0; --i) {
            if (!list.get((int)i).mod.dependsOn(mod) && !list.get((int)i).mod.optionalDependsOn(mod)) continue;
            minIndex = i;
        }
        list.add(minIndex, new ModNextListData(mod, true));
    }

    public static List<LoadedMod> getEnabledMods() {
        return enabledMods;
    }

    public static List<LoadedMod> getNotEnabledMods() {
        return notEnabledMods;
    }

    public static List<LoadedMod> getAllMods() {
        return allMods;
    }

    public static List<ModNextListData> getAllModsSortedByCurrentList() {
        ArrayList<ModNextListData> mods = new ArrayList<ModNextListData>(allMods.size());
        ArrayList<LoadedMod> modsToAdd = new ArrayList<LoadedMod>(allMods);
        block0: for (ModListData data : currentList) {
            for (int i = 0; i < modsToAdd.size(); ++i) {
                LoadedMod modToAdd = (LoadedMod)modsToAdd.get(i);
                if (!data.matchesMod(modToAdd)) continue;
                mods.add(new ModNextListData(modToAdd, data.enabled));
                modsToAdd.remove(i);
                continue block0;
            }
        }
        for (LoadedMod mod : modsToAdd) {
            ModLoader.addSortedNextMod(mods, mod);
        }
        return mods;
    }

    public static ArrayList<LoadedMod> getResponsibleMods(Iterable<? extends Throwable> errors, boolean markMod) {
        ArrayList<LoadedMod> responsibleMods = new ArrayList<LoadedMod>();
        for (LoadedMod mod : ModLoader.getEnabledMods()) {
            for (Throwable throwable : errors) {
                if (!mod.isResponsibleForError(throwable)) continue;
                if (markMod) {
                    mod.runError = true;
                }
                responsibleMods.add(mod);
            }
        }
        return responsibleMods;
    }

    private static void generateModsHash() {
        modsHash = 0;
        for (LoadedMod mod : ModLoader.getEnabledMods()) {
            if (mod.clientside) continue;
            modsHash = modsHash * 37 + mod.id.hashCode();
            modsHash = modsHash * 13 + mod.version.hashCode();
        }
    }

    public static int getModsHash() {
        return modsHash;
    }

    public static boolean hasLoadedMods() {
        return hasLoadedMods;
    }

    public static String getModName(String modID) {
        LoadedMod mod = ModLoader.getAllMods().stream().filter(m -> m.id.equals(modID)).findAny().orElse(null);
        return mod == null ? modID : mod.name;
    }

    public static void saveModListSettings(List<ModListData> list) {
        File file = new File(ModLoader.modsListFile());
        SaveData save = ModListData.getSaveList(list);
        save.saveScript(file);
        currentList = list;
    }

    public static boolean matchesCurrentMods(WorldSave worldSave) {
        WorldEntity worldEntity = worldSave.getWorldEntity();
        if (worldEntity == null) {
            return true;
        }
        if (worldEntity.lastMods == null) {
            return true;
        }
        return ModLoader.matchesCurrentMods(worldEntity.lastMods);
    }

    public static boolean matchesCurrentMods(List<ModSaveInfo> list) {
        List<LoadedMod> myMods = ModLoader.getEnabledMods();
        if (list.size() != myMods.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); ++i) {
            ModSaveInfo listMod = list.get(i);
            LoadedMod myMod = myMods.get(i);
            if (!listMod.id.equals(myMod.id)) {
                return false;
            }
            if (listMod.version.equals(myMod.version)) continue;
            return false;
        }
        return true;
    }

    static {
        ArrayList<ModClassSignature> illegalModPatches = new ArrayList<ModClassSignature>();
        illegalModPatches.add(ModClassSignature.method(WorldSettings.class, "setupBeforeConnectedPacket", PacketWriter.class));
        illegalModPatches.add(ModClassSignature.method(WorldSettings.class, "applyBeforeConnectedPacket", PacketReader.class));
        illegalModPatches.add(ModClassSignature.method(LoadedMod.class, "isResponsibleForError", Throwable.class));
        illegalModPatches.add(ModClassSignature.method(LoadedMod.class, "hasEdited", StackTraceElement.class));
        illegalModPatches.add(ModClassSignature.method(ModLoader.class, "getResponsibleMods", Iterable.class, Boolean.TYPE));
        illegalModPatches.add(ModClassSignature.allInClass(PacketServerStatus.class));
        illegalModPatches.add(ModClassSignature.allInClass(PacketServerStatusRequest.class));
        illegalModPatches.add(ModClassSignature.allInClass(PacketPing.class));
        illegalModPatches.add(ModClassSignature.allInClass(PacketConnectRequest.class));
        illegalModPatches.add(ModClassSignature.allInClass(ReportUtils.class));
        illegalModPatches.add(ModClassSignature.allInClass(BasicsData.class));
        illegalModPatches.add(ModClassSignature.allInClass(CrashReportData.class));
        illegalModPatches.add(ModClassSignature.allInClass(GameCrashLog.class));
        illegalModPatches.add(ModClassSignature.allInClass(ModCrashJFrame.class));
        illegalModPatches.add(ModClassSignature.allInClass(GeneralModdingCrashJFrame.class));
        illegalModPatches.add(ModClassSignature.allInClass(CrashJFrame.class));
        illegalModPatches.add(ModClassSignature.allInClass(FeedbackForm.class));
        for (ModClassSignature signature : illegalModPatches) {
            if (signature.targetClass == null || signature.targetMethod == null || signature.arguments == null) {
                abstractIllegalModPatches.add(signature);
                continue;
            }
            specificIllegalModPatches.add(signature);
        }
        enabledMods = new ArrayList<LoadedMod>();
        notEnabledMods = new ArrayList<LoadedMod>();
        allMods = new ArrayList<LoadedMod>();
        currentList = new ArrayList<ModListData>();
        loadedMods = false;
        modErrors = new HashMap();
    }
}

