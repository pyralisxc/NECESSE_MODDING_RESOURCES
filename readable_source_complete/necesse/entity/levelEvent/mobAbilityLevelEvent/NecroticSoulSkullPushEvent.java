/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.HashSet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PointSetAbstract;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class NecroticSoulSkullPushEvent
extends HitboxEffectEvent
implements Attacker {
    public static float poisonDuration = 2.0f;
    private int lifeTime = 0;
    private HashSet<Integer> hits = new HashSet();

    public NecroticSoulSkullPushEvent() {
    }

    public NecroticSoulSkullPushEvent(Mob owner) {
        super(owner, new GameRandom());
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.lifeTime);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lifeTime = reader.getNextShortUnsigned();
    }

    @Override
    public void init() {
        super.init();
        this.hitsObjects = false;
        this.hits = new HashSet();
        if (this.owner != null) {
            SoundManager.playSound(GameResources.magicbolt1, (SoundEffect)SoundEffect.effect(this.owner).pitch(0.8f));
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 600) {
            this.over();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 600) {
            this.over();
        }
    }

    @Override
    public Shape getHitBox() {
        if (this.owner != null) {
            int size = 150;
            return new Rectangle(this.owner.getX() - size / 2, this.owner.getY() - size / 2, size, size);
        }
        return new Rectangle();
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && !this.hits.contains(mob.getUniqueID());
    }

    @Override
    public void clientHit(Mob target) {
        this.hits.add(target.getUniqueID());
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || !this.hits.contains(target.getUniqueID())) {
            target.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, target, 0.2f, (Attacker)this), true);
            target.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.NECROTIC_POISON, target, poisonDuration, (Attacker)this), true);
            float modifier = target.getKnockbackModifier();
            if (modifier != 0.0f) {
                float knockback = 250.0f / modifier;
                target.isServerHit(new GameDamage(0.0f), target.x - this.owner.x, target.y - this.owner.y, knockback, this.owner);
            }
            this.hits.add(target.getUniqueID());
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
    }

    @Override
    public PointSetAbstract<?> getRegionPositions() {
        if (this.owner != null) {
            return this.owner.getRegionPositions();
        }
        return super.getRegionPositions();
    }

    @Override
    public Point getSaveToRegionPos() {
        if (this.owner != null) {
            return new Point(this.level.regionManager.getRegionCoordByTile(this.owner.getTileX()), this.level.regionManager.getRegionCoordByTile(this.owner.getTileY()));
        }
        return super.getSaveToRegionPos();
    }
}

