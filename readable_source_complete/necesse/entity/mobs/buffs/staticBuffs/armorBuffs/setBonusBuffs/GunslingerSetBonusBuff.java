/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.awt.geom.Line2D;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketFireSixShooter;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.WaitForSecondsEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.projectile.bulletProjectile.HandGunBulletProjectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.level.maps.CollisionFilter;

public class GunslingerSetBonusBuff
extends SetBonusBuff
implements BuffAbility {
    public FloatUpgradeValue abilityDamage = new FloatUpgradeValue(0.0f, 0.2f).setBaseValue(100.0f).setUpgradedValue(1.0f, 200.0f);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void runAbility(final PlayerMob player, final ActiveBuff buff, Packet content) {
        if (!player.isServer()) {
            return;
        }
        final Mob owner = buff.owner;
        int targetRange = 448;
        List targets = GameUtils.streamTargets(owner, GameUtils.rangeBounds(owner.x, owner.y, targetRange)).filter(m -> owner.isHostile || m.isHostile || m instanceof TrainingDummyMob).filter(m -> m.getDistance(owner) <= (float)targetRange).filter(m -> {
            Line2D.Float line = new Line2D.Float(owner.x, owner.y, m.x, m.y);
            CollisionFilter collisionFilter = owner.modifyChasingCollisionFilter(new CollisionFilter().projectileCollision(), (Mob)m);
            return !owner.getLevel().collides(line, collisionFilter);
        }).collect(Collectors.toList());
        if (targets.isEmpty()) {
            return;
        }
        float cooldown = 60.0f;
        owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.GUNSLINGER_SET_COOLDOWN, owner, cooldown, null), true);
        targets.sort(Comparator.comparing(m -> Float.valueOf(m.getDistance(owner.x, owner.y))));
        for (int i = 0; i < 6; ++i) {
            int index = i % targets.size();
            final Mob target = (Mob)targets.get(index);
            owner.getLevel().entityManager.addLevelEventHidden(new WaitForSecondsEvent(0.1f * (float)(i + 1)){

                @Override
                public void onWaitOver() {
                    HandGunBulletProjectile projectile = new HandGunBulletProjectile(owner.x, owner.y, target.x, target.y, 800.0f, 480, GunslingerSetBonusBuff.this.getAbilityDamage(buff), 100, owner);
                    projectile.setLevel(this.getLevel());
                    projectile.moveDist(20.0);
                    this.getLevel().entityManager.projectiles.add(projectile);
                    this.getLevel().getServer().network.sendToClientsWithEntity(new PacketFireSixShooter(player), projectile);
                    InventoryItem item = new InventoryItem("sixshooter");
                    GNDItemMap attackMap = new GNDItemMap();
                    item.item.setupAttackMapContent(attackMap, this.getLevel(), target.getX(), target.getY(), player, 0, item);
                    player.showItemAttack(item, target.getX(), target.getY(), 0, 0, attackMap);
                    this.getServer().network.sendToClientsWithEntity(new PacketShowAttack(player, item, target.getX(), target.getY(), 0, 0, attackMap, false), player);
                }
            });
        }
    }

    private GameDamage getAbilityDamage(ActiveBuff buff) {
        return new GameDamage(this.abilityDamage.getValue(buff.getUpgradeTier()).floatValue() * GameDamage.getDamageModifier(buff.owner, DamageTypeRegistry.RANGED));
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !buff.owner.buffManager.hasBuff(BuffRegistry.Debuffs.GUNSLINGER_SET_COOLDOWN.getID());
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "gunslingerset"));
        return tooltips;
    }
}

