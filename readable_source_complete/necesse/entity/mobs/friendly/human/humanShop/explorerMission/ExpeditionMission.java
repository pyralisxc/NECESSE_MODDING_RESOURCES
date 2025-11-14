/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanShop.explorerMission;

import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.explorerMission.RunOutMission;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootInventoryItem;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class ExpeditionMission
extends RunOutMission {
    private SettlerExpedition expedition;
    private int usedCoins;
    private boolean success;
    private int ticks;
    private int completeTicks;
    private boolean skipWaitForPlayerItemRetrieval;

    public ExpeditionMission() {
    }

    public ExpeditionMission(SettlerExpedition expedition, int usedCoins, float successChance, boolean skipWaitForPlayerItemRetrieval) {
        this.expedition = expedition;
        this.usedCoins = usedCoins;
        this.success = GameRandom.globalRandom.getChance(successChance);
        this.skipWaitForPlayerItemRetrieval = skipWaitForPlayerItemRetrieval;
        this.completeTicks = expedition.getTicksToComplete();
        if (!this.success) {
            this.completeTicks /= 2;
        }
    }

    @Override
    public LootTable getLootTable(HumanMob mob) {
        LootTable lootTable = super.getLootTable(mob);
        if (this.usedCoins > 0) {
            lootTable.items.add(new LootInventoryItem(new InventoryItem("coin", this.usedCoins)));
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
        ServerSettlementData serverData = mob.getSettlerSettlementServerData();
        if (serverData != null) {
            serverData.networkData.streamTeamMembers().forEach(c -> c.sendChatMessage(new LocalMessage("ui", "settlergoingout", "settler", mob.getLocalization(), "mission", this.expedition.getFullDisplayName())));
        }
    }

    @Override
    public GameMessage getActivityMessage(HumanMob mob) {
        return new LocalMessage("activities", "onmissiontype", "mission", this.expedition.getFullDisplayName());
    }

    @Override
    public void addSaveData(HumanMob mob, SaveData save) {
        super.addSaveData(mob, save);
        if (this.expedition != null) {
            save.addUnsafeString("expedition", this.expedition.getStringID());
        }
        save.addBoolean("success", this.success);
        save.addInt("ticks", this.ticks);
        save.addInt("completeTicks", this.completeTicks);
        if (this.skipWaitForPlayerItemRetrieval) {
            save.addBoolean("skipWaitForPlayerItemRetrieval", this.skipWaitForPlayerItemRetrieval);
        }
        if (this.usedCoins > 0) {
            save.addInt("usedCoins", this.usedCoins);
        }
    }

    @Override
    public void applySaveData(HumanMob mob, LoadData save) {
        super.applySaveData(mob, save);
        String expeditionStringID = save.getUnsafeString("expedition", null, false);
        if (expeditionStringID != null) {
            this.expedition = ExpeditionMissionRegistry.getExpedition(expeditionStringID);
        }
        this.success = save.getBoolean("success", this.success, false);
        this.ticks = save.getInt("ticks", this.ticks, false);
        this.completeTicks = save.getInt("completeTicks", this.completeTicks, false);
        this.skipWaitForPlayerItemRetrieval = save.getBoolean("skipWaitForPlayerItemRetrieval", this.skipWaitForPlayerItemRetrieval, false);
        this.usedCoins = save.getInt("usedCoins", this.usedCoins, false);
    }

    @Override
    public void serverTick(HumanMob mob) {
        if (this.expedition == null) {
            this.markOver();
            return;
        }
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
        mob.completedMission = true;
        mob.missionFailed = !this.success;
        ServerSettlementData settlementData = mob.getSettlerSettlementServerData();
        if (settlementData != null && this.success) {
            for (InventoryItem item : this.expedition.getRewardItems(settlementData, mob)) {
                mob.getWorkInventory().add(item);
            }
            mob.getWorkInventory().markDirty();
        } else if (settlementData == null) {
            mob.missionFailedMessage = new LocalMessage("ui", "explorerexpfail");
        }
        if (this.skipWaitForPlayerItemRetrieval) {
            mob.clearMissionResult();
        }
        if (mob.home != null) {
            mob.moveIn(mob.home.x, mob.home.y, true);
        }
        this.usedCoins = 0;
        mob.sendMovementPacket(true);
        NetworkSettlementData settlement = mob.getSettlerSettlementNetworkData();
        if (settlement != null) {
            settlement.streamTeamMembers().forEach(c -> c.sendChatMessage(new LocalMessage("ui", "settlerreturningfrom", "settler", mob.getLocalization(), "settlement", settlement.getSettlementName(), "mission", this.expedition.getFullDisplayName())));
        }
    }
}

