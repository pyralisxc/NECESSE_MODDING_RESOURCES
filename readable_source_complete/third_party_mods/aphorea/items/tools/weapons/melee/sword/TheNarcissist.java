/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.NetworkPacket
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameUtils
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent
 *  necesse.entity.mobs.AttackAnimMob
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.sword;

import aphorea.items.vanillaitemtypes.weapons.AphSwordToolItem;
import aphorea.levelevents.AphNarcissistEvent;
import java.util.HashMap;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class TheNarcissist
extends AphSwordToolItem {
    private static final int MAX_COMBO = 4;
    private static final long COMBO_TIMEOUT = 6L;

    public TheNarcissist() {
        super(1000);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(200);
        this.attackDamage.setBaseValue(30.0f).setUpgradedValue(1.0f, 60.0f);
        this.attackRange.setBaseValue(85);
        this.knockback.setBaseValue(100);
        this.resilienceGain.setBaseValue(1.0f);
        this.attackXOffset = 8;
        this.attackYOffset = 8;
    }

    public float getHitboxSwingAngle(InventoryItem item, int dir) {
        return 180.0f;
    }

    public float getSwingRotationAngle(InventoryItem item, int dir) {
        return 180.0f;
    }

    public boolean getAnimInverted(InventoryItem item) {
        return this.getCombo(item) == 1 || this.getCombo(item) == 3;
    }

    public int getFlatItemCooldownTime(InventoryItem item) {
        return item.getGndData().getInt("lastCombo") == 4 ? 5000 : this.getFlatAttackAnimTime(item) * 2;
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int combo = this.getComboAndCalc(item, attackerMob);
        item.getGndData().setInt("lastCombo", combo);
        int animTime = this.getAttackAnimTime(item, attackerMob);
        if (combo == 4) {
            if (level.isServer()) {
                attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.getBuff((String)"narcissistbuff"), (Mob)attackerMob, animTime, null), true);
                attackerMob.getLevel().entityManager.events.add((LevelEvent)new AphNarcissistEvent((Mob)attackerMob, (float)Math.atan2((float)y - attackerMob.y, (float)x - attackerMob.x), attackHeight, this.getDefaultAttackDamage(item)));
            }
            item.getGndData().setInt("lastCombo", 4);
        } else {
            ToolItemMobAbilityEvent event = new ToolItemMobAbilityEvent((AttackAnimMob)attackerMob, seed, item, x - attackerMob.getX(), y - attackerMob.getY() + attackHeight, animTime, animTime, combo == 1 ? new HashMap() : null);
            attackerMob.addAndSendAttackerLevelEvent((LevelEvent)event);
        }
        return item;
    }

    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        int lastCombo;
        super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        if (level.isClient() && 0 < (lastCombo = item.getGndData().getInt("lastCombo")) && lastCombo < 4) {
            level.getClient().startCameraShake(attackerMob.x, attackerMob.y, 500, 40, (float)lastCombo / 2.0f, (float)lastCombo / 4.0f, true);
        }
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"thenarcissist"));
        return tooltips;
    }

    public int getCombo(InventoryItem item) {
        return item.getGndData().getInt("combo") - 1;
    }

    public int getComboAndCalc(InventoryItem item, ItemAttackerMob attackerMob) {
        int returnValue;
        int combo = item.getGndData().getInt("combo");
        if (combo == 0) {
            item.getGndData().setInt("combo", 1);
            returnValue = 0;
        } else if (item.getGndData().getBoolean("mobAttacked") && item.getGndData().getLong("lastAttack") + (long)this.getAttackAnimTime(item, attackerMob) * 6L > attackerMob.getTime()) {
            item.getGndData().setInt("combo", combo == 4 ? 0 : combo + 1);
            returnValue = combo;
        } else {
            item.getGndData().setInt("combo", 1);
            returnValue = 0;
        }
        item.getGndData().setLong("lastAttack", attackerMob.getTime());
        item.getGndData().setBoolean("mobAttacked", false);
        return returnValue;
    }

    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        item.getGndData().setBoolean("mobAttacked", true);
        attacker.getServer().network.sendToClientsAtEntireLevel((Packet)new NarcissistHitMob(attacker), level);
    }

    public GameDamage getAttackDamage(InventoryItem item) {
        float modDamage = 1.0f + 0.2f * (float)item.getGndData().getInt("lastCombo", 0);
        return super.getAttackDamage(item).modDamage(modDamage);
    }

    public GameDamage getDefaultAttackDamage(InventoryItem item) {
        return super.getAttackDamage(item);
    }

    public static class NarcissistHitMob
    extends Packet {
        public final int mobUniqueID;

        public NarcissistHitMob(byte[] data) {
            super(data);
            PacketReader reader = new PacketReader((Packet)this);
            this.mobUniqueID = reader.getNextInt();
        }

        public NarcissistHitMob(Mob mob) {
            this.mobUniqueID = mob.getUniqueID();
            PacketWriter writer = new PacketWriter((Packet)this);
            writer.putNextInt(this.mobUniqueID);
        }

        public void processClient(NetworkPacket packet, Client client) {
            Mob mob = GameUtils.getLevelMob((int)this.mobUniqueID, (Level)client.getLevel());
            if (mob instanceof ItemAttackerMob) {
                InventoryItem item = ((ItemAttackerMob)mob).getCurrentSelectedAttackSlot().getItem();
                if (item.item instanceof TheNarcissist) {
                    item.getGndData().setBoolean("mobAttacked", true);
                }
            }
        }
    }
}

