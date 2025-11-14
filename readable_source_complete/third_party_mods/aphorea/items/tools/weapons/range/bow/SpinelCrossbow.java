/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GameLog
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.Packet
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.sound.PrimitiveSoundEmitter
 *  necesse.engine.sound.SoundEffect
 *  necesse.engine.sound.SoundManager
 *  necesse.engine.sound.gameSound.GameSound
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.ParticleTypeSwitcher
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.attackHandler.MousePositionAttackHandler
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.AmmoConsumed
 *  necesse.entity.mobs.itemAttacker.AmmoUserMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.particle.Particle$GType
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.GameResources
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.Item$Type
 *  necesse.inventory.item.ItemAttackerWeaponItem
 *  necesse.inventory.item.ItemInteractAction
 *  necesse.inventory.item.arrowItem.ArrowItem
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.range.bow;

import aphorea.items.vanillaitemtypes.weapons.AphBowProjectileToolItem;
import aphorea.packets.AphCustomPushPacket;
import aphorea.projectiles.toolitem.SpinelArrowProjectile;
import aphorea.utils.AphColors;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.AmmoConsumed;
import necesse.entity.mobs.itemAttacker.AmmoUserMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.level.maps.Level;

public class SpinelCrossbow
extends AphBowProjectileToolItem
implements ItemInteractAction {
    public GameTexture arrowlessAttackTexture;

    public SpinelCrossbow() {
        super(1300);
        this.rarity = Item.Rarity.UNCOMMON;
        this.attackAnimTime.setBaseValue(800);
        this.attackDamage.setBaseValue(65.0f).setUpgradedValue(1.0f, 120.0f);
        this.attackRange.setBaseValue(1000);
        this.velocity.setBaseValue(200);
        this.attackXOffset = 12;
        this.attackYOffset = 10;
    }

    protected void loadAttackTexture() {
        super.loadAttackTexture();
        try {
            this.arrowlessAttackTexture = GameTexture.fromFileRaw((String)("player/weapons/" + this.getStringID() + "_arrowless"));
        }
        catch (FileNotFoundException var2) {
            this.arrowlessAttackTexture = null;
        }
    }

    public GameSprite getArrowlessAttackSprite(InventoryItem item, PlayerMob player) {
        return this.arrowlessAttackTexture != null ? new GameSprite(this.arrowlessAttackTexture) : new GameSprite(this.getItemSprite(item, player), 24);
    }

    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        return item.getGndData().getBoolean("charging") ? super.getAttackSprite(item, player) : this.getArrowlessAttackSprite(item, player);
    }

    public Item getArrowItem(Level level, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        return ItemRegistry.getItem((String)"stonearrow");
    }

    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        super.setupAttackMapContent(map, level, x, y, attackerMob, seed, item);
        Item arrow = this.getArrowItem(level, attackerMob, seed, item);
        map.setShortUnsigned("arrowID", arrow == null ? 65535 : arrow.getID());
    }

    public int getAvailableAmmo(AmmoUserMob ammoUser) {
        return ammoUser == null ? 0 : ammoUser.getAvailableAmmo(new Item[]{ItemRegistry.getItem((String)"stonearrow")}, "arrowammo");
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        item.getGndData().setBoolean("charging", false);
        int arrowID = mapContent.getShortUnsigned("arrowID", 65535);
        if (arrowID != 65535) {
            Item arrow = ItemRegistry.getItem((int)arrowID);
            if (arrow != null && arrow.type == Item.Type.ARROW) {
                boolean dropItem;
                boolean shouldFire;
                boolean consumeAmmo;
                GameRandom random = new GameRandom((long)(seed + 5));
                float ammoConsumeChance = ((ArrowItem)arrow).getAmmoConsumeChance() * this.getAmmoConsumeChance(attackerMob, item);
                boolean bl = consumeAmmo = ammoConsumeChance >= 1.0f || ammoConsumeChance > 0.0f && random.getChance(ammoConsumeChance);
                if (!consumeAmmo) {
                    shouldFire = true;
                    dropItem = false;
                } else if (attackerMob instanceof AmmoUserMob) {
                    AmmoConsumed consumed = ((AmmoUserMob)attackerMob).removeAmmo(arrow, 1, "arrowammo");
                    shouldFire = consumed.amount >= 1;
                    dropItem = random.getChance(consumed.dropChance);
                } else {
                    shouldFire = true;
                    dropItem = false;
                }
                if (shouldFire) {
                    this.fireProjectiles(level, x, y, attackerMob, item, seed, (ArrowItem)arrow, dropItem, mapContent);
                }
            } else {
                GameLog.warn.println(attackerMob.getDisplayName() + " tried to use item " + (arrow == null ? Integer.valueOf(arrowID) : arrow.getStringID()) + " as arrow.");
            }
        }
        return item;
    }

    public void tripleAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, GNDItemMap mapContent) {
        int arrowID = mapContent.getShortUnsigned("arrowID", 65535);
        if (arrowID != 65535) {
            Item arrow = ItemRegistry.getItem((int)arrowID);
            if (arrow != null && arrow.type == Item.Type.ARROW) {
                boolean dropItem;
                boolean shouldFire;
                boolean consumeAmmo;
                GameRandom random = new GameRandom((long)(seed + 5));
                float ammoConsumeChance = ((ArrowItem)arrow).getAmmoConsumeChance() * this.getAmmoConsumeChance(attackerMob, item);
                boolean bl = consumeAmmo = ammoConsumeChance >= 1.0f || ammoConsumeChance > 0.0f && random.getChance(ammoConsumeChance);
                if (!consumeAmmo) {
                    shouldFire = true;
                    dropItem = false;
                } else if (attackerMob instanceof AmmoUserMob) {
                    AmmoConsumed consumed = ((AmmoUserMob)attackerMob).removeAmmo(arrow, 1, "arrowammo");
                    shouldFire = consumed.amount >= 1;
                    dropItem = random.getChance(consumed.dropChance);
                } else {
                    shouldFire = true;
                    dropItem = false;
                }
                if (shouldFire) {
                    float ax = attackerMob.x;
                    float ay = attackerMob.y;
                    float dx = (float)x - ax;
                    float dy = (float)y - ay;
                    double angle = Math.atan2(dy, dx);
                    double dist = Math.hypot(dx, dy);
                    for (int offset : new int[]{-10, 0, 10}) {
                        double a = angle + Math.toRadians(offset);
                        int tx = (int)((double)ax + Math.cos(a) * dist);
                        int ty = (int)((double)ay + Math.sin(a) * dist);
                        this.fireProjectiles(level, tx, ty, attackerMob, item, seed, (ArrowItem)arrow, dropItem, mapContent);
                    }
                    attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, (Mob)attackerMob, 0.15f, null), level.isServer());
                    attackerMob.buffManager.forceUpdateBuffs();
                    if (attackerMob.isServer()) {
                        int strength = 50;
                        Point2D.Float dir = GameMath.normalize((float)((float)x - attackerMob.x), (float)((float)y - attackerMob.y));
                        level.getServer().network.sendToClientsAtEntireLevel((Packet)new AphCustomPushPacket((Mob)attackerMob, -dir.x, -dir.y, strength), level);
                    }
                }
            } else {
                GameLog.warn.println(attackerMob.getDisplayName() + " tried to use item " + (arrow == null ? Integer.valueOf(arrowID) : arrow.getStringID()) + " as arrow.");
            }
        }
    }

    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        return this.getProjectile(level, x, y, owner, item);
    }

    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, GNDItemMap mapContent) {
        return this.getProjectile(level, x, y, attackerMob, item);
    }

    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return new SpinelArrowProjectile(level, (Mob)attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, (Mob)attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, (Attacker)attackerMob));
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"spinelcrossbow"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"spinelcrossbow2"));
        return tooltips;
    }

    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return this.canAttack(level, x, y, attackerMob, item) == null && (!attackerMob.isPlayer || this.getAvailableAmmo((AmmoUserMob)((PlayerMob)attackerMob)) > 0);
    }

    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        int animTime = this.getAttackAnimTime(item, attackerMob) * 2;
        attackerMob.startAttackHandler(new SpinelCrossbowInteractionAttackHandler(attackerMob, slot, item, this, animTime, seed).startFromInteract());
        return item;
    }

    public static class SpinelCrossbowInteractionAttackHandler
    extends MousePositionAttackHandler {
        public int chargeTime;
        public boolean fullyCharged;
        public SpinelCrossbow toolItem;
        public long startTime;
        public InventoryItem item;
        public int seed;
        public boolean endedByInteract;
        protected int endAttackBuffer;

        public SpinelCrossbowInteractionAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, SpinelCrossbow toolItem, int chargeTime, int seed) {
            super(attackerMob, slot, 20);
            this.item = item;
            this.toolItem = toolItem;
            this.chargeTime = chargeTime;
            this.seed = seed;
            this.startTime = attackerMob.getWorldEntity().getLocalTime();
        }

        public long getTimeSinceStart() {
            return this.attackerMob.getWorldEntity().getLocalTime() - this.startTime;
        }

        public float getChargePercent() {
            return (float)this.getTimeSinceStart() / (float)this.chargeTime;
        }

        public Point getNextItemAttackerLevelPos(Mob currentTarget) {
            InventoryItem attackItem = this.item.copy();
            attackItem.getGndData().setFloat("skillPercent", 1.0f);
            return ((ItemAttackerWeaponItem)attackItem.item).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, currentTarget, -1, attackItem);
        }

        public void onUpdate() {
            super.onUpdate();
            Point2D.Float dir = GameMath.normalize((float)((float)this.lastX - this.attackerMob.x), (float)((float)this.lastY - this.attackerMob.y));
            float chargePercent = this.getChargePercent();
            InventoryItem showItem = this.item.copy();
            showItem.getGndData().setBoolean("charging", true);
            showItem.getGndData().setFloat("chargePercent", chargePercent);
            this.attackerMob.showAttackAndSendAttacker(showItem, this.lastX, this.lastY, 0, this.seed);
            if (chargePercent >= 1.0f) {
                if (!this.attackerMob.isPlayer) {
                    this.endAttackBuffer += this.updateInterval;
                    if (this.endAttackBuffer >= 350) {
                        this.endAttackBuffer = 0;
                        this.attackerMob.endAttackHandler(true);
                        return;
                    }
                }
                if (this.attackerMob.isClient()) {
                    this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob.x + dir.x * 16.0f + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), this.attackerMob.y + 4.0f + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.attackerMob.dx / 10.0f, this.attackerMob.dy / 10.0f).color(AphColors.spinel).height(20.0f - dir.y * 16.0f);
                }
                if (!this.fullyCharged) {
                    this.fullyCharged = true;
                    if (this.attackerMob.isClient()) {
                        int particles = 35;
                        float anglePerParticle = 360.0f / (float)particles;
                        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
                        for (int i = 0; i < particles; ++i) {
                            int angle = (int)((float)i * anglePerParticle + GameRandom.globalRandom.nextFloat() * anglePerParticle);
                            float dx = (float)Math.sin(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                            float dy = (float)Math.cos(Math.toRadians(angle)) * (float)GameRandom.globalRandom.getIntBetween(30, 50) * 0.8f;
                            this.attackerMob.getLevel().entityManager.addParticle((Entity)this.attackerMob, typeSwitcher.next()).movesFriction(dx, dy, 0.8f).color(AphColors.spinel).heightMoves(0.0f, 30.0f).lifeTime(500);
                        }
                        SoundManager.playSound((GameSound)GameResources.tick, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this.attackerMob).volume(0.1f).pitch(2.5f));
                    }
                }
            }
        }

        public void onMouseInteracted(int levelX, int levelY) {
            this.endedByInteract = true;
            this.attackerMob.endAttackHandler(false);
        }

        public void onControllerInteracted(float aimX, float aimY) {
            this.endedByInteract = true;
            this.attackerMob.endAttackHandler(false);
        }

        public void onEndAttack(boolean bySelf) {
            float chargePercent = this.getChargePercent();
            if (!this.endedByInteract && chargePercent >= 1.0f) {
                if (this.attackerMob.isPlayer) {
                    ((PlayerMob)this.attackerMob).constantAttack = true;
                }
                InventoryItem attackItem = this.item.copy();
                attackItem.getGndData().setFloat("chargePercent", chargePercent);
                attackItem.getGndData().setBoolean("charged", true);
                if (!this.attackerMob.isPlayer && this.lastItemAttackerTarget != null) {
                    Point attackPos = ((ItemAttackerWeaponItem)attackItem.item).getItemAttackerAttackPosition(this.attackerMob.getLevel(), this.attackerMob, this.lastItemAttackerTarget, -1, attackItem);
                    this.lastX = attackPos.x;
                    this.lastY = attackPos.y;
                }
                if (this.attackerMob.isClient()) {
                    SoundManager.playSound((GameSound)GameResources.run, (SoundEffect)SoundEffect.effect((PrimitiveSoundEmitter)this.attackerMob));
                }
                GNDItemMap attackMap = this.attackerMob.showAttackAndSendAttacker(attackItem, this.lastX, this.lastY, 0, this.seed);
                this.toolItem.tripleAttack(this.attackerMob.getLevel(), this.lastX, this.lastY, this.attackerMob, attackItem, this.seed, attackMap);
            }
            this.attackerMob.doAndSendStopAttackAttacker(false);
        }
    }
}

