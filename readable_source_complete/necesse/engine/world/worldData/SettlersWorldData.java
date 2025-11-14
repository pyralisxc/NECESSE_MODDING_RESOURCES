/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldData;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.MobSave;
import necesse.engine.util.TeleportResult;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.engine.world.worldData.WorldData;
import necesse.entity.levelEvent.SmokePuffLevelEvent;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class SettlersWorldData
extends WorldData {
    private final HashMap<Integer, SettlerData> settlers = new HashMap();

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        if (!this.settlers.isEmpty()) {
            SaveData settlersSave = new SaveData("settlers");
            for (Map.Entry<Integer, SettlerData> entry : this.settlers.entrySet()) {
                int uniqueID = entry.getKey();
                SettlerData settlerData = entry.getValue();
                SaveData settlerSave = new SaveData("settler");
                settlerSave.addInt("uniqueID", uniqueID);
                settlerSave.addLong("returnTime", settlerData.returnTime);
                settlerSave.addSaveData(MobSave.getSave("mob", settlerData.mob));
                settlersSave.addSaveData(settlerSave);
            }
            save.addSaveData(settlersSave);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.settlers.clear();
        LoadData settlersSave = save.getFirstLoadDataByName("settlers");
        if (settlersSave != null) {
            for (LoadData settlerSave : settlersSave.getLoadDataByName("settler")) {
                try {
                    int uniqueID = settlerSave.getInt("uniqueID");
                    long returnTime = settlerSave.getLong("returnTime");
                    Mob mob = MobSave.loadSave(settlerSave.getFirstLoadDataByName("mob"), null);
                    if (!(mob instanceof HumanMob)) continue;
                    this.settlers.put(uniqueID, new SettlerData(returnTime, (HumanMob)mob));
                }
                catch (Exception e) {
                    System.err.println("Error loading world settler:");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    public HumanMob getSettler(int mobUniqueID) {
        SettlerData data = this.settlers.get(mobUniqueID);
        if (data != null) {
            return data.mob;
        }
        return null;
    }

    public boolean exists(int mobUniqueID) {
        return this.settlers.containsKey(mobUniqueID);
    }

    public void remove(int mobUniqueID) {
        this.settlers.remove(mobUniqueID);
    }

    public boolean returnIfShould(int mobUniqueID, ServerSettlementData settlement) {
        SettlerData data = this.settlers.get(mobUniqueID);
        if (data != null) {
            if (data.returnTime < this.worldEntity.getTime()) {
                this.returnToSettlement(mobUniqueID, data, settlement, false);
            }
            return false;
        }
        return false;
    }

    public boolean returnToSettlement(HumanMob mob, boolean useTeleportEvent) {
        ServerSettlementData settlement;
        int settlementUniqueID;
        SettlerData data = this.settlers.get(mob.getUniqueID());
        if (data != null && (settlementUniqueID = data.mob.getSettlementUniqueID()) != 0 && (settlement = SettlementsWorldData.getSettlementsData(this.worldEntity).getOrLoadServerData(settlementUniqueID)) != null) {
            this.returnToSettlement(mob.getUniqueID(), data, settlement, useTeleportEvent);
            return true;
        }
        return false;
    }

    public static Point getReturnPos(Mob mob, ServerSettlementData settlement) {
        return PortalObjectEntity.getTeleportDestinationAroundObject(settlement.getLevel(), mob, settlement.networkData.getTileX(), settlement.networkData.getTileY(), true);
    }

    private void returnToSettlement(int mobUniqueID, SettlerData data, ServerSettlementData settlement, boolean useTeleportEvent) {
        Point returnPos = SettlersWorldData.getReturnPos(data.mob, settlement);
        Level mobLevel = data.mob.getLevel();
        if (mobLevel.isDisposed()) {
            mobLevel = null;
        }
        if (mobLevel != null) {
            mobLevel.regionManager.ensureTileIsLoaded(data.mob.getTileX(), data.mob.getTileY());
            Mob newMob = mobLevel.entityManager.mobs.get(mobUniqueID, false);
            if (newMob instanceof HumanMob) {
                data.mob = (HumanMob)newMob;
                mobLevel = data.mob.getLevel();
            }
        }
        if (mobLevel != null && mobLevel.entityManager.mobs.get(mobUniqueID, false) == data.mob) {
            if (useTeleportEvent) {
                TeleportEvent teleportEvent = new TeleportEvent(data.mob, 0, settlement.getLevel().getIdentifier(), 0.0f, null, newLevel -> new TeleportResult(true, settlement.getLevel().getIdentifier(), returnPos.x, returnPos.y));
                mobLevel.entityManager.events.add(teleportEvent);
            } else {
                mobLevel.entityManager.events.add(new SmokePuffLevelEvent(data.mob.x, data.mob.y, 64, new Color(50, 50, 50)));
                if (mobLevel.isSamePlace(settlement.getLevel())) {
                    data.mob.setPos(returnPos.x, returnPos.y, true);
                } else {
                    mobLevel.entityManager.events.add(new SmokePuffLevelEvent(data.mob.x, data.mob.y, 64, new Color(50, 50, 50)));
                    mobLevel.entityManager.changeMobLevel(data.mob, settlement.getLevel(), returnPos.x, returnPos.y, true);
                }
            }
        } else {
            if (data.mob.removed()) {
                data.mob.restore();
            }
            settlement.getLevel().entityManager.addMob(data.mob, returnPos.x, returnPos.y);
        }
        settlement.onReturned(mobUniqueID);
        data.mob.clearCommandsOrders(null);
        this.settlers.remove(mobUniqueID);
    }

    public void refreshWorldSettler(HumanMob mob, boolean refreshTime) {
        SettlerData data = this.settlers.computeIfAbsent(mob.getUniqueID(), key -> new SettlerData(0L, mob));
        data.mob = mob;
        if (refreshTime) {
            data.returnTime = this.worldEntity.getTime() + 5000L;
        }
    }

    public static SettlersWorldData getSettlersData(Server server) {
        WorldData settlers = server.world.worldEntity.getWorldData("settlers");
        if (settlers instanceof SettlersWorldData) {
            return (SettlersWorldData)settlers;
        }
        SettlersWorldData data = new SettlersWorldData();
        server.world.worldEntity.addWorldData("settlers", data);
        return data;
    }

    private static class SettlerData {
        public long returnTime;
        public HumanMob mob;

        public SettlerData(long returnTime, HumanMob mob) {
            this.returnTime = returnTime;
            this.mob = mob;
        }
    }
}

