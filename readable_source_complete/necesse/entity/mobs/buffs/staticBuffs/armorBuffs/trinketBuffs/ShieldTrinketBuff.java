/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import necesse.engine.Settings;
import necesse.engine.input.Control;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventoryItem;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobBeforeHitEvent;
import necesse.entity.mobs.MobWasHitEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.HumanDrawBuff;
import necesse.entity.mobs.buffs.staticBuffs.StaminaBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.CombinedTrinketItem;
import necesse.inventory.item.trinketItem.ShieldTrinketItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class ShieldTrinketBuff
extends TrinketBuff
implements ActiveBuffAbility,
HumanDrawBuff {
    public InventoryItem getTrinketInventoryItem(GNDItem gndItem) {
        if (gndItem instanceof GNDItemInventoryItem) {
            GNDItemInventoryItem gndInventoryItem = (GNDItemInventoryItem)gndItem;
            InventoryItem trinketInventoryItem = gndInventoryItem.invItem;
            if (trinketInventoryItem != null && trinketInventoryItem.item instanceof TrinketItem) {
                return trinketInventoryItem;
            }
        }
        return null;
    }

    public ShieldTrinketItem getShieldItem(InventoryItem trinketItem) {
        if (trinketItem != null) {
            if (trinketItem.item instanceof ShieldTrinketItem) {
                return (ShieldTrinketItem)trinketItem.item;
            }
            if (trinketItem.item instanceof CombinedTrinketItem) {
                CombinedTrinketItem combinedTrinketItem = (CombinedTrinketItem)trinketItem.item;
                return combinedTrinketItem.streamCombinedTrinkets().filter(item -> item instanceof ShieldTrinketItem).map(item -> (ShieldTrinketItem)item).findFirst().orElse(null);
            }
        }
        return null;
    }

    public <T> T runShieldGet(GNDItem gndItem, BiFunction<ShieldTrinketItem, InventoryItem, T> method, T defaultReturn) {
        ShieldTrinketItem shieldItem;
        InventoryItem trinketInventoryItem = this.getTrinketInventoryItem(gndItem);
        if (trinketInventoryItem != null && (shieldItem = this.getShieldItem(trinketInventoryItem)) != null) {
            return method.apply(shieldItem, trinketInventoryItem);
        }
        return defaultReturn;
    }

    public <T> T runShieldGet(GNDItem gndItem, BiFunction<ShieldTrinketItem, InventoryItem, T> method) {
        return this.runShieldGet(gndItem, method, null);
    }

    public boolean runShieldMethod(GNDItem gndItem, BiConsumer<ShieldTrinketItem, InventoryItem> method) {
        ShieldTrinketItem shieldItem;
        InventoryItem trinketInventoryItem = this.getTrinketInventoryItem(gndItem);
        if (trinketInventoryItem != null && (shieldItem = this.getShieldItem(trinketInventoryItem)) != null) {
            method.accept(shieldItem, trinketInventoryItem);
            return true;
        }
        return false;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        int armorValue = this.runShieldGet(buff.getGndData().getItem("trinketItem"), (shieldItem, invItem) -> shieldItem.getShieldArmorValue((InventoryItem)invItem, buff.owner), 0);
        if (armorValue != 0) {
            buff.setModifier(BuffModifiers.ARMOR_FLAT, armorValue);
        }
    }

    @Override
    public void onBeforeHit(ActiveBuff buff, MobBeforeHitEvent event) {
        super.onBeforeHit(buff, event);
        if (buff.owner.isServer() && !event.isPrevented() && buff.owner.buffManager.hasBuff(BuffRegistry.SHIELD_ACTIVE)) {
            float angleCoverage = this.runShieldGet(buff.getGndData().getItem("trinketItem"), (shieldItem, invItem) -> Float.valueOf(shieldItem.getShieldAngleCoverage((InventoryItem)invItem, buff.owner)), Float.valueOf(270.0f)).floatValue();
            boolean shouldBlock = false;
            if (angleCoverage >= 360.0f) {
                shouldBlock = true;
            } else {
                float hitAngle = GameMath.getAngle(GameMath.normalize(event.knockbackX, event.knockbackY)) - 90.0f;
                float myAngle = 0.0f;
                int dir = buff.owner.getDir();
                if (dir == 0) {
                    myAngle = 0.0f;
                } else if (dir == 1) {
                    myAngle = 90.0f;
                } else if (dir == 2) {
                    myAngle = 180.0f;
                } else if (dir == 3) {
                    myAngle = 270.0f;
                }
                float angleDifference = GameMath.getAngleDifference(hitAngle, myAngle);
                if (Math.abs(angleDifference) <= angleCoverage / 2.0f) {
                    shouldBlock = true;
                }
            }
            if (shouldBlock) {
                float damageTakenModifier = this.runShieldGet(buff.getGndData().getItem("trinketItem"), (shieldItem, invItem) -> Float.valueOf(shieldItem.getShieldFinalDamageMultiplier((InventoryItem)invItem, buff.owner)), Float.valueOf(0.5f)).floatValue();
                if (damageTakenModifier <= 0.0f) {
                    event.prevent();
                    event.showDamageTip = false;
                    event.playHitSound = false;
                } else {
                    event.damage = event.damage.modFinalMultiplier(damageTakenModifier);
                    event.playHitSound = false;
                }
                event.gndData.setItem("shieldItem", buff.getGndData().getItem("trinketItem"));
            }
        }
    }

    @Override
    public void onWasHit(ActiveBuff buff, MobWasHitEvent event) {
        super.onWasHit(buff, event);
        this.runShieldMethod(event.gndData.getItem("shieldItem"), (shieldItem, invItem) -> {
            float staminaUsageOnBlock = shieldItem.getShieldStaminaUsageOnBlock((InventoryItem)invItem, buff.owner);
            StaminaBuff.useStaminaAndGetValid(buff.owner, staminaUsageOnBlock);
            shieldItem.onShieldHit((InventoryItem)invItem, buff.owner, event);
        });
    }

    @Override
    public Packet getStartAbilityContent(PlayerMob player, ActiveBuff buff, GameCamera camera) {
        return this.getRunningAbilityContent(player, buff);
    }

    @Override
    public Packet getRunningAbilityContent(PlayerMob player, ActiveBuff buff) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        StaminaBuff.writeStaminaData(player, writer);
        return content;
    }

    @Override
    public boolean canRunAbility(PlayerMob player, ActiveBuff buff, Packet content) {
        if (buff.owner.isRiding()) {
            return false;
        }
        if (player.isServer() && !Settings.strictServerAuthority) {
            return true;
        }
        return StaminaBuff.canStartStaminaUsage(buff.owner);
    }

    @Override
    public void onActiveAbilityStarted(PlayerMob player, ActiveBuff buff, Packet content) {
        PacketReader reader = new PacketReader(content);
        if (!player.isServer() || !Settings.strictServerAuthority) {
            StaminaBuff.readStaminaData(player, reader);
        }
        ActiveBuff pbab = new ActiveBuff(BuffRegistry.PERFECT_BLOCK, (Mob)player, 0.1f, null);
        ActiveBuff ab = new ActiveBuff(BuffRegistry.SHIELD_ACTIVE, (Mob)player, 1.0f, null);
        float minSlow = this.runShieldGet(buff.getGndData().getItem("trinketItem"), (shieldItem, item) -> Float.valueOf(shieldItem.getShieldMinSlowModifier((InventoryItem)item, buff.owner)), Float.valueOf(0.5f)).floatValue();
        ab.getGndData().setFloat("minSlow", minSlow);
        player.buffManager.addBuff(pbab, true);
        player.buffManager.addBuff(ab, false);
    }

    @Override
    public boolean tickActiveAbility(PlayerMob player, ActiveBuff buff, boolean isRunningClient) {
        if (player.inLiquid() || player.isAttacking) {
            player.buffManager.removeBuff(BuffRegistry.SHIELD_ACTIVE, false);
        } else {
            float usage;
            ActiveBuff shieldBuff = player.buffManager.getBuff(BuffRegistry.SHIELD_ACTIVE);
            if (shieldBuff != null) {
                shieldBuff.setDurationLeftSeconds(1.0f);
            } else {
                ActiveBuff ab = new ActiveBuff(BuffRegistry.SHIELD_ACTIVE, (Mob)player, 1.0f, null);
                float minSlow = this.runShieldGet(buff.getGndData().getItem("trinketItem"), (shieldItem, item) -> Float.valueOf(shieldItem.getShieldMinSlowModifier((InventoryItem)item, buff.owner)), Float.valueOf(0.5f)).floatValue();
                ab.getGndData().setFloat("minSlow", minSlow);
                player.buffManager.addBuff(ab, false);
            }
            int msToDeplete = this.runShieldGet(buff.getGndData().getItem("trinketItem"), (shieldItem, item) -> shieldItem.getShieldMSToDepleteStamina((InventoryItem)item, buff.owner), 10000);
            if (msToDeplete > 0 && !StaminaBuff.useStaminaAndGetValid(player, usage = 50.0f / (float)msToDeplete)) {
                return false;
            }
        }
        return !isRunningClient || Control.TRINKET_ABILITY.isDown();
    }

    @Override
    public void onActiveAbilityUpdate(PlayerMob player, ActiveBuff buff, Packet content) {
    }

    @Override
    public void onActiveAbilityStopped(PlayerMob player, ActiveBuff buff) {
        player.buffManager.removeBuff(BuffRegistry.SHIELD_ACTIVE, false);
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ShieldTrinketItem shieldItem;
        int armorValue;
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        if (trinketItem instanceof ShieldTrinketItem && (armorValue = (shieldItem = (ShieldTrinketItem)trinketItem).getShieldArmorValue(item, perspective)) != 0) {
            tooltips.add(Localization.translate("itemtooltip", "armorvalue", "value", (Object)armorValue));
        }
        return tooltips;
    }

    @Override
    public void addHumanDraw(ActiveBuff buff, HumanDrawOptions drawOptions) {
        if (buff.owner.buffManager.hasBuff(BuffRegistry.SHIELD_ACTIVE)) {
            this.runShieldMethod(buff.getGndData().getItem("trinketItem"), (shieldItem, invItem) -> drawOptions.holdItem((InventoryItem)invItem));
        }
    }
}

