/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.level.maps.Level;

public class DungeonExitObjectEntity
extends PortalObjectEntity {
    public DungeonExitObjectEntity(Level level, int x, int y) {
        super(level, "dungeonexit", x, y, level.getIdentifier(), x, y);
    }

    @Override
    public void use(Server server, ServerClient client) {
        this.teleportClientToAroundDestination(client, level -> {
            this.runClearMobs((Level)level, this.destinationTileX, this.destinationTileY);
            return true;
        }, true);
    }
}

