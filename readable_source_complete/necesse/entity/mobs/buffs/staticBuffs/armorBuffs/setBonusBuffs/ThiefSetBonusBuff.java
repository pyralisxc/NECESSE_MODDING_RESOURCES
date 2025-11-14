/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.entity.projectile.CoinProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.CoinPouch;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;

public class ThiefSetBonusBuff
extends SetBonusBuff
implements BuffAbility {
    public FloatUpgradeValue damagePerCoin = new FloatUpgradeValue().setBaseValue(10.0f).setUpgradedValue(1.0f, 30.0f).setUpgradedValue(10.0f, 50.0f);
    protected float cooldown = 15.0f;
    protected int maxCoinPrice = 25;
    protected Item coin;
    protected Item coinPouch;

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        this.coin = ItemRegistry.getItem("coin");
        this.coinPouch = ItemRegistry.getItem("coinpouch");
    }

    @Override
    public Packet getAbilityContent(PlayerMob player, ActiveBuff buff, GameCamera camera) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextInt(camera.getMouseLevelPosX());
        writer.putNextInt(camera.getMouseLevelPosY());
        return packet;
    }

    @Override
    public boolean canRunAbility(final PlayerMob player, ActiveBuff buff, Packet content) {
        boolean canRun;
        if (buff.owner.buffManager.hasBuff(BuffRegistry.Debuffs.THIEF_THROW_COOLDOWN)) {
            return false;
        }
        int currentInvCoins = player.getInv().main.getAmount(player.getLevel(), player, this.coin, "coinammo");
        InventoryItem existingPouch = player.getInv().main.getFirstInventoryItem(player.getLevel(), player, this.coinPouch, "coinpouch");
        if (existingPouch != null) {
            canRun = CoinPouch.getCurrentCoins(existingPouch) + currentInvCoins >= 1;
        } else {
            boolean bl = canRun = currentInvCoins >= 1;
        }
        if (!canRun) {
            String errorText = Localization.translate("misc", "cannotusethiefsarmor");
            UniqueFloatText text = new UniqueFloatText(player.getX(), player.getY() - 20, errorText, new FontOptions(16).outline().color(new Color(200, 100, 100)), "thiefsarmorabilityfail"){

                @Override
                public int getAnchorX() {
                    return player.getX();
                }

                @Override
                public int getAnchorY() {
                    return player.getY() - 20;
                }
            };
            player.getLevel().hudManager.addElement(text);
        }
        return canRun;
    }

    @Override
    public void runAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        player.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.THIEF_THROW_COOLDOWN, (Mob)player, this.cooldown, null), false);
        if (player.isClient()) {
            return;
        }
        InventoryItem existingPouch = player.getInv().main.getFirstInventoryItem(player.getLevel(), player, this.coinPouch, "coinpouch");
        int currentInvCoins = player.getInv().main.getAmount(player.getLevel(), player, this.coin, "coinammo");
        int heldCoins = existingPouch != null ? CoinPouch.getCurrentCoins(existingPouch) + currentInvCoins : currentInvCoins;
        int shootAmount = Math.min(heldCoins, this.maxCoinPrice);
        player.removeAmmo(this.coin, shootAmount, "coinammo");
        PacketReader reader = new PacketReader(content);
        int targetX = reader.getNextInt();
        int targetY = reader.getNextInt();
        int distFromCursor = (int)GameMath.getExactDistance(player.x, player.y, targetX, targetY);
        int baseDistance = 220;
        if (distFromCursor < baseDistance) {
            baseDistance = distFromCursor;
        }
        int radius = (int)((float)baseDistance * 0.25f);
        int radiusAngle = (int)((float)radius * 0.31f);
        Point2D.Float moveDir = GameMath.normalize(player.moveX, player.moveY);
        Point2D.Float aimDir = GameMath.normalize((float)targetX - player.x, (float)targetY - player.y);
        float dot = GameMath.dot(moveDir, aimDir);
        baseDistance += (int)(dot * (float)radius);
        int speed = dot < 0.0f ? 80 : 80 + (int)((float)radius * dot);
        for (int i = 0; i < shootAmount; ++i) {
            this.shootOneCoin(player, buff, targetX, targetY, speed, GameRandom.globalRandom.getIntBetween(baseDistance - radius, baseDistance + radius), GameRandom.globalRandom.getIntBetween(-radiusAngle, radiusAngle));
        }
        SoundManager.playSound(GameResources.swing1, (SoundEffect)SoundEffect.effect(player).volume(0.6f));
    }

    protected void shootOneCoin(PlayerMob owner, ActiveBuff buff, float targetX, float targetY, int speed, int distance, int anglemod) {
        GameDamage coinDamage = new GameDamage(DamageTypeRegistry.RANGED, this.damagePerCoin.getValue(buff.getUpgradeTier()).floatValue());
        CoinProjectile coinProjectile = new CoinProjectile(owner.x, owner.y, targetX, targetY, speed, distance, coinDamage, owner);
        if (anglemod != 0) {
            coinProjectile.setAngle(coinProjectile.getAngle() + (float)anglemod);
        }
        owner.getLevel().entityManager.projectiles.add(coinProjectile);
    }

    @Override
    public void onHasAttacked(ActiveBuff buff, MobWasHitEvent event) {
        super.onHasAttacked(buff, event);
        if (buff.owner.isClient()) {
            return;
        }
        if (buff.owner.buffManager.hasBuff(BuffRegistry.Debuffs.THIEF_STEAL_COOLDOWN)) {
            return;
        }
        if (event.target instanceof TrainingDummyMob) {
            return;
        }
        if (event.attacker instanceof CoinProjectile) {
            return;
        }
        if (GameRandom.globalRandom.getEveryXthChance(5)) {
            buff.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.THIEF_STEAL_COOLDOWN, buff.owner, 1.0f, null), true);
            int coinAmount = GameRandom.globalRandom.getIntBetween(1, 5);
            for (int i = 0; i < coinAmount; ++i) {
                PlayerMob player;
                ItemPickupEntity coin = new InventoryItem("coin").getPickupEntity(buff.owner.getLevel(), event.target.x, event.target.y);
                if (buff.owner.isPlayer && (player = (PlayerMob)buff.owner).isServerClient()) {
                    coin.setTarget(player.getServerClient());
                    coin.pickupCooldown = 450;
                }
                buff.owner.getLevel().entityManager.pickups.add(coin);
            }
        }
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "thiefset1"), 380);
        tooltips.add(Localization.translate("itemtooltip", "thiefset2"), 400);
        return tooltips;
    }
}

