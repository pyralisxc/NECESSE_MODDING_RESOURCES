/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.function.Function;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameRandom;
import necesse.inventory.container.object.missionBoard.NetworkMissionBoardMission;
import necesse.inventory.container.object.missionBoard.SingleMissionUpdateEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.jobCondition.DoForeverJobCondition;
import necesse.level.maps.levelData.settlementData.jobCondition.DoWhenThresholdsJobCondition;
import necesse.level.maps.levelData.settlementData.jobCondition.DoXTimesJobCondition;
import necesse.level.maps.levelData.settlementData.jobCondition.JobCondition;

public class SettlementMissionBoardMission {
    public static final LinkedHashMap<String, Function<NetworkMissionBoardMission, JobCondition>> conditionLoaders = new LinkedHashMap();
    public final ServerSettlementData data;
    public final int uniqueID;
    public final SettlerExpedition expedition;
    public boolean allSettlersAssigned = false;
    public LinkedHashSet<Integer> assignedSettlers = new LinkedHashSet();
    public JobCondition condition = new DoXTimesJobCondition(0);

    protected SettlementMissionBoardMission(ServerSettlementData data, int uniqueID, SettlerExpedition expedition) {
        this.data = data;
        this.uniqueID = uniqueID;
        Objects.requireNonNull(expedition);
        this.expedition = expedition;
    }

    public SettlementMissionBoardMission(ServerSettlementData data, SettlerExpedition expedition) {
        this(data, SettlementMissionBoardMission.findNewUniqueID(data), expedition);
    }

    public SettlementMissionBoardMission(ServerSettlementData data, LoadData save) {
        LoadData conditionSave;
        this.data = data;
        this.uniqueID = save.getInt("uniqueID", -1);
        if (this.uniqueID == -1) {
            throw new LoadDataException("Could not find mission uniqueID");
        }
        String expeditionStringID = save.getSafeString("expeditionStringID", null);
        if (expeditionStringID == null) {
            throw new LoadDataException("Could not find mission expeditionStringID");
        }
        this.expedition = ExpeditionMissionRegistry.getExpedition(expeditionStringID);
        if (this.expedition == null) {
            throw new LoadDataException("Could not find mission board mission expedition based on stringID " + expeditionStringID);
        }
        this.allSettlersAssigned = save.getBoolean("allSettlersAssigned", false, false);
        if (!this.allSettlersAssigned) {
            this.assignedSettlers.addAll(save.getIntCollection("assignedSettlers", new ArrayList<Integer>(), false));
        }
        if ((conditionSave = save.getFirstLoadDataByName("CONDITION")) != null) {
            this.condition = JobCondition.getJobConditionFromSave(conditionSave);
        }
    }

    public static int findNewUniqueID(ServerSettlementData data) {
        int uniqueID;
        while ((uniqueID = GameRandom.globalRandom.nextInt()) == -1 || data.missionBoardManager.getMission(uniqueID) != null) {
        }
        return uniqueID;
    }

    public SaveData getSaveData(String name) {
        SaveData save = new SaveData(name);
        save.addInt("uniqueID", this.uniqueID);
        save.addSafeString("expeditionStringID", this.expedition.getStringID());
        if (this.allSettlersAssigned) {
            save.addBoolean("allSettlersAssigned", true);
        } else {
            save.addIntCollection("assignedSettlers", this.assignedSettlers);
        }
        SaveData conditionSave = new SaveData("CONDITION");
        this.condition.addSaveData(conditionSave);
        save.addSaveData(conditionSave);
        return save;
    }

    public boolean cleanAssignedSettlers() {
        boolean changed = this.assignedSettlers.removeIf(uniqueID -> this.data.getSettler((int)uniqueID) == null);
        if (changed) {
            new SingleMissionUpdateEvent(this.data, -1, this).applyAndSendToClientsAt(this.data.getLevel());
        }
        return changed;
    }

    static {
        conditionLoaders.put("doforever", mission -> new DoForeverJobCondition());
        conditionLoaders.put("doxtimes", mission -> new DoXTimesJobCondition());
        conditionLoaders.put("dowhenthreshold", mission -> new DoWhenThresholdsJobCondition());
    }
}

