/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.LogicGateRegistry;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.maps.Level;

public class PacketPlaceLogicGate
extends Packet {
    public final int levelIdentifierHashCode;
    public final int slot;
    public final int gateID;
    public final int tileX;
    public final int tileY;

    public PacketPlaceLogicGate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.slot = reader.getNextByteUnsigned();
        this.gateID = reader.getNextShortUnsigned();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public PacketPlaceLogicGate(Level level, ServerClient client, int gateID, int tileX, int tileY) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.slot = client.slot;
        this.gateID = gateID;
        this.tileX = tileX;
        this.tileY = tileY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextByteUnsigned(this.slot);
        writer.putNextShortUnsigned(gateID);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
    }

    public GameLogicGate getGate() {
        return LogicGateRegistry.getLogicGate(this.gateID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        GameLogicGate gate = this.getGate();
        if (this.slot == client.getSlot()) {
            gate.playPlaceSound(this.tileX, this.tileY);
        }
        gate.placeGate(client.getLevel(), this.tileX, this.tileY);
    }
}

