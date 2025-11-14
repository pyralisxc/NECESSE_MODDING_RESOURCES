/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;

public class PacketLogicGateUpdate
extends Packet {
    public final int levelIdentifierHashCode;
    public final int tileX;
    public final int tileY;
    public final int gateID;
    public final Packet content;

    public PacketLogicGateUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.gateID = reader.getNextShort();
        this.content = this.gateID != -1 ? reader.getNextContentPacket() : null;
    }

    public PacketLogicGateUpdate(Level level, int tileX, int tileY, int gateID, LogicGateEntity entity) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.tileX = tileX;
        this.tileY = tileY;
        this.gateID = gateID;
        if (entity != null) {
            this.content = new Packet();
            entity.writePacket(new PacketWriter(this.content));
        } else {
            this.content = null;
        }
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextShort((short)gateID);
        if (this.content != null) {
            writer.putNextContentPacket(this.content);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        client.getLevel().logicLayer.setLogicGate(this.tileX, this.tileY, this.gateID, this.content == null ? null : new PacketReader(this.content));
    }
}

