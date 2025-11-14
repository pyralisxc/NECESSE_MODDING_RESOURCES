/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import java.awt.Point;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.SettlementWealthCounter;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public interface SettlerMob {
    public void setHome(Point var1);

    public void assignBed(LevelSettler var1, SettlementBed var2, boolean var3);

    public boolean hasBedAssigned();

    public boolean isMovingIn();

    public void moveOut();

    public boolean isMovingOut();

    public void tickSettler(ServerSettlementData var1, LevelSettler var2);

    public void makeSettler(ServerSettlementData var1, LevelSettler var2);

    public boolean isSettler();

    public int getSettlementUniqueID();

    default public LevelIdentifier getSettlementLevelIdentifier() {
        int settlementUniqueID = this.getSettlementUniqueID();
        if (settlementUniqueID == 0) {
            return null;
        }
        Mob mob = this.getMob();
        if (mob.getLevel() == null) {
            return null;
        }
        CachedSettlementData settlement = SettlementsWorldData.getSettlementsData(mob).getCachedData(settlementUniqueID);
        return settlement != null ? settlement.levelIdentifier : null;
    }

    default public boolean isSettlerWithinSettlement(NetworkSettlementData settlement) {
        Mob mob = this.getMob();
        if (mob.getLevel() != null && !mob.getLevel().getIdentifier().equals(settlement.level.getIdentifier())) {
            return false;
        }
        return settlement.isTileWithinBounds(mob.getTileX(), mob.getTileY());
    }

    default public boolean isSettlerWithinSettlement() {
        NetworkSettlementData settlement = this.getSettlerSettlementNetworkData();
        if (settlement == null) {
            return false;
        }
        return this.isSettlerWithinSettlement(settlement);
    }

    default public boolean isSettlerWithinSettlementLoadedRegions(NetworkSettlementData settlement) {
        Mob mob = this.getMob();
        if (mob.getLevel() != null && !mob.getLevel().getIdentifier().equals(settlement.level.getIdentifier())) {
            return false;
        }
        return settlement.isTileWithinLoadedRegionBounds(mob.getTileX(), mob.getTileY());
    }

    default public boolean isSettlerWithinSettlementLoadedRegions() {
        NetworkSettlementData settlement = this.getSettlerSettlementNetworkData();
        if (settlement == null) {
            return false;
        }
        return this.isSettlerWithinSettlementLoadedRegions(settlement);
    }

    default public boolean isSettlerOnCurrentLevel() {
        if (!this.isSettler()) {
            return false;
        }
        LevelIdentifier identifier = this.getSettlementLevelIdentifier();
        if (identifier != null) {
            Level level = this.getMob().getLevel();
            return level != null && level.getIdentifier().equals(identifier);
        }
        return false;
    }

    default public Level getSettlementServerLevel() {
        if (!this.isSettler()) {
            return null;
        }
        Level level = this.getMob().getLevel();
        if (level == null || !level.isServer()) {
            return null;
        }
        LevelIdentifier levelIdentifier = this.getSettlementLevelIdentifier();
        if (levelIdentifier == null) {
            return null;
        }
        return level.getServer().world.getLevel(levelIdentifier);
    }

    default public ServerSettlementData getSettlerSettlementServerData() {
        int settlementUniqueID = this.getSettlementUniqueID();
        if (settlementUniqueID == 0) {
            return null;
        }
        Mob mob = this.getMob();
        if (mob.isClient()) {
            return null;
        }
        if (mob.getWorldEntity() == null) {
            return null;
        }
        return SettlementsWorldData.getSettlementsData(mob).getOrLoadServerData(settlementUniqueID);
    }

    default public NetworkSettlementData getSettlerSettlementNetworkData() {
        int settlementUniqueID = this.getSettlementUniqueID();
        if (settlementUniqueID == 0) {
            return null;
        }
        Mob mob = this.getMob();
        if (mob.getWorldEntity() == null) {
            return null;
        }
        return SettlementsWorldData.getSettlementsData(mob.getWorldEntity()).getNetworkData(settlementUniqueID);
    }

    public void setSettlerSeed(int var1, boolean var2);

    public int getSettlerSeed();

    public void setSettlerName(String var1);

    public String getSettlerName();

    public String getSettlerStringID();

    default public Settler getSettler() {
        String settlerStringID = this.getSettlerStringID();
        if (settlerStringID == null) {
            return null;
        }
        return SettlerRegistry.getSettler(settlerStringID);
    }

    public void addToWealthCounter(SettlementWealthCounter var1);

    public boolean doesEatFood();

    public int getSettlerHappiness();

    public boolean hasCommandOrders();

    public GameMessage getCurrentActivity();

    public Mob getMob();

    default public void runSettlerCheck() {
        if (!this.isSettler() || this.isMovingOut()) {
            return;
        }
        ServerSettlementData settlementData = this.getSettlerSettlementServerData();
        if (settlementData != null && !settlementData.isMobPartOf(this)) {
            this.moveOut();
        }
    }

    default public boolean canSubmitNoBedNotification() {
        return true;
    }
}

