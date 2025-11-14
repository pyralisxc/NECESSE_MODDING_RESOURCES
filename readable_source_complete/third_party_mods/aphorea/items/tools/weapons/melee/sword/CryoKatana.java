/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.registries.BuffRegistry$Debuffs
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.levelEvent.SwordCleanSliceAttackEvent
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.AttackAnimMob
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.trails.Trail
 *  necesse.entity.trails.TrailVector
 *  necesse.gfx.GameResources
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.toolItem.swordToolItem.SwordToolItem
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.sword;

import aphorea.items.vanillaitemtypes.weapons.AphKatanaToolITem;
import aphorea.utils.AphColors;
import java.awt.geom.Point2D;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.SwordCleanSliceAttackEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.level.maps.Level;

public class CryoKatana
extends AphKatanaToolITem {
    public CryoKatana() {
        super(1900);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(200);
        this.attackDamage.setBaseValue(80.0f).setUpgradedValue(1.0f, 80.0f);
        this.attackRange.setBaseValue(120);
        this.knockback.setBaseValue(75);
        this.resilienceGain.setBaseValue(1.0f);
        this.maxDashStacks.setBaseValue(20);
        this.dashRange.setBaseValue(500);
        this.attackXOffset = 4;
        this.attackYOffset = 4;
    }

    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        target.addBuff(new ActiveBuff(BuffRegistry.Debuffs.FROSTBURN, target, 3000, (Attacker)attacker), true);
    }

    public void showKatanaAttack(Level level, final AttackAnimMob mob, final int seed, final InventoryItem item) {
        level.entityManager.events.addHidden((LevelEvent)new SwordCleanSliceAttackEvent(mob, seed, 12, (SwordToolItem)this){
            Trail[] trails;
            {
                super(arg0, arg1, arg2, arg3);
                this.trails = null;
            }

            public void tick(float angle, float currentAttackProgress) {
                int sliceDirOffset;
                int attackRange = CryoKatana.this.getAttackRange(item);
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
                }
                angle = ((Float)CryoKatana.this.getSwingDirection(item, mob).apply(Float.valueOf(currentAttackProgress))).floatValue();
                Point2D.Float dir = GameMath.getAngleDir((float)angle);
                int n = sliceDirOffset = CryoKatana.this.getAnimInverted(item) ? -90 : 90;
                if (attackDir == 3) {
                    sliceDirOffset = -sliceDirOffset;
                }
                Point2D.Float sliceDir = GameMath.getAngleDir((float)(angle + (float)sliceDirOffset));
                if (this.trails == null) {
                    int i = strictTrailAngles ? 1000 : 500;
                    int trailCount = Math.max(1, (attackRange - minTrailRange - 10) / distancePerTrail);
                    this.trails = new Trail[trailCount];
                    for (int ix = 0; ix < this.trails.length; ++ix) {
                        Trail trail;
                        this.trails[ix] = trail = new Trail(this.getVector(currentAttackProgress, attackRange, ix, distancePerTrail, base, dir, sliceDir), this.level, AphColors.ice, i);
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
                float thickness = GameMath.lerp((float)((float)index / (float)(this.trails.length - 1)), (float)25.0f, (float)10.0f);
                if (currentAttackProgress < 0.33f) {
                    thickness *= 3.0f * currentAttackProgress;
                } else if (currentAttackProgress > 0.66f) {
                    thickness *= 3.0f * (1.0f - currentAttackProgress);
                }
                int distanceOffset = attackRange - index * distancePerTrail;
                GameRandom random = new GameRandom((long)seed).nextSeeded(index + 5);
                float xOffset = random.getFloatOffset(0.0f, 10.0f);
                float yOffset = random.getFloatOffset(0.0f, 10.0f);
                Point2D.Float edgePos = new Point2D.Float(base.x + dir.x * (float)distanceOffset + xOffset, base.y + dir.y * (float)distanceOffset + yOffset);
                return new TrailVector(edgePos.x, edgePos.y, sliceDir.x, sliceDir.y, thickness, 0.0f);
            }

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
}

