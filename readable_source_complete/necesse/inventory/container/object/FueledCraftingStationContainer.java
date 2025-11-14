/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.container.settlement.SettlementContainerObjectStatusManager;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.slots.OEInventoryContainerSlot;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class FueledCraftingStationContainer
extends CraftingStationContainer {
    public final FueledInventoryObjectEntity objectEntity;
    public BooleanCustomAction setKeepRunning;
    public int INVENTORY_START = -1;
    public int INVENTORY_END = -1;
    public final OEUsers oeUsers;

    public FueledCraftingStationContainer(NetworkClient client, int uniqueSeed, SettlementDataEvent settlement, final FueledInventoryObjectEntity objectEntity, PacketReader reader) {
        super(client, uniqueSeed, settlement, new LevelObject(objectEntity.getLevel(), objectEntity.tileX, objectEntity.tileY), reader);
        this.objectEntity = objectEntity;
        this.oeUsers = objectEntity instanceof OEUsers ? (OEUsers)((Object)objectEntity) : null;
        if (client.isServer() & this.oeUsers != null) {
            this.oeUsers.startUser(client.playerMob);
        }
        for (int i = 0; i < objectEntity.getInventory().getSize(); ++i) {
            int index = this.addSlot(new OEInventoryContainerSlot(objectEntity, i));
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
        this.setKeepRunning = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                objectEntity.keepRunning = value;
                objectEntity.markFuelDirty();
            }
        });
    }

    @Override
    public int applyCraftingAction(int recipeID, int recipeHash, int craftAmount, boolean transferToInventory) {
        if (!this.objectEntity.isFueled()) {
            this.objectEntity.useFuel();
            if (!this.objectEntity.isFueled()) {
                return 0;
            }
        }
        return super.applyCraftingAction(recipeID, recipeHash, craftAmount, transferToInventory);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.client.isServer() & this.oeUsers != null) {
            this.oeUsers.startUser(this.client.playerMob);
        }
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
        FueledCraftingStationContainer.openAndSendContainer(containerID, client, level, tileX, tileY, null);
    }
}

