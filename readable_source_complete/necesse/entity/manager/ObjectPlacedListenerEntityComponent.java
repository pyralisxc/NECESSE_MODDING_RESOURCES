/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.engine.network.server.ServerClient;
import necesse.entity.manager.EntityComponent;
import necesse.level.gameObject.GameObject;

public interface ObjectPlacedListenerEntityComponent
extends EntityComponent {
    public void onObjectPlaced(GameObject var1, int var2, int var3, int var4, ServerClient var5);
}

