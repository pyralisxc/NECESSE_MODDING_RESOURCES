/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent.splashEvent;

import java.awt.Color;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.explosionEvent.splashEvent.BuffSplashEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.mobs.buffs.ActiveBuff;

public class PolymorphSplashEvent
extends BuffSplashEvent {
    protected int duration;

    public PolymorphSplashEvent() {
        this(0.0f, 0.0f, 96, new GameDamage(0.0f), 0.0f, null, 5000);
    }

    public PolymorphSplashEvent(float x, float y, int range, GameDamage damage, float toolTier, Mob owner, int duration) {
        super(x, y, range, damage, toolTier, owner);
        this.duration = duration;
    }

    @Override
    protected boolean canHitMob(Mob target) {
        return super.canHitMob(target) && !target.isBoss() && !(target instanceof TrainingDummyMob);
    }

    @Override
    protected ActiveBuff getBuff(Mob buffOwner) {
        return GameRandom.globalRandom.getOneOf(new ActiveBuff(BuffRegistry.Debuffs.POLYMORPH_CHICKEN, buffOwner, this.duration, null), new ActiveBuff(BuffRegistry.Debuffs.POLYMORPH_FROG, buffOwner, this.duration, null), new ActiveBuff(BuffRegistry.Debuffs.POLYMORPH_SQUIRREL, buffOwner, this.duration, null), new ActiveBuff(BuffRegistry.Debuffs.POLYMORPH_RABBIT, buffOwner, this.duration, null), new ActiveBuff(BuffRegistry.Debuffs.POLYMORPH_TURTLE, buffOwner, this.duration, null), new ActiveBuff(BuffRegistry.Debuffs.POLYMORPH_ROOSTER, buffOwner, this.duration, null), new ActiveBuff(BuffRegistry.Debuffs.POLYMORPH_DUCK, buffOwner, this.duration, null));
    }

    @Override
    protected Color getInnerSplashColor() {
        return GameRandom.globalRandom.getOneOfWeighted(Color.class, 3, new Color(255, 255, 255), 1, new Color(171, 255, 239), 1, new Color(245, 222, 255));
    }

    @Override
    protected Color getOuterSplashColor() {
        return GameRandom.globalRandom.getOneOf(new Color(210, 255, 175), new Color(192, 171, 255), new Color(255, 245, 222));
    }
}

