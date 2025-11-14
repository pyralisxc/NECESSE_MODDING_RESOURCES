/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.HumanDrawBuff;
import necesse.entity.mobs.buffs.MovementTickBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.human.HumanDrawOptions;

public class HoverBootsActiveBuff
extends Buff
implements MovementTickBuff,
HumanDrawBuff {
    public HoverBootsActiveBuff() {
        this.shouldSave = false;
        this.isVisible = false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setMinModifier(BuffModifiers.SPEED_FLAT, Float.valueOf(200.0f));
        buff.setMaxModifier(BuffModifiers.FRICTION, Float.valueOf(0.2f), Integer.MAX_VALUE);
        buff.setMaxModifier(BuffModifiers.ACCELERATION, Float.valueOf(0.25f), Integer.MAX_VALUE);
        buff.setMaxModifier(BuffModifiers.SPEED, Float.valueOf(1.0f), Integer.MAX_VALUE);
        buff.setModifier(BuffModifiers.WATER_WALKING, true);
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        Mob owner = buff.owner;
        int dir = owner.getDir();
        float yOffset = GameRandom.globalRandom.floatGaussian() * 2.0f;
        owner.getLevel().entityManager.addParticle(owner.x + 4.0f, owner.y + yOffset - 2.0f + (float)(dir == 1 ? -4 : 0), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).color((options, lifeTime, timeAlive, lifePercent) -> options.color(new Color(255 - GameMath.lerp(lifePercent, 0, 245), 255 - GameMath.lerp(lifePercent, 0, 50), 255 - GameMath.lerp(lifePercent, 0, 33)))).sizeFadesInAndOut(10, 16, 50, 200).movesConstant(GameRandom.globalRandom.floatGaussian(), 10.0f).lifeTime(300).height(2.0f);
        owner.getLevel().entityManager.addParticle(owner.x - 4.0f, owner.y + yOffset - 2.0f + (float)(dir == 3 ? -4 : 0), Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).color((options, lifeTime, timeAlive, lifePercent) -> options.color(new Color(255 - GameMath.lerp(lifePercent, 0, 245), 255 - GameMath.lerp(lifePercent, 0, 50), 255 - GameMath.lerp(lifePercent, 0, 33)))).sizeFadesInAndOut(10, 16, 50, 200).movesConstant(GameRandom.globalRandom.floatGaussian(), 10.0f).lifeTime(300).height(2.0f);
    }

    @Override
    public void tickMovement(ActiveBuff buff, float delta) {
        float hoverHeight = buff.getGndData().getFloat("hoverHeight");
        hoverHeight += 0.04f * delta;
        hoverHeight = Math.min(hoverHeight, 10.0f);
        buff.getGndData().setFloat("hoverHeight", hoverHeight);
        Mob owner = buff.owner;
        if (owner.isClient() && (owner.dx != 0.0f || owner.dy != 0.0f)) {
            float speed = owner.getCurrentSpeed() * delta / 250.0f;
            GNDItemMap gndData = buff.getGndData();
            float soundBuffer = gndData.getFloat("soundBuffer") + Math.min(speed, 80.0f * delta / 250.0f);
            if (soundBuffer >= 55.0f) {
                soundBuffer -= 55.0f;
                SoundManager.playSound(GameResources.hoverboots, (SoundEffect)SoundEffect.effect(owner).pitch(1.0f));
            }
            gndData.setFloat("soundBuffer", soundBuffer);
        }
    }

    @Override
    public void addHumanDraw(ActiveBuff buff, HumanDrawOptions drawOptions) {
        int dir = buff.owner.getDir();
        drawOptions.sprite(new Point(0, dir % 4));
        drawOptions.addDrawOffset(0, (int)(-buff.getGndData().getFloat("hoverHeight")));
        float rotate = buff.owner.dx;
        drawOptions.rotate(Math.min(rotate / 10.0f, 10.0f), 32, 55);
    }
}

