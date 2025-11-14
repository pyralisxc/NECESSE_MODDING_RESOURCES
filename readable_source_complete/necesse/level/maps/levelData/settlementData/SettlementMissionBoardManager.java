/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.ArrayList;
import java.util.Collection;
import necesse.engine.GameLog;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.inventory.container.object.missionBoard.DeletedMissionUpdateEvent;
import necesse.inventory.container.object.missionBoard.SingleMissionUpdateEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementMissionBoardMission;

public class SettlementMissionBoardManager {
    public static int maxSlots = 50;
    public static int[] slotsCost = new int[]{0, 0, 500, 1000, 2000, 3000, 5000, 7500, 10000};
    public final ServerSettlementData data;
    public int missionBoardSlots = 2;
    private final ArrayList<SettlementMissionBoardMission> missionBoardMissions = new ArrayList();

    public static int getNextSlotCost(int currentSlots) {
        if (currentSlots >= maxSlots) {
            return -1;
        }
        return slotsCost[GameMath.limit(currentSlots, 0, slotsCost.length - 1)];
    }

    public SettlementMissionBoardManager(ServerSettlementData data) {
        this.data = data;
    }

    public void addSaveData(SaveData save) {
        save.addInt("missionBoardSlots", this.missionBoardSlots);
        SaveData missionsSave = new SaveData("missions");
        for (SettlementMissionBoardMission mission : this.missionBoardMissions) {
            missionsSave.addSaveData(mission.getSaveData(""));
        }
        if (!missionsSave.isEmpty()) {
            save.addSaveData(missionsSave);
        }
    }

    public void applyLoadData(LoadData save) {
        this.missionBoardSlots = save.getInt("missionBoardSlots", this.missionBoardSlots, false);
        this.missionBoardMissions.clear();
        LoadData missionsSave = save.getFirstLoadDataByName("missions");
        if (missionsSave != null) {
            for (LoadData missionSave : missionsSave.getLoadData()) {
                try {
                    SettlementMissionBoardMission mission = new SettlementMissionBoardMission(this.data, missionSave);
                    this.missionBoardMissions.add(mission);
                }
                catch (LoadDataException e) {
                    GameLog.warn.println("Could not load saved mission: " + e.getMessage());
                }
                catch (Exception e) {
                    System.err.println("Could not load saved mission:");
                    e.printStackTrace();
                }
            }
        }
    }

    public SettlementMissionBoardMission addNewMission(SettlerExpedition expedition) {
        SettlementMissionBoardMission mission = new SettlementMissionBoardMission(this.data, expedition);
        this.missionBoardMissions.add(mission);
        new SingleMissionUpdateEvent(this.data, this.missionBoardMissions.size() - 1, mission).applyAndSendToClientsAt(this.data.getLevel());
        return mission;
    }

    public boolean deleteMission(int missionUniqueID) {
        boolean success = this.missionBoardMissions.removeIf(mission -> mission.uniqueID == missionUniqueID);
        if (success) {
            new DeletedMissionUpdateEvent(this.data, missionUniqueID).applyAndSendToClientsAt(this.data.getLevel());
        }
        return success;
    }

    public SettlementMissionBoardMission getMission(int uniqueID) {
        return this.missionBoardMissions.stream().filter(mission -> mission.uniqueID == uniqueID).findFirst().orElse(null);
    }

    public SettlementMissionBoardMission setMissionSlot(int uniqueID, int slot) {
        for (int i = 0; i < this.missionBoardMissions.size(); ++i) {
            SettlementMissionBoardMission mission = this.missionBoardMissions.get(i);
            if (mission.uniqueID != uniqueID) continue;
            this.missionBoardMissions.remove(i);
            this.missionBoardMissions.add(Math.min(slot, this.missionBoardMissions.size()), mission);
            return mission;
        }
        return null;
    }

    public Collection<SettlementMissionBoardMission> getMissions() {
        return this.missionBoardMissions;
    }
}

