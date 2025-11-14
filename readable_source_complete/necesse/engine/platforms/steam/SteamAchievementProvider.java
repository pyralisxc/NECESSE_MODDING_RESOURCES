/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codedisaster.steamworks.SteamAchievementUnlocked
 *  com.codedisaster.steamworks.SteamUserStats
 */
package necesse.engine.platforms.steam;

import com.codedisaster.steamworks.SteamAchievementUnlocked;
import com.codedisaster.steamworks.SteamUserStats;
import java.util.HashMap;
import necesse.engine.achievements.AchievementProviderInterface;

public class SteamAchievementProvider
implements AchievementProviderInterface {
    public final AchievementProviderInterface.AchievementState spelunker;
    public final AchievementProviderInterface.AchievementState defeat_boss;
    public final AchievementProviderInterface.AchievementState get_pet;
    public final AchievementProviderInterface.AchievementState start_settlement;
    public final AchievementProviderInterface.AchievementState set_spawn;
    public final AchievementProviderInterface.AchievementState enchant_item;
    public final AchievementProviderInterface.AchievementState obtain_items;
    public final AchievementProviderInterface.AchievementState visit_biomes;
    public final AchievementProviderInterface.AchievementState fish_up;
    public final AchievementProviderInterface.AchievementState run_marathon;
    public final AchievementProviderInterface.AchievementState play_24h;
    public final AchievementProviderInterface.AchievementState magical_drop;
    public final AchievementProviderInterface.AchievementState village_helper;
    public final AchievementProviderInterface.AchievementState hoarder;
    public final AchievementProviderInterface.AchievementState self_proclaimed;
    public final AchievementProviderInterface.AchievementState double_catch;
    public final AchievementProviderInterface.AchievementState complete_host;
    public final AchievementProviderInterface.AchievementState getting_hot;
    public final AchievementProviderInterface.AchievementState my_jam;
    public final AchievementProviderInterface.AchievementState cloud_nine;
    public final AchievementProviderInterface.AchievementState one_tapped;
    public final AchievementProviderInterface.AchievementState too_easy;
    public final AchievementProviderInterface.AchievementState headhunter;
    public final AchievementProviderInterface.AchievementState rematch;
    public final AchievementProviderInterface.AchievementState defeat_pirate;
    public final AchievementProviderInterface.AchievementState grave_digger;
    public final AchievementProviderInterface.AchievementState equip_ability;
    public final AchievementProviderInterface.AchievementState demolition_expert;
    public final AchievementProviderInterface.AchievementState complete_incursion;
    public final AchievementProviderInterface.AchievementState master_of_sun_and_moon;
    public final AchievementProviderInterface.AchievementState teamwork;
    public final AchievementProviderInterface.AchievementState empowered;
    public final AchievementProviderInterface.AchievementState feeling_stylish;
    public final AchievementProviderInterface.AchievementState safety_last;
    public final AchievementProviderInterface.AchievementState get_4_item_sets;
    public final AchievementProviderInterface.AchievementState hot_tub;
    public final AchievementProviderInterface.AchievementState adventure_begins;
    public final AchievementProviderInterface.AchievementState dodge_this;
    public final AchievementProviderInterface.AchievementState secret_service;
    public final AchievementProviderInterface.AchievementState home_alone;
    public final AchievementProviderInterface.AchievementState crystallized;
    public final AchievementProviderInterface.AchievementState you_and_what_army;
    public final AchievementProviderInterface.AchievementState me_and_this_army;
    private final HashMap<String, AchievementProviderInterface.AchievementState> loadedAchievements = new HashMap();

    public SteamAchievementProvider(SteamUserStats stats) {
        this.spelunker = this.getAchieved(stats, "spelunker");
        this.defeat_boss = this.getAchieved(stats, "defeat_boss");
        this.get_pet = this.getAchieved(stats, "get_pet");
        this.start_settlement = this.getAchieved(stats, "start_settlement");
        this.set_spawn = this.getAchieved(stats, "set_spawn");
        this.enchant_item = this.getAchieved(stats, "enchant_item");
        this.obtain_items = this.getAchieved(stats, "obtain_items");
        this.visit_biomes = this.getAchieved(stats, "visit_biomes");
        this.fish_up = this.getAchieved(stats, "fish_up");
        this.run_marathon = this.getAchieved(stats, "run_marathon");
        this.play_24h = this.getAchieved(stats, "play_24h");
        this.magical_drop = this.getAchieved(stats, "magical_drop");
        this.village_helper = this.getAchieved(stats, "village_helper");
        this.hoarder = this.getAchieved(stats, "hoarder");
        this.self_proclaimed = this.getAchieved(stats, "self_proclaimed");
        this.double_catch = this.getAchieved(stats, "double_catch");
        this.complete_host = this.getAchieved(stats, "complete_host");
        this.getting_hot = this.getAchieved(stats, "getting_hot");
        this.my_jam = this.getAchieved(stats, "my_jam");
        this.cloud_nine = this.getAchieved(stats, "cloud_nine");
        this.one_tapped = this.getAchieved(stats, "one_tapped");
        this.too_easy = this.getAchieved(stats, "too_easy");
        this.headhunter = this.getAchieved(stats, "headhunter");
        this.rematch = this.getAchieved(stats, "rematch");
        this.defeat_pirate = this.getAchieved(stats, "defeat_pirate");
        this.grave_digger = this.getAchieved(stats, "grave_digger");
        this.equip_ability = this.getAchieved(stats, "equip_ability");
        this.demolition_expert = this.getAchieved(stats, "demolition_expert");
        this.complete_incursion = this.getAchieved(stats, "complete_incursion");
        this.master_of_sun_and_moon = this.getAchieved(stats, "master_of_sun_and_moon");
        this.teamwork = this.getAchieved(stats, "teamwork");
        this.empowered = this.getAchieved(stats, "empowered");
        this.feeling_stylish = this.getAchieved(stats, "feeling_stylish");
        this.safety_last = this.getAchieved(stats, "safety_last");
        this.get_4_item_sets = this.getAchieved(stats, "get_4_item_sets");
        this.hot_tub = this.getAchieved(stats, "hot_tub");
        this.adventure_begins = this.getAchieved(stats, "adventure_begins");
        this.dodge_this = this.getAchieved(stats, "dodge_this");
        this.secret_service = this.getAchieved(stats, "secret_service");
        this.home_alone = this.getAchieved(stats, "home_alone");
        this.crystallized = this.getAchieved(stats, "crystallized");
        this.you_and_what_army = this.getAchieved(stats, "you_and_what_army");
        this.me_and_this_army = this.getAchieved(stats, "me_and_this_army");
    }

    private AchievementProviderInterface.AchievementState getAchieved(SteamUserStats stats, String apiName) {
        SteamAchievementUnlocked value = stats.getAchievementAndUnlockTime(apiName);
        AchievementProviderInterface.AchievementState achievementState = new AchievementProviderInterface.AchievementState(value.unlocked, value.unlockTime);
        this.loadedAchievements.put(apiName, achievementState);
        return achievementState;
    }

    @Override
    public AchievementProviderInterface.AchievementState getAchievementState(String achievementID) {
        return this.loadedAchievements.get(achievementID);
    }
}

