/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats;

import java.util.HashMap;
import necesse.engine.playerStats.StatsProvider;

public class GameStats {
    private final HashMap<String, Integer> loadedStats = new HashMap();
    public final int time_played;
    public final int distance_ran;
    public final int distance_ridden;
    public final int deaths;
    public final int damage_dealt;
    public final int normal_damage_dealt;
    public final int true_damage_dealt;
    public final int melee_damage_dealt;
    public final int ranged_damage_dealt;
    public final int magic_damage_dealt;
    public final int summon_damage_dealt;
    public final int damage_taken;
    public final int island_travels;
    public final int islands_discovered;
    public final int islands_visited;
    public final int objects_mined;
    public final int objects_placed;
    public final int tiles_mined;
    public final int tiles_placed;
    public final int food_consumed;
    public final int potions_consumed;
    public final int fish_caught;
    public final int quests_completed;
    public final int money_earned;
    public final int items_sold;
    public final int money_spent;
    public final int items_bought;
    public final int items_enchanted;
    public final int items_upgraded;
    public final int items_salvaged;
    public final int ladders_used;
    public final int doors_used;
    public final int plates_triggered;
    public final int levers_flicked;
    public final int crafted_items;
    public final int crates_broken;
    public final int items_obtained;
    public final int homestones_used;
    public final int waystones_used;
    public final int mob_kills;
    public final int boss_kills;
    public final int opened_incursions;
    public final int completed_incursions;

    public GameStats(StatsProvider statsProvider) {
        this.time_played = this.getStat(statsProvider, "time_played");
        this.distance_ran = this.getStat(statsProvider, "distance_ran");
        this.distance_ridden = this.getStat(statsProvider, "distance_ridden");
        this.deaths = this.getStat(statsProvider, "deaths");
        this.damage_dealt = this.getStat(statsProvider, "damage_dealt");
        this.normal_damage_dealt = this.getStat(statsProvider, "normal_damage_dealt");
        this.true_damage_dealt = this.getStat(statsProvider, "true_damage_dealt");
        this.melee_damage_dealt = this.getStat(statsProvider, "melee_damage_dealt");
        this.ranged_damage_dealt = this.getStat(statsProvider, "ranged_damage_dealt");
        this.magic_damage_dealt = this.getStat(statsProvider, "magic_damage_dealt");
        this.summon_damage_dealt = this.getStat(statsProvider, "summon_damage_dealt");
        this.damage_taken = this.getStat(statsProvider, "damage_taken");
        this.island_travels = this.getStat(statsProvider, "island_travels");
        this.islands_discovered = this.getStat(statsProvider, "islands_discovered");
        this.islands_visited = this.getStat(statsProvider, "islands_visited");
        this.objects_mined = this.getStat(statsProvider, "objects_mined");
        this.objects_placed = this.getStat(statsProvider, "objects_placed");
        this.tiles_mined = this.getStat(statsProvider, "tiles_mined");
        this.tiles_placed = this.getStat(statsProvider, "tiles_placed");
        this.food_consumed = this.getStat(statsProvider, "food_consumed");
        this.potions_consumed = this.getStat(statsProvider, "potions_consumed");
        this.fish_caught = this.getStat(statsProvider, "fish_caught");
        this.quests_completed = this.getStat(statsProvider, "quests_completed");
        this.money_earned = this.getStat(statsProvider, "money_earned");
        this.items_sold = this.getStat(statsProvider, "items_sold");
        this.money_spent = this.getStat(statsProvider, "money_spent");
        this.items_bought = this.getStat(statsProvider, "items_bought");
        this.items_enchanted = this.getStat(statsProvider, "items_enchanted");
        this.items_upgraded = this.getStat(statsProvider, "items_upgraded");
        this.items_salvaged = this.getStat(statsProvider, "items_salvaged");
        this.ladders_used = this.getStat(statsProvider, "ladders_used");
        this.doors_used = this.getStat(statsProvider, "doors_used");
        this.plates_triggered = this.getStat(statsProvider, "plates_triggered");
        this.levers_flicked = this.getStat(statsProvider, "levers_flicked");
        this.crafted_items = this.getStat(statsProvider, "crafted_items");
        this.crates_broken = this.getStat(statsProvider, "crates_broken");
        this.items_obtained = this.getStat(statsProvider, "items_obtained");
        this.homestones_used = this.getStat(statsProvider, "homestones_used");
        this.waystones_used = this.getStat(statsProvider, "waystones_used");
        this.mob_kills = this.getStat(statsProvider, "mob_kills");
        this.boss_kills = this.getStat(statsProvider, "boss_kills");
        this.opened_incursions = this.getStat(statsProvider, "opened_incursions");
        this.completed_incursions = this.getStat(statsProvider, "completed_incursions");
    }

    private int getStat(StatsProvider statProvider, String apiName) {
        int value = statProvider.getStat(apiName, 0);
        this.loadedStats.put(apiName, value);
        return value;
    }

    public int getStatByName(String apiName, int defaultValue) {
        return this.loadedStats.getOrDefault(apiName, defaultValue);
    }
}

