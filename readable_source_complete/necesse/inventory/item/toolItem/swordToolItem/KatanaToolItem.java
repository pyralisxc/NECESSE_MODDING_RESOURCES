/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.SwordCleanSliceAttackEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.KatanaDashAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.lootTable.presets.CloseRangeWeaponsLootTable;
import necesse.level.maps.Level;

public class KatanaToolItem
extends SwordToolItem
implements ItemInteractAction {
    public IntUpgradeValue maxDashStacks = new IntUpgradeValue(10, 0.0f);
    public IntUpgradeValue dashRange = new IntUpgradeValue(300, 0.0f);
    public GameTexture invertedAttackTexture;

    public KatanaToolItem(int enchantCost) {
        super(enchantCost, CloseRangeWeaponsLootTable.closeRangeWeapons);
    }

    public KatanaToolItem() {
        this(400);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(200);
        this.attackDamage.setBaseValue(38.0f).setUpgradedValue(1.0f, 116.6667f);
        this.attackRange.setBaseValue(100);
        this.knockback.setBaseValue(75);
        this.resilienceGain.setBaseValue(1.0f);
        this.maxDashStacks.setBaseValue(10);
        this.dashRange.setBaseValue(300);
        this.attackXOffset = 4;
        this.attackYOffset = 4;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.2f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "katanasecondarytip", "stacks", (Object)this.maxDashStacks.getValue(this.getUpgradeTier(item))), 400);
        tooltips.add(Localization.translate("itemtooltip", "katanaprimarytip"), 400);
        return tooltips;
    }

    @Override
    protected void loadAttackTexture() {
        super.loadAttackTexture();
        try {
            this.invertedAttackTexture = GameTexture.fromFileRaw("player/weapons/" + this.getStringID() + "_inverted");
        }
        catch (FileNotFoundException e) {
            this.invertedAttackTexture = null;
        }
    }

    @Override
    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        if (item.getGndData().getBoolean("chargeUp")) {
            float chargePercent = Math.min(item.getGndData().getFloat("chargePercent"), 1.0f);
            ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(this.getAttackSprite(item, player));
            if (options.dir == 0) {
                itemSprite.itemRotateOffsetAdd(GameMath.lerp(chargePercent, 70.0f, 90.0f));
                itemSprite.itemRotatePoint(this.attackXOffset + GameMath.lerp(chargePercent, 16, 18), this.attackYOffset - GameMath.lerp(chargePercent, 0, 4));
            } else if (options.dir == 2) {
                itemSprite.itemRotateOffsetAdd(GameMath.lerp(chargePercent, 150.0f, 170.0f));
                itemSprite.itemRotatePoint(this.attackXOffset + GameMath.lerp(chargePercent, 22, 26), this.attackYOffset);
            } else {
                itemSprite.itemRotateOffsetAdd(GameMath.lerp(chargePercent, 120.0f, 140.0f));
                itemSprite.itemRotatePoint(this.attackXOffset + GameMath.lerp(chargePercent, 20, 22), this.attackYOffset);
            }
            if (itemColor != null) {
                itemSprite.itemColor(itemColor);
            }
            return itemSprite.itemEnd();
        }
        if (item.getGndData().getBoolean("sliceDash")) {
            ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(this.getAttackSprite(item, player));
            if (options.dir == 0) {
                itemSprite.itemRotateOffsetAdd(90.0f);
                itemSprite.itemRotatePoint(this.attackXOffset + 18, this.attackYOffset - 4);
            } else if (options.dir == 2) {
                itemSprite.itemRotateOffsetAdd(170.0f);
                itemSprite.itemRotatePoint(this.attackXOffset + 26, this.attackYOffset);
            } else {
                itemSprite.itemRotateOffsetAdd(140.0f);
                itemSprite.itemRotatePoint(this.attackXOffset + 22, this.attackYOffset);
            }
            if (itemColor != null) {
                itemSprite.itemColor(itemColor);
            }
            return itemSprite.itemEnd();
        }
        return super.setupItemSpriteAttackDrawOptions(options, item, player, mobDir, attackDirX, attackDirY, attackProgress, itemColor);
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        if (item.getGndData().getBoolean("chargeUp")) {
            float chargePercent = Math.min(item.getGndData().getFloat("chargePercent"), 1.0f);
            if (drawOptions.dir == 2) {
                drawOptions.rotation(GameMath.lerp(chargePercent, -20.0f, 0.0f));
            } else {
                drawOptions.rotation(GameMath.lerp(chargePercent, 80.0f, 100.0f));
            }
        } else if (item.getGndData().getBoolean("sliceDash")) {
            if (drawOptions.dir == 2) {
                drawOptions.rotation(GameMath.lerp(attackProgress, 0.0f, -270.0f));
            } else {
                drawOptions.rotation(GameMath.lerp(attackProgress, 100.0f, -170.0f));
            }
        } else {
            super.setDrawAttackRotation(item, drawOptions, attackDirX, attackDirY, attackProgress);
        }
    }

    @Override
    public float getSwingRotationAngle(InventoryItem item, int dir) {
        if (item.getGndData().getBoolean("chargeUp")) {
            return 150.0f;
        }
        if (item.getGndData().getBoolean("sliceDash")) {
            return 150.0f;
        }
        return 150.0f;
    }

    @Override
    public float getSwingRotationOffset(InventoryItem item, int dir, float swingAngle) {
        float offset = super.getSwingRotationOffset(item, dir, swingAngle);
        if (item.getGndData().getBoolean("chargeUp")) {
            return offset;
        }
        if (item.getGndData().getBoolean("sliceDash")) {
            return offset;
        }
        if (item.getGndData().getBoolean("slash")) {
            return offset;
        }
        return offset - 40.0f;
    }

    @Override
    public float getHitboxSwingAngle(InventoryItem item, int dir) {
        if (item.getGndData().getBoolean("chargeUp")) {
            return 150.0f;
        }
        if (item.getGndData().getBoolean("sliceDash")) {
            return 150.0f;
        }
        return 150.0f;
    }

    @Override
    public float getHitboxSwingAngleOffset(InventoryItem item, int dir, float swingAngle) {
        if (item.getGndData().getBoolean("chargeUp")) {
            return 0.0f;
        }
        if (item.getGndData().getBoolean("sliceDash")) {
            return 0.0f;
        }
        if (item.getGndData().getBoolean("slash")) {
            return 0.0f;
        }
        return -40.0f;
    }

    @Override
    public boolean animDrawBehindHand(InventoryItem item) {
        if (item.getGndData().getBoolean("chargeUp")) {
            return true;
        }
        return super.animDrawBehindHand(item);
    }

    @Override
    public boolean getAnimInverted(InventoryItem item) {
        return item.getGndData().getBoolean("slash") || item.getGndData().getBoolean("sliceDash");
    }

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        if ((this.getAnimInverted(item) || item.getGndData().getBoolean("chargeUp") || item.getGndData().getBoolean("sliceDash")) && this.invertedAttackTexture != null) {
            return new GameSprite(this.invertedAttackTexture);
        }
        return super.getAttackSprite(item, player);
    }

    public void showKatanaAttack(Level level, final AttackAnimMob mob, final int seed, final InventoryItem item) {
        level.entityManager.events.addHidden(new SwordCleanSliceAttackEvent(mob, seed, 12, this){
            Trail[] trails;
            final float maxTrailThickness = 25.0f;
            final float minTrailThickness = 10.0f;
            {
                super(attackMob, attackSeed, totalTicksPerAttack, sword);
                this.trails = null;
                this.maxTrailThickness = 25.0f;
                this.minTrailThickness = 10.0f;
            }

            @Override
            public void tick(float angle, float currentAttackProgress) {
                int sliceDirOffset;
                int attackRange = KatanaToolItem.this.getAttackRange(item);
                Point2D.Float base = new Point2D.Float(mob.x, mob.y);
                int attackDir = mob.getDir();
                if (attackDir == 0) {
                    base.x += 8.0f;
                } else if (attackDir == 2) {
                    base.x -= 8.0f;
                }
                int minTrailRange = 60;
                int distancePerTrail = 5;
                boolean strictTrailAngles = item.getGndData().getBoolean("sliceDash");
                if (strictTrailAngles) {
                    attackRange -= 20;
                    minTrailRange -= 20;
                    angle = KatanaToolItem.this.getSwingDirection(item, mob).apply(Float.valueOf(currentAttackProgress)).floatValue();
                } else {
                    angle = KatanaToolItem.this.getSwingDirection(item, mob).apply(Float.valueOf(currentAttackProgress)).floatValue();
                }
                Point2D.Float dir = GameMath.getAngleDir(angle);
                int n = sliceDirOffset = KatanaToolItem.this.getAnimInverted(item) ? -90 : 90;
                if (attackDir == 3) {
                    sliceDirOffset = -sliceDirOffset;
                }
                Point2D.Float sliceDir = GameMath.getAngleDir(angle + (float)sliceDirOffset);
                if (this.trails == null) {
                    int fadeTime = strictTrailAngles ? 1000 : 500;
                    int trailCount = Math.max(1, (attackRange - minTrailRange - 10) / distancePerTrail);
                    this.trails = new Trail[trailCount];
                    for (int i = 0; i < this.trails.length; ++i) {
                        Trail trail;
                        this.trails[i] = trail = new Trail(this.getVector(currentAttackProgress, attackRange, i, distancePerTrail, base, dir, sliceDir), this.level, new Color(182, 218, 220), fadeTime);
                        trail.removeOnFadeOut = false;
                        trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
                        this.level.entityManager.addTrail(trail);
                    }
                } else {
                    for (int i = 0; i < this.trails.length; ++i) {
                        if (strictTrailAngles) {
                            this.trails[i].addPointIfSameDirection(this.getVector(currentAttackProgress, attackRange, i, distancePerTrail, base, dir, sliceDir), 0.2f, 20.0f, 50.0f);
                            continue;
                        }
                        this.trails[i].addPoint(this.getVector(currentAttackProgress, attackRange, i, distancePerTrail, base, dir, sliceDir));
                    }
                }
            }

            public TrailVector getVector(float currentAttackProgress, int attackRange, int index, int distancePerTrail, Point2D.Float base, Point2D.Float dir, Point2D.Float sliceDir) {
                float thickness = GameMath.lerp((float)index / (float)(this.trails.length - 1), 25.0f, 10.0f);
                if (currentAttackProgress < 0.33f) {
                    thickness *= 3.0f * currentAttackProgress;
                } else if (currentAttackProgress > 0.66f) {
                    thickness *= 3.0f * (1.0f - currentAttackProgress);
                }
                int distanceOffset = attackRange - index * distancePerTrail;
                GameRandom random = new GameRandom(seed).nextSeeded(index + 5);
                float xOffset = random.getFloatOffset(0.0f, 10.0f);
                float yOffset = random.getFloatOffset(0.0f, 10.0f);
                Point2D.Float edgePos = new Point2D.Float(base.x + dir.x * (float)distanceOffset + xOffset, base.y + dir.y * (float)distanceOffset + yOffset);
                return new TrailVector(edgePos.x, edgePos.y, sliceDir.x, sliceDir.y, thickness, 0.0f);
            }

            @Override
            public void onDispose() {
                super.onDispose();
                if (this.trails != null) {
                    for (Trail trail : this.trails) {
                        trail.removeOnFadeOut = true;
                    }
                }
            }
        });
    }

    @Override
    public int getFlatItemCooldownTime(InventoryItem item) {
        if (item.getGndData().getBoolean("chargeUp") || item.getGndData().getBoolean("sliceDash")) {
            return 0;
        }
        if (item.getGndData().getBoolean("slash")) {
            return (int)((float)this.getFlatAttackAnimTime(item) * this.getSecondSliceAttackCooldownModifier());
        }
        return super.getFlatItemCooldownTime(item);
    }

    public float getSecondSliceAttackCooldownModifier() {
        return 2.5f;
    }

    @Override
    public boolean canItemAttackerHitTarget(ItemAttackerMob attackerMob, float fromX, float fromY, Mob target, InventoryItem item) {
        return this.itemAttackerHasLineOfSightToTarget(attackerMob, fromX, fromY, target, 5.0f);
    }

    @Override
    public int getItemAttackerAttackRange(ItemAttackerMob mob, InventoryItem item) {
        if (!mob.isPlayer && this.canDash(mob)) {
            return (int)((float)this.dashRange.getValue(this.getUpgradeTier(item)).intValue() * 0.8f);
        }
        return super.getItemAttackerAttackRange(mob, item);
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (item.getGndData().getBoolean("chargeUp") || mapContent.getBoolean("chargeUp")) {
            return;
        }
        boolean isDash = item.getGndData().getBoolean("sliceDash");
        if (!isDash) {
            super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        }
        if (level.isClient()) {
            this.showKatanaAttack(level, attackerMob, seed, item);
        }
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isPlayer && this.canDash(attackerMob)) {
            float stacksPercent = (float)attackerMob.buffManager.getStacks(BuffRegistry.KATANA_DASH_STACKS) / (float)this.maxDashStacks.getValue(this.getUpgradeTier(item)).intValue();
            float animModifier = GameMath.lerp(Math.min(Math.pow(stacksPercent * 2.0f, 0.5), 1.0), 8L, 1L);
            int animTime = (int)((float)this.getAttackAnimTime(item, attackerMob) * animModifier);
            mapContent.setBoolean("chargeUp", true);
            attackerMob.startAttackHandler(new KatanaDashAttackHandler(attackerMob, slot, item, this, animTime, new Color(190, 220, 220), seed));
            return item;
        }
        boolean isSlash = item.getGndData().getBoolean("slash");
        item.getGndData().setBoolean("slash", !isSlash);
        item.getGndData().setBoolean("chargeUp", false);
        item.getGndData().setBoolean("sliceDash", false);
        if (animAttack == 0) {
            int animTime = this.getAttackAnimTime(item, attackerMob);
            ToolItemMobAbilityEvent event = new ToolItemMobAbilityEvent(attackerMob, seed, item, x - attackerMob.getX(), y - attackerMob.getY() + attackHeight, animTime, animTime, isSlash ? new HashMap() : null);
            attackerMob.addAndSendAttackerLevelEvent(event);
        }
        return item;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        if (event.totalHits == 0) {
            attacker.buffManager.removeStack(BuffRegistry.KATANA_DASH_STACKS, true, level.isServer());
        }
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return this.canDash(attackerMob);
    }

    public boolean canDash(ItemAttackerMob attackerMob) {
        return !attackerMob.isRiding() && !attackerMob.buffManager.hasBuff(BuffRegistry.Debuffs.KATANA_DASH_COOLDOWN);
    }

    @Override
    public int getLevelInteractCooldownTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return 0;
    }

    @Override
    public boolean getConstantInteract(InventoryItem item) {
        return true;
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        float stacksPercent = (float)attackerMob.buffManager.getStacks(BuffRegistry.KATANA_DASH_STACKS) / (float)this.maxDashStacks.getValue(this.getUpgradeTier(item)).intValue();
        float animModifier = GameMath.lerp(Math.min(Math.pow(stacksPercent * 2.0f, 0.5), 1.0), 8L, 1L);
        int animTime = (int)((float)this.getAttackAnimTime(item, attackerMob) * animModifier);
        mapContent.setBoolean("chargeUp", true);
        attackerMob.startAttackHandler(new KatanaDashAttackHandler(attackerMob, slot, item, this, animTime, new Color(190, 220, 220), seed).startFromInteract());
        return item;
    }

    @Override
    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        Point2D.Float controllerAimDir = player.getControllerAimDir();
        Point levelPos = this.getControllerAttackLevelPos(level, controllerAimDir.x, controllerAimDir.y, player, item);
        return new ItemControllerInteract(levelPos.x, levelPos.y){

            @Override
            public DrawOptions getDrawOptions(GameCamera camera) {
                return null;
            }

            @Override
            public void onCurrentlyFocused(GameCamera camera) {
            }
        };
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.katanaSwing).volume(1.3f);
    }
}

