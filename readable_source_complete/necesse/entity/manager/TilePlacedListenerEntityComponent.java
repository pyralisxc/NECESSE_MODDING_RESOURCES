/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.engine.network.server.ServerClient;
import necesse.entity.manager.EntityComponent;
import necesse.level.gameTile.GameTile;

public interface TilePlacedListenerEntityComponent
extends EntityComponent {
    public void onTilePlaced(GameTile var1, int var2, int var3, ServerClient var4);
}

