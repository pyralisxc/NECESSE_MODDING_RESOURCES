/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.events;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.events.ContainerEvent;
import necesse.level.gameObject.RespawnObject;
import necesse.level.maps.LevelObject;

public class SpawnUpdateContainerEvent
extends ContainerEvent {
    public final int tileX;
    public final int tileY;
    public final boolean isCurrentSpawn;

    public SpawnUpdateContainerEvent(int tileX, int tileY, boolean isCurrentSpawn) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.isCurrentSpawn = isCurrentSpawn;
    }

    public SpawnUpdateContainerEvent(ServerClient client, LevelObject object) {
        this.tileX = object.tileX;
        this.tileY = object.tileY;
        this.isCurrentSpawn = object.level.isSamePlace(client.getLevel()) ? (object.object instanceof RespawnObject ? ((RespawnObject)((Object)object.object)).isCurrentSpawn(object.level, this.tileX, this.tileY, client) : false) : false;
    }

    public SpawnUpdateContainerEvent(PacketReader reader) {
        super(reader);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.isCurrentSpawn = reader.getNextBoolean();
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextBoolean(this.isCurrentSpawn);
    }
}

