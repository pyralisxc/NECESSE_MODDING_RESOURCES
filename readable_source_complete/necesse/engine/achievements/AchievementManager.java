/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.achievements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.zip.DataFormatException;
import necesse.engine.GlobalData;
import necesse.engine.achievements.Achievement;
import necesse.engine.achievements.AchievementProviderInterface;
import necesse.engine.achievements.BoolAchievement;
import necesse.engine.achievements.IntAchievement;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.team.PlayerTeam;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;

public class AchievementManager {
    private final ArrayList<Achievement> achievements = new ArrayList();
    public static HashSet<String> GET_PET_ITEMS = new HashSet();
    public static HashSet<String> ONE_TAPPED_MOBS = new HashSet<String>(Arrays.asList("zombie", "trapperzombie", "zombiearcher", "crawlingzombie", "swampzombie", "enchantedzombie", "enchantedzombiearcher", "enchantedcrawlingzombie"));
    public final BoolAchievement SPELUNKER = this.add(new BoolAchievement("spelunker", "spelunker", "spelunker_desc"));
    public final BoolAchievement GET_PET;
    public final BoolAchievement START_SETTLEMENT;
    public final BoolAchievement SET_SPAWN;
    public final BoolAchievement ENCHANT_ITEM;
    public final BoolAchievement MAGICAL_DROP;
    public final BoolAchievement VILLAGE_HELPER;
    public final BoolAchievement HOARDER;
    public final BoolAchievement SELF_PROCLAIMED;
    public final BoolAchievement DOUBLE_CATCH;
    public final BoolAchievement COMPLETE_HOST;
    public final BoolAchievement GETTING_HOT;
    public final BoolAchievement MY_JAM;
    public final BoolAchievement CLOUD_NINE;
    public final BoolAchievement ONE_TAPPED;
    public final BoolAchievement TOO_EASY;
    public final BoolAchievement HEADHUNTER;
    public final BoolAchievement REMATCH;
    public final BoolAchievement DEFEAT_PIRATE;
    public final BoolAchievement GRAVE_DIGGER;
    public final BoolAchievement EQUIP_ABILITY;
    public final BoolAchievement DEMOLITION_EXPERT;
    public final BoolAchievement COMPLETE_INCURSION;
    public final BoolAchievement MASTER_OF_SUN_AND_MOON;
    public final BoolAchievement TEAMWORK;
    public final BoolAchievement EMPOWERED;
    public final BoolAchievement FEELING_STYLISH;
    public final BoolAchievement SAFETY_LAST;
    public final BoolAchievement GET_4_ITEM_SETS;
    public final BoolAchievement HOT_TUB;
    public final BoolAchievement ADVENTURE_BEGINS;
    public final BoolAchievement DODGE_THIS;
    public final BoolAchievement SECRET_SERVICE;
    public final BoolAchievement HOME_ALONE;
    public final BoolAchievement CRYSTALLIZED;
    public final BoolAchievement YOU_AND_WHAT_ARMY;
    public final BoolAchievement ME_AND_THIS_ARMY;
    public final BoolAchievement HAVE_40_PERKS;
    public final BoolAchievement DEFEAT_ASCENDED;
    public final BoolAchievement OVERPOWERED;
    public final BoolAchievement SETTLING_DOWN;
    public final BoolAchievement EXPANSIONIST;
    public final IntAchievement DEFEAT_BOSS = this.add(new IntAchievement("defeat_boss", "defeat_boss", "defeat_boss_desc", stats.mob_kills::getBossKills, 0, 1, IntAchievement.DrawMode.BOOL));
    public final IntAchievement OBTAIN_ITEMS;
    public final IntAchievement VISIT_BIOMES;
    public final IntAchievement FISH_UP;
    public final IntAchievement RUN_MARATHON;
    public final IntAchievement PLAY_24H;

    public static String filePath() {
        return GlobalData.cfgPath() + "achievements";
    }

    public AchievementManager(PlayerStats stats) {
        this.GET_PET = this.add(new BoolAchievement("get_pet", "get_pet", "get_pet_desc"));
        this.START_SETTLEMENT = this.add(new BoolAchievement("start_settlement", "start_settlement", "start_settlement_desc"));
        this.SET_SPAWN = this.add(new BoolAchievement("set_spawn", "set_spawn", "set_spawn_desc"));
        this.ENCHANT_ITEM = this.add(new BoolAchievement("enchant_item", "enchant_item", "enchant_item_desc"));
        this.MAGICAL_DROP = this.add(new BoolAchievement("magical_drop", "magical_drop", "magical_drop_desc"));
        this.VILLAGE_HELPER = this.add(new BoolAchievement("village_helper", "village_helper", "village_helper_desc"));
        this.HOARDER = this.add(new BoolAchievement("hoarder", "hoarder", "hoarder_desc"));
        this.SELF_PROCLAIMED = this.add(new BoolAchievement("self_proclaimed", "self_proclaimed", "self_proclaimed_desc"));
        this.DOUBLE_CATCH = this.add(new BoolAchievement("double_catch", "double_catch", "double_catch_desc"));
        this.COMPLETE_HOST = this.add(new BoolAchievement("complete_host", "complete_host", "complete_host_desc"));
        this.GETTING_HOT = this.add(new BoolAchievement("getting_hot", "getting_hot", "getting_hot_desc"));
        this.MY_JAM = this.add(new BoolAchievement("my_jam", "my_jam", "my_jam_desc"));
        this.CLOUD_NINE = this.add(new BoolAchievement("cloud_nine", "cloud_nine", "cloud_nine_desc"));
        this.ONE_TAPPED = this.add(new BoolAchievement("one_tapped", "one_tapped", "one_tapped_desc"));
        this.TOO_EASY = this.add(new BoolAchievement("too_easy", "too_easy", "too_easy_desc"));
        this.HEADHUNTER = this.add(new BoolAchievement("headhunter", "headhunter", "headhunter_desc"));
        this.REMATCH = this.add(new BoolAchievement("rematch", "rematch", "rematch_desc"));
        this.DEFEAT_PIRATE = this.add(new BoolAchievement("defeat_pirate", "defeat_pirate", "defeat_pirate_desc"));
        this.GRAVE_DIGGER = this.add(new BoolAchievement("grave_digger", "grave_digger", "grave_digger_desc"));
        this.EQUIP_ABILITY = this.add(new BoolAchievement("equip_ability", "equip_ability", "equip_ability_desc"));
        this.DEMOLITION_EXPERT = this.add(new BoolAchievement("demolition_expert", "demolition_expert", "demolition_expert_desc"));
        this.COMPLETE_INCURSION = this.add(new BoolAchievement("complete_incursion", "complete_incursion", "complete_incursion_desc"));
        this.MASTER_OF_SUN_AND_MOON = this.add(new BoolAchievement("master_of_sun_and_moon", "master_of_sun_and_moon", "master_of_sun_and_moon_desc"));
        this.TEAMWORK = this.add(new BoolAchievement("teamwork", "teamwork", "teamwork_desc"));
        this.EMPOWERED = this.add(new BoolAchievement("empowered", "empowered", "empowered_desc"));
        this.FEELING_STYLISH = this.add(new BoolAchievement("feeling_stylish", "feeling_stylish", "feeling_stylish_desc"));
        this.SAFETY_LAST = this.add(new BoolAchievement("safety_last", "safety_last", "safety_last_desc"));
        this.GET_4_ITEM_SETS = this.add(new BoolAchievement("get_4_item_sets", "get_4_item_sets", "get_4_item_sets_desc"));
        this.HOT_TUB = this.add(new BoolAchievement("hot_tub", "hot_tub", "hot_tub_desc"));
        this.ADVENTURE_BEGINS = this.add(new BoolAchievement("adventure_begins", "adventure_begins", "adventure_begins_desc"));
        this.DODGE_THIS = this.add(new BoolAchievement("dodge_this", "dodge_this", "dodge_this_desc"));
        this.SECRET_SERVICE = this.add(new BoolAchievement("secret_service", "secret_service", "secret_service_desc"));
        this.HOME_ALONE = this.add(new BoolAchievement("home_alone", "home_alone", "home_alone_desc"));
        this.CRYSTALLIZED = this.add(new BoolAchievement("crystallized", "crystallized", "crystallized_desc"));
        this.YOU_AND_WHAT_ARMY = this.add(new BoolAchievement("you_and_what_army", "you_and_what_army", "you_and_what_army_new_desc"));
        this.ME_AND_THIS_ARMY = this.add(new BoolAchievement("me_and_this_army", "me_and_this_army", "me_and_this_army_desc"));
        this.HAVE_40_PERKS = this.add(new BoolAchievement("have_40_perks", "have_40_perks", "have_40_perks_desc"));
        this.DEFEAT_ASCENDED = this.add(new BoolAchievement("defeated_ascended", "defeated_ascended", "defeated_ascended_desc"));
        this.OVERPOWERED = this.add(new BoolAchievement("overpowered", "overpowered", "overpowered_desc"));
        this.SETTLING_DOWN = this.add(new BoolAchievement("settling_down", "settling_down", "settling_down_desc"));
        this.EXPANSIONIST = this.add(new BoolAchievement("expansionist", "expansionist", "expansionist_desc"));
        this.OBTAIN_ITEMS = this.add(new IntAchievement("obtain_items", "obtain_items", "obtain_items_desc", stats.items_obtained::getTotalStatItems, 0, ItemRegistry.getTotalStatItemsObtainable(), IntAchievement.DrawMode.NORMAL));
        this.VISIT_BIOMES = this.add(new IntAchievement("visit_biomes", "visit_biomes", "visit_biomes_desc", stats.biomes_visited::getTotalVisitedUniqueStatsBiomes, 0, BiomeRegistry.getTotalStatsBiomes(), IntAchievement.DrawMode.NORMAL));
        this.FISH_UP = this.add(new IntAchievement("fish_up", "fish_up", "fish_up_desc", stats.fish_caught::get, 0, 500, IntAchievement.DrawMode.NORMAL));
        this.RUN_MARATHON = this.add(new IntAchievement("run_marathon", "run_marathon", "run_marathon_desc", stats.distance_ran::get, 0, GameMath.metersToPixels(42195.0f), IntAchievement.DrawMode.PERCENT));
        this.PLAY_24H = this.add(new IntAchievement("play_24h", "timewellspent", "timewellspent_desc", stats.time_played::get, 0, 86400, IntAchievement.DrawMode.PERCENT));
    }

    public void runStatsUpdate(ServerClient client) {
        this.achievements.forEach(a -> a.runStatsUpdate(client));
    }

    public void setupContentPacket(PacketWriter writer) {
        for (Achievement achievement : this.achievements) {
            achievement.setupContentPacket(writer);
        }
    }

    public void applyContentPacket(PacketReader reader) {
        for (Achievement achievement : this.achievements) {
            achievement.applyContentPacket(reader);
        }
    }

    public int getCompleted() {
        return (int)this.achievements.stream().filter(Achievement::isCompleted).count();
    }

    public int getTotal() {
        return this.achievements.size();
    }

    private <T extends Achievement> T add(T achievement) {
        if (this.achievements.stream().anyMatch(a -> a.stringID.equals(achievement.stringID))) {
            throw new IllegalArgumentException("Achievement stringID " + achievement.stringID + " already taken.");
        }
        int index = this.achievements.size();
        this.achievements.add(achievement);
        achievement.setDataID(index);
        return achievement;
    }

    public Achievement getAchievement(int index) {
        return this.achievements.get(index);
    }

    public List<Achievement> getAchievements() {
        return this.achievements;
    }

    public void saveAchievementsFile() {
        try {
            SaveData data = new SaveData("");
            for (Achievement ac : this.achievements) {
                SaveData acData = new SaveData(ac.stringID);
                ac.addSaveData(acData);
                if (ac.isCompleted()) {
                    ac.addCompletedTimeSave(acData);
                }
                if (acData.isEmpty()) continue;
                data.addSaveData(acData);
            }
            data.saveScriptRaw(new File(AchievementManager.filePath()), true);
            if (GlobalData.isDevMode()) {
                data.saveScript(new File(AchievementManager.filePath() + "-debug"));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAchievementsFileSafe() {
        try {
            this.saveAchievementsFile();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAchievementsFile() {
        try {
            LoadData data = LoadData.newRaw(new File(AchievementManager.filePath()), true);
            File debugFile = new File(AchievementManager.filePath() + "-debug");
            if (GlobalData.isDevMode() && debugFile.exists()) {
                data = LoadData.newRaw(debugFile, false);
            }
            block5: for (LoadData acData : data.getLoadData()) {
                String name = acData.getName();
                for (Achievement ac : this.achievements) {
                    if (!ac.stringID.equals(name)) continue;
                    ac.applyLoadData(acData);
                    if (!ac.isCompleted()) continue block5;
                    ac.applyCompletedTimeSave(acData);
                    continue block5;
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("Couldn't find achievements file, does not exist. Creating new.");
            try {
                this.saveAchievementsFile();
            }
            catch (Exception e2) {
                System.err.println("Error saving achievement file: " + e2.getMessage());
                e2.printStackTrace();
            }
        }
        catch (IOException | DataFormatException e) {
            e.printStackTrace();
        }
    }

    public void loadTextures() {
        this.loadTextures(null);
    }

    public void loadTextures(AchievementManager oldManager) {
        if (oldManager == null) {
            this.achievements.forEach(a -> a.loadTextures(null));
        } else {
            for (int i = 0; i < this.achievements.size(); ++i) {
                this.achievements.get(i).loadTextures(oldManager.achievements.get(i));
            }
        }
    }

    public void loadFromPlatform(AchievementProviderInterface achievementProvider) {
        this.achievements.forEach(a -> a.loadFromPlatform(achievementProvider));
    }

    public static void checkMeAndThisArmyKill(Level level, HashSet<Attacker> attackers) {
        if (level.isServer() && level instanceof IncursionLevel) {
            boolean hasHuman = false;
            HashSet<ServerClient> ownerClients = new HashSet<ServerClient>();
            for (Attacker attacker : attackers) {
                Mob owner = attacker.getFirstAttackOwner();
                if (!(owner instanceof HumanMob)) continue;
                ServerClient serverClient = ((HumanMob)owner).adventureParty.getServerClient();
                if (serverClient != null) {
                    ownerClients.add(serverClient);
                }
                hasHuman = true;
            }
            if (hasHuman) {
                HashSet<ServerClient> allClients = new HashSet<ServerClient>();
                for (ServerClient client : ownerClients) {
                    allClients.add(client);
                    PlayerTeam team = client.getPlayerTeam();
                    if (team == null) continue;
                    team.streamOnlineMembers(client.getServer()).filter(c -> c.hasSpawned() && c.isSamePlace(client)).forEach(allClients::add);
                }
                for (ServerClient client : allClients) {
                    if (!client.achievementsLoaded()) continue;
                    client.achievements().ME_AND_THIS_ARMY.markCompleted(client);
                }
            }
        }
    }
}

