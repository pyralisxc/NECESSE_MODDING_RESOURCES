/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DataFormatException;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.ClientGameLoop;
import necesse.engine.loading.ClientLoader;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.stats.BiomesVisitedStat;
import necesse.engine.playerStats.stats.ChallengesClaimedStat;
import necesse.engine.playerStats.stats.ChallengesCompletedStat;
import necesse.engine.playerStats.stats.DamageTypesStat;
import necesse.engine.playerStats.stats.GNDItemMapStat;
import necesse.engine.playerStats.stats.IncIntGameStat;
import necesse.engine.playerStats.stats.IncursionBiomeGameStat;
import necesse.engine.playerStats.stats.ItemCountStat;
import necesse.engine.playerStats.stats.ItemsObtainedStat;
import necesse.engine.playerStats.stats.JournalEntriesDiscovered;
import necesse.engine.playerStats.stats.MobKillsStat;
import necesse.engine.playerStats.stats.SetBonusesWornStat;
import necesse.engine.playerStats.stats.TrinketsWornStat;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.gameFont.FontManager;

public class PlayerStats
extends EmptyStats {
    public final IncIntGameStat time_played = this.addStat(new IncIntGameStat(this, "time_played", false, true));
    public final IncIntGameStat distance_ran = this.addStat(new IncIntGameStat(this, "distance_ran", false, true));
    public final IncIntGameStat distance_ridden = this.addStat(new IncIntGameStat(this, "distance_ridden", false, true));
    public final IncIntGameStat deaths;
    public final IncIntGameStat damage_dealt = this.addStat(new IncIntGameStat(this, "damage_dealt", false, true));
    public final IncIntGameStat damage_taken = this.addStat(new IncIntGameStat(this, "damage_taken", false, true));
    public final IncIntGameStat objects_mined;
    public final IncIntGameStat objects_placed;
    public final IncIntGameStat tiles_mined;
    public final IncIntGameStat tiles_placed;
    public final IncIntGameStat fish_caught;
    public final IncIntGameStat quests_completed;
    public final IncIntGameStat money_earned;
    public final IncIntGameStat items_sold;
    public final IncIntGameStat money_spent;
    public final IncIntGameStat items_bought;
    public final IncIntGameStat items_enchanted;
    public final IncIntGameStat items_upgraded;
    public final IncIntGameStat items_salvaged;
    public final IncIntGameStat ladders_used;
    public final IncIntGameStat doors_used;
    public final IncIntGameStat plates_triggered;
    public final IncIntGameStat levers_flicked;
    public final IncIntGameStat homestones_used;
    public final IncIntGameStat waystones_used;
    public final IncIntGameStat crafted_items;
    public final IncIntGameStat crates_broken;
    @Deprecated
    public final IncIntGameStat island_travels;
    public final DamageTypesStat type_damage_dealt;
    public final SetBonusesWornStat set_bonuses_worn;
    public final TrinketsWornStat trinkets_worn;
    public final BiomesVisitedStat biomes_visited;
    public final JournalEntriesDiscovered discovered_journal_entries;
    public final ItemsObtainedStat items_obtained;
    public final MobKillsStat mob_kills;
    public final ItemCountStat food_consumed;
    public final ItemCountStat potions_consumed;
    public final IncursionBiomeGameStat opened_incursions;
    public final IncursionBiomeGameStat completed_incursions;
    public final ChallengesCompletedStat challenges_completed;
    public final ChallengesClaimedStat challenges_claimed;
    public final GNDItemMapStat challenges_data;

    public static String filePath() {
        return GlobalData.cfgPath() + "stats";
    }

    public PlayerStats(boolean controlAchievements, EmptyStats.Mode mode) {
        super(controlAchievements, mode);
        this.deaths = this.addStat(new IncIntGameStat(this, "deaths", true, true));
        this.island_travels = this.addStat(new IncIntGameStat(this, "island_travels", true, true));
        this.objects_mined = this.addStat(new IncIntGameStat(this, "objects_mined", true, true));
        this.objects_placed = this.addStat(new IncIntGameStat(this, "objects_placed", true, true));
        this.tiles_mined = this.addStat(new IncIntGameStat(this, "tiles_mined", true, true));
        this.tiles_placed = this.addStat(new IncIntGameStat(this, "tiles_placed", true, true));
        this.fish_caught = this.addStat(new IncIntGameStat(this, "fish_caught", true, true));
        this.quests_completed = this.addStat(new IncIntGameStat(this, "quests_completed", true, true));
        this.money_earned = this.addStat(new IncIntGameStat(this, "money_earned", true, true));
        this.items_sold = this.addStat(new IncIntGameStat(this, "items_sold", true, true));
        this.money_spent = this.addStat(new IncIntGameStat(this, "money_spent", true, true));
        this.items_bought = this.addStat(new IncIntGameStat(this, "items_bought", true, true));
        this.items_enchanted = this.addStat(new IncIntGameStat(this, "items_enchanted", true, true));
        this.items_upgraded = this.addStat(new IncIntGameStat(this, "items_upgraded", true, true));
        this.items_salvaged = this.addStat(new IncIntGameStat(this, "items_salvaged", true, true));
        this.ladders_used = this.addStat(new IncIntGameStat(this, "ladders_used", true, true));
        this.doors_used = this.addStat(new IncIntGameStat(this, "doors_used", true, true));
        this.plates_triggered = this.addStat(new IncIntGameStat(this, "plates_triggered", true, true));
        this.levers_flicked = this.addStat(new IncIntGameStat(this, "levers_flicked", true, true));
        this.homestones_used = this.addStat(new IncIntGameStat(this, "homestones_used", true, true));
        this.waystones_used = this.addStat(new IncIntGameStat(this, "waystones_used", true, true));
        this.crafted_items = this.addStat(new IncIntGameStat(this, "crafted_items", true, true));
        this.crates_broken = this.addStat(new IncIntGameStat(this, "crates_broken", true, true));
        this.type_damage_dealt = this.addStat(new DamageTypesStat(this, "type_damage_dealt"));
        this.set_bonuses_worn = this.addStat(new SetBonusesWornStat(this, "set_bonuses_worn"));
        this.trinkets_worn = this.addStat(new TrinketsWornStat(this, "trinkets_worn"));
        this.biomes_visited = this.addStat(new BiomesVisitedStat(this, "biomes_known"));
        this.discovered_journal_entries = this.addStat(new JournalEntriesDiscovered(this, "discovered_journal_entries"));
        this.items_obtained = this.addStat(new ItemsObtainedStat(this, "items_obtained"));
        this.mob_kills = this.addStat(new MobKillsStat(this, "mob_kills"));
        this.food_consumed = this.addStat(new ItemCountStat(this, "food_consumed"));
        this.potions_consumed = this.addStat(new ItemCountStat(this, "potions_consumed"));
        this.opened_incursions = this.addStat(new IncursionBiomeGameStat(this, "opened_incursions"));
        this.completed_incursions = this.addStat(new IncursionBiomeGameStat(this, "completed_incursions"));
        this.challenges_completed = this.addStat(new ChallengesCompletedStat(this, "challenges_completed"));
        this.challenges_claimed = this.addStat(new ChallengesClaimedStat(this, "challenges_claimed"));
        this.challenges_data = this.addStat(new GNDItemMapStat(this, "challenges_data", true));
    }

    public void saveStatsFile() {
        try {
            SaveData data = new SaveData("");
            this.addSaveData(data);
            data.saveScriptRaw(new File(PlayerStats.filePath()), true);
            if (GlobalData.isDevMode()) {
                data.saveScript(new File(PlayerStats.filePath() + "-debug"));
            }
        }
        catch (IllegalArgumentException e) {
            if (FontManager.isLoaded()) {
                GameMessageBuilder message = new GameMessageBuilder().append("misc", "statssaveeerror");
                if (e.getCause() != null) {
                    message.append("\n\n").append(e.getCause().getMessage());
                } else {
                    message.append("\n\n").append(e.getMessage());
                }
                if (GlobalData.getCurrentGameLoop() instanceof ClientGameLoop) {
                    ClientLoader.addLoadingNotice("statserrornotice", message);
                }
            }
            System.err.println("Error creating folder to save stats. Stats may not be saved.");
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadStatsFile() {
        try {
            LoadData data = LoadData.newRaw(new File(PlayerStats.filePath()), true);
            File debugFile = new File(PlayerStats.filePath() + "-debug");
            if (GlobalData.isDevMode() && debugFile.exists()) {
                data = LoadData.newRaw(debugFile, false);
            }
            this.applyLoadData(data);
        }
        catch (FileNotFoundException e) {
            System.out.println("Could not find stats file, does not exist. Creating new.");
            this.saveStatsFile();
        }
        catch (IOException | DataFormatException e) {
            e.printStackTrace();
        }
    }
}

