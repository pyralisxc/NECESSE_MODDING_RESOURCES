/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.SwordCleanSliceAttackEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.VoidClawDashAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.itemAttack.HumanAttackDrawOptions;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.KatanaToolItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VoidClawSwordToolItem
extends KatanaToolItem {
    public GameTexture clawsTexture;
    public GameTexture clawsTextureInverted;

    public VoidClawSwordToolItem() {
        super(300);
        this.rarity = Item.Rarity.UNIQUE;
        this.attackAnimTime.setBaseValue(120);
        this.attackDamage.setBaseValue(160.0f).setUpgradedValue(1.0f, 186.66672f);
        this.attackRange.setBaseValue(150);
        this.knockback.setBaseValue(75);
        this.canBeUsedForRaids = true;
        this.attackXOffset = 4;
        this.attackYOffset = 4;
        this.dashRange.setBaseValue(500);
    }

    @Override
    protected void loadAttackTexture() {
        super.loadAttackTexture();
        try {
            this.clawsTexture = GameTexture.fromFileRaw("player/weapons/voidclaw_front");
            this.clawsTextureInverted = GameTexture.fromFileRaw("player/weapons/voidclaw_inverted_front");
        }
        catch (FileNotFoundException e) {
            this.clawsTexture = null;
            this.clawsTextureInverted = null;
        }
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        ItemAttackerMob equippedMob = blackboard.get(ItemAttackerMob.class, "equippedMob", perspective);
        if (equippedMob == null) {
            equippedMob = blackboard.get(ItemAttackerMob.class, "perspective", perspective);
        }
        if (equippedMob == null) {
            equippedMob = perspective;
        }
        this.addStatTooltips(tooltips, item, blackboard.get(InventoryItem.class, "compareItem"), blackboard.getBoolean("showDifference"), blackboard.getBoolean("forceAdd"), equippedMob);
        tooltips.add(Localization.translate("itemtooltip", "voidclawprimarytip"), 400);
        tooltips.add(Localization.translate("itemtooltip", "voidclawsecondarytip"), 400);
        return tooltips;
    }

    @Override
    public boolean canDash(ItemAttackerMob attackerMob) {
        return !attackerMob.isRiding();
    }

    @Override
    public boolean animDrawBehindHand(InventoryItem item) {
        return false;
    }

    @Override
    public float getSecondSliceAttackCooldownModifier() {
        return 1.5f;
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        VoidClawSwordToolItem.restoreHealthOnHit(null, level, null, attacker);
    }

    public static void restoreHealthOnHit(InventoryItem item, Level level, Mob target, Mob attacker) {
        if (attacker == null || attacker.isClient()) {
            return;
        }
        float healthToAdd = (float)(attacker.getMaxHealth() - attacker.getHealth()) * 0.03f;
        MobHealthChangeEvent healthEvent = new MobHealthChangeEvent(attacker, GameMath.max(1, (int)healthToAdd));
        level.entityManager.events.add(healthEvent);
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
                drawOptions.rotation(GameMath.lerp(attackProgress, 0.0f, -170.0f));
            } else {
                drawOptions.rotation(GameMath.lerp(attackProgress, 70.0f, -100.0f));
            }
        } else if (item.getGndData().getBoolean("slash")) {
            drawOptions.rotation(this.getSwingRotation(item, drawOptions.dir, attackProgress) - 60.0f - (float)(drawOptions.dir * 10));
        } else {
            drawOptions.rotation(this.getSwingRotation(item, drawOptions.dir, attackProgress) - 10.0f - (float)(drawOptions.dir * 10));
        }
    }

    @Override
    public ItemAttackDrawOptions setupItemSpriteAttackDrawOptions(ItemAttackDrawOptions options, InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress, Color itemColor) {
        ItemAttackDrawOptions.AttackItemSprite itemSprite = options.itemSprite(this.getAttackSprite(item, player));
        return this.applySpriteOffsets(options, item, itemSprite);
    }

    protected ItemAttackDrawOptions applySpriteOffsets(ItemAttackDrawOptions options, InventoryItem item, ItemAttackDrawOptions.AttackItemSprite itemSprite) {
        boolean chargeUp = item.getGndData().getBoolean("chargeUp");
        boolean sliceDash = item.getGndData().getBoolean("sliceDash");
        if (chargeUp || sliceDash) {
            if (options.dir == 0) {
                itemSprite.itemRotatePoint(12, 12);
            } else if (options.dir == 2) {
                itemSprite.itemRotatePoint(14, 14);
            } else {
                itemSprite.itemRotatePoint(14, 14);
            }
        }
        if (chargeUp) {
            float chargePercent = Math.min(item.getGndData().getFloat("chargePercent"), 1.0f);
            if (options.dir == 0) {
                itemSprite.itemRotateOffsetAdd(GameMath.lerp(chargePercent, 0.0f, 10.0f));
            } else if (options.dir == 2) {
                itemSprite.itemRotateOffsetAdd(GameMath.lerp(chargePercent, 0.0f, 70.0f));
                options.addedArmRotationOffset(GameMath.lerp(chargePercent, 0.0f, 70.0f));
            } else {
                itemSprite.itemRotateOffsetAdd(GameMath.lerp(chargePercent, 0.0f, 60.0f));
            }
            return itemSprite.itemEnd();
        }
        if (sliceDash) {
            if (options.dir == 0) {
                itemSprite.itemRotateOffsetAdd(10.0f);
            } else if (options.dir == 2) {
                itemSprite.itemRotateOffsetAdd(70.0f);
                options.addedArmRotationOffset(70.0f);
            } else {
                itemSprite.itemRotateOffsetAdd(60.0f);
            }
            return itemSprite.itemEnd();
        }
        if (item.getGndData().getBoolean("slash")) {
            itemSprite.itemRotatePoint(14, 14);
        } else {
            options.addedArmRotationOffset(-50.0f);
            if (options.dir == 0 || options.dir == 1) {
                itemSprite.itemRotatePoint(18, 8);
                itemSprite.itemRotateOffsetAdd(-45.0f);
            } else if (options.dir == 2) {
                itemSprite.itemRotatePoint(22, 0);
                itemSprite.itemRotateOffsetAdd(20.0f);
            } else {
                itemSprite.itemRotatePoint(18, 8);
                itemSprite.itemRotateOffsetAdd(-25.0f);
            }
        }
        return itemSprite.itemEnd();
    }

    @Override
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
                int attackRange = VoidClawSwordToolItem.this.getAttackRange(item);
                Point2D.Float base = new Point2D.Float(mob.x, mob.y);
                int attackDir = mob.getDir();
                if (attackDir == 0) {
                    base.x += 8.0f;
                } else if (attackDir == 2) {
                    base.x -= 8.0f;
                }
                int distancePerTrail = 15;
                boolean strictTrailAngles = item.getGndData().getBoolean("sliceDash");
                if (strictTrailAngles) {
                    attackRange -= 20;
                    angle = VoidClawSwordToolItem.this.getSwingDirection(item, mob).apply(Float.valueOf(currentAttackProgress)).floatValue();
                } else {
                    angle = VoidClawSwordToolItem.this.getSwingDirection(item, mob).apply(Float.valueOf(currentAttackProgress)).floatValue();
                }
                Point2D.Float dir = GameMath.getAngleDir(angle);
                int n = sliceDirOffset = VoidClawSwordToolItem.this.getAnimInverted(item) ? -90 : 90;
                if (attackDir == 3) {
                    sliceDirOffset = -sliceDirOffset;
                }
                Point2D.Float sliceDir = GameMath.getAngleDir(angle + (float)sliceDirOffset);
                if (this.trails == null) {
                    int fadeTime = strictTrailAngles ? 500 : 250;
                    int trailCount = 3;
                    this.trails = new Trail[trailCount];
                    for (int i = 0; i < this.trails.length; ++i) {
                        Trail trail;
                        this.trails[i] = trail = new Trail(this.getVector(currentAttackProgress, attackRange, i, distancePerTrail, base, dir, sliceDir), this.level, new Color(244, 177, 255), fadeTime){

                            @Override
                            public void addDrawables(OrderableDrawables list, int startTileY, int endTileY, TickManager tickManager, GameCamera camera) {
                                super.addDrawables(list, startTileY, endTileY, tickManager, camera);
                            }

                            @Override
                            public void addDrawables(List<LevelSortedDrawable> list, int startTileY, int endTileY, TickManager tickManager, GameCamera camera) {
                                super.addDrawables(list, startTileY, endTileY, tickManager, camera);
                            }
                        };
                        trail.removeOnFadeOut = false;
                        trail.sprite = new GameSprite(GameResources.chains, 11, 0, 32);
                        trail.lightLevel = 100;
                        trail.lightHue = 300.0f;
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
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isPlayer && this.canDash(attackerMob) && attackerMob.getHealthPercent() > 0.8f) {
            long lastDashTime = item.getGndData().getLong("lastDashTime");
            if (attackerMob.getTime() > lastDashTime + 5000L) {
                int animTime = this.getAttackAnimTime(item, attackerMob);
                mapContent.setBoolean("chargeUp", true);
                item.getGndData().setLong("lastDashTime", attackerMob.getTime());
                attackerMob.startAttackHandler(new VoidClawDashAttackHandler(attackerMob, slot, item, this, animTime, new Color(221, 99, 255), seed));
            }
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
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        int animTime = this.getAttackAnimTime(item, attackerMob);
        mapContent.setBoolean("chargeUp", true);
        attackerMob.startAttackHandler(new VoidClawDashAttackHandler(attackerMob, slot, item, this, animTime, new Color(215, 82, 255), seed).startFromInteract());
        return item;
    }

    public ItemAttackDrawOptions getClawDrawOptions(InventoryItem item, PlayerMob player, int mobDir, float attackDirX, float attackDirY, float attackProgress) {
        ItemAttackDrawOptions options = ItemAttackDrawOptions.start(mobDir);
        ItemAttackDrawOptions.AttackItemSprite itemSprite = (this.getAnimInverted(item) || item.getGndData().getBoolean("chargeUp") || item.getGndData().getBoolean("sliceDash")) && this.invertedAttackTexture != null ? options.itemSprite(new GameSprite(this.clawsTextureInverted)) : options.itemSprite(new GameSprite(this.clawsTexture));
        options = this.applySpriteOffsets(options, item, itemSprite);
        if (!this.animDrawBehindHand(item)) {
            options.itemAfterHand();
        }
        this.setDrawAttackRotation(item, options, attackDirX, attackDirY, attackProgress);
        return options;
    }

    @Override
    public HumanAttackDrawOptions getAttackDrawOptions(InventoryItem item, Level level, PlayerMob player, InventoryItem headItem, InventoryItem chestItem, InventoryItem feetItem, int mobDir, float attackDirX, float attackDirY, float attackProgress, GameSprite armSprite) {
        final HumanAttackDrawOptions attackDrawOptions = super.getAttackDrawOptions(item, level, player, headItem, chestItem, feetItem, mobDir, attackDirX, attackDirY, attackProgress, armSprite);
        final ItemAttackDrawOptions clawOverlayOptions = this.getClawDrawOptions(item, player, mobDir, attackDirX, attackDirY, attackProgress);
        clawOverlayOptions.light(new GameLight(150.0f));
        return new HumanAttackDrawOptions(){

            @Override
            public HumanAttackDrawOptions setOffsets(int centerX, int centerY, int armPosX, int armPosY, float armRotationOffset, int armRotateX, int armRotateY, int armLength, int armCenterHeight, int itemYOffset) {
                attackDrawOptions.setOffsets(centerX, centerY, armPosX, armPosY, armRotationOffset, armRotateX, armRotateY, armLength, armCenterHeight, itemYOffset);
                clawOverlayOptions.setOffsets(centerX, centerY, armPosX, armPosY, armRotationOffset, armRotateX, armRotateY, armLength, armCenterHeight, itemYOffset);
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
                DrawOptions options2 = clawOverlayOptions.pos(drawX, drawY);
                return () -> {
                    options1.draw();
                    options2.draw();
                };
            }
        };
    }
}

