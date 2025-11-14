/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Objects;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.LightningEffectHandler;
import necesse.entity.levelEvent.explosionEvent.ArcanicPylonExplosionLevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobAbilityLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;

public class ArcanicPylonLightningLevelEvent
extends MobAbilityLevelEvent {
    private int zapTimeBetweenTargets;
    private Mob owner;
    private LightningEffectHandler handler;
    private int damageNumber;
    private Point targetPos;

    public ArcanicPylonLightningLevelEvent() {
    }

    public ArcanicPylonLightningLevelEvent(Mob owner, int zapTimeBetweenTargets, Integer damageNumber, Point targetPos) {
        super(owner, GameRandom.globalRandom);
        this.zapTimeBetweenTargets = zapTimeBetweenTargets;
        this.damageNumber = damageNumber;
        Objects.requireNonNull(owner);
        this.owner = owner;
        this.targetPos = targetPos;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.owner.getUniqueID());
        writer.putNextInt(this.zapTimeBetweenTargets);
        writer.putNextInt(this.targetPos.x);
        writer.putNextInt(this.targetPos.y);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        int targetUniqueID = reader.getNextInt();
        this.owner = GameUtils.getLevelMob(targetUniqueID, this.getLevel(), true);
        this.zapTimeBetweenTargets = reader.getNextInt();
        this.targetPos = new Point();
        this.targetPos.x = reader.getNextInt();
        this.targetPos.y = reader.getNextInt();
    }

    @Override
    public void init() {
        super.init();
        this.handler = new LightningEffectHandler(this.getLevel(), 4);
        this.fireLightningBeam(this.targetPos);
    }

    @Override
    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.handler.tickMovement(delta);
    }

    public void fireLightningBeam(Point targetPos) {
        this.handler.addNextPoint(() -> new Point2D.Float(targetPos.x, targetPos.y), 200, false, () -> {
            this.handler.addNextPoint(this.owner.x, this.owner.y - 45.0f, 0, true, null);
            GameDamage bombDamage = new GameDamage(this.damageNumber);
            ArcanicPylonExplosionLevelEvent event = new ArcanicPylonExplosionLevelEvent(targetPos.x, targetPos.y, 70, bombDamage, false, 0.0f, this.owner);
            this.getLevel().entityManager.addLevelEventHidden(event);
            this.over();
        });
    }

    @Override
    public void over() {
        super.over();
        if (this.handler != null) {
            this.handler.dispose();
        }
    }
}

