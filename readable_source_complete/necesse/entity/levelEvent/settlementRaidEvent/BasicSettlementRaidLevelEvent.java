/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.function.UnaryOperator;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadout;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadoutGenerator;
import necesse.level.maps.levelData.settlementData.SettlementRaidOptions;

public class BasicSettlementRaidLevelEvent
extends SettlementRaidLevelEvent {
    protected String mobStringID;
    protected String raidTypeStringID;

    public BasicSettlementRaidLevelEvent(String mobStringID, String raidTypeStringID) {
        this.mobStringID = mobStringID;
        this.raidTypeStringID = raidTypeStringID;
    }

    public static ArrayList<SettlementRaidLoadout> getRaidLoadouts(SettlementRaidOptions options, UnaryOperator<SettlementRaidLoadoutGenerator> modifier, String mobStringID, int limit, boolean debug) {
        long startTime = System.nanoTime();
        if (debug) {
            options.wealthCounter.printDebug();
        }
        double settlerCountWealth = options.serverData.countTotalSettlers() * 1000;
        double unequippedWealth = options.wealthCounter.getUnequippedWealth();
        double totalValue = unequippedWealth / 10.0 + options.wealthCounter.getEquippedWealth() + settlerCountWealth;
        double variation = options.wealthCounter.getSettlersGearCoefficientOfVariation();
        SettlementRaidLoadoutGenerator generator = SettlementRaidLoadoutGenerator.init().maxWeaponValue(options.wealthCounter.getBestWeaponValue()).maxArmorValue(options.wealthCounter.getBestArmorValue()).equipmentVariationCoefficient(variation).obtainedItems(options.obtainedItems);
        if (modifier != null) {
            generator = (SettlementRaidLoadoutGenerator)modifier.apply(generator);
        }
        ArrayList<SettlementRaidLoadout> loadouts = generator.generateLoadouts(GameRandom.globalRandom, totalValue, limit, mobStringID);
        if (debug) {
            System.out.println("FINDING LOADOUTS TOOK " + GameUtils.getTimeStringNano(System.nanoTime() - startTime));
            System.out.println("STORED WEALTH: " + (int)unequippedWealth + " (" + GameMath.toDecimals(unequippedWealth / 10.0 / totalValue * 100.0, 2) + "% of counted value)");
        }
        return loadouts;
    }

    public static ArrayList<SettlementRaidLoadout> getRaidDebugLoadouts(ServerSettlementData serverData, String mobStringID, int limit, boolean printDebug) {
        SettlementRaidOptions raidOptions = serverData.getRaidOptions(printDebug);
        return BasicSettlementRaidLevelEvent.getRaidLoadouts(raidOptions, null, mobStringID, limit, printDebug);
    }

    public void modifyLoadouts(SettlementRaidLoadoutGenerator generator) {
    }

    @Override
    public void initializeFromServerData(ServerSettlementData serverData, SettlementRaidOptions options) {
        super.initializeFromServerData(serverData, options);
        int present = 1 + this.getCurrentSettlers() + Math.max(serverData.getLevel().presentPlayers - 1, 0);
        int limit = (int)((Math.pow((double)present / 5.0, 0.35) * 15.0 - 12.0) * 3.0);
        limit = GameMath.limit(limit, 1, 60);
        this.attackTiles = new ArrayList<Point>(options.wealthCounter.getStorageTiles());
        this.loadouts = BasicSettlementRaidLevelEvent.getRaidLoadouts(options, g -> {
            this.modifyLoadouts((SettlementRaidLoadoutGenerator)g);
            return g;
        }, this.mobStringID, limit, false);
    }

    @Override
    public GameMessage getApproachMessage(GameMessage settlementName, boolean isFirst) {
        return new LocalMessage("misc", "raidapproaching", "settlement", settlementName, "direction", this.direction.displayName, "raidtype", Localization.translate("misc", this.raidTypeStringID));
    }

    @Override
    public GameMessage getPreparingMessage(GameMessage settlementName) {
        return new LocalMessage("misc", "raidpreparing", "settlement", settlementName, "raidtype", Localization.translate("misc", this.raidTypeStringID));
    }

    @Override
    public GameMessage getStartMessage(GameMessage settlementName) {
        return new LocalMessage("misc", "raidattacking", "settlement", settlementName, "raidtype", Localization.translate("misc", this.raidTypeStringID));
    }

    @Override
    public GameMessage getDefeatedMessage() {
        return new LocalMessage("misc", "raiddefeated", "raidtype", Localization.translate("misc", this.raidTypeStringID));
    }

    @Override
    public GameMessage getLeavingMessage() {
        return new LocalMessage("misc", "raidended", "raidtype", Localization.translate("misc", this.raidTypeStringID));
    }

    @Override
    public GameMessage getLeavingWithLootMessage() {
        return new LocalMessage("misc", "raidlooting", "raidtype", Localization.translate("misc", this.raidTypeStringID));
    }

    public String getMobStringID() {
        return this.mobStringID;
    }

    public String getRaidTypeStringID() {
        return this.raidTypeStringID;
    }
}

