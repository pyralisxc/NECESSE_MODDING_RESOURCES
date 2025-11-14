/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.projectile.SpideriteWaveProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.level.maps.Level;

public class SpideriteSetBonusBuff
extends SetBonusBuff
implements BuffAbility {
    public FloatUpgradeValue spitDamage = new FloatUpgradeValue(0.0f, 0.2f).setBaseValue(55.0f).setUpgradedValue(1.0f, 55.0f);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setMaxModifier(BuffModifiers.SLOW, Float.valueOf(0.0f));
    }

    @Override
    public void runAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        PacketReader reader = new PacketReader(content);
        float cooldown = 60.0f;
        Level level = player.getLevel();
        float velocity = 160.0f * player.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY).floatValue();
        GameDamage damage = new GameDamage(this.spitDamage.getValue(buff.getUpgradeTier()).floatValue());
        if (player.isServer()) {
            SpideriteWaveProjectile projectile = new SpideriteWaveProjectile(level, player.x, player.y, reader.getNextInt(), reader.getNextInt(), velocity, 1000, damage, player);
            level.entityManager.projectiles.addHidden(projectile);
            projectile.moveDist(20.0);
            level.getServer().network.sendToClientsWithEntity(new PacketSpawnProjectile(projectile), projectile);
        }
        player.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.SPIDERITE_SET_COOLDOWN, (Mob)player, cooldown, null), false);
    }

    @Override
    public Packet getAbilityContent(PlayerMob player, ActiveBuff buff, GameCamera camera) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(camera.getMouseLevelPosX());
        writer.putNextInt(camera.getMouseLevelPosY());
        return content;
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !buff.owner.buffManager.hasBuff(BuffRegistry.Debuffs.SPIDERITE_SET_COOLDOWN.getID());
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "spideriteset"), 400);
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).excludeLimits(BuffModifiers.SLOW).buildToStatList(list);
    }
}

