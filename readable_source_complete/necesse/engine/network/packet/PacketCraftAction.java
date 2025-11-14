/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.Container;

public class PacketCraftAction
extends Packet {
    public final int recipeID;
    public final int recipeHash;
    public final int craftAmount;
    public final int actionResult;
    public final boolean transferToInventory;

    public PacketCraftAction(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.recipeID = reader.getNextInt();
        this.recipeHash = reader.getNextInt();
        this.craftAmount = reader.getNextShortUnsigned();
        this.actionResult = reader.getNextByteUnsigned();
        this.transferToInventory = reader.getNextBoolean();
    }

    public PacketCraftAction(int recipeID, int recipeHash, int craftAmount, int actionResult, boolean transferToInventory) {
        this.recipeID = recipeID;
        this.recipeHash = recipeHash;
        this.craftAmount = craftAmount;
        this.actionResult = actionResult;
        this.transferToInventory = transferToInventory;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(recipeID);
        writer.putNextInt(recipeHash);
        writer.putNextShortUnsigned(craftAmount);
        writer.putNextByteUnsigned(actionResult);
        writer.putNextBoolean(transferToInventory);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        int result = client.getContainer().applyCraftingAction(this.recipeID, this.recipeHash, this.craftAmount, this.transferToInventory);
        if (((byte)result & 0xFF) != this.actionResult) {
            client.getContainer().markFullDirty();
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        Container container = client.getContainer();
        if (container != null) {
            container.applyCraftingAction(this.recipeID, this.recipeHash, this.craftAmount, this.transferToInventory);
        }
    }
}

