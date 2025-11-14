/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.OverChargedManaHitsLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobManaChangedEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.TrainingDummyMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.entity.mobs.buffs.staticBuffs.OverchargedManaBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;

public class ArcanicHelmetSetBonusBuff
extends SetBonusBuff
implements BuffAbility {
    public IntUpgradeValue lightningDamage = new IntUpgradeValue(0, 0.2f).setBaseValue(80).setUpgradedValue(1.0f, 80).setUpgradedValue(10.0f, 180);
    public static final int manaConsumedBeforeFiring = 20;
    private final int targetsToShootLightningAt = 3;
    private final int timesLightningChain = 3;

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        eventSubscriber.subscribeEvent(MobManaChangedEvent.class, event -> {
            BuffManager buffManager = buff.owner.buffManager;
            if (buffManager.hasBuff(BuffRegistry.ARCANIC_HOOD_ACTIVE) && !buffManager.hasBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION)) {
                if (event.currentMana < event.lastMana && !event.fromUpdatePacket) {
                    float manaLost = event.lastMana - event.currentMana;
                    float manaLostBuffer = buff.getGndData().getFloat("manaLostBuffer");
                    if ((manaLostBuffer += manaLost) >= 1.0f) {
                        int intManaLost = (int)manaLostBuffer;
                        manaLostBuffer -= (float)intManaLost;
                        for (int i = 0; i < intManaLost; ++i) {
                            if (buffManager.getStacks(BuffRegistry.OVERCHARGED_MANA) >= 20) continue;
                            ActiveBuff activeBuff = new ActiveBuff(BuffRegistry.OVERCHARGED_MANA, buff.owner, 9999.0f, null);
                            buffManager.addBuff(activeBuff, false);
                        }
                    }
                    buff.getGndData().setFloat("manaLostBuffer", manaLostBuffer);
                }
            } else {
                this.removeBuffs(buffManager);
            }
        });
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        super.serverTick(buff);
        Mob owner = buff.owner;
        if (owner.isPlayer && owner.buffManager.hasBuff(BuffRegistry.ARCANIC_HOOD_ACTIVE)) {
            List<Mob> mobsFound;
            PlayerMob player = (PlayerMob)owner;
            float manaDrainedPerSecond = (float)player.getMaxMana() * 0.0075f;
            float manaDrainedPerTick = manaDrainedPerSecond / 20.0f;
            player.useMana(manaDrainedPerTick, player.isServerClient() ? player.getServerClient() : null);
            int overChargedManaStacks = player.buffManager.getStacks(BuffRegistry.OVERCHARGED_MANA);
            if (overChargedManaStacks >= 20 && !(mobsFound = this.lookForNearbyTargets(player)).isEmpty()) {
                int targetsShotAt = 0;
                for (Mob target : mobsFound) {
                    if (targetsShotAt >= 3) continue;
                    ++targetsShotAt;
                    this.fireLightning(player, buff, target);
                }
            }
        }
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        super.clientTick(buff);
        Mob owner = buff.owner;
        if (owner.isPlayer && owner.buffManager.hasBuff(BuffRegistry.ARCANIC_HOOD_ACTIVE)) {
            List<Mob> mobsFound;
            PlayerMob player = (PlayerMob)owner;
            float manaDrainedPerSecond = (float)player.getMaxMana() * 0.0075f;
            float manaDrainedPerTick = manaDrainedPerSecond / 20.0f;
            player.useMana(manaDrainedPerTick, player.isServerClient() ? player.getServerClient() : null);
            int overChargedManaStacks = player.buffManager.getStacks(BuffRegistry.OVERCHARGED_MANA);
            if (overChargedManaStacks >= 20 && !(mobsFound = this.lookForNearbyTargets(player)).isEmpty()) {
                int targetsShotAt = 0;
                for (Mob target : mobsFound) {
                    if (targetsShotAt >= 3) continue;
                    ++targetsShotAt;
                    this.fireLightning(player, buff, target);
                }
            }
        }
    }

    @Override
    public void addStatTooltips(LinkedList<ItemStatTip> list, ActiveBuff currentValues, ActiveBuff lastValues) {
        super.addStatTooltips(list, currentValues, lastValues);
        currentValues.getModifierTooltipsBuilder(true, true).addLastValues(lastValues).buildToStatList(list);
    }

    @Override
    public ListGameTooltips getTooltip(ActiveBuff ab, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltip(ab, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "arcanicset1"), 400);
        tooltips.add("\n", 400);
        tooltips.add(Localization.translate("itemtooltip", "arcanicset2"), 400);
        tooltips.add("\n", 400);
        tooltips.add(Localization.translate("itemtooltip", "arcanicset3", "value", (Object)20), 400);
        return tooltips;
    }

    @Override
    public void runAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        BuffManager buffManager = player.buffManager;
        if (player.isServer()) {
            if (buffManager.hasBuff(BuffRegistry.ARCANIC_HOOD_ACTIVE)) {
                this.removeBuffs(buffManager);
            } else {
                player.addBuff(new ActiveBuff(BuffRegistry.ARCANIC_HOOD_ACTIVE, buff.owner, 9999.0f, null), true);
            }
        }
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        return !player.buffManager.hasBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION);
    }

    @Override
    public void onRemoved(ActiveBuff buff) {
        super.onRemoved(buff);
        this.removeBuffs(buff.owner.buffManager);
    }

    private void fireLightning(PlayerMob player, ActiveBuff buff, Mob targetFound) {
        if (player.isClient()) {
            SoundManager.playSound(GameResources.zap1, (SoundEffect)SoundEffect.effect(player));
        }
        Mob attacker = buff.owner;
        attacker.buffManager.removeBuff(BuffRegistry.OVERCHARGED_MANA, attacker.isServer());
        if (attacker.isServer()) {
            attacker.getLevel().entityManager.events.add(new OverChargedManaHitsLevelEvent(attacker, 200, targetFound, 3, this.lightningDamage.getValue(buff.getUpgradeTier()), new OverchargedManaBuff.ArcanicHelmetAttacker(attacker)));
        }
    }

    private List<Mob> lookForNearbyTargets(Mob owner) {
        int checkForMobsRange = 384;
        return GameUtils.streamTargetsRange(owner, owner.getX(), owner.getY(), checkForMobsRange).filter(mob -> mob.isHostile || mob instanceof TrainingDummyMob).filter(m -> m.getDistance(owner) <= (float)checkForMobsRange).collect(Collectors.toList());
    }

    public void removeBuffs(BuffManager buffManager) {
        if (buffManager.hasBuff(BuffRegistry.ARCANIC_HOOD_ACTIVE)) {
            buffManager.removeBuff(BuffRegistry.ARCANIC_HOOD_ACTIVE, true);
        }
        if (buffManager.hasBuff(BuffRegistry.OVERCHARGED_MANA)) {
            buffManager.removeBuff(BuffRegistry.OVERCHARGED_MANA, true);
        }
    }
}

