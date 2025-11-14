/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.function.Supplier;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;

public class GameDamage {
    public static boolean staticDamage = false;
    public final necesse.entity.mobs.gameDamageType.DamageType type;
    public final float damage;
    public final float armorPen;
    public final float baseCritChance;
    public final float playerDamageMultiplier;
    public final float finalDamageMultiplier;

    public GameDamage(necesse.entity.mobs.gameDamageType.DamageType type, float damage, float armorPen, float baseCritChance, float playerDamageMultiplier, float finalDamageMultiplier) {
        this.type = type;
        this.damage = damage;
        this.armorPen = armorPen;
        this.baseCritChance = baseCritChance;
        this.playerDamageMultiplier = playerDamageMultiplier;
        this.finalDamageMultiplier = finalDamageMultiplier;
    }

    public GameDamage(necesse.entity.mobs.gameDamageType.DamageType type, float damage, float armorPen, float baseCritChance) {
        this(type, damage, armorPen, baseCritChance, 1.0f, 1.0f);
    }

    public GameDamage(necesse.entity.mobs.gameDamageType.DamageType type, float damage, float baseCritChance) {
        this(type, damage, 0.0f, baseCritChance);
    }

    public GameDamage(necesse.entity.mobs.gameDamageType.DamageType type, float damage) {
        this(type, damage, 0.0f);
    }

    public GameDamage(float damage, float armorPen, float baseCritChance, float playerDamageMultiplier, float finalDamageMultiplier) {
        this(DamageTypeRegistry.NORMAL, damage, armorPen, baseCritChance, playerDamageMultiplier, finalDamageMultiplier);
    }

    public GameDamage(float damage, float armorPen, float baseCritChance) {
        this(DamageTypeRegistry.NORMAL, damage, armorPen, baseCritChance);
    }

    public GameDamage(float damage, float armorPen) {
        this(DamageTypeRegistry.NORMAL, damage, armorPen, 0.0f);
    }

    public GameDamage(float damage) {
        this(damage, 0.0f);
    }

    @Deprecated
    public GameDamage(DamageType type, float damage, float armorPen, float baseCritChance, float playerDamageMultiplier, float finalDamageMultiplier) {
        this(type.converter.get(), damage, armorPen, baseCritChance, playerDamageMultiplier, finalDamageMultiplier);
    }

    @Deprecated
    public GameDamage(DamageType type, float damage, float armorPen, float baseCritChance, float playerDamageMultiplier) {
        this(type.converter.get(), damage, armorPen, baseCritChance, playerDamageMultiplier, 1.0f);
    }

    @Deprecated
    public GameDamage(DamageType type, float damage, float armorPen, float baseCritChance) {
        this(type, damage, armorPen, baseCritChance, 1.0f, 1.0f);
    }

    @Deprecated
    public GameDamage(DamageType type, float damage, float baseCritChance) {
        this(type, damage, 0.0f, baseCritChance);
    }

    @Deprecated
    public GameDamage(DamageType type, float damage) {
        this(type, damage, 0.0f);
    }

    private GameDamage(GameDamage copy) {
        this.type = copy.type;
        this.damage = copy.damage;
        this.armorPen = copy.armorPen;
        this.baseCritChance = copy.baseCritChance;
        this.playerDamageMultiplier = copy.playerDamageMultiplier;
        this.finalDamageMultiplier = copy.finalDamageMultiplier;
    }

    public GameDamage enchantedDamage(ModifierContainer container, Modifier<Float> damageModifier, Modifier<Integer> armorPenModifier, Modifier<Float> critModifier) {
        float damage = container == null ? this.damage : this.damage * container.applyModifierLimited(damageModifier, (Float)damageModifier.defaultBuffManagerValue).floatValue();
        float armorPen = container == null ? this.armorPen : this.armorPen + (float)container.applyModifierLimited(armorPenModifier, (Integer)armorPenModifier.defaultBuffManagerValue).intValue();
        float crit = container == null ? this.baseCritChance : this.baseCritChance + container.applyModifierLimited(critModifier, (Float)critModifier.defaultBuffManagerValue).floatValue();
        return new GameDamage(this.type, damage, armorPen, crit);
    }

    public GameDamage add(float damage, int armorPen, float critChance) {
        return new GameDamage(this.type, this.damage + damage, this.armorPen + (float)armorPen, this.baseCritChance + critChance, this.playerDamageMultiplier, this.finalDamageMultiplier);
    }

    public GameDamage setDamage(float damage) {
        return new GameDamage(this.type, damage, this.armorPen, this.baseCritChance, this.playerDamageMultiplier, this.finalDamageMultiplier);
    }

    public GameDamage setArmorPen(float armorPen) {
        return new GameDamage(this.type, this.damage, armorPen, this.baseCritChance, this.playerDamageMultiplier, this.finalDamageMultiplier);
    }

    public GameDamage setCritChance(float critChance) {
        return new GameDamage(this.type, this.damage, this.armorPen, critChance, this.playerDamageMultiplier, this.finalDamageMultiplier);
    }

    public GameDamage setPlayerMultiplier(float multiplier) {
        return new GameDamage(this.type, this.damage, this.armorPen, this.baseCritChance, multiplier, this.finalDamageMultiplier);
    }

    public GameDamage setFinalMultiplier(float multiplier) {
        return new GameDamage(this.type, this.damage, this.armorPen, this.baseCritChance, this.playerDamageMultiplier, multiplier);
    }

    public GameDamage addDamage(float damage) {
        return this.setDamage(this.damage + damage);
    }

    public GameDamage addArmorPen(float armorPen) {
        return this.setArmorPen(this.armorPen + armorPen);
    }

    public GameDamage addCritChance(float baseCritChance) {
        return this.setCritChance(this.baseCritChance + baseCritChance);
    }

    public GameDamage modDamage(float multiplier) {
        return this.setDamage(this.damage * multiplier);
    }

    public GameDamage modArmorPen(float multiplier) {
        return this.setArmorPen(this.armorPen * multiplier);
    }

    public GameDamage modCritChance(float multiplier) {
        return this.setCritChance(this.baseCritChance * multiplier);
    }

    public GameDamage modPlayerMultiplier(float multiplier) {
        return this.setPlayerMultiplier(this.playerDamageMultiplier * multiplier);
    }

    public GameDamage modFinalMultiplier(float multiplier) {
        return this.setFinalMultiplier(this.finalDamageMultiplier * multiplier);
    }

    public static float getDamageModifier(Attacker attacker, necesse.entity.mobs.gameDamageType.DamageType type) {
        Mob owner;
        float damageMod = 1.0f;
        if (attacker != null && (owner = attacker.getAttackOwner()) != null) {
            damageMod = owner.getOutgoingDamageModifier();
        }
        damageMod += type.getDamageModifier(attacker);
        damageMod = BuffModifiers.ALL_DAMAGE.finalLimit(Float.valueOf(damageMod)).floatValue();
        return damageMod;
    }

    public float getFinalDamageModifier(Attacker attacker) {
        Mob owner;
        float damageMod = this.finalDamageMultiplier;
        if (attacker != null && (owner = attacker.getAttackOwner()) != null) {
            damageMod *= owner.getFinalOutgoingDamageModifier();
        }
        return damageMod;
    }

    public float getBuffedDamage(Attacker attacker) {
        return this.damage * GameDamage.getDamageModifier(attacker, this.type);
    }

    public float getCritChanceModifier(Attacker attacker, necesse.entity.mobs.gameDamageType.DamageType type) {
        Mob owner;
        float chance = 0.0f;
        if (attacker != null && (owner = attacker.getAttackOwner()) != null) {
            chance = owner.getCritChance();
        }
        return GameMath.limit(chance += type.getTypeCritChanceModifier(attacker), 0.0f, 1.0f);
    }

    public float getBuffedCritChance(Attacker attacker) {
        return GameMath.limit(this.baseCritChance + this.getCritChanceModifier(attacker, this.type), 0.0f, 1.0f);
    }

    private static float applyRandomizer(float damage) {
        if (staticDamage) {
            return damage;
        }
        return damage * GameRandom.globalRandom.getFloatBetween(0.8f, 1.2f);
    }

    public int getTotalDamage(Mob target, Attacker attacker, float critModifier) {
        float damage = this.getBuffedDamage(attacker);
        if (target != null) {
            if (target.isPlayer) {
                float damageTakenModifier = this.playerDamageMultiplier;
                if (target.isServer()) {
                    damageTakenModifier *= target.getLevel().getServer().world.settings.difficulty.damageTakenModifier;
                } else if (target.isClient()) {
                    damageTakenModifier *= target.getLevel().getClient().worldSettings.difficulty.damageTakenModifier;
                }
                damage *= damageTakenModifier;
            }
            damage = Math.max(0.0f, damage - this.getDamageReduction(target, attacker));
        }
        damage *= critModifier;
        if (target != null) {
            damage *= target.getIncomingDamageModifier();
        }
        return (int)Math.max(1.0f, GameDamage.applyRandomizer(damage *= this.getFinalDamageModifier(attacker)));
    }

    public float getDamageReduction(Mob target, Attacker attacker) {
        return this.type.getDamageReduction(target, attacker, this);
    }

    public static float getDamageReduction(float armor) {
        return armor * 0.5f;
    }

    public boolean equals(GameDamage other) {
        if (other == null) {
            return false;
        }
        return this.type == other.type && this.damage == other.damage && this.armorPen == other.armorPen && this.baseCritChance == other.baseCritChance && this.playerDamageMultiplier == other.playerDamageMultiplier && this.finalDamageMultiplier == other.finalDamageMultiplier;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof GameDamage) {
            return this.equals((GameDamage)obj);
        }
        return false;
    }

    public static GameDamage fromLoadData(LoadData save) {
        String typeString = save.getUnsafeString("type");
        int id = DamageTypeRegistry.getDamageTypeID(typeString);
        if (id == -1) {
            id = DamageTypeRegistry.getDamageTypeID(typeString.toLowerCase());
        }
        if (id == -1) {
            throw new LoadDataException("Could not load damage type: " + typeString);
        }
        necesse.entity.mobs.gameDamageType.DamageType type = DamageTypeRegistry.getDamageType(id);
        float damage = save.getFloat("damage");
        float armorPen = save.getFloat("armorPen", 0.0f, false);
        float baseCritChance = save.getFloat("baseCritChance", 0.0f, false);
        float playerDamageMultiplier = save.getFloat("playerDamageMultiplier", 1.0f, false);
        float finalDamageMultiplier = save.getFloat("finalDamageMultiplier", 1.0f, false);
        return new GameDamage(type, damage, armorPen, baseCritChance, playerDamageMultiplier, finalDamageMultiplier);
    }

    public void addSaveData(SaveData save) {
        save.addUnsafeString("type", this.type.getStringID());
        save.addFloat("damage", this.damage);
        if (this.armorPen != 0.0f) {
            save.addFloat("armorPen", this.armorPen);
        }
        if (this.baseCritChance != 0.0f) {
            save.addFloat("baseCritChance", this.baseCritChance);
        }
        if (this.playerDamageMultiplier != 1.0f) {
            save.addFloat("playerDamageMultiplier", this.playerDamageMultiplier);
        }
        if (this.finalDamageMultiplier != 1.0f) {
            save.addFloat("finalDamageMultiplier", this.finalDamageMultiplier);
        }
    }

    public Packet getPacket() {
        Packet out = new Packet();
        this.writePacket(new PacketWriter(out));
        return out;
    }

    public void writePacket(PacketWriter writer) {
        writer.putNextByteUnsigned(this.type.getID());
        writer.putNextFloat(this.damage);
        writer.putNextFloat(this.armorPen);
        writer.putNextFloat(this.baseCritChance);
        writer.putNextBoolean(this.playerDamageMultiplier != 1.0f);
        if (this.playerDamageMultiplier != 1.0f) {
            writer.putNextFloat(this.playerDamageMultiplier);
        }
        writer.putNextBoolean(this.finalDamageMultiplier != 1.0f);
        if (this.finalDamageMultiplier != 1.0f) {
            writer.putNextFloat(this.finalDamageMultiplier);
        }
    }

    public static GameDamage fromPacket(Packet packet) {
        return GameDamage.fromReader(new PacketReader(packet));
    }

    public static GameDamage fromReader(PacketReader reader) {
        necesse.entity.mobs.gameDamageType.DamageType type = DamageTypeRegistry.getDamageType(reader.getNextByteUnsigned());
        if (type == null) {
            type = DamageTypeRegistry.NORMAL;
        }
        float damage = reader.getNextFloat();
        int armorPen = reader.getNextInt();
        float baseCritChance = reader.getNextFloat();
        boolean hasPlayerDamageMultiplier = reader.getNextBoolean();
        float playerDamageMultiplier = 1.0f;
        if (hasPlayerDamageMultiplier) {
            playerDamageMultiplier = reader.getNextFloat();
        }
        boolean hasFinalDamageMultiplier = reader.getNextBoolean();
        float finalDamageMultiplier = 1.0f;
        if (hasFinalDamageMultiplier) {
            finalDamageMultiplier = reader.getNextFloat();
        }
        return new GameDamage(type, damage, (float)armorPen, baseCritChance, playerDamageMultiplier, finalDamageMultiplier);
    }

    @Deprecated
    public static enum DamageType {
        NORMAL(() -> DamageTypeRegistry.NORMAL),
        TRUE(() -> DamageTypeRegistry.TRUE),
        MELEE(() -> DamageTypeRegistry.MELEE),
        RANGED(() -> DamageTypeRegistry.RANGED),
        MAGIC(() -> DamageTypeRegistry.MAGIC),
        SUMMON(() -> DamageTypeRegistry.SUMMON);

        public final Supplier<necesse.entity.mobs.gameDamageType.DamageType> converter;

        private DamageType(Supplier<necesse.entity.mobs.gameDamageType.DamageType> converter) {
            this.converter = converter;
        }
    }
}

