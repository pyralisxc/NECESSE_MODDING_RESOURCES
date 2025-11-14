/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitCalculatedEvent;
import necesse.entity.mobs.gameDamageType.DamageType;

public class MobWasHitEvent {
    public final Mob target;
    public final Attacker attacker;
    public final int beforeHealth;
    public final DamageType damageType;
    public final int damage;
    public final float knockbackX;
    public final float knockbackY;
    public final float knockbackAmount;
    public final boolean isCrit;
    public final GNDItemMap gndData;
    public final boolean showDamageTip;
    public final boolean playHitSound;
    public final boolean wasPrevented;

    public MobWasHitEvent(MobBeforeHitCalculatedEvent event) {
        this.target = event.target;
        this.attacker = event.attacker;
        this.beforeHealth = this.target.getHealth();
        this.damageType = event.damageType;
        this.damage = event.damage;
        this.knockbackX = event.knockbackX;
        this.knockbackY = event.knockbackY;
        this.knockbackAmount = event.knockbackAmount;
        this.isCrit = event.isCrit;
        this.showDamageTip = event.showDamageTip;
        this.playHitSound = event.playHitSound;
        this.wasPrevented = event.isPrevented();
        this.gndData = event.gndData;
    }

    public MobWasHitEvent(Mob target, Attacker attacker, PacketReader reader) {
        this.target = target;
        this.attacker = attacker;
        this.beforeHealth = target.getHealth();
        DamageType damageType = DamageTypeRegistry.getDamageType(reader.getNextByteUnsigned());
        if (damageType == null) {
            damageType = DamageTypeRegistry.NORMAL;
        }
        this.damageType = damageType;
        this.damage = reader.getNextInt();
        this.knockbackX = reader.getNextFloat();
        this.knockbackY = reader.getNextFloat();
        this.knockbackAmount = reader.getNextFloat();
        this.isCrit = reader.getNextBoolean();
        this.gndData = new GNDItemMap(reader);
        this.showDamageTip = reader.getNextBoolean();
        this.playHitSound = reader.getNextBoolean();
        this.wasPrevented = reader.getNextBoolean();
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextByteUnsigned(this.damageType.getID());
        writer.putNextInt(this.damage);
        writer.putNextFloat(this.knockbackX);
        writer.putNextFloat(this.knockbackY);
        writer.putNextFloat(this.knockbackAmount);
        writer.putNextBoolean(this.isCrit);
        this.gndData.writePacket(writer);
        writer.putNextBoolean(this.showDamageTip);
        writer.putNextBoolean(this.playHitSound);
        writer.putNextBoolean(this.wasPrevented);
    }
}

