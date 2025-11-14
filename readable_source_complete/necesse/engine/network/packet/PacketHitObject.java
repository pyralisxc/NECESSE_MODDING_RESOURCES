/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class PacketHitObject
extends Packet {
    public final int levelIdentifierHashCode;
    public final int tileX;
    public final int tileY;
    public final GameObject object;
    public final GameDamage damage;

    public PacketHitObject(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        int objectID = reader.getNextShortUnsigned();
        this.object = ObjectRegistry.getObject(objectID);
        this.damage = GameDamage.fromPacket(reader.getNextContentPacket());
    }

    public PacketHitObject(Level level, int tileX, int tileY, GameObject object, GameDamage damage) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.tileX = tileX;
        this.tileY = tileY;
        this.object = object;
        this.damage = damage;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextShortUnsigned(object.getID());
        writer.putNextContentPacket(damage.getPacket());
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        this.object.attackThrough(client.getLevel(), this.tileX, this.tileY, this.damage);
    }
}

