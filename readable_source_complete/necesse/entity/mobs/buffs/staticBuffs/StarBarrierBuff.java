/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.HumanDrawBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTexture.GameTexture;

public class StarBarrierBuff
extends Buff
implements HumanDrawBuff {
    public GameTexture starBarrierTexture;

    public StarBarrierBuff() {
        this.canCancel = false;
        this.isImportant = true;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.starBarrierTexture = GameTexture.fromFile("particles/starbarrier");
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public int getStackSize(ActiveBuff buff) {
        return 4;
    }

    @Override
    public boolean shouldDrawDuration(ActiveBuff buff) {
        return false;
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        if (buff.owner.isVisible() && buff.getStacks() < 4) {
            Mob owner = buff.owner;
            GameRandom random = GameRandom.globalRandom;
            AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
            float distance = 150 - 30 * buff.getStacks();
            owner.getLevel().entityManager.addParticle(owner.x + GameMath.sin(currentAngle.get().floatValue()) * distance, owner.y + GameMath.cos(currentAngle.get().floatValue()) * distance, Particle.GType.CRITICAL).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).color(new Color(184, 174, 255)).givesLight(247.0f, 0.3f).height(20.0f).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 2.5f * (float)(buff.getStacks() * 2) / 250.0f), Float::sum).floatValue();
                pos.x = owner.x + GameMath.sin(angle) * distance;
                pos.y = owner.y + GameMath.cos(angle) * distance;
            }).lifeTime(1000).sizeFades(16, 24);
        }
    }

    @Override
    public void addHumanDraw(ActiveBuff buff, HumanDrawOptions drawOptions) {
        if (buff.owner.buffManager.hasBuff(BuffRegistry.STAR_BARRIER_BUFF)) {
            drawOptions.addTopDraw((player, dir, spriteX, spriteY, spriteRes, drawX, drawY, width, height, mirrorX, mirrorY, light, alpha, mask) -> this.starBarrierTexture.initDraw().sprite((int)(player.getLocalTime() / 100L) % 4, 0, 64).size(width, height).addMaskShader(mask).pos(drawX, drawY).alpha(0.25f * (float)buff.getStacks()));
        }
    }
}

