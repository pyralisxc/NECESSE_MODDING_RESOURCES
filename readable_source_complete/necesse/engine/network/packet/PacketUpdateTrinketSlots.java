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

public class PacketUpdateTrinketSlots
extends Packet {
    public final int slot;
    public final int trinketSlots;

    public PacketUpdateTrinketSlots(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.trinketSlots = reader.getNextByteUnsigned();
    }

    public PacketUpdateTrinketSlots(ServerClient client) {
        this.slot = client.slot;
        this.trinketSlots = client.playerMob.getInv().equipment.getTrinketSlotsSize();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        writer.putNextByteUnsigned(this.trinketSlots);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        PlayerMob player = client.getPlayer(this.slot);
        if (player != null && this.trinketSlots != player.getInv().equipment.getTrinketSlotsSize()) {
            player.getInv().equipment.changeTrinketSlotsSize(this.trinketSlots);
            player.equipmentBuffManager.updateTrinketBuffs();
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

