/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.playerStats;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormItemDisplayComponent;
import necesse.gfx.forms.components.FormPlayerStatComponent;
import necesse.gfx.forms.components.FormPlayerStatIntComponent;
import necesse.gfx.forms.components.FormPlayerStatLongComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.DisabledPreForm;
import necesse.gfx.forms.presets.playerStats.PlayerStatsContentBox;
import necesse.gfx.forms.presets.playerStats.PlayerStatsObtainedItemsForm;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSelected;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSubForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.item.Item;

public class PlayerStatsForm
extends Form
implements PlayerStatsSelected {
    public FormSwitcher switcher;
    public PlayerStatsContentBox generalStats;
    public PlayerStatsContentBox damageTypes;
    public PlayerStatsContentBox mobKills;
    public PlayerStatsContentBox foodConsumed;
    public PlayerStatsContentBox potionsConsumed;
    public PlayerStatsContentBox biomesDiscovered;
    public PlayerStatsContentBox trinketsWorn;
    public Runnable damageTypesUpdate;
    public Runnable mobKillsUpdate;
    public Runnable foodConsumedUpdate;
    public Runnable potionsConsumedUpdate;
    public Runnable biomesDiscoveredUpdate;
    public Runnable trinketsWornUpdate;
    public PlayerStatsObtainedItemsForm itemsObtained;
    public LinkedList<PlayerStatsSubForm> subForms = new LinkedList();
    public Runnable subMenuBackPressed;
    private Form disabledContent;

    public PlayerStatsForm(int x, int y, int width, int height, PlayerStats stats) {
        super(width, height);
        this.setPosition(x, y);
        this.drawBase = false;
        this.switcher = this.addComponent(new FormSwitcher());
        int padding = 8;
        int compWidth = width - padding * 2;
        this.generalStats = this.switcher.addComponent(new PlayerStatsContentBox(this));
        int scrollWidth = this.generalStats.getScrollBarWidth();
        this.subForms.add(this.generalStats);
        FormFlow generalFlow = new FormFlow();
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatComponent<String>(0, 0, compWidth - scrollWidth, new LocalMessage("stats", "time_played"), () -> GameUtils.formatSeconds(stats.time_played.get()))));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "distance_ran"), () -> (long)GameMath.pixelsToMeters(stats.distance_ran.get()), "m")));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "distance_ridden"), () -> (long)GameMath.pixelsToMeters(stats.distance_ridden.get()), "m")));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "damage_dealt"), stats.damage_dealt::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormLocalTextButton("stats", "show_types", 0, 0, compWidth - scrollWidth, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked(e -> this.switcher.makeCurrent(this.damageTypes));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "damage_taken"), stats.damage_taken::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "deaths"), stats.deaths::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "mob_kills"), stats.mob_kills::getTotalKills)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "boss_kills"), stats.mob_kills::getBossKills)));
        this.generalStats.addComponent(generalFlow.nextY(new FormLocalTextButton("stats", "show_mobs", 0, 0, compWidth - scrollWidth, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked(e -> this.switcher.makeCurrent(this.mobKills));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatComponent<String>(0, 0, compWidth - scrollWidth, new LocalMessage("stats", "biomes_visited"), () -> stats.biomes_visited.getTotalVisitedUniqueStatsBiomes() + "/" + BiomeRegistry.getTotalStatsBiomes())));
        this.generalStats.addComponent(generalFlow.nextY(new FormLocalTextButton("stats", "show_biomes", 0, 0, compWidth - scrollWidth, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked(e -> this.switcher.makeCurrent(this.biomesDiscovered));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "objects_mined"), stats.objects_mined::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "objects_placed"), stats.objects_placed::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "tiles_mined"), stats.tiles_mined::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "tiles_placed"), stats.tiles_placed::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "food_consumed"), stats.food_consumed::getTotal)));
        this.generalStats.addComponent(generalFlow.nextY(new FormLocalTextButton("stats", "show_food", 0, 0, compWidth - scrollWidth, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked(e -> this.switcher.makeCurrent(this.foodConsumed));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "potions_consumed"), stats.potions_consumed::getTotal)));
        this.generalStats.addComponent(generalFlow.nextY(new FormLocalTextButton("stats", "show_potions", 0, 0, compWidth - scrollWidth, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked(e -> this.switcher.makeCurrent(this.potionsConsumed));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "fish_caught"), stats.fish_caught::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "quests_completed"), stats.quests_completed::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "money_earned"), stats.money_earned::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "items_sold"), stats.items_sold::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "money_spent"), stats.money_spent::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "items_bought"), stats.items_bought::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "items_enchanted"), stats.items_enchanted::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "items_upgraded"), stats.items_upgraded::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "items_salvaged"), stats.items_salvaged::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatComponent<String>(0, 0, compWidth - scrollWidth, new LocalMessage("stats", "items_obtained"), () -> stats.items_obtained.getTotalStatItems() + "/" + ItemRegistry.getTotalStatItemsObtainable())));
        this.generalStats.addComponent(generalFlow.nextY(new FormLocalTextButton("stats", "show_item_list", 0, 0, compWidth - scrollWidth, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked(e -> this.switcher.makeCurrent(this.itemsObtained));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatComponent<String>(0, 0, compWidth - scrollWidth, new LocalMessage("stats", "trinkets_worn"), () -> stats.trinkets_worn.getTotalTrinketsWorn() + "/" + ItemRegistry.getTotalTrinkets())));
        this.generalStats.addComponent(generalFlow.nextY(new FormLocalTextButton("stats", "show_missing_trinkets", 0, 0, compWidth - scrollWidth, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked(e -> this.switcher.makeCurrent(this.trinketsWorn));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatComponent<String>(0, 0, compWidth - scrollWidth, new LocalMessage("stats", "set_bonuses_worn"), () -> stats.set_bonuses_worn.getTotalSetBonusesWorn() + "/" + BuffRegistry.getTotalSetBonuses())));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "ladders_used"), stats.ladders_used::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "doors_used"), stats.doors_used::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "plates_triggered"), stats.plates_triggered::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "levers_flicked"), stats.levers_flicked::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "homestones_used"), stats.homestones_used::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "waystones_used"), stats.waystones_used::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "crafted_items"), stats.crafted_items::get)));
        this.generalStats.addComponent(generalFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "crates_broken"), stats.crates_broken::get)));
        this.generalStats.fitContentBoxToComponents(padding);
        this.damageTypes = this.switcher.addComponent(new PlayerStatsContentBox(this){

            @Override
            public boolean backPressed() {
                if (PlayerStatsForm.this.subMenuBackPressed != null) {
                    PlayerStatsForm.this.subMenuBackPressed.run();
                    PlayerStatsForm.this.subMenuBackPressed = null;
                } else {
                    PlayerStatsForm.this.switcher.makeCurrent(PlayerStatsForm.this.generalStats);
                }
                return true;
            }
        }, (c, active) -> {
            if (active.booleanValue()) {
                this.damageTypesUpdate.run();
            }
        });
        scrollWidth = this.damageTypes.getScrollBarWidth();
        this.subForms.add(this.damageTypes);
        this.damageTypesUpdate = () -> {
            this.damageTypes.clearComponents();
            FormFlow damageFlow = new FormFlow();
            for (DamageType type : DamageTypeRegistry.getDamageTypes()) {
                this.damageTypes.addComponent(damageFlow.nextY(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, type.getStatsText(), () -> stats.type_damage_dealt.getDamage(type))));
            }
            this.damageTypes.fitContentBoxToComponents(padding);
        };
        this.mobKills = this.switcher.addComponent(new PlayerStatsContentBox(this){

            @Override
            public boolean backPressed() {
                if (PlayerStatsForm.this.subMenuBackPressed != null) {
                    PlayerStatsForm.this.subMenuBackPressed.run();
                    PlayerStatsForm.this.subMenuBackPressed = null;
                } else {
                    PlayerStatsForm.this.switcher.makeCurrent(PlayerStatsForm.this.generalStats);
                }
                return true;
            }
        }, (c, active) -> {
            if (active.booleanValue()) {
                this.mobKillsUpdate.run();
            }
        });
        scrollWidth = this.mobKills.getScrollBarWidth();
        this.subForms.add(this.mobKills);
        this.mobKillsUpdate = () -> {
            this.mobKills.clearComponents();
            FormFlow mobFlow = new FormFlow();
            LinkedList<FormPlayerStatComponent> statComponents = new LinkedList<FormPlayerStatComponent>();
            stats.mob_kills.forEach((mobStringID, kills) -> statComponents.add(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, MobRegistry.getLocalization(mobStringID), () -> kills)));
            statComponents.sort(Comparator.comparing(c -> c.getDisplayName().translate()));
            statComponents.forEach(c -> this.mobKills.addComponent(mobFlow.nextY(c)));
            this.mobKills.fitContentBoxToComponents(padding);
        };
        this.foodConsumed = this.switcher.addComponent(new PlayerStatsContentBox(this){

            @Override
            public boolean backPressed() {
                if (PlayerStatsForm.this.subMenuBackPressed != null) {
                    PlayerStatsForm.this.subMenuBackPressed.run();
                    PlayerStatsForm.this.subMenuBackPressed = null;
                } else {
                    PlayerStatsForm.this.switcher.makeCurrent(PlayerStatsForm.this.generalStats);
                }
                return true;
            }
        }, (c, active) -> {
            if (active.booleanValue()) {
                this.foodConsumedUpdate.run();
            }
        });
        scrollWidth = this.foodConsumed.getScrollBarWidth();
        this.subForms.add(this.foodConsumed);
        this.foodConsumedUpdate = () -> {
            this.foodConsumed.clearComponents();
            FormFlow mobFlow = new FormFlow();
            LinkedList<FormPlayerStatComponent> statComponents = new LinkedList<FormPlayerStatComponent>();
            stats.food_consumed.forEach((itemStringID, kills) -> statComponents.add(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, ItemRegistry.getLocalization(ItemRegistry.getItemID(itemStringID)), () -> kills)));
            statComponents.sort(Comparator.comparing(c -> c.getDisplayName().translate()));
            statComponents.forEach(c -> this.foodConsumed.addComponent(mobFlow.nextY(c)));
            this.foodConsumed.fitContentBoxToComponents(padding);
        };
        this.potionsConsumed = this.switcher.addComponent(new PlayerStatsContentBox(this){

            @Override
            public boolean backPressed() {
                if (PlayerStatsForm.this.subMenuBackPressed != null) {
                    PlayerStatsForm.this.subMenuBackPressed.run();
                    PlayerStatsForm.this.subMenuBackPressed = null;
                } else {
                    PlayerStatsForm.this.switcher.makeCurrent(PlayerStatsForm.this.generalStats);
                }
                return true;
            }
        }, (c, active) -> {
            if (active.booleanValue()) {
                this.potionsConsumedUpdate.run();
            }
        });
        scrollWidth = this.potionsConsumed.getScrollBarWidth();
        this.subForms.add(this.potionsConsumed);
        this.potionsConsumedUpdate = () -> {
            this.potionsConsumed.clearComponents();
            FormFlow mobFlow = new FormFlow();
            LinkedList<FormPlayerStatComponent> statComponents = new LinkedList<FormPlayerStatComponent>();
            stats.potions_consumed.forEach((itemStringID, kills) -> statComponents.add(new FormPlayerStatIntComponent(0, 0, compWidth - scrollWidth, ItemRegistry.getLocalization(ItemRegistry.getItemID(itemStringID)), () -> kills)));
            statComponents.sort(Comparator.comparing(c -> c.getDisplayName().translate()));
            statComponents.forEach(c -> this.potionsConsumed.addComponent(mobFlow.nextY(c)));
            this.potionsConsumed.fitContentBoxToComponents(padding);
        };
        this.biomesDiscovered = this.switcher.addComponent(new PlayerStatsContentBox(this){

            @Override
            public boolean backPressed() {
                if (PlayerStatsForm.this.subMenuBackPressed != null) {
                    PlayerStatsForm.this.subMenuBackPressed.run();
                    PlayerStatsForm.this.subMenuBackPressed = null;
                } else {
                    PlayerStatsForm.this.switcher.makeCurrent(PlayerStatsForm.this.generalStats);
                }
                return true;
            }
        }, (c, active) -> {
            if (active.booleanValue()) {
                this.biomesDiscoveredUpdate.run();
            }
        });
        scrollWidth = this.biomesDiscovered.getScrollBarWidth();
        this.subForms.add(this.biomesDiscovered);
        this.biomesDiscoveredUpdate = () -> {
            this.biomesDiscovered.clearComponents();
            FormFlow biomeFlow = new FormFlow();
            LinkedList<FormPlayerStatComponent> statComponents = new LinkedList<FormPlayerStatComponent>();
            stats.biomes_visited.forEachVisitedStatsBiome(biomeStringID -> statComponents.add(new FormPlayerStatComponent<String>(0, 0, compWidth - scrollWidth, BiomeRegistry.getBiome(biomeStringID).getLocalization(), () -> "")));
            statComponents.sort(Comparator.comparing(c -> c.getDisplayName().translate()));
            statComponents.forEach(c -> this.biomesDiscovered.addComponent(biomeFlow.nextY(c)));
            this.biomesDiscovered.fitContentBoxToComponents(padding);
        };
        this.itemsObtained = this.switcher.addComponent(new PlayerStatsObtainedItemsForm(this, stats, padding, () -> {
            if (this.subMenuBackPressed != null) {
                this.subMenuBackPressed.run();
                this.subMenuBackPressed = null;
            } else {
                this.switcher.makeCurrent(this.generalStats);
            }
        }), (c, active) -> {
            if (active.booleanValue()) {
                c.updateList(stats);
            }
        });
        this.subForms.add(this.itemsObtained);
        this.trinketsWorn = this.switcher.addComponent(new PlayerStatsContentBox(this){

            @Override
            public boolean backPressed() {
                if (PlayerStatsForm.this.subMenuBackPressed != null) {
                    PlayerStatsForm.this.subMenuBackPressed.run();
                    PlayerStatsForm.this.subMenuBackPressed = null;
                } else {
                    PlayerStatsForm.this.switcher.makeCurrent(PlayerStatsForm.this.generalStats);
                }
                return true;
            }
        }, (c, active) -> {
            if (active.booleanValue()) {
                this.trinketsWornUpdate.run();
            }
        });
        this.subForms.add(this.trinketsWorn);
        this.trinketsWornUpdate = () -> {
            this.trinketsWorn.clearComponents();
            FormFlow trinketFlow = new FormFlow();
            LinkedList<String> wornTrinkets = new LinkedList<String>();
            for (String s : stats.trinkets_worn.getTrinketsWorn()) {
                wornTrinkets.add(s);
            }
            List<Item> trinkets = ItemRegistry.getItems();
            trinkets.removeIf(i -> !ItemRegistry.countsInStats(i.getID()) || !i.isTrinketItem() || wornTrinkets.contains(i.getStringID()));
            if (trinkets.isEmpty()) {
                this.trinketsWorn.addComponent(trinketFlow.nextY(new FormLocalLabel("stats", "have_all_trinkets", new FontOptions(20), 0, this.trinketsWorn.getWidth() / 2, 0, this.trinketsWorn.getWidth())));
                this.trinketsWorn.fitContentBoxToComponents(padding);
                this.trinketsWorn.centerContentHorizontal();
            } else {
                int itemSize = 32;
                int itemPadding = 2;
                int maxPerRow = (this.trinketsWorn.getWidth() - itemPadding) / (itemSize + itemPadding);
                int edgePadding = (this.trinketsWorn.getWidth() - itemPadding) % (itemSize + itemPadding) / 2;
                this.trinketsWorn.addComponent(trinketFlow.nextY(new FormLocalLabel("stats", "missing_trinkets", new FontOptions(20), -1, edgePadding, 0, this.trinketsWorn.getWidth())));
                int startY = trinketFlow.next();
                int i2 = 0;
                for (Item trinket : trinkets) {
                    int column = i2 % maxPerRow;
                    int row = i2 / maxPerRow;
                    this.trinketsWorn.addComponent(new FormItemDisplayComponent(column * (itemSize + itemPadding), row * (itemSize + itemPadding) + startY, trinket.getDefaultItem(null, 1)));
                    ++i2;
                }
                this.trinketsWorn.fitContentBoxToComponents(edgePadding, 0, itemPadding, itemPadding);
            }
        };
        this.switcher.makeCurrent(this.generalStats);
    }

    @Override
    public void onSelected() {
        this.switcher.makeCurrent(this.generalStats);
    }

    @Override
    public boolean backPressed() {
        Object current = this.switcher.getCurrent();
        if (current instanceof PlayerStatsSubForm) {
            return ((PlayerStatsSubForm)current).backPressed();
        }
        return false;
    }

    public void removeDisabledTip() {
        if (this.disabledContent != null) {
            this.removeComponent(this.disabledContent);
        }
        for (PlayerStatsSubForm form : this.subForms) {
            form.updateDisabled(0);
        }
    }

    public void setDisabledTip(GameMessage label, GameMessage tooltip) {
        if (this.disabledContent != null) {
            this.removeComponent(this.disabledContent);
        }
        this.disabledContent = this.addComponent(new DisabledPreForm(this.getWidth(), label, tooltip));
        for (PlayerStatsSubForm form : this.subForms) {
            form.updateDisabled(this.disabledContent.getHeight());
        }
    }
}

