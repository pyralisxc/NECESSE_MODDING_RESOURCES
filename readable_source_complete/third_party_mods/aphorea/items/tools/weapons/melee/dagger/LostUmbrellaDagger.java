/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.AttackAnimMob
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions
 *  necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions$AttackItemSprite
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.dagger;

import aphorea.items.tools.weapons.melee.dagger.AphDaggerToolItem;
import aphorea.projectiles.toolitem.DaggerProjectile;
import aphorea.utils.AphColors;
import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class LostUmbrellaDagger
extends AphDaggerToolItem {
    protected GameTexture attackOpenTexture;

    public LostUmbrellaDagger() {
        super(1900);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(100.0f).setUpgradedValue(1.0f, 100.0f);
        this.attackRange.setBaseValue(55);
        this.knockback.setBaseValue(25);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, float throwingVelocity, boolean shouldDrop) {
        return new DaggerProjectile.LostUmbrellaDaggerProjectile(level, (Mob)attackerMob, attackerMob.x, attackerMob.y, x, y, 100.0f * throwingVelocity, this.projectileRange(), this.getAttackDamage(item), this.getKnockback(item, (Attacker)attackerMob), shouldDrop, item.item.getStringID(), item.getGndData());
    }

    @Override
    public int projectileRange() {
        return 400;
    }

    @Override
    public Color getSecondaryAttackColor() {
        return AphColors.pink_witch;
    }

    public void loadTextures() {
        super.loadTextures();
        this.attackOpenTexture = GameTexture.fromFile((String)("player/weapons/" + this.getStringID() + "_open"));
    }

    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        if (attackProgress < 0.5f) {
            return super.setupItemSpriteAttackDrawOptions(options, item, player, mobDir, attackDirX, attackDirY, attackProgress, itemColor);
        }
        ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(new GameSprite(this.attackOpenTexture));
        itemSprite.itemRotatePoint(this.attackXOffset, this.attackYOffset);
        if (itemColor != null) {
            itemSprite.itemColor(itemColor);
        }
        return itemSprite.itemEnd();
    }

    public ArrayList<Shape> getHitboxes(InventoryItem item, AttackAnimMob mob, int aimX, int aimY, ToolItemMobAbilityEvent event, boolean forDebug) {
        float attackProgress = mob.getAttackAnimProgress();
        this.width = attackProgress < 0.5f ? 8.0f : 46.0f;
        return super.getHitboxes(item, mob, aimX, aimY, event, forDebug);
    }
}

