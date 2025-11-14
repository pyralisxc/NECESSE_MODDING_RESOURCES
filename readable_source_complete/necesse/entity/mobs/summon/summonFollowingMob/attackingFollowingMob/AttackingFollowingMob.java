/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.SummonedFollowingMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.ToolItemSummonedMob;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.Item;

public abstract class AttackingFollowingMob
extends SummonedFollowingMob
implements ToolItemSummonedMob {
    protected GameDamage summonDamage = new GameDamage(0.0f);
    protected ToolItemEnchantment summonEnchantment = ToolItemEnchantment.noEnchant;
    protected ModifierValue<?>[] summonModifiers = new ModifierValue[0];

    public AttackingFollowingMob(int health) {
        super(health);
    }

    @Override
    public void updateDamage(GameDamage damage) {
        this.summonDamage = damage;
    }

    @Override
    public void setEnchantment(ToolItemEnchantment enchantment) {
        this.summonEnchantment = enchantment;
        this.summonModifiers = new ModifierValue[]{new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, enchantment.getModifier(ToolItemModifiers.DAMAGE)), new ModifierValue<Float>(BuffModifiers.SPEED, enchantment.getModifier(ToolItemModifiers.SUMMONS_SPEED)), new ModifierValue<Float>(BuffModifiers.CHASER_RANGE, enchantment.getModifier(ToolItemModifiers.SUMMONS_TARGET_RANGE)), new ModifierValue<Float>(BuffModifiers.KNOCKBACK_OUT, enchantment.getModifier(ToolItemModifiers.KNOCKBACK))};
    }

    @Override
    public void setRemoveWhenNotInInventory(Item summonItem, CheckSlotType slotType) {
        this.removeWhenNotInInventoryItem = summonItem;
        this.removeWhenNotInInventorySlotType = slotType;
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.setEnchantment(EnchantmentRegistry.getEnchantment(reader.getNextShortUnsigned(), ToolItemEnchantment.class, ToolItemEnchantment.noEnchant));
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextShortUnsigned(this.summonEnchantment.getID());
    }

    @Override
    public float getSpeedModifier() {
        Mob attackOwner;
        if (this.isFollowing() && (attackOwner = this.getAttackOwner()) != null) {
            return attackOwner.buffManager.getModifier(BuffModifiers.SUMMONS_SPEED).floatValue() * super.getSpeedModifier();
        }
        return super.getSpeedModifier();
    }

    @Override
    public boolean canTarget(Mob target) {
        ItemAttackerMob followingAttacker = this.getFollowingItemAttacker();
        if (followingAttacker != null && !followingAttacker.isHostile) {
            return target.isHostile || target.getUniqueID() == followingAttacker.getSummonFocusUniqueID();
        }
        return super.canTarget(target);
    }

    @Override
    public boolean canCollisionHit(Mob target) {
        return super.canCollisionHit(target);
    }

    @Override
    public boolean onMouseHover(GameCamera camera, PlayerMob perspective, boolean debug) {
        if (!debug) {
            return false;
        }
        return super.onMouseHover(camera, perspective, debug);
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        if (this.summonModifiers == null) {
            return super.getDefaultModifiers();
        }
        return Stream.of(this.summonModifiers);
    }
}

