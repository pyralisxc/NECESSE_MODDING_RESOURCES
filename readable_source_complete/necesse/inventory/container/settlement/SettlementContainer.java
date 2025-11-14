/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement;

import necesse.engine.GlobalData;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainGame;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.customAction.PointCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.actions.BanishSettlerAction;
import necesse.inventory.container.settlement.actions.ChangeClaimAction;
import necesse.inventory.container.settlement.actions.ChangeNameAction;
import necesse.inventory.container.settlement.actions.ChangePrivacyAction;
import necesse.inventory.container.settlement.actions.ChangeRestrictZoneAction;
import necesse.inventory.container.settlement.actions.CloneRestrictZoneAction;
import necesse.inventory.container.settlement.actions.CommandSettlersAttackAction;
import necesse.inventory.container.settlement.actions.CommandSettlersClearOrdersAction;
import necesse.inventory.container.settlement.actions.CommandSettlersFollowMeAction;
import necesse.inventory.container.settlement.actions.CommandSettlersGuardAction;
import necesse.inventory.container.settlement.actions.CommandSettlersSetHideOnLowHealthAction;
import necesse.inventory.container.settlement.actions.CreateNewRestrictZoneAction;
import necesse.inventory.container.settlement.actions.DeleteRestrictZoneAction;
import necesse.inventory.container.settlement.actions.DeleteWorkZoneCustomAction;
import necesse.inventory.container.settlement.actions.DisbandSettlementAction;
import necesse.inventory.container.settlement.actions.ExpandSettlementAction;
import necesse.inventory.container.settlement.actions.ForestryZoneConfigCustomAction;
import necesse.inventory.container.settlement.actions.HusbandryZoneConfigCustomAction;
import necesse.inventory.container.settlement.actions.LockNoSettlerRoomAction;
import necesse.inventory.container.settlement.actions.MoveSettlerRoomAction;
import necesse.inventory.container.settlement.actions.MoveSettlerSettlementAction;
import necesse.inventory.container.settlement.actions.RecolorRestrictZoneAction;
import necesse.inventory.container.settlement.actions.RenameRestrictZoneAction;
import necesse.inventory.container.settlement.actions.RenameSettlerNameAction;
import necesse.inventory.container.settlement.actions.RenameWorkZoneCustomAction;
import necesse.inventory.container.settlement.actions.RequestFullRestrictAction;
import necesse.inventory.container.settlement.actions.RequestJoinSettlementAction;
import necesse.inventory.container.settlement.actions.RequestMoveSettlerListCustomAction;
import necesse.inventory.container.settlement.actions.RequestSettlerBasicsAction;
import necesse.inventory.container.settlement.actions.RequestSettlerDietsAction;
import necesse.inventory.container.settlement.actions.RequestSettlerEquipmentFiltersAction;
import necesse.inventory.container.settlement.actions.RequestSettlerPrioritiesAction;
import necesse.inventory.container.settlement.actions.SetNewSettlerDietAction;
import necesse.inventory.container.settlement.actions.SetNewSettlerEquipmentFilterAction;
import necesse.inventory.container.settlement.actions.SetNewSettlerRestrictZoneAction;
import necesse.inventory.container.settlement.actions.SetSettlerDietAction;
import necesse.inventory.container.settlement.actions.SetSettlerEquipmentFilterAction;
import necesse.inventory.container.settlement.actions.SetSettlerPriorityAction;
import necesse.inventory.container.settlement.actions.SetSettlerRestrictZoneAction;
import necesse.inventory.container.settlement.actions.SetSettlerSelfManageEquipmentAction;
import necesse.inventory.container.settlement.actions.SubscribeDietsAction;
import necesse.inventory.container.settlement.actions.SubscribeEquipmentAction;
import necesse.inventory.container.settlement.actions.SubscribePrioritiesAction;
import necesse.inventory.container.settlement.actions.SubscribeRestrictAction;
import necesse.inventory.container.settlement.actions.SubscribeSettlerBasicsAction;
import necesse.inventory.container.settlement.actions.SubscribeStorageCustomAction;
import necesse.inventory.container.settlement.actions.SubscribeWorkContentCustomAction;
import necesse.inventory.container.settlement.actions.SubscribeWorkZoneConfigCustomAction;
import necesse.inventory.container.settlement.actions.SubscribeWorkstationCustomAction;
import necesse.inventory.container.settlement.actions.storage.AssignSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.ChangeAllowedSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.ChangeLimitsSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.FullUpdateSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.PriorityLimitSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.RemoveSettlementStorageAction;
import necesse.inventory.container.settlement.actions.workstation.AssignWorkstationAction;
import necesse.inventory.container.settlement.actions.workstation.RemoveWorkstationAction;
import necesse.inventory.container.settlement.actions.workstation.RemoveWorkstationRecipeAction;
import necesse.inventory.container.settlement.actions.workstation.UpdateWorkstationRecipeAction;
import necesse.inventory.container.settlement.actions.zones.CreateNewWorkZoneAction;
import necesse.inventory.container.settlement.actions.zones.ExpandWorkZoneAction;
import necesse.inventory.container.settlement.actions.zones.ShrinkWorkZoneAction;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementOpenStorageConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkZoneConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementSettlersChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;
import necesse.level.maps.levelData.settlementData.NetworkSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.notifications.SettlementNotificationSeverity;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class SettlementContainer
extends SettlementDependantContainer {
    public RequestJoinSettlementAction requestJoin;
    public BooleanCustomAction changePrivacy;
    public BooleanCustomAction changeClaim;
    public ChangeNameAction changeName;
    public ExpandSettlementAction expandSettlement;
    public EmptyCustomAction disbandSettlement;
    public RequestSettlerBasicsAction requestSettlerBasics;
    public SubscribeSettlerBasicsAction subscribeSettlerBasics;
    public RenameSettlerNameAction renameSettler;
    public MoveSettlerRoomAction moveSettlerRoom;
    public LockNoSettlerRoomAction lockNoSettlerRoom;
    public IntCustomAction banishSettler;
    public RequestMoveSettlerListCustomAction requestMoveSettlerList;
    public MoveSettlerSettlementAction moveSettlerSettlement;
    public CommandSettlersClearOrdersAction commandSettlersClearOrders;
    public CommandSettlersFollowMeAction commandSettlersFollow;
    public CommandSettlersGuardAction commandSettlersGuard;
    public CommandSettlersAttackAction commandSettlersAttack;
    public CommandSettlersSetHideOnLowHealthAction commandSettlersSetHideOnLowHealth;
    public SubscribeWorkContentCustomAction subscribeWorkContent;
    public RequestSettlerPrioritiesAction requestSettlerPriorities;
    public SubscribePrioritiesAction subscribePriorities;
    public SetSettlerPriorityAction setSettlerPriority;
    public RequestSettlerEquipmentFiltersAction requestSettlerEquipmentFilters;
    public SubscribeEquipmentAction subscribeEquipment;
    public SetSettlerSelfManageEquipmentAction setSettlerSelfManageEquipment;
    public SetNewSettlerEquipmentFilterAction setNewSettlerEquipmentFilter;
    public SetSettlerEquipmentFilterAction setSettlerEquipmentFilter;
    public RequestSettlerDietsAction requestSettlerDiets;
    public SubscribeDietsAction subscribeDiets;
    public SetNewSettlerDietAction setNewSettlerDiet;
    public SetSettlerDietAction setSettlerDiet;
    public RequestFullRestrictAction requestFullRestricts;
    public SubscribeRestrictAction subscribeRestrict;
    public SetNewSettlerRestrictZoneAction setNewSettlerRestrictZone;
    public SetSettlerRestrictZoneAction setSettlerRestrictZone;
    public CreateNewRestrictZoneAction createNewRestrictZone;
    public CloneRestrictZoneAction cloneRestrictZone;
    public ChangeRestrictZoneAction changeRestrictZone;
    public RenameRestrictZoneAction renameRestrictZone;
    public RecolorRestrictZoneAction recolorRestrictZone;
    public DeleteRestrictZoneAction deleteRestrictZone;
    public PointCustomAction openStorage;
    public SubscribeStorageCustomAction subscribeStorage;
    public AssignSettlementStorageAction assignStorage;
    public RemoveSettlementStorageAction removeStorage;
    public ChangeAllowedSettlementStorageAction changeAllowedStorage;
    public ChangeLimitsSettlementStorageAction changeLimitsStorage;
    public PriorityLimitSettlementStorageAction priorityLimitStorage;
    public FullUpdateSettlementStorageAction fullUpdateSettlementStorage;
    public PointCustomAction openWorkstation;
    public SubscribeWorkstationCustomAction subscribeWorkstation;
    public AssignWorkstationAction assignWorkstation;
    public RemoveWorkstationAction removeWorkstation;
    public UpdateWorkstationRecipeAction updateWorkstationRecipe;
    public RemoveWorkstationRecipeAction removeWorkstationRecipe;
    public CreateNewWorkZoneAction createWorkZone;
    public ExpandWorkZoneAction expandWorkZone;
    public ShrinkWorkZoneAction shrinkWorkZone;
    public DeleteWorkZoneCustomAction deleteWorkZone;
    public RenameWorkZoneCustomAction renameWorkZone;
    public IntCustomAction openWorkZoneConfig;
    public SubscribeWorkZoneConfigCustomAction subscribeWorkZoneConfig;
    public ForestryZoneConfigCustomAction forestryZoneConfig;
    public HusbandryZoneConfigCustomAction husbandryZoneConfig;

    public SettlementContainer(final NetworkClient client, int uniqueSeed, SettlementDataEvent settlement, Packet contentPacket) {
        super(client, uniqueSeed, settlement, false);
        this.subscribeEvent(SettlementSettlersChangedEvent.class, e -> e.settlementUniqueID == this.getSettlementUniqueID(), () -> true);
        this.requestJoin = this.registerAction(new RequestJoinSettlementAction(this));
        this.changePrivacy = this.registerAction(new ChangePrivacyAction(this));
        this.changeClaim = this.registerAction(new ChangeClaimAction(this));
        this.changeName = this.registerAction(new ChangeNameAction(this));
        this.expandSettlement = this.registerAction(new ExpandSettlementAction(this));
        this.disbandSettlement = this.registerAction(new DisbandSettlementAction(this));
        this.requestSettlerBasics = this.registerAction(new RequestSettlerBasicsAction(this));
        this.subscribeSettlerBasics = this.registerAction(new SubscribeSettlerBasicsAction(this));
        this.renameSettler = this.registerAction(new RenameSettlerNameAction(this));
        this.moveSettlerRoom = this.registerAction(new MoveSettlerRoomAction(this));
        this.lockNoSettlerRoom = this.registerAction(new LockNoSettlerRoomAction(this));
        this.banishSettler = this.registerAction(new BanishSettlerAction(this));
        this.requestMoveSettlerList = this.registerAction(new RequestMoveSettlerListCustomAction(this));
        this.moveSettlerSettlement = this.registerAction(new MoveSettlerSettlementAction(this));
        this.commandSettlersClearOrders = this.registerAction(new CommandSettlersClearOrdersAction(this));
        this.commandSettlersFollow = this.registerAction(new CommandSettlersFollowMeAction(this));
        this.commandSettlersGuard = this.registerAction(new CommandSettlersGuardAction(this));
        this.commandSettlersAttack = this.registerAction(new CommandSettlersAttackAction(this));
        this.commandSettlersSetHideOnLowHealth = this.registerAction(new CommandSettlersSetHideOnLowHealthAction(this));
        this.subscribeWorkContent = this.registerAction(new SubscribeWorkContentCustomAction(this));
        this.requestSettlerPriorities = this.registerAction(new RequestSettlerPrioritiesAction(this));
        this.subscribePriorities = this.registerAction(new SubscribePrioritiesAction(this));
        this.setSettlerPriority = this.registerAction(new SetSettlerPriorityAction(this));
        this.requestSettlerEquipmentFilters = this.registerAction(new RequestSettlerEquipmentFiltersAction(this));
        this.subscribeEquipment = this.registerAction(new SubscribeEquipmentAction(this));
        this.setSettlerSelfManageEquipment = this.registerAction(new SetSettlerSelfManageEquipmentAction(this));
        this.setNewSettlerEquipmentFilter = this.registerAction(new SetNewSettlerEquipmentFilterAction(this));
        this.setSettlerEquipmentFilter = this.registerAction(new SetSettlerEquipmentFilterAction(this));
        this.requestSettlerDiets = this.registerAction(new RequestSettlerDietsAction(this));
        this.subscribeDiets = this.registerAction(new SubscribeDietsAction(this));
        this.setNewSettlerDiet = this.registerAction(new SetNewSettlerDietAction(this));
        this.setSettlerDiet = this.registerAction(new SetSettlerDietAction(this));
        this.requestFullRestricts = this.registerAction(new RequestFullRestrictAction(this));
        this.subscribeRestrict = this.registerAction(new SubscribeRestrictAction(this));
        this.setNewSettlerRestrictZone = this.registerAction(new SetNewSettlerRestrictZoneAction(this));
        this.setSettlerRestrictZone = this.registerAction(new SetSettlerRestrictZoneAction(this));
        this.createNewRestrictZone = this.registerAction(new CreateNewRestrictZoneAction(this));
        this.cloneRestrictZone = this.registerAction(new CloneRestrictZoneAction(this));
        this.changeRestrictZone = this.registerAction(new ChangeRestrictZoneAction(this));
        this.renameRestrictZone = this.registerAction(new RenameRestrictZoneAction(this));
        this.recolorRestrictZone = this.registerAction(new RecolorRestrictZoneAction(this));
        this.deleteRestrictZone = this.registerAction(new DeleteRestrictZoneAction(this));
        this.openStorage = this.registerAction(new PointCustomAction(){

            @Override
            protected void run(int x, int y) {
                SettlementInventory storage;
                ServerSettlementData serverData;
                if (client.isServer() && (serverData = SettlementContainer.this.getServerData()) != null && (storage = serverData.storageManager.getStorage(x, y)) != null) {
                    new SettlementOpenStorageConfigEvent(storage).applyAndSendToClient(client.getServerClient());
                }
            }
        });
        this.subscribeStorage = this.registerAction(new SubscribeStorageCustomAction(this));
        this.assignStorage = this.registerAction(new AssignSettlementStorageAction(this));
        this.removeStorage = this.registerAction(new RemoveSettlementStorageAction(this));
        this.changeAllowedStorage = this.registerAction(new ChangeAllowedSettlementStorageAction(this));
        this.changeLimitsStorage = this.registerAction(new ChangeLimitsSettlementStorageAction(this));
        this.priorityLimitStorage = this.registerAction(new PriorityLimitSettlementStorageAction(this));
        this.fullUpdateSettlementStorage = this.registerAction(new FullUpdateSettlementStorageAction(this));
        this.openWorkstation = this.registerAction(new PointCustomAction(){

            @Override
            protected void run(int x, int y) {
                SettlementWorkstation workstation;
                ServerSettlementData serverData;
                if (client.isServer() && (serverData = SettlementContainer.this.getServerData()) != null && (workstation = serverData.storageManager.getWorkstation(x, y)) != null) {
                    new SettlementOpenWorkstationEvent(workstation).applyAndSendToClient(client.getServerClient());
                }
            }
        });
        this.subscribeWorkstation = this.registerAction(new SubscribeWorkstationCustomAction(this));
        this.assignWorkstation = this.registerAction(new AssignWorkstationAction(this));
        this.removeWorkstation = this.registerAction(new RemoveWorkstationAction(this));
        this.updateWorkstationRecipe = this.registerAction(new UpdateWorkstationRecipeAction(this));
        this.removeWorkstationRecipe = this.registerAction(new RemoveWorkstationRecipeAction(this));
        this.createWorkZone = this.registerAction(new CreateNewWorkZoneAction(this));
        this.expandWorkZone = this.registerAction(new ExpandWorkZoneAction(this));
        this.shrinkWorkZone = this.registerAction(new ShrinkWorkZoneAction(this));
        this.deleteWorkZone = this.registerAction(new DeleteWorkZoneCustomAction(this));
        this.renameWorkZone = this.registerAction(new RenameWorkZoneCustomAction(this));
        this.openWorkZoneConfig = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                ServerSettlementData serverData;
                if (client.isServer() && (serverData = SettlementContainer.this.getServerData()) != null) {
                    SettlementWorkZone zone = serverData.getWorkZones().getZone(value);
                    if (zone == null) {
                        new SettlementWorkZoneRemovedEvent(serverData, value).applyAndSendToClient(client.getServerClient());
                    } else {
                        new SettlementOpenWorkZoneConfigEvent(zone).applyAndSendToClient(client.getServerClient());
                    }
                }
            }
        });
        this.subscribeWorkZoneConfig = this.registerAction(new SubscribeWorkZoneConfigCustomAction(this));
        this.forestryZoneConfig = this.registerAction(new ForestryZoneConfigCustomAction(this));
        this.husbandryZoneConfig = this.registerAction(new HusbandryZoneConfigCustomAction(this));
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        ServerSettlementData serverData = this.getServerData();
        return serverData != null && !serverData.networkData.isUnloadedOrDisbanded();
    }

    @Override
    public void init() {
        super.init();
        if (this.client.isClient() && GlobalData.getCurrentState() instanceof MainGame) {
            ((MainGame)GlobalData.getCurrentState()).formManager.settlementNotifications.showSeverityAbove(SettlementNotificationSeverity.NOTE);
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.client.isClient() && GlobalData.getCurrentState() instanceof MainGame) {
            ((MainGame)GlobalData.getCurrentState()).formManager.settlementNotifications.showSeverityAbove(SettlementNotificationSeverity.URGENT);
        }
    }

    public static boolean hasAccess(NetworkSettlementData networkData, CachedSettlementData cachedData, ServerClient client) {
        return cachedData.getOwnerAuth() == client.authentication || client.isSameTeam(cachedData.getTeamID()) || cachedData.getOwnerAuth() != -1L && networkData.getOwnerAuth() == cachedData.getOwnerAuth() || cachedData.getTeamID() != -1 && networkData.getTeamID() == cachedData.getTeamID();
    }
}

