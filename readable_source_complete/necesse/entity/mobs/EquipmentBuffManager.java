/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.ArrayList;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventoryItem;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.ObjectValue;
import necesse.entity.mobs.ActiveArmorBuff;
import necesse.entity.mobs.ActiveTrinketBuff;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.EquipmentActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.ArmorBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.SetBonusBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.item.upgradeUtils.UpgradedItem;

public abstract class EquipmentBuffManager {
    private final Mob owner;
    private SetBonusBuff cosmeticEffectBuff;
    private ObjectValue<ActiveBuff, SetBonusBuff> setBonusBuff;
    private final ArrayList<ActiveArmorBuff> armorBuffs = new ArrayList();
    private final ArrayList<ActiveTrinketBuff> trinketBuffs = new ArrayList();
    private EquipmentActiveBuff equipmentBuff;

    public EquipmentBuffManager(Mob owner) {
        this.owner = owner;
    }

    public abstract InventoryItem getArmorItem(int var1);

    public abstract InventoryItem getCosmeticItem(int var1);

    public abstract ArrayList<InventoryItem> getTrinketItems();

    public void clientTickEffects() {
        if (this.cosmeticEffectBuff != null) {
            this.cosmeticEffectBuff.tickEffect(null, this.owner);
        } else if (this.setBonusBuff != null) {
            InventoryItem cosmeticHead = this.getCosmeticItem(0);
            InventoryItem cosmeticChest = this.getCosmeticItem(1);
            InventoryItem cosmeticFeet = this.getCosmeticItem(2);
            if (cosmeticHead == null && cosmeticChest == null && cosmeticFeet == null) {
                ((SetBonusBuff)this.setBonusBuff.value).tickEffect((ActiveBuff)this.setBonusBuff.object, this.owner);
            }
        }
        if (this.equipmentBuff != null) {
            this.equipmentBuff.tickEffects(this.owner);
        }
        for (ActiveTrinketBuff tb : this.trinketBuffs) {
            if (tb == null) continue;
            for (int i = 0; i < tb.buffs.length; ++i) {
                tb.buffs[i].tickEffect(tb.activeBuffs[i], this.owner);
            }
        }
        for (ActiveArmorBuff ab : this.armorBuffs) {
            if (ab == null) continue;
            for (int i = 0; i < ab.buffs.length; ++i) {
                ab.buffs[i].tickEffect(ab.activeBuffs[i], this.owner);
            }
        }
    }

    public void updateEquipmentBuff() {
        this.owner.buffManager.removeBuff(BuffRegistry.EQUIPMENT_BUFF.getID(), false);
        this.equipmentBuff = new EquipmentActiveBuff(BuffRegistry.EQUIPMENT_BUFF, this.owner);
        for (ActiveArmorBuff armorBuff : this.armorBuffs) {
            if (armorBuff.enchant == null) continue;
            int mod = UpgradedItem.getEnchantmentMod(armorBuff.item);
            for (int i = 0; i < mod; ++i) {
                this.equipmentBuff.addEquipmentEnchant(armorBuff.enchant);
            }
        }
        for (ActiveTrinketBuff trinketBuff : this.trinketBuffs) {
            if (trinketBuff.enchant == null) continue;
            this.equipmentBuff.addEquipmentEnchant(trinketBuff.enchant);
        }
        for (int i = 0; i < 3; ++i) {
            InventoryItem armorItem = this.getArmorItem(i);
            InventoryItem cosmeticItem = this.getCosmeticItem(i);
            this.equipmentBuff.addArmorBuff(this.owner, armorItem, cosmeticItem);
        }
        this.owner.buffManager.addBuff(this.equipmentBuff, false);
    }

    public void updateAll() {
        this.updateArmorBuffs(false);
        this.updateCosmeticSetBonus(false);
        this.updateTrinketBuffs(false);
        this.updateEquipmentBuff();
    }

    public void updateArmorBuffs() {
        this.updateArmorBuffs(true);
    }

    private void updateArmorBuffs(boolean updateEquipmentBuff) {
        InventoryItem armorHead = this.getArmorItem(0);
        InventoryItem armorChest = this.getArmorItem(1);
        InventoryItem armorFeet = this.getArmorItem(2);
        SetBonusBuff nextSetBuff = null;
        if (armorHead != null && armorChest != null && armorFeet != null && armorHead.item.isArmorItem() && ((ArmorItem)armorHead.item).hasSet(armorHead, armorChest, armorFeet)) {
            nextSetBuff = ((ArmorItem)armorHead.item).getSetBuff(armorHead, this.owner, false);
        }
        if (this.setBonusBuff != null) {
            this.owner.buffManager.removeBuff((Buff)this.setBonusBuff.value, false);
            this.setBonusBuff = null;
        }
        if (nextSetBuff != null) {
            ServerClient serverClient;
            ActiveBuff activeBuff = new ActiveBuff((Buff)nextSetBuff, this.owner, 0, null);
            activeBuff.getGndData().setItem("headItem", (GNDItem)new GNDItemInventoryItem(armorHead));
            activeBuff.getGndData().setItem("chestItem", (GNDItem)new GNDItemInventoryItem(armorChest));
            activeBuff.getGndData().setItem("feetItem", (GNDItem)new GNDItemInventoryItem(armorFeet));
            ((ArmorItem)armorHead.item).setupSetBuff(armorHead, armorChest, armorFeet, this.owner, activeBuff, false);
            this.setBonusBuff = new ObjectValue<ActiveBuff, SetBonusBuff>(this.owner.buffManager.addBuff(activeBuff, false), nextSetBuff);
            if (this.owner.isPlayer && this.owner.getLevel() != null && this.owner.isServer() && (serverClient = ((PlayerMob)this.owner).getServerClient()) != null) {
                serverClient.newStats.set_bonuses_worn.addSetBonusWorn(nextSetBuff);
            }
        }
        ActiveArmorBuff[] newArmorBuffs = new ActiveArmorBuff[3];
        for (int i = 0; i < 3; ++i) {
            ArmorBuff[] buffs = null;
            EquipmentItemEnchant enchant = null;
            InventoryItem item = this.getArmorItem(i);
            if (item != null && item.item.isArmorItem()) {
                ArmorItem armor = (ArmorItem)item.item;
                buffs = armor.getBuffs(item);
                enchant = armor.getEnchantment(item);
            }
            newArmorBuffs[i] = buffs == null ? null : new ActiveArmorBuff(item, buffs, enchant);
        }
        for (ActiveArmorBuff armorBuff : this.armorBuffs) {
            for (ArmorBuff buff : armorBuff.buffs) {
                this.owner.buffManager.removeBuff(buff, false);
            }
        }
        this.armorBuffs.clear();
        for (ActiveArmorBuff armorBuff : newArmorBuffs) {
            if (armorBuff == null) continue;
            this.armorBuffs.add(armorBuff);
            for (int i = 0; i < armorBuff.buffs.length; ++i) {
                ActiveBuff ab = new ActiveBuff((Buff)armorBuff.buffs[i], this.owner, 0, null);
                ab.getGndData().setItem("armorItem", (GNDItem)new GNDItemInventoryItem(armorBuff.item));
                armorBuff.activeBuffs[i] = this.owner.buffManager.addBuff(ab, false);
            }
        }
        if (updateEquipmentBuff) {
            this.updateEquipmentBuff();
        }
    }

    public void updateCosmeticSetBonus() {
        this.updateCosmeticSetBonus(true);
    }

    private void updateCosmeticSetBonus(boolean updateEquipmentBuff) {
        this.cosmeticEffectBuff = null;
        InventoryItem cosmeticHead = this.getCosmeticItem(0);
        InventoryItem cosmeticChest = this.getCosmeticItem(1);
        InventoryItem cosmeticFeet = this.getCosmeticItem(2);
        if (cosmeticHead != null && cosmeticChest != null && cosmeticFeet != null && cosmeticHead.item.isArmorItem() && ((ArmorItem)cosmeticHead.item).hasSet(cosmeticHead, cosmeticChest, cosmeticFeet)) {
            this.cosmeticEffectBuff = ((ArmorItem)cosmeticHead.item).getSetBuff(cosmeticHead, this.owner, true);
        }
        if (updateEquipmentBuff) {
            this.updateEquipmentBuff();
        }
    }

    public void updateTrinketBuffs() {
        this.updateTrinketBuffs(true);
    }

    private void updateTrinketBuffs(boolean updateEquipmentBuff) {
        int i;
        ArrayList<InventoryItem> trinketItems = this.getTrinketItems();
        ActiveTrinketBuff[] newTrinketBuffs = new ActiveTrinketBuff[trinketItems.size()];
        for (i = trinketItems.size() - 1; i >= 0; --i) {
            ArrayList<Integer> disables = new ArrayList<Integer>();
            TrinketBuff[] buffs = null;
            EquipmentItemEnchant enchant = null;
            InventoryItem item = trinketItems.get(i);
            if (item != null && item.item.isTrinketItem()) {
                TrinketItem trinket = (TrinketItem)item.item;
                buffs = trinket.getBuffs(item);
                enchant = trinket.getEnchantment(item);
                for (int j = 0; j < trinketItems.size(); ++j) {
                    InventoryItem oItem;
                    if (j == i || (oItem = trinketItems.get(j)) == null || newTrinketBuffs[j] != null && !newTrinketBuffs[j].disables.isEmpty() || !trinket.disabledBy(oItem) && trinket != oItem.item) continue;
                    disables.add(oItem.item.getID());
                }
            }
            newTrinketBuffs[i] = buffs == null ? null : new ActiveTrinketBuff(item, buffs, enchant, disables);
        }
        for (i = 0; i < trinketItems.size(); ++i) {
            InventoryItem item = trinketItems.get(i);
            if (newTrinketBuffs[i] == null || item == null || !newTrinketBuffs[i].disables.isEmpty() || !item.item.isTrinketItem()) continue;
            TrinketItem trinket = (TrinketItem)item.item;
            for (int j = trinketItems.size() - 1; j >= 0; --j) {
                InventoryItem oItem;
                if (j == i || newTrinketBuffs[j] == null || !newTrinketBuffs[j].disables.isEmpty() || (oItem = trinketItems.get(j)) == null || !trinket.disables(oItem)) continue;
                newTrinketBuffs[j].disables.add(trinket.getID());
            }
        }
        for (ActiveTrinketBuff trinketBuff : this.trinketBuffs) {
            for (TrinketBuff buff : trinketBuff.buffs) {
                this.owner.buffManager.removeBuff(buff, false);
            }
        }
        this.trinketBuffs.clear();
        for (ActiveTrinketBuff trinketBuff : newTrinketBuffs) {
            if (trinketBuff == null) continue;
            this.trinketBuffs.add(trinketBuff);
            if (!trinketBuff.disables.isEmpty()) continue;
            for (int i2 = 0; i2 < trinketBuff.buffs.length; ++i2) {
                ActiveBuff ab = new ActiveBuff((Buff)trinketBuff.buffs[i2], this.owner, 0, null);
                ab.getGndData().setItem("trinketItem", (GNDItem)new GNDItemInventoryItem(trinketBuff.item));
                trinketBuff.activeBuffs[i2] = this.owner.buffManager.addBuff(ab, false);
            }
        }
        if (updateEquipmentBuff) {
            this.updateEquipmentBuff();
        }
    }

    public boolean hasSetBuff() {
        return this.setBonusBuff != null;
    }

    public ObjectValue<ActiveBuff, SetBonusBuff> getSetBonusBuff() {
        return this.setBonusBuff;
    }

    public ListGameTooltips getSetBonusBuffTooltip(GameBlackboard blackboard) {
        if (this.setBonusBuff != null) {
            return ((ActiveBuff)this.setBonusBuff.object).getTooltips(blackboard);
        }
        return null;
    }

    public ArrayList<ActiveArmorBuff> getArmorBuffs() {
        return this.armorBuffs;
    }

    public ArrayList<ActiveTrinketBuff> getTrinketBuffs() {
        return this.trinketBuffs;
    }

    public ArrayList<Integer> getTrinketBuffDisables(InventoryItem item) {
        return this.trinketBuffs.stream().filter(b -> b.item == item).map(b -> b.disables).findFirst().orElseGet(ArrayList::new);
    }
}

