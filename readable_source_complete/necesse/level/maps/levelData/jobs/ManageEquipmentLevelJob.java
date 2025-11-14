/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.jobs;

import java.awt.Point;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.engine.util.GameLinkedList;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.activeJob.EquipItemActiveJob;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.UnequipItemActiveJob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemAttackerWeaponItem;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.jobs.HasStorageLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupFuture;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.ZoneTester;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageEquipmentTypeIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;
import necesse.level.maps.levelData.settlementData.storage.ValidatedSettlementStorageRecord;

public class ManageEquipmentLevelJob
extends LevelJob {
    public JobTypeHandler.SubHandler<?> handler;
    public ItemCategoriesFilter equipmentFilter;
    public boolean preferArmorSets;

    public ManageEquipmentLevelJob(int tileX, int tileY, JobTypeHandler.SubHandler<?> handler, ItemCategoriesFilter equipmentFilter, boolean preferArmorSets) {
        super(tileX, tileY);
        this.handler = handler;
        this.equipmentFilter = equipmentFilter;
        this.preferArmorSets = preferArmorSets;
    }

    public ManageEquipmentLevelJob(LoadData save) {
        super(save);
    }

    @Override
    public boolean isWithinRestrictZone(ZoneTester zone) {
        return true;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    public boolean isSameJob(LevelJob other) {
        return other.getID() == this.getID();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int getFirstPriority() {
        return Integer.MAX_VALUE;
    }

    public static <T extends ManageEquipmentLevelJob> JobSequence getJobSequence(EntityJobWorker worker, FoundJob<T> foundJob, HumanMob humanMob) {
        Predicate<InventoryItem> itemFilter = item -> ((ManageEquipmentLevelJob)foundJob.job).equipmentFilter == null || ((ManageEquipmentLevelJob)foundJob.job).equipmentFilter.isItemAllowed(item.item);
        SettlementStorageRecords storageRecords = PickupSettlementStorageActiveJob.getStorageRecords(worker);
        if (storageRecords != null) {
            SettlementStoragePickupSlot pickup;
            LocalMessage activityDescription;
            AtomicReference pickedUp;
            Inventory inventory = humanMob.getInventory();
            InventoryItem weaponInventoryItem = inventory.getItem(6);
            ItemAttackerWeaponItem weaponItem = ManageEquipmentLevelJob.getValidWeaponItem(weaponInventoryItem, humanMob);
            boolean weaponValid = weaponItem != null && itemFilter.test(weaponInventoryItem) && weaponItem.getItemAttackerWeaponValue(humanMob, weaponInventoryItem) > 0.0f;
            ValidatedStorageEquipmentRecord currentWeaponRecord = ValidatedStorageEquipmentRecord.WeaponItem(weaponItem == null || !weaponValid ? null : weaponInventoryItem, humanMob);
            InventoryItem helmetInventoryItem = inventory.getItem(0);
            ArmorItem helmetItem = ManageEquipmentLevelJob.getValidArmorItem(helmetInventoryItem, ArmorItem.ArmorType.HEAD, humanMob);
            boolean helmetValid = helmetItem != null && itemFilter.test(helmetInventoryItem) && helmetItem.getSettlerEquipmentValue(helmetInventoryItem, humanMob) > 0.0f;
            ValidatedStorageEquipmentRecord currentHelmetRecord = ValidatedStorageEquipmentRecord.ArmorItem(helmetItem == null || !helmetValid ? null : helmetInventoryItem, humanMob);
            InventoryItem chestInventoryItem = inventory.getItem(1);
            ArmorItem chestItem = ManageEquipmentLevelJob.getValidArmorItem(chestInventoryItem, ArmorItem.ArmorType.CHEST, humanMob);
            boolean chestValid = chestItem != null && itemFilter.test(chestInventoryItem) && chestItem.getSettlerEquipmentValue(chestInventoryItem, humanMob) > 0.0f;
            ValidatedStorageEquipmentRecord currentChestRecord = ValidatedStorageEquipmentRecord.ArmorItem(chestItem == null || !chestValid ? null : chestInventoryItem, humanMob);
            InventoryItem bootsInventoryItem = inventory.getItem(2);
            ArmorItem bootsItem = ManageEquipmentLevelJob.getValidArmorItem(bootsInventoryItem, ArmorItem.ArmorType.FEET, humanMob);
            boolean bootsValid = bootsItem != null && itemFilter.test(bootsInventoryItem) && bootsItem.getSettlerEquipmentValue(bootsInventoryItem, humanMob) > 0.0f;
            ValidatedStorageEquipmentRecord currentBootsRecord = ValidatedStorageEquipmentRecord.ArmorItem(bootsItem == null || !bootsValid ? null : bootsInventoryItem, humanMob);
            float currentScore = ManageEquipmentLevelJob.getScore(currentHelmetRecord, currentChestRecord, currentBootsRecord, ((ManageEquipmentLevelJob)foundJob.job).preferArmorSets);
            SettlementStorageEquipmentTypeIndex index = storageRecords.getIndex(SettlementStorageEquipmentTypeIndex.class);
            HashMap<Integer, ValidatedStorageEquipmentRecord> weaponItems = ManageEquipmentLevelJob.getAvailableWeaponItems(worker, humanMob, index, itemFilter);
            HashMap<Integer, ValidatedStorageEquipmentRecord> headItems = ManageEquipmentLevelJob.getAvailableArmorItems(worker, humanMob, index, SettlementStorageEquipmentTypeIndex.EquipmentType.HEAD, itemFilter);
            HashMap<Integer, ValidatedStorageEquipmentRecord> chestItems = ManageEquipmentLevelJob.getAvailableArmorItems(worker, humanMob, index, SettlementStorageEquipmentTypeIndex.EquipmentType.CHEST, itemFilter);
            HashMap<Integer, ValidatedStorageEquipmentRecord> bootsItems = ManageEquipmentLevelJob.getAvailableArmorItems(worker, humanMob, index, SettlementStorageEquipmentTypeIndex.EquipmentType.FEET, itemFilter);
            weaponItems.put(-1, currentWeaponRecord);
            headItems.put(-1, currentHelmetRecord);
            chestItems.put(-1, currentChestRecord);
            bootsItems.put(-1, currentBootsRecord);
            float bestScore = currentScore;
            ValidatedStorageEquipmentRecord bestWeapon = weaponItems.values().stream().max(Comparator.comparingDouble(r -> r.equipmentValue)).orElse(null);
            ValidatedStorageEquipmentRecord bestHelmet = currentHelmetRecord;
            ValidatedStorageEquipmentRecord bestChest = currentChestRecord;
            ValidatedStorageEquipmentRecord bestBoots = currentBootsRecord;
            if (((ManageEquipmentLevelJob)foundJob.job).preferArmorSets) {
                for (ValidatedStorageEquipmentRecord helmet : headItems.values()) {
                    for (ValidatedStorageEquipmentRecord chest : chestItems.values()) {
                        for (ValidatedStorageEquipmentRecord boots : bootsItems.values()) {
                            float score = ManageEquipmentLevelJob.getScore(helmet, chest, boots, ((ManageEquipmentLevelJob)foundJob.job).preferArmorSets);
                            if (!(score > bestScore)) continue;
                            bestScore = score;
                            bestHelmet = helmet;
                            bestChest = chest;
                            bestBoots = boots;
                        }
                    }
                }
            } else {
                bestHelmet = headItems.values().stream().max(Comparator.comparingDouble(r -> r.equipmentValue)).orElse(null);
                bestChest = chestItems.values().stream().max(Comparator.comparingDouble(r -> r.equipmentValue)).orElse(null);
                bestBoots = bootsItems.values().stream().max(Comparator.comparingDouble(r -> r.equipmentValue)).orElse(null);
            }
            final LinkedListJobSequence sequence = new LinkedListJobSequence(null);
            if (weaponInventoryItem != null && !weaponValid) {
                sequence.add(new UnequipItemActiveJob(worker, foundJob.priority, 6, humanMob));
            }
            if (helmetInventoryItem != null && !helmetValid) {
                sequence.add(new UnequipItemActiveJob(worker, foundJob.priority, 0, humanMob));
            }
            if (chestInventoryItem != null && !chestValid) {
                sequence.add(new UnequipItemActiveJob(worker, foundJob.priority, 1, humanMob));
            }
            if (bootsInventoryItem != null && !bootsValid) {
                sequence.add(new UnequipItemActiveJob(worker, foundJob.priority, 2, humanMob));
            }
            if (bestWeapon != null && bestWeapon.futurePickup != null) {
                pickedUp = new AtomicReference();
                activityDescription = new LocalMessage("activities", "equipping", "item", bestWeapon.invItem.getItemLocalization());
                pickup = bestWeapon.futurePickup.accept(1);
                sequence.add(new PickupSettlementStorageActiveJob(worker, foundJob.priority, pickup.storage.tileX, pickup.storage.tileY, pickup, pickedUp){

                    @Override
                    public void onMadeCurrent() {
                        super.onMadeCurrent();
                        sequence.setActivityDescription(activityDescription);
                    }
                });
                sequence.add(new EquipItemActiveJob(worker, foundJob.priority, pickedUp, 6, humanMob){

                    @Override
                    public boolean useItem(InventoryItem item, ListIterator<InventoryItem> li) {
                        sequence.setActivityDescription(null);
                        return super.useItem(item, li);
                    }
                });
            }
            if (bestHelmet != null && bestHelmet.futurePickup != null) {
                pickedUp = new AtomicReference();
                activityDescription = new LocalMessage("activities", "equipping", "item", bestHelmet.invItem.getItemLocalization());
                pickup = bestHelmet.futurePickup.accept(1);
                sequence.add(new PickupSettlementStorageActiveJob(worker, foundJob.priority, pickup.storage.tileX, pickup.storage.tileY, pickup, pickedUp){

                    @Override
                    public void onMadeCurrent() {
                        super.onMadeCurrent();
                        sequence.setActivityDescription(activityDescription);
                    }
                });
                sequence.add(new EquipItemActiveJob(worker, foundJob.priority, pickedUp, 0, humanMob){

                    @Override
                    public boolean useItem(InventoryItem item, ListIterator<InventoryItem> li) {
                        sequence.setActivityDescription(null);
                        return super.useItem(item, li);
                    }
                });
            }
            if (bestChest != null && bestChest.futurePickup != null) {
                pickedUp = new AtomicReference();
                activityDescription = new LocalMessage("activities", "equipping", "item", bestChest.invItem.getItemLocalization());
                pickup = bestChest.futurePickup.accept(1);
                sequence.add(new PickupSettlementStorageActiveJob(worker, foundJob.priority, pickup.storage.tileX, pickup.storage.tileY, pickup, pickedUp){

                    @Override
                    public void onMadeCurrent() {
                        super.onMadeCurrent();
                        sequence.setActivityDescription(activityDescription);
                    }
                });
                sequence.add(new EquipItemActiveJob(worker, foundJob.priority, pickedUp, 1, humanMob){

                    @Override
                    public boolean useItem(InventoryItem item, ListIterator<InventoryItem> li) {
                        sequence.setActivityDescription(null);
                        return super.useItem(item, li);
                    }
                });
            }
            if (bestBoots != null && bestBoots.futurePickup != null) {
                pickedUp = new AtomicReference();
                activityDescription = new LocalMessage("activities", "equipping", "item", bestBoots.invItem.getItemLocalization());
                pickup = bestBoots.futurePickup.accept(1);
                sequence.add(new PickupSettlementStorageActiveJob(worker, foundJob.priority, pickup.storage.tileX, pickup.storage.tileY, pickup, pickedUp){

                    @Override
                    public void onMadeCurrent() {
                        super.onMadeCurrent();
                        sequence.setActivityDescription(activityDescription);
                    }
                });
                sequence.add(new EquipItemActiveJob(worker, foundJob.priority, pickedUp, 2, humanMob){

                    @Override
                    public boolean useItem(InventoryItem item, ListIterator<InventoryItem> li) {
                        sequence.setActivityDescription(null);
                        return super.useItem(item, li);
                    }
                });
            }
            if (sequence.isEmpty()) {
                return null;
            }
            worker.setPrioritizeNextJob(HasStorageLevelJob.class, true);
            return sequence;
        }
        return null;
    }

    protected static ItemAttackerWeaponItem getValidWeaponItem(InventoryItem invItem, HumanMob humanMob) {
        if (invItem == null) {
            return null;
        }
        if (!(invItem.item instanceof ItemAttackerWeaponItem)) {
            return null;
        }
        ItemAttackerWeaponItem weaponItem = (ItemAttackerWeaponItem)((Object)invItem.item);
        if (weaponItem.getItemAttackerCanUseError(humanMob, invItem) != null) {
            return null;
        }
        return weaponItem;
    }

    protected static HashMap<Integer, ValidatedStorageEquipmentRecord> getAvailableWeaponItems(EntityJobWorker worker, HumanMob humanMob, SettlementStorageEquipmentTypeIndex index, Predicate<InventoryItem> itemFilter) {
        SettlementStorageRecordsRegionData regionData = index.getEquipmentType(SettlementStorageEquipmentTypeIndex.EquipmentType.WEAPON);
        if (regionData == null) {
            return new HashMap<Integer, ValidatedStorageEquipmentRecord>();
        }
        GameLinkedList<Map.Entry<Point, GameLinkedList<SettlementStorageRecord>>> records = regionData.getRecords(worker);
        return records.stream().sorted(Comparator.comparingDouble(e -> ((Point)e.getKey()).distance(worker.getMobWorker().getTileX(), worker.getMobWorker().getTileY()))).flatMap(e -> ((GameLinkedList)e.getValue()).streamElements().map(rElement -> {
            InventoryItem inventoryItem = regionData.validateItem((SettlementStorageRecord)rElement.object, null);
            if (inventoryItem != null) {
                if (!itemFilter.test(inventoryItem)) {
                    return null;
                }
                ItemAttackerWeaponItem weaponItem = (ItemAttackerWeaponItem)((Object)inventoryItem.item);
                if (weaponItem.getItemAttackerCanUseError(humanMob, inventoryItem) != null) {
                    return null;
                }
                ValidatedStorageEquipmentRecord record = ValidatedStorageEquipmentRecord.WeaponItem(rElement, inventoryItem, humanMob);
                if (record.futurePickup == null) {
                    return null;
                }
                return record;
            }
            return null;
        })).filter(Objects::nonNull).reduce(new HashMap(), (map, record) -> {
            if (map == null) {
                map = new HashMap<Integer, ValidatedStorageEquipmentRecord>();
            }
            map.compute(record.invItem.item.getID(), (k, prev) -> {
                if (prev == null || prev.equipmentValue < record.equipmentValue) {
                    return record;
                }
                return prev;
            });
            return map;
        }, (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        });
    }

    protected static ArmorItem getValidArmorItem(InventoryItem invItem, ArmorItem.ArmorType type, HumanMob humanMob) {
        if (invItem == null) {
            return null;
        }
        if (!invItem.item.isArmorItem()) {
            return null;
        }
        ArmorItem armorItem = (ArmorItem)invItem.item;
        if (armorItem.armorType != type) {
            return null;
        }
        if (armorItem.getSettlerEquipmentValue(invItem, humanMob) <= 0.0f) {
            return null;
        }
        if (!armorItem.canMobEquip(humanMob, invItem)) {
            return null;
        }
        return armorItem;
    }

    protected static HashMap<Integer, ValidatedStorageEquipmentRecord> getAvailableArmorItems(EntityJobWorker worker, HumanMob humanMob, SettlementStorageEquipmentTypeIndex index, SettlementStorageEquipmentTypeIndex.EquipmentType type, Predicate<InventoryItem> itemFilter) {
        SettlementStorageRecordsRegionData regionData = index.getEquipmentType(type);
        if (regionData == null) {
            return new HashMap<Integer, ValidatedStorageEquipmentRecord>();
        }
        GameLinkedList<Map.Entry<Point, GameLinkedList<SettlementStorageRecord>>> records = regionData.getRecords(worker);
        return records.stream().sorted(Comparator.comparingDouble(e -> ((Point)e.getKey()).distance(worker.getMobWorker().getTileX(), worker.getMobWorker().getTileY()))).flatMap(e -> ((GameLinkedList)e.getValue()).streamElements().map(rElement -> {
            InventoryItem inventoryItem = regionData.validateItem((SettlementStorageRecord)rElement.object, null);
            if (inventoryItem != null) {
                if (!itemFilter.test(inventoryItem)) {
                    return null;
                }
                if (((ArmorItem)inventoryItem.item).getSettlerEquipmentValue(inventoryItem, humanMob) <= 0.0f) {
                    return null;
                }
                ValidatedStorageEquipmentRecord record = ValidatedStorageEquipmentRecord.ArmorItem(rElement, inventoryItem, humanMob);
                if (record.futurePickup == null) {
                    return null;
                }
                return record;
            }
            return null;
        })).filter(Objects::nonNull).reduce(new HashMap(), (map, record) -> {
            if (map == null) {
                map = new HashMap<Integer, ValidatedStorageEquipmentRecord>();
            }
            map.compute(record.invItem.item.getID(), (k, prev) -> {
                if (prev == null || prev.equipmentValue < record.equipmentValue) {
                    return record;
                }
                return prev;
            });
            return map;
        }, (map1, map2) -> {
            map1.putAll(map2);
            return map1;
        });
    }

    protected static float getScore(ValidatedStorageEquipmentRecord head, ValidatedStorageEquipmentRecord chest, ValidatedStorageEquipmentRecord feet, boolean preferSet) {
        float score = 0.0f;
        if (feet != null) {
            score += feet.equipmentValue;
        }
        if (chest != null) {
            score += chest.equipmentValue;
        }
        if (head != null) {
            score += head.equipmentValue;
            if (preferSet && head.invItem != null && chest != null && chest.invItem != null && feet != null && feet.invItem != null && ((ArmorItem)head.invItem.item).hasSet(head.invItem, chest.invItem, feet.invItem)) {
                score *= 1.5f;
            }
        }
        return score;
    }

    public static JobTypeHandler.JobStreamSupplier<? extends ManageEquipmentLevelJob> getJobStreamer(Supplier<ItemCategoriesFilter> equipmentItemFilter, Supplier<Boolean> preferArmorSetsGetter) {
        return (worker, handler) -> {
            Mob mobWorker = worker.getMobWorker();
            return Stream.of(new ManageEquipmentLevelJob(mobWorker.getTileX(), mobWorker.getTileY(), handler, (ItemCategoriesFilter)equipmentItemFilter.get(), (Boolean)preferArmorSetsGetter.get()));
        };
    }

    protected static class ValidatedStorageEquipmentRecord
    extends ValidatedSettlementStorageRecord {
        public float equipmentValue;
        public SettlementStoragePickupFuture futurePickup;

        private ValidatedStorageEquipmentRecord(SettlementStorageRecord record, InventoryItem invItem, float equipmentValue) {
            super(record, invItem);
            this.equipmentValue = equipmentValue;
        }

        private ValidatedStorageEquipmentRecord(GameLinkedList.Element element, InventoryItem invItem, float equipmentValue) {
            super(element, invItem);
            this.equipmentValue = equipmentValue;
            if (invItem != null && this.record != null) {
                this.futurePickup = this.record.storage.getFutureReserve(this.record.inventorySlot, invItem.copy(1), 1, slot -> {
                    this.record.itemAmount -= slot.item.getAmount();
                    if (this.record.itemAmount <= 0 && !element.isRemoved()) {
                        element.remove();
                    }
                });
            }
        }

        public static ValidatedStorageEquipmentRecord WeaponItem(InventoryItem invItem, HumanMob mob) {
            float equipmentValue = invItem == null ? 0.0f : ((ItemAttackerWeaponItem)((Object)invItem.item)).getItemAttackerWeaponValue(mob, invItem);
            return new ValidatedStorageEquipmentRecord((SettlementStorageRecord)null, invItem, equipmentValue);
        }

        public static ValidatedStorageEquipmentRecord WeaponItem(GameLinkedList.Element element, InventoryItem invItem, HumanMob mob) {
            float equipmentValue = invItem == null ? 0.0f : ((ItemAttackerWeaponItem)((Object)invItem.item)).getItemAttackerWeaponValue(mob, invItem);
            return new ValidatedStorageEquipmentRecord(element, invItem, equipmentValue);
        }

        public static ValidatedStorageEquipmentRecord ArmorItem(InventoryItem invItem, HumanMob mob) {
            float equipmentValue = invItem == null ? 0.0f : ((ArmorItem)invItem.item).getSettlerEquipmentValue(invItem, mob);
            return new ValidatedStorageEquipmentRecord((SettlementStorageRecord)null, invItem, equipmentValue);
        }

        public static ValidatedStorageEquipmentRecord ArmorItem(GameLinkedList.Element element, InventoryItem invItem, HumanMob mob) {
            float equipmentValue = invItem == null ? 0.0f : ((ArmorItem)invItem.item).getSettlerEquipmentValue(invItem, mob);
            return new ValidatedStorageEquipmentRecord(element, invItem, equipmentValue);
        }
    }
}

