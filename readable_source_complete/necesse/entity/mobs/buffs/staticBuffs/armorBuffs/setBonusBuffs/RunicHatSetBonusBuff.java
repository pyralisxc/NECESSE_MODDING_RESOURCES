/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.FloatUpgradeValue;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class RunicHatSetBonusBuff
extends SetBonusBuff
implements BuffAbility {
    public IntUpgradeValue maxManaFlat = new IntUpgradeValue().setBaseValue(100).setUpgradedValue(1.0f, 250);
    public FloatUpgradeValue lifeEssenceGain = new FloatUpgradeValue().setBaseValue(0.5f).setUpgradedValue(0.5f, 1.0f);

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        buff.setModifier(BuffModifiers.MAX_MANA_FLAT, this.maxManaFlat.getValue(buff.getUpgradeTier()));
        buff.setModifier(BuffModifiers.LIFE_ESSENCE_GAIN, this.lifeEssenceGain.getValue(buff.getUpgradeTier()));
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "runicset2"));
        return tooltips;
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }

    @Override
    public void runAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        ActiveBuff ab = new ActiveBuff(BuffRegistry.LIFE_ESSENCE, (Mob)player, 10.0f, null);
        for (int i = 0; i < 45; ++i) {
            ab.addStack(60000, null);
        }
        Mob owner = buff.owner;
        owner.buffManager.addBuff(ab, false);
        owner.buffManager.addBuff(new ActiveBuff(BuffRegistry.Debuffs.RUNIC_HAT_COOLDOWN, (Mob)player, 60.0f, null), false);
        if (player.isClient()) {
            int particleCount = 15;
            GameRandom random = GameRandom.globalRandom;
            ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
            float anglePerParticle = 360.0f / (float)particleCount;
            for (int j = 0; j < particleCount; ++j) {
                int angle = (int)((float)j * anglePerParticle + random.nextFloat() * anglePerParticle);
                owner.getLevel().entityManager.addParticle(owner, (float)Math.sin(Math.toRadians(angle)) * 25.0f, (float)Math.cos(Math.toRadians(angle)) * 25.0f, typeSwitcher.next()).sizeFades(22, 44).heightMoves(-10.0f, 60.0f).lifeTime(200);
            }
            SoundManager.playSound(GameResources.teleport, (SoundEffect)SoundEffect.effect(owner).volume(0.8f).pitch(GameRandom.globalRandom.getFloatBetween(1.9f, 2.1f)));
            SoundManager.playSound(GameResources.magicbolt4, (SoundEffect)SoundEffect.effect(owner).volume(0.8f).pitch(GameRandom.globalRandom.getFloatBetween(1.9f, 2.1f)));
        }
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !buff.owner.buffManager.hasBuff(BuffRegistry.Debuffs.RUNIC_HAT_COOLDOWN);
    }
}

