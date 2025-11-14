/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import java.util.ArrayList;
import necesse.engine.network.server.ServerClient;
import necesse.entity.manager.EntityComponent;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.level.gameObject.GameObject;

public interface ObjectDestroyedListenerEntityComponent
extends EntityComponent {
    public void onObjectDestroyed(GameObject var1, int var2, int var3, int var4, ServerClient var5, ArrayList<ItemPickupEntity> var6);
}

