/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.level.maps.LevelObjectHit
 */
package aphorea.levelevents.runes;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.HashMap;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.level.maps.LevelObjectHit;

public class AphRuneOfPestWardenEvent
extends HitboxEffectEvent
implements Attacker {
    private HashMap<Integer, Integer> hits = new HashMap();
    int count;

    public AphRuneOfPestWardenEvent() {
    }

    public AphRuneOfPestWardenEvent(Mob owner) {
        super(owner, new GameRandom());
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
    }

    public void init() {
        super.init();
        this.hitsObjects = false;
        this.hits = new HashMap();
        this.count = 0;
    }

    public void clientTick() {
        super.clientTick();
        ++this.count;
        if (!this.owner.buffManager.hasBuff("runeofpestwardenactive")) {
            this.over();
        }
    }

    public void serverTick() {
        super.serverTick();
        ++this.count;
        if (!this.owner.buffManager.hasBuff("runeofpestwardenactive")) {
            this.over();
        }
    }

    public Shape getHitBox() {
        int size = 50;
        return new Rectangle((int)this.owner.x - size / 2, (int)this.owner.y - size / 2, size, size);
    }

    public void clientHit(Mob target) {
        this.hits.put(target.getUniqueID(), this.count);
    }

    public void serverHit(Mob target, boolean clientSubmitted) {
        if (clientSubmitted || this.notInCooldown(target)) {
            float modifier = target.getKnockbackModifier();
            if (modifier != 0.0f) {
                float damagePercent = 0.2f;
                if (target.isBoss()) {
                    damagePercent /= 50.0f;
                } else if (target.isPlayer || target.isHuman) {
                    damagePercent /= 5.0f;
                }
                GameDamage damage = new GameDamage(DamageTypeRegistry.TRUE, (float)target.getMaxHealth() * damagePercent);
                float knockback = 200.0f / modifier;
                target.isServerHit(damage, target.x - (float)((int)this.owner.x), target.y - (float)((int)this.owner.y), knockback, (Attacker)this.owner);
            }
            this.hits.put(target.getUniqueID(), this.count);
        }
    }

    public boolean canHit(Mob mob) {
        return super.canHit(mob) && this.notInCooldown(mob);
    }

    public boolean notInCooldown(Mob mob) {
        int lastHit = this.hits.getOrDefault(mob.getUniqueID(), -1);
        return lastHit == -1 || lastHit + 10 <= this.count;
    }

    public void hitObject(LevelObjectHit hit) {
    }
}

