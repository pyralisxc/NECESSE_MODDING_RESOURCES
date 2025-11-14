/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameRandom
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.gfx.GameResources
 *  necesse.level.maps.LevelObjectHit
 */
package aphorea.levelevents.runes;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.HashSet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.GameResources;
import necesse.level.maps.LevelObjectHit;

public class AphTidalRuneEvent
extends HitboxEffectEvent
implements Attacker {
    private int lifeTime = 0;
    private HashSet<Integer> hits = new HashSet();

    public AphTidalRuneEvent() {
    }

    public AphTidalRuneEvent(Mob owner) {
        super(owner, new GameRandom());
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.lifeTime);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.lifeTime = reader.getNextShortUnsigned();
    }

    public void init() {
        super.init();
        this.hitsObjects = false;
        this.hits = new HashSet();
        if (this.owner != null) {
            SoundManager.playSound((GameSound)GameResources.magicbolt1, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this.owner).volume(1.0f).pitch(0.8f));
        }
    }

    public void clientTick() {
        super.clientTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 600) {
            this.over();
        }
    }

    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= 600) {
            this.over();
        }
    }

    public Shape getHitBox() {
        if (this.owner != null) {
            int size = 150;
            return new Rectangle(this.owner.getX() - size / 2, this.owner.getY() - size / 2, size, size);
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
            target.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, target, 0.2f, (Attacker)this), true);
            float modifier = target.getKnockbackModifier();
            if (modifier != 0.0f) {
                float knockback = 250.0f / modifier;
                target.isServerHit(new GameDamage(0.0f), target.x - this.owner.x, target.y - this.owner.y, knockback, (Attacker)this.owner);
            }
            this.hits.add(target.getUniqueID());
        }
    }

    public void hitObject(LevelObjectHit hit) {
    }
}

