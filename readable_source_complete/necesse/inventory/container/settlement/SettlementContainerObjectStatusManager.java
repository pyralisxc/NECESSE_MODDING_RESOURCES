/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementObjectStatusFormManager;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.actions.SubscribeStorageCustomAction;
import necesse.inventory.container.settlement.actions.SubscribeWorkstationCustomAction;
import necesse.inventory.container.settlement.actions.storage.ChangeAllowedSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.ChangeLimitsSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.FullUpdateSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.PriorityLimitSettlementStorageAction;
import necesse.inventory.container.settlement.actions.storage.RemoveSettlementStorageAction;
import necesse.inventory.container.settlement.actions.workstation.RemoveWorkstationAction;
import necesse.inventory.container.settlement.actions.workstation.RemoveWorkstationRecipeAction;
import necesse.inventory.container.settlement.actions.workstation.UpdateWorkstationRecipeAction;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementOpenStorageConfigEvent;
import necesse.inventory.container.settlement.events.SettlementOpenWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;

public class SettlementContainerObjectStatusManager {
    private final SettlementDependantContainer container;
    public final Level level;
    public final int tileX;
    public final int tileY;
    public final int masterTileX;
    public final int masterTileY;
    public final boolean canSettlementStorageConfigure;
    public boolean isSettlementStorage;
    public final boolean canSettlementWorkstationConfigure;
    public boolean isSettlementWorkstation;
    public EmptyCustomAction openSettlementStorageConfig;
    public SubscribeStorageCustomAction subscribeStorage;
    public RemoveSettlementStorageAction removeStorage;
    public ChangeAllowedSettlementStorageAction changeAllowedStorage;
    public ChangeLimitsSettlementStorageAction changeLimitsStorage;
    public PriorityLimitSettlementStorageAction priorityLimitStorage;
    public FullUpdateSettlementStorageAction fullUpdateSettlementStorage;
    public EmptyCustomAction openWorkstationConfig;
    public SubscribeWorkstationCustomAction subscribeWorkstation;
    public RemoveWorkstationAction removeWorkstation;
    public UpdateWorkstationRecipeAction updateWorkstationRecipe;
    public RemoveWorkstationRecipeAction removeWorkstationRecipe;

    public SettlementContainerObjectStatusManager(final SettlementDependantContainer container, Level level, int tileX, int tileY, PacketReader reader) {
        this.container = container;
        this.level = level;
        this.tileX = tileX;
        this.tileY = tileY;
        if (reader.getNextBoolean()) {
            this.masterTileX = reader.getNextInt();
            this.masterTileY = reader.getNextInt();
            this.canSettlementStorageConfigure = reader.getNextBoolean();
            if (this.canSettlementStorageConfigure) {
                this.isSettlementStorage = reader.getNextBoolean();
                container.subscribeEvent(SettlementSingleStorageEvent.class, e -> e.settlementUniqueID == container.getSettlementUniqueID() && e.tileX == this.masterTileX && e.tileY == this.masterTileY, () -> true);
                container.onEvent(SettlementSingleStorageEvent.class, event -> {
                    if (event.tileX == this.masterTileX && event.tileY == this.masterTileY) {
                        this.isSettlementStorage = event.exists;
                    }
                });
            }
            this.canSettlementWorkstationConfigure = reader.getNextBoolean();
            if (this.canSettlementWorkstationConfigure) {
                this.isSettlementWorkstation = reader.getNextBoolean();
                container.subscribeEvent(SettlementSingleWorkstationsEvent.class, e -> e.settlementUniqueID == container.getSettlementUniqueID() && e.tileX == this.masterTileX && e.tileY == this.masterTileY, () -> true);
                container.onEvent(SettlementSingleWorkstationsEvent.class, event -> {
                    if (event.tileX == this.masterTileX && event.tileY == this.masterTileY) {
                        this.isSettlementWorkstation = event.exists;
                    }
                });
            }
        } else {
            this.canSettlementStorageConfigure = false;
            this.canSettlementWorkstationConfigure = false;
            this.masterTileX = -1;
            this.masterTileY = -1;
        }
        this.openSettlementStorageConfig = container.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (SettlementContainerObjectStatusManager.this.canSettlementStorageConfigure && container.client.isServer()) {
                    ServerClient serverClient = container.client.getServerClient();
                    ServerSettlementData serverData = container.getServerData();
                    if (serverData != null) {
                        if (!serverData.networkData.doesClientHaveAccess(serverClient)) {
                            new SettlementDataEvent(serverData).applyAndSendToClient(serverClient);
                            return;
                        }
                        SettlementInventory inventory = serverData.storageManager.assignStorage(SettlementContainerObjectStatusManager.this.masterTileX, SettlementContainerObjectStatusManager.this.masterTileY, true);
                        if (inventory != null) {
                            new SettlementOpenStorageConfigEvent(inventory).applyAndSendToClient(serverClient);
                        }
                    } else {
                        new SettlementRemovedEvent(0).applyAndSendToClient(serverClient);
                    }
                }
            }
        });
        this.subscribeStorage = container.registerAction(new SubscribeStorageCustomAction(container));
        this.removeStorage = container.registerAction(new RemoveSettlementStorageAction(container));
        this.changeAllowedStorage = container.registerAction(new ChangeAllowedSettlementStorageAction(container));
        this.changeLimitsStorage = container.registerAction(new ChangeLimitsSettlementStorageAction(container));
        this.priorityLimitStorage = container.registerAction(new PriorityLimitSettlementStorageAction(container));
        this.fullUpdateSettlementStorage = container.registerAction(new FullUpdateSettlementStorageAction(container));
        this.openWorkstationConfig = container.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (container.client.isServer()) {
                    ServerClient serverClient = container.client.getServerClient();
                    ServerSettlementData serverData = container.getServerData();
                    if (serverData != null) {
                        if (!serverData.networkData.doesClientHaveAccess(serverClient)) {
                            new SettlementDataEvent(serverData).applyAndSendToClient(serverClient);
                            return;
                        }
                        SettlementWorkstation workstation = serverData.storageManager.assignWorkstation(SettlementContainerObjectStatusManager.this.masterTileX, SettlementContainerObjectStatusManager.this.masterTileY, true);
                        if (workstation != null) {
                            new SettlementOpenWorkstationEvent(workstation).applyAndSendToClient(serverClient);
                        }
                    } else {
                        new SettlementRemovedEvent(0).applyAndSendToClient(serverClient);
                    }
                }
            }
        });
        this.subscribeWorkstation = container.registerAction(new SubscribeWorkstationCustomAction(container));
        this.removeWorkstation = container.registerAction(new RemoveWorkstationAction(container));
        this.updateWorkstationRecipe = container.registerAction(new UpdateWorkstationRecipeAction(container));
        this.removeWorkstationRecipe = container.registerAction(new RemoveWorkstationRecipeAction(container));
    }

    public static void writeContent(ServerSettlementData settlementData, Level level, int tileX, int tileY, PacketWriter writer) {
        LevelObject levelObject = level.getLevelObject(tileX, tileY);
        LevelObject master = levelObject.getMasterLevelObject().orElse(null);
        if (master != null) {
            writer.putNextBoolean(true);
            writer.putNextInt(master.tileX);
            writer.putNextInt(master.tileY);
            ObjectEntity objectEntity = level.entityManager.getObjectEntity(master.tileX, master.tileY);
            if (objectEntity instanceof OEInventory) {
                OEInventory oeInventory = (OEInventory)((Object)objectEntity);
                if (oeInventory.getSettlementStorage() != null) {
                    writer.putNextBoolean(true);
                    writer.putNextBoolean(settlementData != null && settlementData.storageManager.getStorage(master.tileX, master.tileY) != null);
                } else {
                    writer.putNextBoolean(false);
                }
            } else {
                writer.putNextBoolean(false);
            }
            if (master.object instanceof SettlementWorkstationObject) {
                writer.putNextBoolean(true);
                writer.putNextBoolean(settlementData != null && settlementData.storageManager.getWorkstation(master.tileX, master.tileY) != null);
            } else {
                writer.putNextBoolean(false);
            }
        } else {
            writer.putNextBoolean(false);
            writer.putNextBoolean(false);
        }
    }

    public SettlementObjectStatusFormManager getFormManager(FormSwitcher switcher, FormComponent defaultForm, Client client) {
        return new SettlementObjectStatusFormManager(this.container, this, switcher, defaultForm, client);
    }
}

