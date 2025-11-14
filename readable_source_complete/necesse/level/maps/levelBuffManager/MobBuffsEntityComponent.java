/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelBuffManager;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.manager.EntityComponent;
import necesse.entity.mobs.Mob;

public interface MobBuffsEntityComponent
extends EntityComponent {
    public Stream<ModifierValue<?>> getLevelModifiers(Mob var1);
}

