/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.engine.network.server.ServerClient;
import necesse.entity.ObjectDamageResult;
import necesse.entity.manager.EntityComponent;
import necesse.level.gameObject.GameObject;

public interface ObjectDamagedListenerEntityComponent
extends EntityComponent {
    public void onObjectDamaged(GameObject var1, int var2, int var3, int var4, ServerClient var5, ObjectDamageResult var6);
}

