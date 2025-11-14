/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;

public class MyceliumHoodActiveBuff
extends Buff {
    public MyceliumHoodActiveBuff() {
        this.shouldSave = false;
        this.isVisible = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setMinModifier(BuffModifiers.SLOW, Float.valueOf(1.0f), 1000000);
        buff.setModifier(BuffModifiers.RANGED_ATTACK_SPEED, Float.valueOf(1.0f));
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.getLevel().tickManager().getTotalTicks() % 2L == 0L) {
            float[] angles;
            for (float angle : angles = new float[]{40.0f, 140.0f, 220.0f, 320.0f}) {
                Point2D.Float dir = GameMath.getAngleDir(angle);
                buff.owner.getLevel().entityManager.addParticle(buff.owner.x, buff.owner.y, Particle.GType.IMPORTANT_COSMETIC).color(new Color(158, 82, 8)).sizeFadesInAndOut(5, 10, 50, 200).movesConstant(dir.x * 10.0f, dir.y * 10.0f).lifeTime(500).heightMoves(8.0f, 0.0f);
            }
        }
    }
}

