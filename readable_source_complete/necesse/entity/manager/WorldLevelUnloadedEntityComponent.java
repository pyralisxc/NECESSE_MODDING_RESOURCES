/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.manager;

import necesse.engine.util.LevelIdentifier;
import necesse.entity.manager.EntityComponent;

public interface WorldLevelUnloadedEntityComponent
extends EntityComponent {
    public void onLevelUnloaded(LevelIdentifier var1);
}

