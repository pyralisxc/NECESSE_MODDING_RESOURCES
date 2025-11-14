/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop.explorerMission;

import java.util.ArrayList;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.RunOutMission;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootInventoryItem;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;

public class TradingMission
extends RunOutMission {
    private ArrayList<InventoryItem> items = new ArrayList();
    private int profit;
    private int ticks;
    private int completeTicks;
    private boolean skipWaitForPlayerItemRetrieval;

    public TradingMission() {
    }

    public TradingMission(ArrayList<InventoryItem> items, int profit, int ticksToComplete, boolean skipWaitForPlayerItemRetrieval) {
        this.items = items;
        this.profit = profit;
        this.skipWaitForPlayerItemRetrieval = skipWaitForPlayerItemRetrieval;
        this.completeTicks = ticksToComplete;
    }

    public TradingMission(ArrayList<InventoryItem> items, int profit, boolean skipWaitForPlayerItemRetrieval) {
        this(items, profit, GameRandom.globalRandom.getIntBetween(SettlerExpedition.minCompleteTicks, SettlerExpedition.maxCompleteTicks), skipWaitForPlayerItemRetrieval);
    }

    @Override
    public LootTable getLootTable(HumanMob mob) {
        LootTable lootTable = super.getLootTable(mob);
        for (InventoryItem item : this.items) {
            lootTable.items.add(new LootInventoryItem(item));
        }
        return lootTable;
    }

    @Override
    public boolean canStart(HumanMob mob) {
        return true;
    }

    @Override
    public void start(HumanMob mob) {
        super.start(mob);
        mob.completedMission = false;
        this.ticks = 0;
        NetworkSettlementData settlement = mob.getSettlerSettlementNetworkData();
        if (settlement != null) {
            settlement.streamTeamMembers().forEach(c -> c.sendChatMessage(new LocalMessage("ui", "settlergoingout", "settler", mob.getLocalization(), "mission", TradingMission.getMissionName())));
        }
    }

    public static GameMessage getMissionName() {
        return new LocalMessage("ui", "tradingmission");
    }

    @Override
    public GameMessage getActivityMessage(HumanMob mob) {
        return new LocalMessage("activities", "onmissiontype", "mission", TradingMission.getMissionName());
    }

    @Override
    public void addSaveData(HumanMob mob, SaveData save) {
        super.addSaveData(mob, save);
        save.addInt("ticks", this.ticks);
        save.addInt("completeTicks", this.completeTicks);
        if (this.skipWaitForPlayerItemRetrieval) {
            save.addBoolean("skipWaitForPlayerItemRetrieval", this.skipWaitForPlayerItemRetrieval);
        }
        save.addInt("profit", this.profit);
        SaveData itemsSave = new SaveData("ITEMS");
        for (InventoryItem item : this.items) {
            itemsSave.addSaveData(item.getSaveData(""));
        }
        if (!itemsSave.isEmpty()) {
            save.addSaveData(itemsSave);
        }
    }

    @Override
    public void applySaveData(HumanMob mob, LoadData save) {
        super.applySaveData(mob, save);
        this.ticks = save.getInt("ticks", this.ticks, false);
        this.completeTicks = save.getInt("completeTicks", this.completeTicks, false);
        this.skipWaitForPlayerItemRetrieval = save.getBoolean("skipWaitForPlayerItemRetrieval", this.skipWaitForPlayerItemRetrieval, false);
        this.profit = save.getInt("profit", this.profit, false);
        LoadData itemsSave = save.getFirstLoadDataByName("ITEMS");
        if (itemsSave != null) {
            this.items.clear();
            for (LoadData itemSave : itemsSave.getLoadData()) {
                try {
                    InventoryItem item = InventoryItem.fromLoadData(itemSave);
                    if (item == null) continue;
                    this.items.add(item);
                }
                catch (LoadDataException item) {
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void serverTick(HumanMob mob) {
        if (this.isOut) {
            ++this.ticks;
            if (this.ticks >= this.completeTicks) {
                this.endMission(mob);
            }
        }
    }

    private void endMission(HumanMob mob) {
        if (this.isOver()) {
            return;
        }
        this.markOver();
        mob.completedMission = this.profit > 0;
        mob.missionFailed = false;
        if (this.skipWaitForPlayerItemRetrieval) {
            mob.clearMissionResult();
        }
        if (mob.home != null) {
            mob.moveIn(mob.home.x, mob.home.y, true);
        }
        if (this.profit > 0) {
            mob.getWorkInventory().add(new InventoryItem("coin", this.profit));
            this.profit = 0;
        }
        this.items.clear();
        mob.sendMovementPacket(true);
        NetworkSettlementData settlement = mob.getSettlerSettlementNetworkData();
        if (settlement != null) {
            settlement.streamTeamMembers().forEach(c -> c.sendChatMessage(new LocalMessage("ui", "settlerreturningfrom", "settler", mob.getLocalization(), "settlement", settlement.getSettlementName(), "mission", TradingMission.getMissionName())));
        }
    }
}

