/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.enchants.Enchantable
 *  necesse.inventory.enchants.ItemEnchantment
 *  necesse.inventory.enchants.ToolItemEnchantment
 *  necesse.inventory.item.ItemInteractAction
 *  necesse.inventory.item.toolItem.spearToolItem.SpearToolItem
 *  necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.dagger;

import aphorea.items.tools.weapons.melee.dagger.logic.DaggerSecondaryAttackHandler;
import aphorea.registry.AphBuffs;
import aphorea.registry.AphEnchantments;
import aphorea.registry.AphModifiers;
import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.spearToolItem.SpearToolItem;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;
import necesse.level.maps.Level;

public abstract class AphDaggerToolItem
extends SpearToolItem
implements ItemInteractAction {
    public AphDaggerToolItem(int enchantCost) {
        super(enchantCost, CloseRangeWeaponsLootTable.closeRangeWeapons);
        this.keyWords.add("dagger");
        this.keyWords.remove("spear");
        this.width = 8.0f;
        this.attackXOffset = 12;
        this.attackYOffset = 2;
    }

    public GameSprite getWorldItemSprite(InventoryItem item, PlayerMob perspective) {
        GameSprite attackSprite = this.getAttackSprite(item, perspective);
        return attackSprite != null && Math.max(attackSprite.width, attackSprite.height) >= 32 ? attackSprite : this.getItemSprite(item, perspective);
    }

    public int getItemAttackerMinimumAttackRange(ItemAttackerMob attackerMob, InventoryItem item) {
        return 0;
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.buffManager.addBuff(new ActiveBuff(AphBuffs.DAGGER_ATTACK, (Mob)attackerMob, this.getAttackAnimTime(item, attackerMob), null), false);
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    protected ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.addAll((Collection)this.getDisplayNameTooltips(item, perspective, blackboard));
        tooltips.addAll((Collection)this.getDebugTooltips(item, perspective, blackboard));
        tooltips.addAll((Collection)this.getCraftingMatTooltips(item, perspective, blackboard));
        return tooltips;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"dagger"));
        return tooltips;
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }

    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        float animation;
        drawOptions.pointRotation(attackDirX, attackDirY);
        if (item.getGndData().getBoolean("charging")) {
            animation = item.getGndData().getFloat("chargePercent") / 2.0f + 0.5f;
        } else {
            animation = attackProgress;
            if ((double)attackProgress < 0.25) {
                animation += 0.25f;
            } else if ((double)attackProgress < 0.5) {
                animation += 0.5f;
            } else if ((double)attackProgress < 0.75) {
                animation -= 0.25f;
            }
        }
        drawOptions.thrustOffsets(attackDirX, attackDirY, animation);
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (!item.getGndData().getBoolean("charging")) {
            super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        }
    }

    public String getTranslatedTypeName() {
        return Localization.translate((String)"item", (String)"dagger");
    }

    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return !attackerMob.isRiding() && !attackerMob.isAttacking && !attackerMob.buffManager.hasBuff(AphBuffs.SPIN_ATTACK_COOLDOWN);
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        attackerMob.startAttackHandler(new DaggerSecondaryAttackHandler(attackerMob, slot, item, this, this.getAttackAnimTime(item, attackerMob) / 2, seed).startFromInteract());
        return item;
    }

    public void doSecondaryAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, ItemAttackSlot itemAttackSlot, int seed) {
        PlayerMob player;
        boolean throwItem;
        boolean bl = throwItem = !this.loyal(item);
        if (throwItem && attackerMob.isPlayer && player.attackSlot.isItemLocked((player = (PlayerMob)attackerMob).getInv())) {
            if (player.isServer()) {
                player.getServerClient().sendChatMessage(Localization.translate((String)"message", (String)"cannottrhowlockeditem"));
            }
            return;
        }
        Projectile projectile = this.getProjectile(level, x, y, attackerMob, item, ((Float)attackerMob.buffManager.getModifier(BuffModifiers.THROWING_VELOCITY)).floatValue(), throwItem);
        projectile.resetUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile(projectile, 0);
        if (throwItem) {
            itemAttackSlot.setItem(null);
        }
    }

    public ToolItemEnchantment getRandomEnchantment(GameRandom random, InventoryItem item) {
        return (ToolItemEnchantment)Enchantable.getRandomEnchantment((GameRandom)random, this.getValidEnchantmentIDs(item), (int)this.getEnchantmentID(item), ToolItemEnchantment.class);
    }

    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return this.getValidEnchantmentIDs(item).contains(enchantment.getID());
    }

    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        HashSet<Integer> enchantments = new HashSet<Integer>(super.getValidEnchantmentIDs(item));
        enchantments.addAll(AphEnchantments.daggerItemEnchantments);
        return enchantments;
    }

    public boolean loyal(InventoryItem item) {
        return (Boolean)this.getEnchantment(item).getModifier(AphModifiers.LOYAL);
    }

    public abstract Projectile getProjectile(Level var1, int var2, int var3, ItemAttackerMob var4, InventoryItem var5, float var6, boolean var7);

    public abstract Color getSecondaryAttackColor();

    public abstract int projectileRange();
}

