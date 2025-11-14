/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.platforms.Platform;
import necesse.engine.playerStats.StatsProvider;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.gameDamageType.DamageType;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormPlayerStatComponent;
import necesse.gfx.forms.components.FormPlayerStatLongComponent;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.ui.ButtonColor;

public class GlobalStatsForm
extends Form {
    private FormSwitcher switcher;
    private FormContentBox generalStats;
    private FormContentBox damageTypes;

    public GlobalStatsForm(int x, int y, int width, int height) {
        super(width, height);
        this.setPosition(x, y);
        this.drawBase = false;
        this.switcher = this.addComponent(new FormSwitcher());
        int padding = 8;
        int compWidth = width - padding * 2;
        StatsProvider statsProvider = Platform.getStatsProvider();
        this.generalStats = this.switcher.addComponent(new FormContentBox(0, 0, width, height));
        int scrollWidth = this.generalStats.getScrollBarWidth();
        FormFlow flow = new FormFlow();
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatComponent<String>(0, 0, compWidth - scrollWidth, new LocalMessage("stats", "time_played"), () -> GameUtils.formatSeconds(statsProvider.getGlobalStats().time_played))));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "distance_ran"), () -> (long)GameMath.pixelsToMeters(statsProvider.getGlobalStats().distance_ran), "m")));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "distance_ridden"), () -> (long)GameMath.pixelsToMeters(statsProvider.getGlobalStats().distance_ridden), "m")));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "damage_dealt"), () -> statsProvider.getGlobalStats().damage_dealt)));
        this.generalStats.addComponent(flow.nextY(new FormLocalTextButton("stats", "show_types", 0, 0, compWidth - scrollWidth, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked(e -> this.switcher.makeCurrent(this.damageTypes));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "damage_taken"), () -> statsProvider.getGlobalStats().damage_taken)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "deaths"), () -> statsProvider.getGlobalStats().deaths)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "mob_kills"), () -> statsProvider.getGlobalStats().mob_kills)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "boss_kills"), () -> statsProvider.getGlobalStats().boss_kills)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new StaticMessage("Islands visited (deprecated)"), () -> statsProvider.getGlobalStats().islands_discovered + statsProvider.getGlobalStats().islands_visited)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "objects_mined"), () -> statsProvider.getGlobalStats().objects_mined)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "objects_placed"), () -> statsProvider.getGlobalStats().objects_placed)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "tiles_mined"), () -> statsProvider.getGlobalStats().tiles_mined)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "tiles_placed"), () -> statsProvider.getGlobalStats().tiles_placed)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "food_consumed"), () -> statsProvider.getGlobalStats().food_consumed)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "potions_consumed"), () -> statsProvider.getGlobalStats().potions_consumed)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "fish_caught"), () -> statsProvider.getGlobalStats().fish_caught)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "quests_completed"), () -> statsProvider.getGlobalStats().quests_completed)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "money_earned"), () -> statsProvider.getGlobalStats().money_earned)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "items_sold"), () -> statsProvider.getGlobalStats().items_sold)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "money_spent"), () -> statsProvider.getGlobalStats().money_spent)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "items_bought"), () -> statsProvider.getGlobalStats().items_bought)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "items_enchanted"), () -> statsProvider.getGlobalStats().items_enchanted)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "items_upgraded"), () -> statsProvider.getGlobalStats().items_upgraded)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "items_salvaged"), () -> statsProvider.getGlobalStats().items_salvaged)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "ladders_used"), () -> statsProvider.getGlobalStats().ladders_used)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "doors_used"), () -> statsProvider.getGlobalStats().doors_used)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "plates_triggered"), () -> statsProvider.getGlobalStats().plates_triggered)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "levers_flicked"), () -> statsProvider.getGlobalStats().levers_flicked)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "homestones_used"), () -> statsProvider.getGlobalStats().homestones_used)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "waystones_used"), () -> statsProvider.getGlobalStats().waystones_used)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "crafted_items"), () -> statsProvider.getGlobalStats().crafted_items)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "crates_broken"), () -> statsProvider.getGlobalStats().crates_broken)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "opened_incursions"), () -> statsProvider.getGlobalStats().opened_incursions)));
        this.generalStats.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, (GameMessage)new LocalMessage("stats", "completed_incursions"), () -> statsProvider.getGlobalStats().completed_incursions)));
        this.generalStats.fitContentBoxToComponents(padding);
        this.damageTypes = this.switcher.addComponent(new FormContentBox(0, 0, width, height));
        scrollWidth = this.damageTypes.getScrollBarWidth();
        flow = new FormFlow();
        this.damageTypes.addComponent(flow.nextY(new FormLocalTextButton("ui", "backbutton", 0, 0, compWidth - scrollWidth, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked(e -> this.switcher.makeCurrent(this.generalStats));
        for (DamageType type : DamageTypeRegistry.getDamageTypes()) {
            String statKey = type.getSteamStatKey();
            if (statKey == null) continue;
            this.damageTypes.addComponent(flow.nextY(new FormPlayerStatLongComponent(0, 0, compWidth - scrollWidth, type.getStatsText(), () -> statsProvider.getGlobalStats().getStatByName(statKey, 0L))));
        }
        this.damageTypes.fitContentBoxToComponents(padding);
        this.switcher.makeCurrent(this.generalStats);
    }
}

