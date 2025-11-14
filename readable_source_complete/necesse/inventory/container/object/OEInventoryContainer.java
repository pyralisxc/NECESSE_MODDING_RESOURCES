/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOEInventoryNameUpdate;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.StringCustomAction;
import necesse.inventory.container.settlement.SettlementContainerObjectStatusManager;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.OEInventoryContainerSlot;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class OEInventoryContainer
extends SettlementDependantContainer {
    public StringCustomAction renameButton;
    public EmptyCustomAction quickStackButton;
    public EmptyCustomAction transferAll;
    public EmptyCustomAction restockButton;
    public EmptyCustomAction lootButton;
    public EmptyCustomAction sortButton;
    public final OEInventory oeInventory;
    public final ObjectEntity objectEntity;
    public final OEUsers oeUsers;
    public int INVENTORY_START = -1;
    public int INVENTORY_END = -1;
    public SettlementContainerObjectStatusManager settlementObjectManager;

    public OEInventoryContainer(final NetworkClient client, int uniqueSeed, SettlementDataEvent settlement, final OEInventory oeInventory, PacketReader reader) {
        super(client, uniqueSeed, settlement, true);
        this.oeInventory = oeInventory;
        this.objectEntity = (ObjectEntity)((Object)oeInventory);
        oeInventory.triggerInteracted();
        this.oeUsers = this.objectEntity instanceof OEUsers ? (OEUsers)((Object)this.objectEntity) : null;
        if (client.isServer() & this.oeUsers != null) {
            this.oeUsers.startUser(client.playerMob);
        }
        this.settlementObjectManager = new SettlementContainerObjectStatusManager(this, this.objectEntity.getLevel(), this.objectEntity.tileX, this.objectEntity.tileY, reader);
        InventoryRange inventoryRange = this.getOEInventoryRange();
        for (int i = inventoryRange.startSlot; i <= inventoryRange.endSlot; ++i) {
            int index = this.addSlot(this.getOEContainerSlot(oeInventory, i));
            if (this.INVENTORY_START == -1) {
                this.INVENTORY_START = index;
            }
            if (this.INVENTORY_END == -1) {
                this.INVENTORY_END = index;
            }
            this.INVENTORY_START = Math.min(this.INVENTORY_START, index);
            this.INVENTORY_END = Math.max(this.INVENTORY_END, index);
        }
        this.addInventoryQuickTransfer(this.INVENTORY_START, this.INVENTORY_END);
        this.renameButton = this.registerAction(new StringCustomAction(){

            @Override
            protected void run(String value) {
                if (oeInventory.canSetInventoryName()) {
                    oeInventory.setInventoryName(value);
                    if (client.isServer()) {
                        client.getServerClient().getServer().network.sendToClientsWithEntity(new PacketOEInventoryNameUpdate(oeInventory, value), OEInventoryContainer.this.objectEntity);
                    }
                }
            }
        });
        this.quickStackButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (oeInventory.canQuickStackInventory()) {
                    ArrayList<InventoryRange> targets = new ArrayList<InventoryRange>(Collections.singleton(OEInventoryContainer.this.getOEInventoryRange()));
                    OEInventoryContainer.this.quickStackToInventories(targets, client.playerMob.getInv().main);
                }
            }
        });
        this.transferAll = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                for (int i = OEInventoryContainer.this.CLIENT_INVENTORY_START; i <= OEInventoryContainer.this.CLIENT_INVENTORY_END; ++i) {
                    if (OEInventoryContainer.this.getSlot(i).isItemLocked()) continue;
                    OEInventoryContainer.this.transferToSlots(OEInventoryContainer.this.getSlot(i), OEInventoryContainer.this.INVENTORY_START, OEInventoryContainer.this.INVENTORY_END, "transferall");
                }
            }
        });
        this.restockButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                ArrayList<InventoryRange> targets = new ArrayList<InventoryRange>(Collections.singleton(OEInventoryContainer.this.getOEInventoryRange()));
                OEInventoryContainer.this.restockFromInventories(targets, client.playerMob.getInv().main);
            }
        });
        this.lootButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                for (int i = OEInventoryContainer.this.INVENTORY_START; i <= OEInventoryContainer.this.INVENTORY_END; ++i) {
                    if (OEInventoryContainer.this.getSlot(i).isItemLocked()) continue;
                    OEInventoryContainer.this.transferToSlots(OEInventoryContainer.this.getSlot(i), Arrays.asList(new SlotIndexRange(OEInventoryContainer.this.CLIENT_HOTBAR_START, OEInventoryContainer.this.CLIENT_HOTBAR_END), new SlotIndexRange(OEInventoryContainer.this.CLIENT_INVENTORY_START, OEInventoryContainer.this.CLIENT_INVENTORY_END)), "lootall");
                }
            }
        });
        this.sortButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (oeInventory.canSortInventory()) {
                    InventoryRange range = OEInventoryContainer.this.getOEInventoryRange();
                    range.inventory.sortItems(client.playerMob.getLevel(), client.playerMob, range.startSlot, range.endSlot);
                }
            }
        });
    }

    public ContainerSlot getOEContainerSlot(OEInventory oeInventory, int slot) {
        return new OEInventoryContainerSlot(oeInventory, slot);
    }

    @Override
    public void lootAllControlPressed() {
        this.lootButton.runAndSend();
    }

    @Override
    public void sortInventoryControlPressed() {
        this.sortButton.runAndSend();
    }

    @Override
    public void quickStackControlPressed() {
        this.quickStackButton.runAndSend();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.client.isServer() & this.oeUsers != null) {
            this.oeUsers.startUser(this.client.playerMob);
        }
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        Level level = client.getLevel();
        return !this.objectEntity.removed() && level.getObject(this.objectEntity.tileX, this.objectEntity.tileY).isInInteractRange(level, this.objectEntity.tileX, this.objectEntity.tileY, client.playerMob);
    }

    public OEInventory getOEInventory() {
        return this.oeInventory;
    }

    public InventoryRange getOEInventoryRange() {
        return new InventoryRange(this.oeInventory.getInventory());
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.client.isServer() & this.oeUsers != null) {
            this.oeUsers.stopUser(this.client.playerMob);
        }
    }

    public static void openAndSendContainer(int containerID, ServerClient client, Level level, int tileX, int tileY, Packet extraContent) {
        if (!level.isServer()) {
            throw new IllegalStateException("Level must be a server level");
        }
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        ServerSettlementData settlement = SettlementsWorldData.getSettlementsData(level).getServerDataAtTile(level.getIdentifier(), tileX, tileY);
        SettlementContainerObjectStatusManager.writeContent(settlement, level, tileX, tileY, writer);
        if (extraContent != null) {
            writer.putNextContentPacket(extraContent);
        }
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        PacketOpenContainer p = PacketOpenContainer.SettlementObjectEntity(containerID, settlement, objectEntity, packet);
        ContainerRegistry.openAndSendContainer(client, p);
    }

    public static void openAndSendContainer(int containerID, ServerClient client, Level level, int tileX, int tileY) {
        OEInventoryContainer.openAndSendContainer(containerID, client, level, tileX, tileY, null);
    }
}

