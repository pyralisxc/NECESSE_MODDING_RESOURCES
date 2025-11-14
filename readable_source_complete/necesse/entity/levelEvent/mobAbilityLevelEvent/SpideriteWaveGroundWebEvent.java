/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Rectangle;
import java.awt.Shape;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.GroundEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobHitCooldowns;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.entity.particle.SpideriteWaveWebParticle;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class SpideriteWaveGroundWebEvent
extends GroundEffectEvent {
    private GameDamage damage = new GameDamage(0.0f);
    protected MobHitCooldowns hitCooldowns = new MobHitCooldowns(1000);
    protected int tickCounter;
    protected int hitCounter;
    private final long lifetime = 10000L;
    private SpideriteWaveWebParticle particle;

    public SpideriteWaveGroundWebEvent() {
    }

    public SpideriteWaveGroundWebEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom, GameDamage damage) {
        super(owner, x, y, uniqueIDRandom);
        this.damage = damage;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        SaveData damageSave = new SaveData("damage");
        this.damage.addSaveData(damageSave);
        save.addSaveData(damageSave);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        LoadData damageSave = save.getFirstLoadDataByName("damage");
        if (damageSave != null) {
            try {
                this.damage = GameDamage.fromLoadData(save);
            }
            catch (Exception e) {
                this.damage = new GameDamage(0.0f);
                System.err.println("Could not load damage from " + this.getStringID() + " event");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        this.damage.writePacket(writer);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        GameDamage.fromReader(reader);
    }

    @Override
    public void init() {
        super.init();
        this.tickCounter = 0;
        if (this.isClient()) {
            this.particle = new SpideriteWaveWebParticle(this.level, this.x, this.y, 10000L);
            this.level.entityManager.addParticle(this.particle, true, Particle.GType.CRITICAL);
            SoundManager.playSound(GameResources.fizz, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(0.5f).pitch(GameRandom.globalRandom.getFloatBetween(0.5f, 1.0f)));
        }
    }

    @Override
    public Shape getHitBox() {
        int width = 24;
        int height = 24;
        return new Rectangle(this.x - width / 2, this.y - height / 2, width, height);
    }

    @Override
    public void clientHit(Mob target) {
        target.startHitCooldown();
        this.hitCooldowns.startCooldown(target);
        ++this.hitCounter;
        if (this.hitCounter >= 10) {
            this.over();
        }
    }

    @Override
    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || this.hitCooldowns.canHit(target)) {
            target.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDER_WEB_SLOW, target, 1.0f, (Attacker)this.owner), true);
            target.isServerHit(this.damage, 0.0f, 0.0f, 0.0f, this.owner);
            ++this.hitCounter;
            if (this.hitCounter >= 10) {
                this.over();
            }
            this.hitCooldowns.startCooldown(target);
        }
    }

    @Override
    public void hitObject(LevelObjectHit hit) {
        hit.getLevelObject().attackThrough(this.damage, this.owner);
    }

    @Override
    public boolean canHit(Mob mob) {
        return super.canHit(mob) && this.hitCooldowns.canHit(mob);
    }

    @Override
    public void clientTick() {
        ++this.tickCounter;
        if ((long)this.tickCounter > 200L) {
            this.over();
        } else {
            super.clientTick();
        }
    }

    @Override
    public void serverTick() {
        ++this.tickCounter;
        if ((long)this.tickCounter > 200L) {
            this.over();
        } else {
            super.serverTick();
        }
    }

    @Override
    public void over() {
        super.over();
        if (this.particle != null) {
            this.particle.despawnNow();
        }
    }
}

