/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object.missionBoard;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.object.missionBoard.AllMissionsUpdateEvent;
import necesse.inventory.container.object.missionBoard.AvailableExpeditionSettlersResponseEvent;
import necesse.inventory.container.object.missionBoard.DeletedMissionUpdateEvent;
import necesse.inventory.container.object.missionBoard.MissionBoardSlotsUpdateEvent;
import necesse.inventory.container.object.missionBoard.NetworkMissionBoardMission;
import necesse.inventory.container.object.missionBoard.SingleMissionUpdateEvent;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.actions.RequestJoinSettlementAction;
import necesse.inventory.container.settlement.actions.RequestSettlerListAction;
import necesse.inventory.container.settlement.data.SettlementSettlerData;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementSettlerListEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementMissionBoardManager;
import necesse.level.maps.levelData.settlementData.SettlementMissionBoardMission;
import necesse.level.maps.levelData.settlementData.jobCondition.JobCondition;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class MissionBoardContainer
extends SettlementDependantContainer {
    public LinkedHashSet<Integer> validExpeditionIDs;
    public RequestSettlerListAction requestSettlerList;
    public ArrayList<SettlementSettlerData> settlers;
    public int missionBoardSlots;
    public int nextSlotCost;
    public ArrayList<NetworkMissionBoardMission> missionBoardMissions;
    public RequestJoinSettlementAction requestJoinSettlementAction;
    public IntCustomAction addNewMissionAction;
    public IntCustomAction deleteMissionAction;
    public IntCustomAction requestAvailableSettlersAction;
    public AssignMissionSettlerAction assignSettlerAction;
    public SetMissionSlotAction setMissionSlotAction;
    public SetConditionPacketAction setConditionAction;
    public UpdateConditionPacketAction updateConditionAction;
    public EmptyCustomAction buyMoreSlotsAction;

    public MissionBoardContainer(final NetworkClient client, int uniqueSeed, SettlementDataEvent settlement, Packet content) {
        super(client, uniqueSeed, settlement, false);
        PacketReader reader = new PacketReader(content);
        this.settlers = new SettlementSettlerListEvent((PacketReader)reader).settlers;
        this.subscribeEvent(SettlementSettlerListEvent.class, e -> true, () -> true);
        this.onEvent(SettlementSettlerListEvent.class, (T event) -> {
            this.settlers = event.settlers;
        });
        this.validExpeditionIDs = reader.getNextCollection(LinkedHashSet::new, reader::getNextShortUnsigned);
        this.requestSettlerList = this.registerAction(new RequestSettlerListAction(this));
        this.subscribeEvent(SettlementSettlersChangedEvent.class, e -> e.settlementUniqueID == this.getSettlementUniqueID(), () -> true);
        if (client.isClient()) {
            this.onEvent(SettlementSettlersChangedEvent.class, (T event) -> this.requestSettlerList.runAndSend());
        }
        this.missionBoardSlots = reader.getNextShortUnsigned();
        this.nextSlotCost = reader.getNextInt();
        this.subscribeEvent(MissionBoardSlotsUpdateEvent.class, e -> e.settlementUniqueID == this.getSettlementUniqueID(), () -> true);
        this.onEvent(MissionBoardSlotsUpdateEvent.class, (T event) -> {
            this.missionBoardSlots = event.slots;
            this.nextSlotCost = event.nextSlotCost;
        });
        this.missionBoardMissions = reader.getNextCollection(ArrayList::new, () -> new NetworkMissionBoardMission(reader));
        this.subscribeEvent(AllMissionsUpdateEvent.class, e -> e.settlementUniqueID == this.getSettlementUniqueID(), () -> true);
        this.onEvent(AllMissionsUpdateEvent.class, (T event) -> {
            this.missionBoardMissions = event.missions;
        });
        this.subscribeEvent(SingleMissionUpdateEvent.class, e -> e.settlementUniqueID == this.getSettlementUniqueID(), () -> true);
        this.onEvent(SingleMissionUpdateEvent.class, (T event) -> {
            if (event.slot == -1) {
                for (int i = 0; i < this.missionBoardMissions.size(); ++i) {
                    if (this.missionBoardMissions.get((int)i).uniqueID != event.mission.uniqueID) continue;
                    this.missionBoardMissions.set(i, event.mission);
                    return;
                }
            } else {
                this.missionBoardMissions.removeIf(mission -> mission.uniqueID == event.mission.uniqueID);
                if (event.slot > this.missionBoardMissions.size()) {
                    this.missionBoardMissions.add(event.mission);
                } else {
                    this.missionBoardMissions.add(event.slot, event.mission);
                }
            }
        });
        this.subscribeEvent(DeletedMissionUpdateEvent.class, e -> e.missionUniqueID == this.getSettlementUniqueID(), () -> true);
        this.onEvent(DeletedMissionUpdateEvent.class, (T event) -> this.missionBoardMissions.removeIf(mission -> mission.uniqueID == event.missionUniqueID));
        this.requestJoinSettlementAction = this.registerAction(new RequestJoinSettlementAction(this));
        this.addNewMissionAction = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                if (client.isServer()) {
                    ServerSettlementData serverData = MissionBoardContainer.this.getServerData();
                    if (serverData != null) {
                        if (!serverData.networkData.doesClientHaveAccess(client.getServerClient())) {
                            new SettlementDataEvent(serverData).applyAndSendToClient(client.getServerClient());
                            return;
                        }
                        if (serverData.missionBoardManager.getMissions().size() >= serverData.missionBoardManager.missionBoardSlots) {
                            new MissionBoardSlotsUpdateEvent(serverData.missionBoardManager).applyAndSendToClient(client.getServerClient());
                            return;
                        }
                        serverData.missionBoardManager.addNewMission(ExpeditionMissionRegistry.getExpedition(value));
                    } else {
                        new SettlementRemovedEvent(0).applyAndSendToClient(client.getServerClient());
                    }
                }
            }
        });
        this.deleteMissionAction = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                if (client.isServer()) {
                    ServerSettlementData serverData = MissionBoardContainer.this.getServerData();
                    if (serverData != null) {
                        if (!serverData.networkData.doesClientHaveAccess(client.getServerClient())) {
                            new SettlementDataEvent(serverData).applyAndSendToClient(client.getServerClient());
                            return;
                        }
                        serverData.missionBoardManager.deleteMission(value);
                    } else {
                        new SettlementRemovedEvent(0).applyAndSendToClient(client.getServerClient());
                    }
                }
            }
        });
        this.requestAvailableSettlersAction = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                if (client.isServer()) {
                    ServerSettlementData serverData = MissionBoardContainer.this.getServerData();
                    if (serverData != null) {
                        if (!serverData.networkData.doesClientHaveAccess(client.getServerClient())) {
                            new SettlementDataEvent(serverData).applyAndSendToClient(client.getServerClient());
                            return;
                        }
                        SettlementMissionBoardMission mission = serverData.missionBoardManager.getMission(value);
                        if (mission != null) {
                            mission.cleanAssignedSettlers();
                            ArrayList<SettlementSettlerData> availableSettlers = new ArrayList<SettlementSettlerData>();
                            for (LevelSettler settler : serverData.settlers) {
                                SettlerMob mob = settler.getMob();
                                if (!(mob instanceof HumanMob) || !((HumanMob)mob).canDoExpedition(mission.expedition)) continue;
                                availableSettlers.add(new SettlementSettlerData(settler));
                            }
                            new AvailableExpeditionSettlersResponseEvent(value, availableSettlers).applyAndSendToClient(client.getServerClient());
                        } else {
                            new AllMissionsUpdateEvent(serverData).applyAndSendToClient(client.getServerClient());
                        }
                    } else {
                        new SettlementRemovedEvent(0).applyAndSendToClient(client.getServerClient());
                    }
                }
            }
        });
        this.assignSettlerAction = this.registerAction(new AssignMissionSettlerAction(){

            @Override
            protected void run(int missionUniqueID, int mobUniqueID, boolean assigned) {
                if (client.isServer()) {
                    ServerSettlementData serverData = MissionBoardContainer.this.getServerData();
                    if (serverData != null) {
                        if (!serverData.networkData.doesClientHaveAccess(client.getServerClient())) {
                            new SettlementDataEvent(serverData).applyAndSendToClient(client.getServerClient());
                            return;
                        }
                        SettlementMissionBoardMission mission = serverData.missionBoardManager.getMission(missionUniqueID);
                        if (mission != null) {
                            if (mobUniqueID == -1) {
                                if (mission.allSettlersAssigned != assigned) {
                                    mission.allSettlersAssigned = assigned;
                                    mission.assignedSettlers.clear();
                                    new SingleMissionUpdateEvent(serverData, -1, mission).applyAndSendToClientsAt(serverData.getLevel());
                                }
                            } else if (assigned) {
                                LevelSettler settler = serverData.getSettler(mobUniqueID);
                                if (settler != null) {
                                    boolean changed;
                                    SettlerMob mob = settler.getMob();
                                    if (mob instanceof HumanMob && ((HumanMob)mob).canDoExpedition(mission.expedition) && (changed = mission.assignedSettlers.add(mobUniqueID))) {
                                        new SingleMissionUpdateEvent(serverData, -1, mission).applyAndSendToClientsAt(serverData.getLevel());
                                    }
                                } else {
                                    new SettlementSettlersChangedEvent(serverData).applyAndSendToClient(client.getServerClient());
                                }
                            } else {
                                boolean changed = mission.assignedSettlers.remove(mobUniqueID);
                                if (changed) {
                                    new SingleMissionUpdateEvent(serverData, -1, mission).applyAndSendToClientsAt(serverData.getLevel());
                                }
                            }
                        } else {
                            new AllMissionsUpdateEvent(serverData).applyAndSendToClient(client.getServerClient());
                        }
                    } else {
                        new SettlementRemovedEvent(0).applyAndSendToClient(client.getServerClient());
                    }
                }
            }
        });
        this.setMissionSlotAction = this.registerAction(new SetMissionSlotAction(){

            @Override
            protected void run(int missionUniqueID, int slot) {
                if (client.isServer()) {
                    ServerSettlementData serverData = MissionBoardContainer.this.getServerData();
                    if (serverData != null) {
                        if (!serverData.networkData.doesClientHaveAccess(client.getServerClient())) {
                            new SettlementDataEvent(serverData).applyAndSendToClient(client.getServerClient());
                            return;
                        }
                        SettlementMissionBoardMission mission = serverData.missionBoardManager.setMissionSlot(missionUniqueID, slot);
                        if (mission != null) {
                            new SingleMissionUpdateEvent(serverData, slot, mission).applyAndSendToClientsAtExcept(serverData.getLevel(), client.getServerClient());
                        } else {
                            new AllMissionsUpdateEvent(serverData).applyAndSendToClient(client.getServerClient());
                        }
                    } else {
                        new SettlementRemovedEvent(0).applyAndSendToClient(client.getServerClient());
                    }
                }
            }
        });
        this.setConditionAction = this.registerAction(new SetConditionPacketAction(){

            @Override
            protected void run(int missionUniqueID, JobCondition condition) {
                if (client.isServer()) {
                    ServerSettlementData serverData = MissionBoardContainer.this.getServerData();
                    if (serverData != null) {
                        if (!serverData.networkData.doesClientHaveAccess(client.getServerClient())) {
                            new SettlementDataEvent(serverData).applyAndSendToClient(client.getServerClient());
                            return;
                        }
                        SettlementMissionBoardMission mission = serverData.missionBoardManager.getMission(missionUniqueID);
                        if (mission != null) {
                            mission.condition = condition;
                            mission.condition.markClean();
                            new SingleMissionUpdateEvent(serverData, -1, mission).applyAndSendToClientsAtExcept(serverData.getLevel(), client.getServerClient());
                        } else {
                            new AllMissionsUpdateEvent(serverData).applyAndSendToClient(client.getServerClient());
                        }
                    } else {
                        new SettlementRemovedEvent(0).applyAndSendToClient(client.getServerClient());
                    }
                }
            }
        });
        this.updateConditionAction = this.registerAction(new UpdateConditionPacketAction(){

            @Override
            protected void run(int expectedConditionID, int missionUniqueID, int type, Packet updatePacket) {
                if (client.isServer()) {
                    ServerSettlementData serverData = MissionBoardContainer.this.getServerData();
                    if (serverData != null) {
                        if (!serverData.networkData.doesClientHaveAccess(client.getServerClient())) {
                            new SettlementDataEvent(serverData).applyAndSendToClient(client.getServerClient());
                            return;
                        }
                        SettlementMissionBoardMission mission = serverData.missionBoardManager.getMission(missionUniqueID);
                        if (mission != null) {
                            if (mission.condition.getID() == expectedConditionID) {
                                mission.condition.applyUpdatePacket(type, new PacketReader(updatePacket));
                                if (mission.condition.isDirty()) {
                                    mission.condition.markClean();
                                    new SingleMissionUpdateEvent(serverData, -1, mission).applyAndSendToClientsAtExcept(serverData.getLevel(), client.getServerClient());
                                }
                            } else {
                                new SingleMissionUpdateEvent(serverData, -1, mission).applyAndSendToClient(client.getServerClient());
                            }
                        } else {
                            new AllMissionsUpdateEvent(serverData).applyAndSendToClient(client.getServerClient());
                        }
                    } else {
                        new SettlementRemovedEvent(0).applyAndSendToClient(client.getServerClient());
                    }
                }
            }
        });
        this.buyMoreSlotsAction = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (client.isServer()) {
                    ServerSettlementData serverData = MissionBoardContainer.this.getServerData();
                    if (serverData != null) {
                        if (!serverData.networkData.doesClientHaveAccess(client.getServerClient())) {
                            new SettlementDataEvent(serverData).applyAndSendToClient(client.getServerClient());
                            return;
                        }
                        if (serverData.missionBoardManager.getMissions().size() >= SettlementMissionBoardManager.maxSlots) {
                            new MissionBoardSlotsUpdateEvent(serverData.missionBoardManager).applyAndSendToClient(client.getServerClient());
                            return;
                        }
                        int cost = SettlementMissionBoardManager.getNextSlotCost(serverData.missionBoardManager.missionBoardSlots);
                        Ingredient[] ingredients = new Ingredient[]{new Ingredient("coin", cost)};
                        CanCraft canCraft = MissionBoardContainer.this.canCraftRecipe(ingredients, MissionBoardContainer.this.getCraftInventories(), true);
                        if (canCraft.canCraft()) {
                            Recipe.craft(ingredients, client.playerMob.getLevel(), client.playerMob, MissionBoardContainer.this.getCraftInventories());
                            ++serverData.missionBoardManager.missionBoardSlots;
                            new MissionBoardSlotsUpdateEvent(serverData.missionBoardManager).applyAndSendToClientsAt(serverData.getLevel());
                        } else {
                            new MissionBoardSlotsUpdateEvent(serverData.missionBoardManager).applyAndSendToClient(client.getServerClient());
                        }
                    } else {
                        new SettlementRemovedEvent(0).applyAndSendToClient(client.getServerClient());
                    }
                }
            }
        });
    }

    public static Packet getContainerContent(ServerSettlementData settlementData) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        new SettlementSettlerListEvent(settlementData).write(writer);
        LinkedHashSet<Integer> validExpeditionIDs = new LinkedHashSet<Integer>();
        for (SettlerExpedition expedition : ExpeditionMissionRegistry.getExpeditions()) {
            if (!expedition.isAvailable(settlementData)) continue;
            validExpeditionIDs.add(expedition.getID());
        }
        writer.putNextCollection(validExpeditionIDs, writer::putNextShortUnsigned);
        writer.putNextShortUnsigned(settlementData.missionBoardManager.missionBoardSlots);
        writer.putNextInt(SettlementMissionBoardManager.getNextSlotCost(settlementData.missionBoardManager.missionBoardSlots));
        writer.putNextCollection(settlementData.missionBoardManager.getMissions(), mission -> new NetworkMissionBoardMission(settlementData, (SettlementMissionBoardMission)mission).writePacket(writer));
        return packet;
    }

    public static abstract class AssignMissionSettlerAction
    extends ContainerCustomAction {
        public void runAndSend(int missionUniqueID, int mobUniqueID, boolean assigned) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(missionUniqueID);
            writer.putNextInt(mobUniqueID);
            writer.putNextBoolean(assigned);
            this.runAndSendAction(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int missionUniqueID = reader.getNextInt();
            int mobUniqueID = reader.getNextInt();
            boolean assigned = reader.getNextBoolean();
            this.run(missionUniqueID, mobUniqueID, assigned);
        }

        protected abstract void run(int var1, int var2, boolean var3);
    }

    public static abstract class SetMissionSlotAction
    extends ContainerCustomAction {
        public void runAndSend(NetworkMissionBoardMission mission, int slot) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(mission.uniqueID);
            writer.putNextInt(slot);
            this.runAndSendAction(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int missionUniqueID = reader.getNextInt();
            int slot = reader.getNextInt();
            this.run(missionUniqueID, slot);
        }

        protected abstract void run(int var1, int var2);
    }

    public static abstract class SetConditionPacketAction
    extends ContainerCustomAction {
        public void runAndSend(int missionUniqueID, JobCondition condition) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextInt(missionUniqueID);
            JobCondition.writeContentPacket(condition, writer);
            this.runAndSendAction(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int missionUniqueID = reader.getNextInt();
            JobCondition condition = JobCondition.fromContentPacket(reader);
            this.run(missionUniqueID, condition);
        }

        protected abstract void run(int var1, JobCondition var2);
    }

    public static abstract class UpdateConditionPacketAction
    extends ContainerCustomAction {
        public void runAndSend(NetworkMissionBoardMission mission, int type, Packet updatePacket) {
            Packet content = new Packet();
            PacketWriter writer = new PacketWriter(content);
            writer.putNextShortUnsigned(mission.condition.getID());
            writer.putNextInt(mission.uniqueID);
            writer.putNextInt(type);
            writer.putNextContentPacket(updatePacket);
            this.runAndSendAction(content);
        }

        @Override
        public void executePacket(PacketReader reader) {
            int conditionID = reader.getNextShortUnsigned();
            int missionUniqueID = reader.getNextInt();
            int type = reader.getNextInt();
            Packet updatePacket = reader.getNextContentPacket();
            this.run(conditionID, missionUniqueID, type, updatePacket);
        }

        protected abstract void run(int var1, int var2, int var3, Packet var4);
    }
}

