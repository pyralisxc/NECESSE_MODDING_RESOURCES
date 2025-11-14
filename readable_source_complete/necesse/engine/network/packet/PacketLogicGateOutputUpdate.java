/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.level.gameLogicGate.entities.LogicGateEntity;

public class PacketLogicGateOutputUpdate
extends Packet {
    public final int levelIdentifierHashCode;
    public final int tileX;
    public final int tileY;
    private final PacketReader reader;

    public PacketLogicGateOutputUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.reader = new PacketReader(reader);
    }

    public PacketLogicGateOutputUpdate(LogicGateEntity entity) {
        this.levelIdentifierHashCode = entity.level.getIdentifierHashCode();
        this.tileX = entity.tileX;
        this.tileY = entity.tileY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        this.reader = new PacketReader(writer);
        entity.setupOutputUpdate(writer);
    }

    public PacketReader getReader() {
        return new PacketReader(this.reader);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        LogicGateEntity entity = client.getLevel().logicLayer.getEntity(this.tileX, this.tileY);
        if (entity != null) {
            entity.applyOutputUpdate(this.getReader());
        } else {
            GameLog.warn.println("Got invalid logic gate output update");
        }
    }
}

