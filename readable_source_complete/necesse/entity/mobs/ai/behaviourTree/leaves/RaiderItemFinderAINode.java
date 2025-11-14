/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.behaviourTree.leaves;

import java.awt.Point;
import necesse.engine.GameTileRange;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PriorityMap;
import necesse.engine.util.TicketSystemList;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.AINodeResult;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.decorators.MoveTaskAINode;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;

public abstract class RaiderItemFinderAINode<T extends Mob>
extends MoveTaskAINode<T> {
    public static GameTileRange SEARCH_RANGE = new GameTileRange(40, new Point[0]);
    public static String[] validCategories = new String[]{"materials", "rawfood", "commonfish", "food", "seeds", "saplings"};
    public long nextFindTime;
    protected FoundItem foundItem;

    @Override
    protected void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
    }

    @Override
    public void init(T mob, Blackboard<T> blackboard) {
    }

    public abstract float getMaxPickupValue(T var1);

    public abstract void onPickedUpItem(T var1, int var2, int var3, InventoryItem var4);

    public void onFoundItem(T mob, int tileX, int tileY, int slot, InventoryItem inventoryItem) {
    }

    public boolean isValidItem(T mob, int tileX, int tileY, int slot, InventoryItem inventoryItem) {
        return true;
    }

    @Override
    public AINodeResult tickNode(T mob, Blackboard<T> blackboard) {
        if (this.foundItem != null) {
            if (!this.checkStillValid(this.foundItem)) {
                this.foundItem = null;
                this.nextFindTime = 0L;
            } else {
                float distance = ((Mob)mob).getDistance(this.foundItem.tileX * 32 + 16, this.foundItem.tileY * 32 + 16);
                if (distance <= 64.0f) {
                    ObjectEntity objectEntity = ((Entity)this.mob()).getLevel().entityManager.getObjectEntity(this.foundItem.tileX, this.foundItem.tileY);
                    Inventory inventory = ((OEInventory)((Object)objectEntity)).getInventory();
                    InventoryItem invItem = inventory.getItem(this.foundItem.inventorySlot);
                    if (invItem != null) {
                        InventoryItem pickedUpItem = invItem.copy(Math.min(invItem.getAmount(), this.foundItem.invItem.getAmount()));
                        this.onPickedUpItem(mob, this.foundItem.tileX, this.foundItem.tileY, pickedUpItem);
                        inventory.setAmount(this.foundItem.inventorySlot, invItem.getAmount() - pickedUpItem.getAmount());
                        this.foundItem = null;
                    } else {
                        this.foundItem = null;
                        this.nextFindTime = 0L;
                    }
                }
            }
        }
        if (this.nextFindTime <= mob.getTime()) {
            if (this.foundItem == null) {
                this.runFinder();
                this.nextFindTime = this.foundItem != null ? 0L : mob.getTime() + 5000L;
            } else {
                return this.moveToTileTask(this.foundItem.tileX, this.foundItem.tileY, TilePathfinding.isAtOrAdjacentObject(((Entity)mob).getLevel(), this.foundItem.tileX, this.foundItem.tileY), path -> {
                    if (path.moveIfWithin(-1, -1, () -> {
                        this.nextFindTime = 0L;
                    })) {
                        int nextPathTimeAdd = path.getNextPathTimeBasedOnPathTime(mob.getSpeed(), 1.5f, 2000, 0.1f);
                        this.nextFindTime = mob.getTime() + (long)nextPathTimeAdd;
                    }
                    return AINodeResult.SUCCESS;
                });
            }
        }
        return this.foundItem == null ? AINodeResult.FAILURE : AINodeResult.SUCCESS;
    }

    private boolean itemHasCategory(Item item, String ... categoryStringIDs) {
        ItemCategory category = ItemCategory.getItemsCategory(item);
        while (category != null) {
            for (String categoryStringID : categoryStringIDs) {
                if (!category.stringID.equals(categoryStringID)) continue;
                return true;
            }
            category = category.parent;
        }
        return false;
    }

    public void runFinder() {
        Object mob = this.mob();
        PriorityMap<FoundItem> foundItems = new PriorityMap<FoundItem>();
        float maxValue = this.getMaxPickupValue(mob);
        for (Point tile : SEARCH_RANGE.getValidTiles(((Entity)mob).getTileX(), ((Entity)mob).getTileY())) {
            ObjectEntity objectEntity = ((Entity)mob).getLevel().entityManager.getObjectEntity(tile.x, tile.y);
            if (!(objectEntity instanceof OEInventory)) continue;
            Inventory inventory = ((OEInventory)((Object)objectEntity)).getInventory();
            for (int slot = 0; slot < inventory.getSize(); ++slot) {
                float singleBrokerValue;
                InventoryItem invItem = inventory.getItem(slot);
                if (invItem == null || !this.itemHasCategory(invItem.item, validCategories) || (singleBrokerValue = invItem.item.getBrokerValue(invItem)) > maxValue || !this.isValidItem(mob, tile.x, tile.y, slot, invItem)) continue;
                int maxAmount = (int)(maxValue / singleBrokerValue);
                float totalBrokerValue = singleBrokerValue * (float)maxAmount;
                foundItems.add((int)totalBrokerValue, new FoundItem(tile.x, tile.y, slot, invItem.copy(maxAmount), singleBrokerValue));
            }
        }
        TicketSystemList lottery = new TicketSystemList();
        for (FoundItem foundItem : foundItems.getBestObjects(40)) {
            lottery.addObject((int)(foundItem.singleBrokerValue * 100.0f), foundItem);
        }
        this.foundItem = (FoundItem)lottery.getRandomObject(GameRandom.globalRandom);
        if (this.foundItem != null) {
            this.onFoundItem(mob, this.foundItem.tileX, this.foundItem.tileY, this.foundItem.inventorySlot, this.foundItem.invItem);
        }
    }

    protected boolean checkStillValid(FoundItem foundItem) {
        ObjectEntity objectEntity = ((Entity)this.mob()).getLevel().entityManager.getObjectEntity(foundItem.tileX, foundItem.tileY);
        if (objectEntity instanceof OEInventory) {
            Inventory inventory = ((OEInventory)((Object)objectEntity)).getInventory();
            if (foundItem.inventorySlot >= inventory.getSize()) {
                return false;
            }
            InventoryItem invItem = inventory.getItem(foundItem.inventorySlot);
            if (invItem == null) {
                return false;
            }
            return invItem.equals(((Entity)this.mob()).getLevel(), foundItem.invItem, true, true, "equals");
        }
        return false;
    }

    protected static class FoundItem {
        public int tileX;
        public int tileY;
        public int inventorySlot;
        public InventoryItem invItem;
        public float singleBrokerValue;

        public FoundItem(int tileX, int tileY, int inventorySlot, InventoryItem invItem, float singleBrokerValue) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.inventorySlot = inventorySlot;
            this.invItem = invItem;
            this.singleBrokerValue = singleBrokerValue;
        }
    }
}

