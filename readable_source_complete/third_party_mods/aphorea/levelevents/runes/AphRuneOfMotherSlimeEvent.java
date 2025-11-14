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
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.gfx.GameResources
 *  necesse.level.maps.LevelObjectHit
 */
package aphorea.levelevents.runes;

import aphorea.patches.PlayerFlyingHeight;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.HashSet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class AphRuneOfMotherSlimeEvent
extends HitboxEffectEvent
implements Attacker {
    private int lifeTime = 0;
    private HashSet<Integer> hits = new HashSet();
    public int startX;
    public int startY;
    public int targetX;
    public int targetY;
    public float effectNumber;
    private boolean showedImpact = false;
    private boolean teleported = false;

    public AphRuneOfMotherSlimeEvent() {
    }

    public AphRuneOfMotherSlimeEvent(Mob owner, int targetX, int targetY, float effectNumber) {
        super(owner, new GameRandom());
        this.startX = owner.getX();
        this.startY = owner.getY();
        this.targetX = targetX;
        this.targetY = targetY;
        this.effectNumber = effectNumber;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.lifeTime);
        writer.putNextInt(this.startX);
        writer.putNextInt(this.startY);
        writer.putNextInt(this.targetX);
        writer.putNextInt(this.targetY);
        writer.putNextFloat(this.effectNumber);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lifeTime = reader.getNextShortUnsigned();
        this.startX = reader.getNextInt();
        this.startY = reader.getNextInt();
        this.targetX = reader.getNextInt();
        this.targetY = reader.getNextInt();
        this.effectNumber = reader.getNextFloat();
    }

    public void init() {
        super.init();
        this.hitsObjects = false;
        this.hits = new HashSet();
        this.showedImpact = false;
        this.teleported = false;
    }

    public void tickMovement(float delta) {
        super.tickMovement(delta);
        this.lifeTime += 50;
        if (this.lifeTime >= 1100) {
            this.over();
        } else if (this.lifeTime >= 1000) {
            if (!this.showedImpact) {
                SoundManager.playSound((GameSound)GameResources.slimeSplash1, (SoundEffect)SoundEffect.effect((float)this.targetX, (float)this.targetY).pitch(1.0f));
                SoundManager.playSound((GameSound)GameResources.flick, (SoundEffect)SoundEffect.effect((float)this.targetX, (float)this.targetY).pitch(0.8f));
                new AphAreaList(new AphArea(200.0f, AphColors.paletteMotherSlime)).setOnlyVision(false).executeClient(this.level, this.targetX, this.targetY, 1.0f, 0.8f, 0.5f);
                this.showedImpact = true;
            }
            if (!this.teleported) {
                PlayerFlyingHeight.playersFlyingHeight.remove(this.owner.getUniqueID());
                this.owner.setPos((float)this.targetX, (float)this.targetY, false);
                this.teleported = true;
            }
        } else {
            float movePercent = (float)this.lifeTime / 1000.0f;
            this.owner.setPos((float)this.startX + (float)(this.targetX - this.startX) * movePercent, (float)this.startY + (float)(this.targetY - this.startY) * movePercent, false);
            if (this.lifeTime >= 500) {
                float downPercent = (float)(this.lifeTime - 500) / 500.0f;
                downPercent = 1.0f - (float)Math.cos((double)downPercent * Math.PI / 2.0);
                PlayerFlyingHeight.playersFlyingHeight.put(this.owner.getUniqueID(), (int)((1.0f - downPercent) * 500.0f));
            } else {
                float upPercent = (float)this.lifeTime / 500.0f;
                upPercent = (float)Math.sin((double)upPercent * Math.PI / 2.0);
                PlayerFlyingHeight.playersFlyingHeight.put(this.owner.getUniqueID(), (int)(upPercent * 500.0f));
            }
        }
    }

    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 2000) {
            this.over();
        }
    }

    public Shape getHitBox() {
        if (this.lifeTime >= 1000) {
            float size = 200.0f;
            return new Ellipse2D.Float((float)this.targetX - size / 2.0f, (float)this.targetY - size / 2.0f, size, size);
        }
        return new Rectangle();
    }

    public boolean canHit(Mob mob) {
        return super.canHit(mob) && !this.hits.contains(mob.getUniqueID());
    }

    public void clientHit(Mob target) {
        this.hits.add(target.getUniqueID());
    }

    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || !this.hits.contains(target.getUniqueID())) {
            float modifier = target.getKnockbackModifier();
            if (modifier != 0.0f) {
                float knockback = 50.0f / modifier;
                float damagePercent = this.effectNumber / 100.0f;
                if (target.isBoss()) {
                    damagePercent /= 50.0f;
                } else if (target.isPlayer || target.isHuman) {
                    damagePercent /= 5.0f;
                }
                target.isServerHit(new GameDamage(DamageTypeRegistry.TRUE, (float)target.getMaxHealth() * damagePercent), target.x - this.owner.x, target.y - this.owner.y, knockback, (Attacker)this.owner);
            }
            this.hits.add(target.getUniqueID());
        }
    }

    public void hitObject(LevelObjectHit hit) {
    }
}

