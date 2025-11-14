/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedHashSet;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.bulletProjectile.SeedBulletProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.bulletItem.SeedBulletItem;
import necesse.inventory.item.placeableItem.objectItem.SeedObjectItem;
import necesse.inventory.item.toolItem.projectileToolItem.gunProjectileToolItem.GunProjectileToolItem;
import necesse.inventory.lootTable.presets.GunWeaponsLootTable;
import necesse.level.maps.Level;

public class SeedGunProjectileToolItem
extends GunProjectileToolItem {
    public static LinkedHashSet<String> SEED_AMMO_TYPES = new LinkedHashSet<String>(Arrays.asList("cabbageseed", "riceseed", "carrotseed", "chilipepperseed", "cornseed", "eggplantseed", "firemoneseed", "iceblossomseed", "onionseed", "potatoseed", "pumpkinseed", "strawberryseed", "sugarbeetseed", "sunflowerseed", "tomatoseed", "wheatseed", "beetseed"));
    protected float fasterAttackAnimModifier = 0.75f;

    public SeedGunProjectileToolItem() {
        super(SEED_AMMO_TYPES, 1300, GunWeaponsLootTable.gunWeapons);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(250);
        this.attackDamage.setBaseValue(35.0f).setUpgradedValue(1.0f, 70.000015f);
        this.attackXOffset = 20;
        this.attackYOffset = 10;
        this.velocity.setBaseValue(200);
        this.moveDist = 65;
        this.attackRange.setBaseValue(500);
        this.resilienceGain.setBaseValue(0.7f);
        this.addGlobalIngredient("bulletuser");
        this.canBeUsedForRaids = false;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "seedguntip"));
        return tooltips;
    }

    @Override
    public int getFlatAttackAnimTime(InventoryItem item) {
        int attackTime = super.getFlatAttackAnimTime(item);
        if (item.getGndData().getBoolean("attackSpeedBullet")) {
            attackTime = (int)((float)attackTime * this.fasterAttackAnimModifier);
        }
        return attackTime;
    }

    @Override
    protected void addAmmoTooltips(ListGameTooltips tooltips, InventoryItem item) {
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int bulletID = mapContent.getShortUnsigned("bulletID", 65535);
        if (bulletID != 65535) {
            Item seedObjectItem = ItemRegistry.getItem(bulletID);
            if (seedObjectItem != null && seedObjectItem.type == Item.Type.SEED) {
                boolean dropItem;
                boolean shouldFire;
                boolean consumeAmmo;
                GameRandom random = new GameRandom(seed + 5);
                float ammoConsumeChance = this.getAmmoConsumeChance(attackerMob, item);
                boolean bl = consumeAmmo = ammoConsumeChance >= 1.0f || ammoConsumeChance > 0.0f && random.getChance(ammoConsumeChance);
                if (!consumeAmmo) {
                    shouldFire = true;
                    dropItem = false;
                } else if (attackerMob instanceof AmmoUserMob) {
                    AmmoConsumed consumed = ((AmmoUserMob)((Object)attackerMob)).removeAmmo(seedObjectItem, 1, "bulletammo");
                    shouldFire = consumed.amount >= 1;
                    dropItem = random.getChance(consumed.dropChance);
                } else {
                    shouldFire = true;
                    dropItem = false;
                }
                if (shouldFire) {
                    this.fireSeedProjectiles(level, x, y, attackerMob, item, seed, (SeedObjectItem)seedObjectItem, dropItem, mapContent);
                    boolean isAttackSpeedBullet = seedObjectItem.getStringID().equals("riceseed") || seedObjectItem.getStringID().equals("strawberryseed");
                    item.getGndData().setBoolean("attackSpeedBullet", isAttackSpeedBullet);
                }
            } else {
                GameLog.warn.println(attackerMob.getDisplayName() + " tried to use item " + (seedObjectItem == null ? Integer.valueOf(bulletID) : seedObjectItem.getStringID()) + " as seed seedObjectItem.");
            }
        }
        return item;
    }

    protected void fireSeedProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, SeedObjectItem seedObjectItem, boolean dropItem, GNDItemMap mapContent) {
        int range;
        if (this.controlledRange) {
            Point newTarget = this.controlledRangePosition(new GameRandom(seed + 10), attackerMob, x, y, item, this.controlledMinRange, this.controlledInaccuracy);
            x = newTarget.x;
            y = newTarget.y;
            range = (int)attackerMob.getDistance(x, y);
        } else {
            range = this.getAttackRange(item);
        }
        Item seedBullet = ItemRegistry.getItem("seedbullet");
        Projectile projectile = this.getProjectile(item, (SeedBulletItem)seedBullet, attackerMob.x, attackerMob.y, x, y, range, attackerMob);
        ((SeedBulletProjectile)projectile).setSeedBulletVariant(seedObjectItem);
        String seedName = seedObjectItem.getStringID();
        float resGain = seedName.equals("cornseed") || seedName.equals("wheatseed") ? this.getResilienceGain(item) + 1.0f : this.getResilienceGain(item);
        projectile.setModifier(new ResilienceOnHitProjectileModifier(resGain));
        projectile.dropItem = dropItem;
        projectile.getUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.handgun).volume(0.24f).basePitch(2.0f).pitchVariance(0.1f);
    }

    @Override
    public Projectile getNormalProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage toolItemDamage, int knockback, ItemAttackerMob attackerMob) {
        return new SeedBulletProjectile(x, y, targetX, targetY, velocity, range, toolItemDamage, knockback, attackerMob);
    }
}

