/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.mobAbilityLevelEvent.CryoStormLevelEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.level.maps.Level;

public class CryoWitchSetBonusBuff
extends SetBonusBuff
implements BuffAbility {
    public IntUpgradeValue maxMana = new IntUpgradeValue(0, 0.1f).setBaseValue(200).setUpgradedValue(1.0f, 250);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_MANA_FLAT, this.maxMana.getValue(buff.getUpgradeTier()));
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !buff.owner.buffManager.hasBuff(BuffRegistry.Debuffs.CRYOWITCH_COOLDOWN) && !buff.owner.buffManager.hasBuff(BuffRegistry.CRYO_IMMUNTY);
    }

    @Override
    public void runAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        if (player.isClient()) {
            SoundManager.playSound(GameResources.cryoSetStorm, (SoundEffect)SoundEffect.effect(player.getX(), player.getY()).volume(1.0f));
            return;
        }
        buff.owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.CRYOWITCH_COOLDOWN, buff.owner, 120.0f, null), true);
        player.getLevel().entityManager.addLevelEvent(new CryoStormLevelEvent(buff.owner, buff.owner.getX(), buff.owner.getY(), new GameRandom(buff.owner.getUniqueID()), new GameDamage(0.0f)));
    }

    protected boolean canHit(Mob mob) {
        return !mob.removed() && !mob.isPlayer;
    }

    protected Stream<Mob> streamTargets(Level level, Mob ownerMob) {
        return Stream.concat(level.entityManager.mobs.getInRegionByTileRange(ownerMob.getTileX(), ownerMob.getTileY(), 3).stream(), GameUtils.streamServerClients(level).map(c -> c.playerMob));
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "cryowitchrobesset"), 320);
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }
}

