/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerAttackHandler;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.attackHandler.AttackHandler;
import necesse.entity.mobs.attackHandler.PlaceItemAttackHandler;

public class PacketPlaceAttackHandlerUpdate
extends Packet {
    public final int attackHandlerSeed;
    public final int nextPlaceX;
    public final int nextPlaceY;
    public final Packet content;

    public PacketPlaceAttackHandlerUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.attackHandlerSeed = reader.getNextInt();
        this.nextPlaceX = reader.getNextInt();
        this.nextPlaceY = reader.getNextInt();
        this.content = reader.getNextContentPacket();
    }

    public PacketPlaceAttackHandlerUpdate(PlaceItemAttackHandler<?> attackHandler, int nextPlaceX, int nextPlaceY, Packet content) {
        this.attackHandlerSeed = attackHandler.seed;
        this.nextPlaceX = nextPlaceX;
        this.nextPlaceY = nextPlaceY;
        this.content = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.attackHandlerSeed);
        writer.putNextInt(nextPlaceX);
        writer.putNextInt(nextPlaceY);
        writer.putNextContentPacket(content);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!client.checkHasRequestedSelf() || client.isDead()) {
            return;
        }
        client.checkSpawned();
        AttackHandler attackHandler = client.playerMob.getAttackHandler();
        if (attackHandler instanceof PlaceItemAttackHandler) {
            PlaceItemAttackHandler placeAttackHandler = (PlaceItemAttackHandler)attackHandler;
            if (placeAttackHandler.seed == this.attackHandlerSeed) {
                placeAttackHandler.handlePlaceClientUpdatePacket(this.nextPlaceX, this.nextPlaceY, new PacketReader(this.content));
            } else {
                client.playerMob.endAttackHandler(false);
                client.sendPacket(PacketPlayerAttackHandler.serverEnd());
            }
        } else {
            client.playerMob.endAttackHandler(false);
            client.sendPacket(PacketPlayerAttackHandler.serverEnd());
        }
    }
}

