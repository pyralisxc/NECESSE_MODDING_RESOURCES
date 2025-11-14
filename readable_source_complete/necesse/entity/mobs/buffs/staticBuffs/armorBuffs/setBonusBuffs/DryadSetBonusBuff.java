/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public class DryadSetBonusBuff
extends SetBonusBuff {
    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        this.tickBarkSkin(buff);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        this.tickBarkSkin(buff);
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "dryadset"), 400);
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }

    private void tickBarkSkin(ActiveBuff buff) {
        if (!(buff.owner.buffManager.hasBuff(BuffRegistry.BARK_SKIN) && buff.owner.buffManager.getBuff(BuffRegistry.BARK_SKIN).getStacks() >= 3 || buff.owner.isInCombat())) {
            ActiveBuff ab = new ActiveBuff(BuffRegistry.BARK_SKIN, buff.owner, Integer.MAX_VALUE, null);
            ab.setStacks(3, Integer.MAX_VALUE, null);
            buff.owner.buffManager.addBuff(ab, false);
            if (buff.owner.isClient()) {
                // empty if block
            }
        }
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        if (buff.owner.isClient()) {
            GameRandom random = GameRandom.globalRandom;
            boolean alternate = random.nextBoolean();
            Level level = buff.owner.getLevel();
            float x = buff.owner.x;
            float y = buff.owner.y;
            for (int i = 0; i < 5; ++i) {
                alternate = !alternate;
                float startHeight = random.getFloatBetween(10.0f, 20.0f);
                float startHeightSpeed = random.getFloatBetween(0.0f, 60.0f);
                float endHeight = random.getFloatBetween(-10.0f, -5.0f);
                float gravity = random.getFloatBetween(8.0f, 20.0f);
                boolean mirror = random.nextBoolean();
                float rotation = random.getFloatBetween(-100.0f, 100.0f);
                float moveX = random.floatGaussian() * 5.0f + random.getFloatBetween(-10.0f, 10.0f);
                float moveY = random.floatGaussian() * 2.0f + random.getFloatBetween(-10.0f, 10.0f);
                ParticleOption.FrictionMover frictionMover = new ParticleOption.FrictionMover(moveX, moveY, 0.0f);
                ParticleOption.CollisionMover mover = new ParticleOption.CollisionMover(level, frictionMover, new CollisionFilter().mobCollision().addFilter(tp -> tp.object().object.isWall));
                int timeToLive = random.getIntBetween(3000, 8000);
                int timeToFadeOut = random.getIntBetween(1000, 2000);
                int totalTime = timeToLive + timeToFadeOut;
                ParticleOption.HeightMover heightMover = new ParticleOption.HeightMover(startHeight, startHeightSpeed, gravity, 2.0f, endHeight, 0.0f);
                AtomicReference<Float> floatingTime = new AtomicReference<Float>(Float.valueOf(0.0f));
                level.entityManager.addParticle(x, y, Particle.GType.COSMETIC).sprite(GameResources.dryadLeafParticles.sprite(random.nextInt(4), 0, 20)).fadesAlphaTime(0, timeToFadeOut).sizeFadesInAndOut(15, 20, 100, 0).height(heightMover).onMoveTick((delta, lifeTime, timeAlive, lifePercent) -> {
                    if (heightMover.currentHeight > endHeight) {
                        floatingTime.set(Float.valueOf(((Float)floatingTime.get()).floatValue() + delta));
                    }
                }).modify((options, lifeTime, timeAlive, lifePercent) -> {
                    float angle = GameMath.sin(((Float)floatingTime.get()).floatValue() / 5.0f) * rotation;
                    options.rotate(angle, 10, -4);
                }).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                    if (heightMover.currentHeight > endHeight) {
                        mover.tick(pos, delta, lifeTime, timeAlive, lifePercent);
                    }
                }).modify((options, lifeTime, timeAlive, lifePercent) -> options.mirror(mirror, false)).lifeTime(totalTime);
            }
        }
    }
}

