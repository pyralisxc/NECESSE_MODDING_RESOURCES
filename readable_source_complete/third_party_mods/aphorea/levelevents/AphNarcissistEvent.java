/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.GameUtils
 *  necesse.engine.util.LineHitbox
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.GameResources
 *  necesse.level.maps.LevelObjectHit
 */
package aphorea.levelevents;

import aphorea.particles.NarcissistParticle;
import aphorea.utils.AphColors;
import java.awt.Color;
import java.awt.Shape;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.LineHitbox;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class AphNarcissistEvent
extends HitboxEffectEvent
implements Attacker {
    public static int maxLifeTime = 5000;
    private int lifeTime = 0;
    public float startX;
    public float startY;
    public float moveX;
    public float moveY;
    public float startAngle;
    public GameDamage damage;
    private HashMap<Integer, Long> mobHits;
    public NarcissistParticle particle;
    public ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});

    public AphNarcissistEvent() {
    }

    public AphNarcissistEvent(Mob owner, float startAngle, float attackHeight, GameDamage damage) {
        super(owner, new GameRandom());
        this.startX = owner.x;
        this.startY = owner.y - attackHeight;
        this.moveX = 0.0f;
        this.moveY = 0.0f;
        this.startAngle = startAngle;
        this.damage = damage;
        this.hitsObjects = true;
    }

    public void init() {
        super.init();
        this.mobHits = new HashMap();
        if (this.isClient()) {
            this.particle = new NarcissistParticle(this.getLevel(), this.owner, this.startX, this.startY, this.startAngle);
            this.getLevel().entityManager.addParticle((Particle)this.particle, Particle.GType.CRITICAL);
        }
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.startX);
        writer.putNextFloat(this.startY);
        writer.putNextFloat(this.moveX);
        writer.putNextFloat(this.moveY);
        writer.putNextShortUnsigned(this.lifeTime);
        writer.putNextFloat(this.startAngle);
        writer.putNextInt(DamageTypeRegistry.getDamageTypeID((String)this.damage.type.getStringID()));
        writer.putNextFloat(this.damage.damage);
        writer.putNextFloat(this.damage.armorPen);
        writer.putNextShortUnsigned(this.mobHits.size());
        for (Map.Entry<Integer, Long> integerLongEntry : this.mobHits.entrySet()) {
            writer.putNextInt(integerLongEntry.getKey().intValue());
            writer.putNextLong(integerLongEntry.getValue().longValue());
        }
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.startX = reader.getNextFloat();
        this.startY = reader.getNextFloat();
        this.moveX = reader.getNextFloat();
        this.moveY = reader.getNextFloat();
        this.lifeTime = reader.getNextShortUnsigned();
        this.startAngle = reader.getNextFloat();
        this.damage = new GameDamage(DamageTypeRegistry.getDamageType((int)reader.getNextInt()), reader.getNextFloat(), reader.getNextFloat());
        int size = reader.getNextShortUnsigned();
        this.mobHits = new HashMap(size);
        for (int i = 0; i < size; ++i) {
            int uniqueID = reader.getNextInt();
            long time = reader.getNextLong();
            this.mobHits.put(uniqueID, time);
        }
    }

    public void clientTick() {
        super.clientTick();
        this.lifeTime += 50;
        if (this.lifeTime >= maxLifeTime) {
            this.over();
            for (int i = 0; i < 20; ++i) {
                SoundManager.playSound((GameSound)GameResources.cling, (SoundEffect)SoundEffect.effect((float)this.getX(), (float)this.getY()).volume(0.25f));
                int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
                float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(40, 80);
                float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(40, 80);
                this.owner.getLevel().entityManager.addParticle(this.getX(), this.getY(), this.particleTypeSwitcher.next()).ignoreLight(true).movesFriction(dx, dy, 0.8f).color((Color)GameRandom.globalRandom.getOneOf((Object[])new Color[]{AphColors.spinel_light, AphColors.spinel})).heightMoves(10.0f, 20.0f, 2.0f, 0.5f, 0.0f, 0.0f).lifeTime(500);
            }
        } else {
            float angularSpeed = AphNarcissistEvent.getAngularSpeed(this.getLifePercent(), AphNarcissistEvent.getDir(this.startAngle));
            int particleCount = (int)(Math.pow(Math.abs(angularSpeed) - 20.0f, 1.5) / 15.0);
            float angle = this.getAngle();
            float dirX = (float)Math.cos(angle);
            float dirY = (float)Math.sin(angle);
            float length = 100.0f;
            float startX = this.getX() - dirX * length / 2.0f;
            float startY = this.getY() - dirY * length / 2.0f;
            float centerX = startX + dirX * length / 2.0f;
            float centerY = startY + dirY * length / 2.0f;
            for (int i = 0; i < particleCount; ++i) {
                float t = GameRandom.globalRandom.nextFloat();
                float x = startX + dirX * length * t;
                float y = startY + dirY * length * t;
                float dist = (float)Math.hypot(x - centerX, y - centerY);
                float distFactor = dist / (length / 2.0f);
                float speed = angularSpeed * distFactor * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f);
                float dx = -dirY * speed;
                float dy = dirX * speed;
                this.owner.getLevel().entityManager.addParticle(x, y, this.particleTypeSwitcher.next()).ignoreLight(true).movesFriction(dx, dy, 0.9f).color((Color)GameRandom.globalRandom.getOneOf((Object[])new Color[]{AphColors.spinel_light, AphColors.spinel})).heightMoves(14.0f, -4.0f, 2.0f, 0.5f, 0.0f, 0.0f).lifeTime(500);
            }
        }
    }

    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= maxLifeTime) {
            this.over();
        }
    }

    public void tickMovement(float delta) {
        float percent = AphNarcissistEvent.easeInSine(this.getLifePercent());
        Mob closestMob = GameUtils.streamTargetsRange((Mob)this.owner, (int)((int)this.getX()), (int)((int)this.getY()), (int)((int)(500.0f * percent))).min(Comparator.comparingDouble(m -> m.getDistance(this.getX(), this.getY()))).orElse(null);
        if (closestMob != null) {
            float angle = (float)Math.atan2(closestMob.y - this.getY(), closestMob.x - this.getX());
            this.moveX += (float)(Math.cos(angle) * 50.0 * (double)percent * (double)delta / 250.0);
            this.moveY += (float)(Math.sin(angle) * 50.0 * (double)percent * (double)delta / 250.0);
            if (this.isClient()) {
                this.particle.moveX = this.moveX;
                this.particle.moveY = this.moveY;
            }
        }
    }

    public boolean canHit(Mob mob) {
        if (!super.canHit(mob)) {
            return false;
        }
        if (!this.mobHits.containsKey(mob.getHitCooldownUniqueID())) {
            return true;
        }
        return (float)this.mobHits.get(mob.getHitCooldownUniqueID()).longValue() + (300.0f - 250.0f * AphNarcissistEvent.easeInSine(this.getLifePercent())) < (float)this.getTime();
    }

    protected void startCooldown(Mob target) {
        this.mobHits.put(target.getHitCooldownUniqueID(), target.getTime());
        target.startHitCooldown();
    }

    public void clientHit(Mob mob) {
        this.startCooldown(mob);
        for (int i = 0; i < 5; ++i) {
            int angle = (int)(360.0f + GameRandom.globalRandom.nextFloat() * 360.0f);
            float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
            float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
            this.owner.getLevel().entityManager.addParticle(mob.x, mob.y, new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC}).next()).ignoreLight(true).movesFriction(dx, dy, 0.8f).color((Color)GameRandom.globalRandom.getOneOf((Object[])new Color[]{AphColors.spinel_light, AphColors.spinel})).heightMoves(10.0f, 20.0f, 2.0f, 0.5f, 0.0f, 0.0f).lifeTime(500);
        }
    }

    public void serverHit(Mob mob, boolean clientSubmitted) {
        this.startCooldown(mob);
        mob.isServerHit(this.damage, mob.x - this.getX(), mob.y - this.getY(), 50.0f, (Attacker)this.owner);
    }

    public float getX() {
        return AphNarcissistEvent.getX(this.startX, this.startAngle, this.getLifePercent()) + this.moveX;
    }

    public float getY() {
        return AphNarcissistEvent.getY(this.startY, this.startAngle, this.getLifePercent()) + this.moveY;
    }

    public float getAngle() {
        return AphNarcissistEvent.getAngle(this.startAngle, this.getLifePercent());
    }

    public float getLifePercent() {
        return (float)this.lifeTime / (float)maxLifeTime;
    }

    public Shape getHitBox() {
        float angle = this.getAngle();
        float dirX = (float)Math.cos(angle);
        float dirY = (float)Math.sin(angle);
        float length = 100.0f;
        float width = 20.0f;
        float startX = this.getX() - dirX * length / 2.0f;
        float startY = this.getY() - dirY * length / 2.0f;
        return new LineHitbox(startX, startY, dirX, dirY, length, width);
    }

    public void hitObject(LevelObjectHit levelObjectHit) {
    }

    public static float getX(float startX, float angle, float lifePercent) {
        return startX + (float)Math.cos(angle) * AphNarcissistEvent.distanceTraveled(lifePercent);
    }

    public static float getY(float startY, float angle, float lifePercent) {
        return startY + (float)Math.sin(angle) * AphNarcissistEvent.distanceTraveled(lifePercent);
    }

    public static float angleOffSet(int startDir) {
        switch (startDir) {
            case 0: {
                return 180.0f;
            }
            case 1: {
                return -45.0f;
            }
            case 2: {
                return 0.0f;
            }
            case 3: {
                return -90.0f;
            }
        }
        return 0.0f;
    }

    public static float getAngle(float startAngle, float lifePercent) {
        int dir = AphNarcissistEvent.getDir(startAngle);
        return AphNarcissistEvent.angleOffSet(dir) + AphNarcissistEvent.easeInSine(lifePercent) * (float)Math.toRadians(3600.0) * (float)(dir == 3 ? -1 : 1);
    }

    public static float distanceTraveled(float lifePercent) {
        return AphNarcissistEvent.easeOutCirc(lifePercent) * 100.0f;
    }

    public static float easeOutCirc(float x) {
        return (float)Math.sqrt(1.0 - Math.pow(x - 1.0f, 2.0));
    }

    public static float easeInSine(float x) {
        return (float)(1.0 - Math.cos((double)x * Math.PI / 2.0));
    }

    public static float getAngularSpeed(float lifePercent, int startAngle) {
        float dirFactor = AphNarcissistEvent.getDir(startAngle) == 3 ? -1 : 1;
        float constant = (float)Math.toRadians(1440.0);
        float derivativeEase = (float)(Math.sin((double)lifePercent * Math.PI / 2.0) * 1.5707963267948966);
        return derivativeEase * constant * dirFactor;
    }

    public static int getDir(float startAngle) {
        float twoPi = (float)Math.PI * 2;
        switch (Math.round((startAngle % twoPi + twoPi) % twoPi / 1.5707964f) % 4) {
            case 0: {
                return 1;
            }
            case 1: {
                return 2;
            }
            case 2: {
                return 3;
            }
            case 3: {
                return 0;
            }
        }
        return -1;
    }
}

