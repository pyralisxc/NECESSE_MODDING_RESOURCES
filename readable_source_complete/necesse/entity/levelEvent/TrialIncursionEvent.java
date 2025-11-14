/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.levelEvent.IncursionLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelBuffManager.MobBuffsEntityComponent;

public class TrialIncursionEvent
extends IncursionLevelEvent
implements MobBuffsEntityComponent,
LevelBuffsEntityComponent {
    public TrialIncursionEvent() {
    }

    public TrialIncursionEvent(String bossStringID) {
        super(bossStringID);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public Stream<ModifierValue<?>> getLevelModifiers() {
        if (this.isDone || this.isFighting || this.bossPortalSpawned) {
            return Stream.of(new ModifierValue<Boolean>(LevelModifiers.ENEMIES_RETREATING, true));
        }
        return Stream.empty();
    }

    @Override
    public Stream<ModifierValue<?>> getLevelModifiers(Mob mob) {
        if (this.isDone || this.isFighting || this.bossPortalSpawned) {
            if (mob.isPlayer) {
                return Stream.of(new ModifierValue<Float>(BuffModifiers.MOB_SPAWN_RATE, Float.valueOf(0.0f)).max(Float.valueOf(0.0f), 1000000));
            }
        } else if (mob.isPlayer) {
            return Stream.of(new ModifierValue<Float>(BuffModifiers.MOB_SPAWN_RATE, Float.valueOf(1.0f)), new ModifierValue<Integer>(BuffModifiers.MOB_SPAWN_LIGHT_THRESHOLD).min(150));
        }
        return Stream.empty();
    }

    @Override
    public boolean isObjectiveDone() {
        return true;
    }

    @Override
    public int getObjectiveCurrent() {
        return 0;
    }

    @Override
    public int getObjectiveMax() {
        return 0;
    }
}

