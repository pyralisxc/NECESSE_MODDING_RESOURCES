/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.util.stream.Stream;
import necesse.engine.modifiers.ModifierValue;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.FollowingWormMobBody;
import necesse.entity.mobs.summon.FollowingWormMobHead;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.ToolItemSummonedMob;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.Item;

public abstract class AttackingFollowingWormMobHead<T extends FollowingWormMobBody<B, T>, B extends FollowingWormMobHead<T, B>>
extends FollowingWormMobHead<T, B>
implements ToolItemSummonedMob {
    protected GameDamage summonDamage = new GameDamage(0.0f);
    protected ToolItemEnchantment summonEnchantment = ToolItemEnchantment.noEnchant;
    protected ModifierValue<?>[] summonModifiers = new ModifierValue[0];

    public AttackingFollowingWormMobHead(int health, float waveLength, float distPerMoveSound, int totalBodyParts, float heightMultiplier, float heightOffset) {
        super(health, waveLength, distPerMoveSound, totalBodyParts, heightMultiplier, heightOffset);
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
    public boolean canTarget(Mob target) {
        ItemAttackerMob followingAttacker = this.getFollowingItemAttacker();
        if (followingAttacker != null && !followingAttacker.isHostile) {
            return target.isHostile || target.getUniqueID() == followingAttacker.getSummonFocusUniqueID();
        }
        return super.canTarget(target);
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        if (this.summonModifiers == null) {
            return super.getDefaultModifiers();
        }
        return Stream.of(this.summonModifiers);
    }
}

