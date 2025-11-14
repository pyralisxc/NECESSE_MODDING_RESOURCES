/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.entity.levelEvent.explosionEvent.ExplosionEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 */
package aphorea.levelevents.runes;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;

public class AphRuneOfDetonationEvent
extends ExplosionEvent {
    public float effectNumber;

    public AphRuneOfDetonationEvent() {
        super(0.0f, 0.0f, 0, new GameDamage(0.0f), false, 0.0f);
    }

    public AphRuneOfDetonationEvent(PlayerMob owner, float x, float y, float effectNumber) {
        super(x, y, 300, new GameDamage(0.0f), false, 0.0f, (Mob)owner);
        this.effectNumber = effectNumber;
    }

    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextFloat(this.effectNumber);
    }

    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.effectNumber = reader.getNextFloat();
    }

    public void init() {
        super.init();
        this.hitsOwner = false;
    }

    protected void onMobWasHit(Mob mob, float distance) {
        float mod = this.getDistanceMod(distance);
        float damagePercent = this.effectNumber;
        if (mob.isBoss()) {
            damagePercent /= 50.0f;
        } else if (mob.isPlayer || mob.isHuman) {
            damagePercent /= 5.0f;
        }
        GameDamage damage = new GameDamage(DamageTypeRegistry.TRUE, (float)mob.getMaxHealth() * damagePercent * mod);
        float knockback = (float)this.knockback * mod;
        mob.isServerHit(damage, (float)mob.getX() - this.x, (float)mob.getY() - this.y, knockback, (Attacker)this);
    }
}

