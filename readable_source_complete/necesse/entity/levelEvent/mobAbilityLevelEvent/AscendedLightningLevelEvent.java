/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Color;
import java.util.Objects;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.LightningEffectHandler;
import necesse.entity.levelEvent.explosionEvent.AscendedLightningExplosionLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.AscendedPylonDummyMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;

public class AscendedLightningLevelEvent
extends MobAbilityLevelEvent {
    private int zapTimeBetweenTargets;
    private Mob owner;
    private LightningEffectHandler handler;
    public GameDamage damage;
    public int targetX;
    public int targetY;

    public AscendedLightningLevelEvent() {
    }

    public AscendedLightningLevelEvent(Mob owner, int zapTimeBetweenTargets, GameDamage damage, int targetX, int targetY) {
        super(owner, GameRandom.globalRandom);
        this.zapTimeBetweenTargets = zapTimeBetweenTargets;
        this.damage = damage;
        Objects.requireNonNull(owner);
        this.owner = owner;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.owner.getUniqueID());
        writer.putNextInt(this.zapTimeBetweenTargets);
        writer.putNextInt(this.targetX);
        writer.putNextInt(this.targetY);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int targetUniqueID = reader.getNextInt();
        this.owner = GameUtils.getLevelMob(targetUniqueID, this.getLevel(), true);
        this.zapTimeBetweenTargets = reader.getNextInt();
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        if (this.owner == null) {
            return;
        }
        this.handler = new LightningEffectHandler(this.getLevel(), 2);
        this.handler.color = new Color(255, 47, 243);
        this.handler.distanceBetweenZaps = 50.0f;
        this.handler.addNextPoint(this.owner.getX(), this.owner.getY() - AscendedPylonDummyMob.CHARGE_PARTICLE_HEIGHT, 0, false, null);
        this.handler.addNextPoint(this.targetX, this.targetY, 200, true, () -> {
            this.level.entityManager.events.addHidden(new AscendedLightningExplosionLevelEvent(this.targetX, this.targetY, 100, this.damage, false, 0.0f, this.owner));
            this.over();
        });
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.handler.tickMovement(delta);
    }

    @Override
    public void over() {
        super.over();
        if (this.handler != null) {
            this.handler.dispose();
        }
    }
}

