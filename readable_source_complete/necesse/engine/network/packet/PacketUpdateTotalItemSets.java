/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GlobalData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainGame;
import necesse.entity.mobs.PlayerMob;

public class PacketUpdateTotalItemSets
extends Packet {
    public final int slot;
    public final int totalSets;

    public PacketUpdateTotalItemSets(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.totalSets = reader.getNextByteUnsigned();
    }

    public PacketUpdateTotalItemSets(ServerClient client) {
        this.slot = client.slot;
        this.totalSets = client.playerMob.getInv().equipment.getTotalSets();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        writer.putNextByteUnsigned(this.totalSets);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        PlayerMob player = client.getPlayer(this.slot);
        if (player != null && this.totalSets != player.getInv().equipment.getTotalSets()) {
            player.getInv().equipment.changeTotalItemSets(this.totalSets);
            player.equipmentBuffManager.updateAll();
        }
        if (this.slot == client.getSlot()) {
            client.closeContainer(false);
            client.initInventoryContainer();
            if (GlobalData.getCurrentState() instanceof MainGame) {
                ((MainGame)GlobalData.getCurrentState()).formManager.updateInventoryForm();
            }
        }
    }
}

