/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.AscendedBowAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.AscendedBowBoltProjectile;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.HumanAttackDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.ProjectileToolItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AscendedBowProjectileToolItem
extends ProjectileToolItem
implements ItemInteractAction {
    public int moveDist = 25;
    public static final int attackAnimTimeMillis = 450;
    public static final Color toggledOnColor = new Color(255, 0, 231);
    public static final Color toggledOffColor = new Color(128, 61, 225);
    public static final Color toggledOffOverlayColor = new Color(200, 200, 255);
    protected GameTexture zapTexture;
    protected IntUpgradeValue arrowsFiredUpgradeValue = new IntUpgradeValue(0, 0.0f);

    public AscendedBowProjectileToolItem() {
        super(800, null);
        this.setItemCategory("equipment", "weapons", "rangedweapons");
        this.setItemCategory(ItemCategory.equipmentManager, "weapons", "rangedweapons");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "weapons", "rangedweapons");
        this.keyWords.add("bow");
        this.damageType = DamageTypeRegistry.RANGED;
        this.knockback.setBaseValue(25);
        this.itemAttackerPredictionDistanceOffset = -25.0f;
        this.keyWords.add("ascended");
        this.rarity = Item.Rarity.UNIQUE;
        this.attackAnimTime.setBaseValue(450);
        this.attackCooldownTime.setBaseValue(450);
        this.attackDamage.setBaseValue(80.0f).setUpgradedValue(1.0f, 93.33336f);
        this.attackRange.setBaseValue(1500);
        this.velocity.setBaseValue(120);
        this.attackXOffset = 14;
        this.attackYOffset = 33;
        this.arrowsFiredUpgradeValue.setBaseValue(7).setUpgradedValue(1.0f, 8);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "ascendedbowtip1"), 380);
        tooltips.add(Localization.translate("itemtooltip", "ascendedbowtip2"), 380);
        return tooltips;
    }

    @Override
    public void addStatTooltips(ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, ItemAttackerMob perspective, boolean forceAdd) {
        this.addAttackDamageTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addAttackSpeedTip(list, currentItem, lastItem, perspective);
        this.addResilienceGainTip(list, currentItem, lastItem, perspective, forceAdd);
        this.addCritChanceTip(list, currentItem, lastItem, perspective, forceAdd);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isAttackHandlerFrom(item, slot)) {
            AscendedBowAttackHandler ascendedBowAttackHandler = new AscendedBowAttackHandler(attackerMob, slot, x, y, seed, this);
            attackerMob.startAttackHandler(ascendedBowAttackHandler);
            attackerMob.startItemCooldown(item.item, this.attackCooldownTime.getValue(this.getUpgradeTier(item)));
        }
        return item;
    }

    public InventoryItem superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        this.fireProjectiles(level, x, y, attackerMob, item, seed, null, false, mapContent);
        return item;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (!item.getGndData().getBoolean("ascendedAttack")) {
            return;
        }
        super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.bow).volume(0.5f);
    }

    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean dropItem, GNDItemMap mapContent) {
        int arrowsFired = !attackerMob.isPlayer && attackerMob.isHostile ? 1 : this.arrowsFiredUpgradeValue.getValue(this.getUpgradeTier(item));
        boolean toggledOn = item.getGndData().getBoolean("toggledOn");
        Point2D.Float dir = GameMath.normalize((float)x - attackerMob.x, (float)y - attackerMob.y);
        int totalWidthToCover = 78;
        float originX = attackerMob.x + dir.x * 16.0f;
        float originY = attackerMob.y + dir.y * 16.0f;
        int distBetweenEachArrow = totalWidthToCover / (arrowsFired - 1);
        float perArrowStartOffsetX = dir.y * (float)distBetweenEachArrow * ((float)arrowsFired / 2.0f);
        float perArrowStartOffsetY = -(dir.x * (float)distBetweenEachArrow * ((float)arrowsFired / 2.0f));
        GameRandom seededRandom = new GameRandom(seed);
        for (int i = 0; i < arrowsFired; ++i) {
            float perArrowOffsetX = -(dir.y * (float)distBetweenEachArrow * (float)i);
            float perArrowOffsetY = dir.x * (float)distBetweenEachArrow * (float)i;
            float offsetX = originX + perArrowOffsetX + perArrowStartOffsetX;
            float offsetY = originY + perArrowOffsetY + perArrowStartOffsetY;
            Projectile projectile = this.getAscendedBoltProjectile(attackerMob, seededRandom, item, (int)offsetX, (int)offsetY, x, y, toggledOn);
            if (!toggledOn) {
                projectile.setAngle(GameMath.getAngle(dir) + 90.0f);
                float pyramidOffset = (float)(i >= arrowsFired / 2 ? arrowsFired - i - 1 : i) * 0.1f;
                projectile.speed *= 2.0f + pyramidOffset;
                projectile.distance = (int)((float)projectile.distance * (1.4f + pyramidOffset));
            }
            attackerMob.addAndSendAttackerProjectile(projectile, this.moveDist);
        }
    }

    protected Projectile getAscendedBoltProjectile(ItemAttackerMob owner, GameRandom seededRandom, InventoryItem item, int startX, int startY, int targetX, int targetY, boolean toggledOn) {
        GameDamage damage = this.getAttackDamage(item).modFinalMultiplier(0.25f);
        AscendedBowBoltProjectile ascendedbolt = new AscendedBowBoltProjectile(owner.getLevel(), startX, startY, targetX, targetY, this.getProjectileVelocity(item, owner), this.getAttackRange(item), damage, owner, toggledOn);
        ascendedbolt.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item) * 0.25f));
        ascendedbolt.dropItem = false;
        ascendedbolt.getUniqueID(seededRandom);
        return ascendedbolt;
    }

    @Override
    protected void loadAttackTexture() {
        try {
            this.attackTexture = GameTexture.fromFileRaw("player/weapons/" + this.getStringID());
            this.zapTexture = GameTexture.fromFileRaw("player/weapons/" + this.getStringID() + "_front");
        }
        catch (FileNotFoundException e) {
            this.attackTexture = null;
            this.zapTexture = null;
        }
    }

    public ItemAttackDrawOptions getLightningDrawOptions(InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress) {
        int timePerFrame = 100;
        int spriteCount = 10;
        int spriteResX = this.zapTexture.getWidth() / spriteCount;
        int spriteResY = this.zapTexture.getHeight();
        int sprite = GameUtils.getAnim(player == null ? System.currentTimeMillis() : player.getLocalTime(), spriteCount, spriteCount * timePerFrame);
        GameSprite lightningSprite = new GameSprite(this.zapTexture, sprite, 0, spriteResX, spriteResY, spriteResX, spriteResY);
        ItemAttackDrawOptions options = ItemAttackDrawOptions.start(mobDir);
        ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(lightningSprite);
        itemSprite.itemRotatePoint(this.attackXOffset, this.attackYOffset);
        if (!this.animDrawBehindHand(item)) {
            options.itemAfterHand();
        }
        this.setDrawAttackRotation(item, options, attackDirX, attackDirY, attackProgress);
        return options;
    }

    @Override
    public Color getDrawColor(InventoryItem item, PlayerMob player) {
        return AscendedBowProjectileToolItem.getToggledBowState(item) ? Color.WHITE : toggledOffOverlayColor;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return true;
    }

    @Override
    public void setupLevelInteractMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        ItemInteractAction.super.setupLevelInteractMapContent(map, level, x, y, attackerMob, item);
        map.setBoolean("desiredToggleState", !AscendedBowProjectileToolItem.getToggledBowState(item));
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        AscendedBowProjectileToolItem.setToggledBowState(mapContent.getBoolean("desiredToggleState"), item, attackerMob);
        return item;
    }

    public static boolean getToggledBowState(InventoryItem item) {
        return item.getGndData().getBoolean("toggledOn");
    }

    public static void setToggledBowState(boolean isToggled, InventoryItem item, ItemAttackerMob owner) {
        boolean previousToggle = AscendedBowProjectileToolItem.getToggledBowState(item);
        item.getGndData().setBoolean("toggledOn", isToggled);
        if (owner == null) {
            return;
        }
        Level level = owner.getLevel();
        if (!level.isClient() || previousToggle == isToggled) {
            return;
        }
        GameRandom random = GameRandom.globalRandom;
        Point2D.Float attackDir = owner.attackDir;
        if (attackDir == null) {
            attackDir = new Point2D.Float(0.0f, -1.0f);
        }
        float anglePerParticle = 72.0f;
        for (int i = 0; i < 9; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)(Math.sin(Math.toRadians(angle)) + (double)attackDir.x) * 20.0f;
            float dy = (float)(Math.cos(Math.toRadians(angle)) + (double)attackDir.y) * 20.0f;
            level.entityManager.addParticle(owner.x, owner.y, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).sizeFades(8, 22).ignoreLight(true).color(isToggled ? Color.WHITE : toggledOffOverlayColor).heightMoves(30.0f, 20.0f).movesFriction(dx * random.getFloatBetween(2.0f, 3.0f) + owner.dx * 2.0f, dy * random.getFloatBetween(2.0f, 3.0f) + owner.dy * 2.0f, 0.8f).lifeTime(800);
        }
    }

    @Override
    public HumanAttackDrawOptions getAttackDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int mobDir, float attackDirX, float attackDirY, float attackProgress, GameSprite armSprite) {
        final HumanAttackDrawOptions attackDrawOptions = super.getAttackDrawOptions(item, level, player, headItem, chestItem, feetItem, mobDir, attackDirX, attackDirY, attackProgress, armSprite);
        final ItemAttackDrawOptions lightningDrawOptions = this.getLightningDrawOptions(item, player, mobDir, attackDirX, attackDirY, attackProgress);
        GameLight gameLight = new GameLight(150.0f);
        lightningDrawOptions.light(gameLight);
        return new HumanAttackDrawOptions(){

            @Override
            public HumanAttackDrawOptions setOffsets(int centerX, int centerY, int armPosX, int armPosY, float armRotationOffset, int armRotateX, int armRotateY, int armLength, int armCenterHeight, int itemYOffset) {
                attackDrawOptions.setOffsets(centerX, centerY, armPosX, armPosY, armRotationOffset, armRotateX, armRotateY, armLength, armCenterHeight, itemYOffset);
                lightningDrawOptions.setOffsets(centerX, centerY, armPosX, armPosY, armRotationOffset, armRotateX, armRotateY, armLength, armCenterHeight, itemYOffset);
                return this;
            }

            @Override
            public HumanAttackDrawOptions light(GameLight light) {
                attackDrawOptions.light(light);
                return this;
            }

            @Override
            public DrawOptions pos(int drawX, int drawY) {
                DrawOptions options1 = attackDrawOptions.pos(drawX, drawY);
                DrawOptions options2 = lightningDrawOptions.pos(drawX, drawY);
                return () -> {
                    options1.draw();
                    options2.draw();
                };
            }
        };
    }
}

