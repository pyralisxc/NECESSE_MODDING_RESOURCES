/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.HashSet;
import java.util.List;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.hostile.ItemAttackerRaiderMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class SettlementRaidLoadout {
    public String mobStringID;
    public Weapon weapon;
    public ArmorSet armorSet;

    public SettlementRaidLoadout(String mobStringID) {
        this.mobStringID = mobStringID;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public void setArmorSet(ArmorSet armorSet) {
        this.armorSet = armorSet;
    }

    public void addSaveData(SaveData save) {
        save.addSafeString("mobStringID", this.mobStringID);
        if (this.weapon != null) {
            this.weapon.addSaveData(save, "weapon");
        }
        if (this.armorSet != null) {
            this.armorSet.addSaveData(save, "armor");
        }
    }

    public SaveData getSaveData(String name) {
        SaveData save = new SaveData(name);
        this.addSaveData(save);
        return save;
    }

    public SettlementRaidLoadout(LoadData save) {
        this.mobStringID = save.getSafeString("mobStringID", "humanraider");
        try {
            this.weapon = new Weapon(save, "weapon");
        }
        catch (Exception e) {
            this.weapon = null;
        }
        try {
            this.armorSet = new ArmorSet(save, "armor");
        }
        catch (Exception e) {
            this.weapon = null;
        }
    }

    public double getTotalValue() {
        return this.weapon.getValue() + this.armorSet.getValue();
    }

    public String getDebugString() {
        return "weapon=" + this.weapon.getDebugString() + ", armor=" + this.armorSet.getDebugString();
    }

    public ItemAttackerRaiderMob getNewMob(Level level) {
        ItemAttackerRaiderMob raider = (ItemAttackerRaiderMob)MobRegistry.getMob(this.mobStringID, level);
        if (this.weapon != null) {
            this.weapon.applyLoadout(raider);
        }
        if (this.armorSet != null) {
            this.armorSet.applyLoadout(raider);
        }
        return raider;
    }

    public static class Weapon {
        public InventoryItem invItem;
        public InventoryItem droppedInvItem;
        protected double weight = 1.0;
        protected boolean overrideObtained = false;
        protected double calculatedValue = -1.0;

        public Weapon(InventoryItem invItem) {
            if (!(invItem.item instanceof ItemAttackerWeaponItem)) {
                throw new IllegalArgumentException("Weapon must be an ItemAttackerWeaponItem");
            }
            this.invItem = invItem;
            if (invItem.getAmount() <= 0) {
                invItem.setAmount(invItem.itemStackSize());
            }
        }

        public Weapon(String weaponStringID) {
            this(new InventoryItem(weaponStringID, 0));
        }

        public Weapon(LoadData save, String dataName) {
            LoadData weaponSave = dataName == null ? save : save.getFirstLoadDataByName(dataName);
            this.invItem = InventoryItem.fromLoadData(weaponSave.getFirstLoadDataByName("item"));
            if (this.invItem == null) {
                throw new LoadDataException("Could not find weapon item for loadout");
            }
            if (!(this.invItem.item instanceof ItemAttackerWeaponItem)) {
                throw new LoadDataException("Loaded weapon was not an ItemAttackerWeaponItem");
            }
            this.droppedInvItem = InventoryItem.fromLoadData(weaponSave.getFirstLoadDataByName("droppedItem"));
            this.calculatedValue = weaponSave.getDouble("value", -1.0, false);
            this.weight = weaponSave.getDouble("weight", this.weight, false);
            this.overrideObtained = weaponSave.getBoolean("overrideObtained", this.overrideObtained, false);
        }

        public void addSaveData(SaveData save, String dataName) {
            SaveData weaponSave = dataName == null ? save : new SaveData(dataName);
            weaponSave.addSaveData(this.invItem.getSaveData("item"));
            if (this.droppedInvItem != null) {
                weaponSave.addSaveData(this.droppedInvItem.getSaveData("droppedItem"));
                weaponSave.addDouble("value", this.calculatedValue);
            }
            if (this.weight != 1.0) {
                weaponSave.addDouble("weight", this.weight);
            }
            if (this.overrideObtained) {
                weaponSave.addBoolean("overrideObtained", this.overrideObtained);
            }
            if (save != weaponSave) {
                save.addSaveData(weaponSave);
            }
        }

        public Weapon setTier(int tier) {
            this.invItem.item.setUpgradeTier(this.invItem, tier);
            return this;
        }

        public Weapon setWeight(float weight) {
            this.weight = weight;
            return this;
        }

        public Weapon overrideObtained() {
            this.overrideObtained = true;
            return this;
        }

        public void addTiersToList(List<Weapon> weapons, int minValue, int maxValue, int valueInterval, int startTierDamage, int fallenTierDamage) {
            Weapon copy;
            if (maxValue >= 1900) {
                for (int i = 1; i <= IncursionData.ITEM_TIER_UPGRADE_CAP; ++i) {
                    copy = new Weapon(this.invItem.copy());
                    copy.setTier(i);
                    copy.weight = this.weight;
                    if (!(copy.getValue() <= (double)maxValue)) break;
                    weapons.add(copy);
                }
                maxValue = 1850;
            }
            for (int currentValue = minValue; currentValue <= maxValue; currentValue += valueInterval) {
                copy = new Weapon(this.invItem.copy());
                copy.weight = this.weight;
                copy.droppedInvItem = this.invItem.copy();
                float floatValue = GameMath.getPercentageBetweenTwoNumbers(currentValue, 100.0f, 1850.0f);
                copy.invItem.getGndData().setFloat("damage", GameMath.lerp(floatValue, startTierDamage, fallenTierDamage));
                copy.calculatedValue = currentValue;
                weapons.add(copy);
            }
        }

        public void addTiersToList(List<Weapon> weapons, int minValue, int maxValue, int startTierDamage, int fallenTierDamage) {
            this.addTiersToList(weapons, minValue, maxValue, 100, startTierDamage, fallenTierDamage);
        }

        public void addTiersToList(List<Weapon> weapons, int startTierDamage, int fallenTierDamage) {
            this.addTiersToList(weapons, 100, Integer.MAX_VALUE, startTierDamage, fallenTierDamage);
        }

        public double getValue() {
            if (this.calculatedValue == -1.0) {
                this.calculatedValue = ((ItemAttackerWeaponItem)((Object)this.invItem.item)).getItemAttackerWeaponValue(null, this.invItem);
            }
            return this.calculatedValue;
        }

        public int getTickets(double variation, HashSet<String> obtainedItems) {
            double modifier = (double)((ItemAttackerWeaponItem)((Object)this.invItem.item)).getRaiderTicketModifier(this.invItem, this.overrideObtained ? null : obtainedItems) * this.weight;
            return (int)(this.getValue() * variation * modifier);
        }

        public void applyLoadout(ItemAttackerRaiderMob mob) {
            mob.setWeapon(this.invItem);
            mob.setDroppedWeapon(this.droppedInvItem);
            mob.weaponValue = this.getValue();
        }

        public String getDebugString() {
            int weaponTier = (int)this.invItem.item.getUpgradeTier(this.invItem);
            if (this.droppedInvItem != null) {
                return this.invItem.item.getStringID() + " (" + this.invItem.getGndData().toString() + ", V" + this.calculatedValue + ")";
            }
            return this.invItem.item.getStringID() + " (" + (weaponTier == 0 ? "" : "T" + weaponTier + ", ") + "V" + ((ItemAttackerWeaponItem)((Object)this.invItem.item)).getItemAttackerWeaponValue(null, this.invItem) + ")";
        }
    }

    public static class ArmorSet {
        public InventoryItem helmet;
        public InventoryItem chestplate;
        public InventoryItem boots;
        protected double weight = 1.0;
        protected boolean overrideObtained = false;
        protected int customArmor = -1;
        protected double calculatedValue = -1.0;

        public ArmorSet(InventoryItem helmet, InventoryItem chestplate, InventoryItem boots) {
            if (helmet != null && !helmet.item.isArmorItem()) {
                throw new IllegalArgumentException("Helmet must be an ArmorItem");
            }
            if (helmet != null && !chestplate.item.isArmorItem()) {
                throw new IllegalArgumentException("Chestplate must be an ArmorItem");
            }
            if (helmet != null && !boots.item.isArmorItem()) {
                throw new IllegalArgumentException("Boots must be an ArmorItem");
            }
            this.helmet = helmet;
            this.chestplate = chestplate;
            this.boots = boots;
        }

        public ArmorSet(String helmetStringID, String chestplateStringID, String bootsStringID) {
            this(new InventoryItem(helmetStringID), new InventoryItem(chestplateStringID), new InventoryItem(bootsStringID));
        }

        public ArmorSet(LoadData save, String dataName) {
            LoadData armorSave = dataName == null ? save : save.getFirstLoadDataByName(dataName);
            this.helmet = InventoryItem.fromLoadData(armorSave.getFirstLoadDataByName("helmet"));
            this.chestplate = InventoryItem.fromLoadData(armorSave.getFirstLoadDataByName("chestplate"));
            this.boots = InventoryItem.fromLoadData(armorSave.getFirstLoadDataByName("boots"));
            if (!this.helmet.item.isArmorItem()) {
                throw new LoadDataException("Loaded helmet was not an ArmorItem");
            }
            if (!this.chestplate.item.isArmorItem()) {
                throw new LoadDataException("Loaded chestplate was not an ArmorItem");
            }
            if (!this.boots.item.isArmorItem()) {
                throw new LoadDataException("Loaded boots was not an ArmorItem");
            }
            this.customArmor = armorSave.getInt("customArmor", -1, false);
            this.calculatedValue = armorSave.getDouble("value", -1.0, false);
            this.weight = armorSave.getDouble("weight", this.weight, false);
            this.overrideObtained = armorSave.getBoolean("overrideObtained", this.overrideObtained, false);
        }

        public void addSaveData(SaveData save, String dataName) {
            SaveData armorSave;
            SaveData saveData = armorSave = dataName == null ? save : new SaveData(dataName);
            if (this.helmet != null) {
                armorSave.addSaveData(this.helmet.getSaveData("helmet"));
            }
            if (this.chestplate != null) {
                armorSave.addSaveData(this.chestplate.getSaveData("helmet"));
            }
            if (this.boots != null) {
                armorSave.addSaveData(this.boots.getSaveData("helmet"));
            }
            if (this.customArmor != -1) {
                armorSave.addInt("customArmor", this.customArmor);
                armorSave.addDouble("value", this.calculatedValue);
            }
            if (this.weight != 1.0) {
                armorSave.addDouble("weight", this.weight);
            }
            if (this.overrideObtained) {
                armorSave.addBoolean("overrideObtained", this.overrideObtained);
            }
            if (save != armorSave) {
                save.addSaveData(armorSave);
            }
        }

        public ArmorSet setCustomArmor(int customArmor) {
            this.customArmor = customArmor;
            return this;
        }

        public ArmorSet setTier(int tier) {
            this.helmet.item.setUpgradeTier(this.helmet, tier);
            this.chestplate.item.setUpgradeTier(this.chestplate, tier);
            this.boots.item.setUpgradeTier(this.boots, tier);
            return this;
        }

        public ArmorSet setWeight(float weight) {
            this.weight = weight;
            return this;
        }

        public ArmorSet overrideObtained() {
            this.overrideObtained = true;
            return this;
        }

        public void addTiersToList(List<ArmorSet> armorSets, int minValue, int maxValue, int valueInterval) {
            ArmorSet copy;
            if (maxValue >= 1900) {
                for (int i = 1; i <= IncursionData.ITEM_TIER_UPGRADE_CAP; ++i) {
                    copy = new ArmorSet(this.helmet.copy(), this.chestplate.copy(), this.boots.copy());
                    copy.weight = this.weight;
                    if (((ArmorItem)this.helmet.item).isCosmetic || ((ArmorItem)this.chestplate.item).isCosmetic || ((ArmorItem)this.boots.item).isCosmetic) {
                        copy.setCustomArmor((int)(72.0f * (1.0f + 0.25f * (float)(i - 1))));
                        copy.calculatedValue = 2000.0f * (1.0f + 0.1f * (float)(i - 1));
                    } else {
                        copy.setTier(i);
                    }
                    if (!(copy.getValue() <= (double)maxValue)) break;
                    armorSets.add(copy);
                }
                maxValue = 1850;
            }
            for (int currentValue = minValue; currentValue <= maxValue; currentValue += valueInterval) {
                copy = new ArmorSet(this.helmet.copy(), this.chestplate.copy(), this.boots.copy());
                copy.weight = this.weight;
                float floatValue = GameMath.getPercentageBetweenTwoNumbers(currentValue, 100.0f, 1850.0f);
                copy.setCustomArmor(GameMath.lerp(floatValue, 8, 60));
                copy.calculatedValue = currentValue;
                armorSets.add(copy);
            }
        }

        public void addTiersToList(List<ArmorSet> armorSets, int minValue, int maxValue) {
            this.addTiersToList(armorSets, minValue, maxValue, 100);
        }

        public void addTiersToList(List<ArmorSet> armorSets) {
            this.addTiersToList(armorSets, 100, Integer.MAX_VALUE);
        }

        public double getValue() {
            if (this.calculatedValue == -1.0) {
                this.calculatedValue = ((ArmorItem)this.helmet.item).getSettlerEquipmentValue(this.helmet, null) + ((ArmorItem)this.chestplate.item).getSettlerEquipmentValue(this.chestplate, null) + ((ArmorItem)this.boots.item).getSettlerEquipmentValue(this.boots, null);
                this.calculatedValue /= 3.0;
            }
            return this.calculatedValue;
        }

        public int getTickets(double variation, HashSet<String> obtainedItems) {
            double modifier = (double)(((ArmorItem)this.helmet.item).getRaiderTicketModifier(this.helmet, this.overrideObtained ? null : obtainedItems) * ((ArmorItem)this.chestplate.item).getRaiderTicketModifier(this.chestplate, this.overrideObtained ? null : obtainedItems) * ((ArmorItem)this.boots.item).getRaiderTicketModifier(this.boots, this.overrideObtained ? null : obtainedItems)) * this.weight;
            return (int)(this.getValue() * variation * modifier);
        }

        public void applyLoadout(ItemAttackerRaiderMob mob) {
            mob.setArmorItems(this.helmet, this.chestplate, this.boots);
            if (this.customArmor != -1) {
                mob.setArmor(this.customArmor);
                mob.isArmorCosmetic = true;
                mob.helmetDropTickets = 0;
                mob.chestDropTickets = 0;
                mob.bootsDropTickets = 0;
            }
            mob.armorValue = this.getValue();
        }

        public String getDebugString() {
            int helmetTier = (int)this.helmet.item.getUpgradeTier(this.helmet);
            int chestplateTier = (int)this.chestplate.item.getUpgradeTier(this.chestplate);
            int bootsTier = (int)this.boots.item.getUpgradeTier(this.boots);
            return this.helmet.item.getStringID() + " (" + (helmetTier == 0 ? "" : "T" + helmetTier + ", ") + "V" + ((ArmorItem)this.helmet.item).getSettlerEquipmentValue(this.helmet, null) + "), " + this.chestplate.item.getStringID() + " (" + (chestplateTier == 0 ? "" : "T" + chestplateTier + ", ") + "V" + ((ArmorItem)this.chestplate.item).getSettlerEquipmentValue(this.chestplate, null) + "), " + this.boots.item.getStringID() + " (" + (bootsTier == 0 ? "" : "T" + bootsTier + ", ") + "V" + ((ArmorItem)this.boots.item).getSettlerEquipmentValue(this.boots, null) + ")" + (this.customArmor == -1 ? "" : ", C" + this.customArmor + ", V" + this.calculatedValue);
        }
    }
}

