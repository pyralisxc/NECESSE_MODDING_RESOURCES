/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MouseProjectileAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.AscendedStaffBeamProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.level.maps.Level;

public class AscendedStaffToolItem
extends MagicProjectileToolItem {
    public AscendedStaffToolItem() {
        super(1900, null);
        this.rarity = Item.Rarity.UNIQUE;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(130.0f).setUpgradedValue(1.0f, 182.00005f);
        this.knockback.setBaseValue(10);
        this.velocity.setBaseValue(120);
        this.attackCooldownTime.setBaseValue(500);
        this.attackRange.setBaseValue(800);
        this.attackXOffset = 18;
        this.attackYOffset = 18;
        this.manaCost.setBaseValue(5.0f).setUpgradedValue(1.0f, 5.0f);
        this.resilienceGain.setBaseValue(1.0f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
        this.canBeUsedForRaids = false;
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        int spriteRes = this.attackTexture.getHeight();
        int sprites = this.attackTexture.getWidth() / spriteRes;
        int sprite = GameUtils.getAnim(player == null ? System.currentTimeMillis() : player.getLocalTime(), sprites, sprites * 100);
        return new GameSprite(this.attackTexture, sprite, 0, spriteRes);
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.rotation(-40.0f);
    }

    @Override
    protected SoundSettings getSwingSound() {
        return null;
    }

    @Override
    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        if (item.getGndData().getBoolean("charging")) {
            return 3000;
        }
        return super.getAttackAnimTime(item, attackerMob);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "ascendedstafftip"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addManaCostTip(list, currentItem, lastItem, perspective);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        GameDamage damage = this.getAttackDamage(item);
        AscendedStaffBeamProjectile ascendedStaffBeamProjectile = new AscendedStaffBeamProjectile(attackerMob.x, attackerMob.y, x, y, (float)this.getProjectileVelocity(item, attackerMob) * 1.0f, 2000, damage, 0, attackerMob);
        ascendedStaffBeamProjectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        ascendedStaffBeamProjectile.resetUniqueID(new GameRandom(seed));
        if (!attackerMob.isAttackHandlerFrom(item, slot)) {
            attackerMob.startAttackHandler(new MouseProjectileAttackHandler(attackerMob, slot, 0, ascendedStaffBeamProjectile));
            attackerMob.addAndSendAttackerProjectile((Projectile)ascendedStaffBeamProjectile, 0);
        }
        this.consumeMana(attackerMob, item);
        return item;
    }
}

