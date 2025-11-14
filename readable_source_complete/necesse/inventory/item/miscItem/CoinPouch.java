/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.IngredientCounter;
import necesse.inventory.recipe.IngredientUser;
import necesse.level.maps.Level;

public class CoinPouch
extends Item {
    public HashSet<String> combinePurposes = new HashSet();

    public CoinPouch() {
        super(1);
        this.setItemCategory("misc", "pouches");
        this.rarity = Item.Rarity.RARE;
        this.combinePurposes.add("leftclick");
        this.combinePurposes.add("leftclickinv");
        this.combinePurposes.add("rightclick");
        this.combinePurposes.add("lootall");
        this.combinePurposes.add("pouchtransfer");
        this.worldDrawSize = 32;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "coinpouchtip1"));
        tooltips.add(Localization.translate("itemtooltip", "coinpouchtip2"));
        tooltips.add(Localization.translate("itemtooltip", "coinpouchstored", "coins", GameUtils.metricNumber(CoinPouch.getCurrentCoins(item))));
        return tooltips;
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.swingRotation(attackProgress);
    }

    @Override
    public float getBrokerValue(InventoryItem item) {
        return super.getBrokerValue(item) + (float)CoinPouch.getCurrentCoins(item);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int currentCoins = CoinPouch.getCurrentCoins(item);
        int thrown = Math.min(1000, currentCoins);
        if (thrown > 0) {
            CoinPouch.setCurrentCoins(item, currentCoins - thrown);
            if (level.isServer()) {
                Point2D.Float dir = GameMath.normalize((float)x - attackerMob.x, (float)y - attackerMob.y);
                level.entityManager.pickups.add(new InventoryItem("coin", thrown).getPickupEntity(level, attackerMob.x, attackerMob.y, dir.x * 175.0f, dir.y * 175.0f));
            } else if (level.isClient()) {
                SoundManager.playSound(GameResources.coins, (SoundEffect)SoundEffect.effect(attackerMob));
            }
        }
        return item;
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        if (container.client.playerMob.isInventoryExtended()) {
            return () -> {
                int currentCoins = CoinPouch.getCurrentCoins(item);
                if (currentCoins > 0) {
                    ContainerSlot clientDraggingSlot = container.getClientDraggingSlot();
                    Item coinItem = ItemRegistry.getItem("coin");
                    int startItems = Math.min(currentCoins, coinItem.getStackSize());
                    InventoryItem coins = new InventoryItem(coinItem, startItems);
                    if (clientDraggingSlot.isClear()) {
                        CoinPouch.setCurrentCoins(item, currentCoins - coins.getAmount());
                        clientDraggingSlot.setItem(coins);
                        return new ContainerActionResult(2657165);
                    }
                    if (clientDraggingSlot.getItem().canCombine(container.client.playerMob.getLevel(), container.client.playerMob, coins, "pouchtransfer") && clientDraggingSlot.getItem().combine((Level)container.client.playerMob.getLevel(), (PlayerMob)container.client.playerMob, (Inventory)clientDraggingSlot.getInventory(), (int)clientDraggingSlot.getInventorySlot(), (InventoryItem)coins, (int)coins.getAmount(), (boolean)false, (String)"pouchtransfer", null).success) {
                        int itemsCombined = startItems - coins.getAmount();
                        CoinPouch.setCurrentCoins(item, currentCoins - itemsCombined);
                    }
                    return new ContainerActionResult(10619587);
                }
                return new ContainerActionResult(3401846);
            };
        }
        return null;
    }

    @Override
    public boolean isSameGNDData(Level level, InventoryItem me, InventoryItem them, String purpose) {
        return me.getGndData().sameKeys(them.getGndData(), "coins");
    }

    @Override
    public boolean canCombineItem(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        if (them == null) {
            return false;
        }
        return this.isSameItem(level, me, them, purpose) || this.combinePurposes.contains(purpose) && them.item.getStringID().equals("coin");
    }

    @Override
    public boolean onCombine(Level level, PlayerMob player, Inventory myInventory, int mySlot, InventoryItem me, InventoryItem other, int maxStackSize, int amount, boolean combineIsNew, String purpose, InventoryAddConsumer addConsumer) {
        if (this.combinePurposes.contains(purpose) && other.item.getStringID().equals("coin")) {
            CoinPouch.setCurrentCoins(me, CoinPouch.getCurrentCoins(me) + amount);
            other.setAmount(other.getAmount() - amount);
            if (addConsumer != null) {
                addConsumer.add(null, 0, amount);
            }
            return true;
        }
        return super.onCombine(level, player, myInventory, mySlot, me, other, maxStackSize, amount, combineIsNew, purpose, addConsumer);
    }

    @Override
    public ComparableSequence<Integer> getInventoryAddPriority(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, InventoryItem input, String purpose) {
        ComparableSequence<Integer> last = super.getInventoryAddPriority(level, player, inventory, inventorySlot, item, input, purpose);
        if (input.item.getStringID().equals("coin") && purpose.equals("itempickup")) {
            return last.beforeBy(-10000);
        }
        return last;
    }

    @Override
    public int getInventoryAmount(Level level, PlayerMob player, InventoryItem item, Item requestItem, String purpose) {
        if (purpose.equals("buy") && requestItem.getStringID().equals("coin")) {
            return CoinPouch.getCurrentCoins(item);
        }
        return super.getInventoryAmount(level, player, item, requestItem, purpose);
    }

    @Override
    public void countIngredientAmount(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose, IngredientCounter handler) {
        if (purpose.equals("buy") || purpose.equals("crafting")) {
            handler.handle(null, inventorySlot, new InventoryItem("coin", CoinPouch.getCurrentCoins(item)));
        }
        super.countIngredientAmount(level, player, inventory, inventorySlot, item, purpose, handler);
    }

    @Override
    public void useIngredientAmount(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose, IngredientUser handler) {
        if (purpose.equals("buy") || purpose.equals("crafting")) {
            int startCoins = CoinPouch.getCurrentCoins(item);
            InventoryItem coins = new InventoryItem("coin", startCoins);
            handler.handle(null, inventorySlot, coins, () -> {
                if (inventory != null) {
                    inventory.markDirty(inventorySlot);
                }
                int usedCoins = startCoins - coins.getAmount();
                CoinPouch.setCurrentCoins(item, CoinPouch.getCurrentCoins(item) - usedCoins);
            });
        }
        super.useIngredientAmount(level, player, inventory, inventorySlot, item, purpose, handler);
    }

    @Override
    public boolean inventoryAddItem(Level level, PlayerMob player, Inventory myInventory, int mySlot, InventoryItem me, InventoryItem input, String purpose, boolean isValid, int stackLimit, boolean combineIsValid, InventoryAddConsumer addConsumer) {
        if (input.item.getStringID().equals("coin") && (purpose.equals("itempickup") || purpose.equals("sell"))) {
            int amount = input.getAmount();
            CoinPouch.setCurrentCoins(me, CoinPouch.getCurrentCoins(me) + amount);
            if (addConsumer != null) {
                addConsumer.add(null, 0, amount);
            }
            input.setAmount(0);
            return true;
        }
        return super.inventoryAddItem(level, player, myInventory, mySlot, me, input, purpose, isValid, stackLimit, combineIsValid, addConsumer);
    }

    @Override
    public int inventoryCanAddItem(Level level, PlayerMob player, InventoryItem item, InventoryItem input, String purpose, boolean isValid, int stackLimit) {
        if (input.item.getStringID().equals("coin")) {
            return input.getAmount();
        }
        return super.inventoryCanAddItem(level, player, item, input, purpose, isValid, stackLimit);
    }

    @Override
    public int removeInventoryAmount(Level level, PlayerMob player, InventoryItem item, Item requestItem, int amount, String purpose) {
        if (requestItem.getStringID().equals("coin")) {
            return CoinPouch.removeCoins(item, amount);
        }
        return super.removeInventoryAmount(level, player, item, requestItem, amount, purpose);
    }

    @Override
    public int removeInventoryAmount(Level level, PlayerMob player, final InventoryItem item, Inventory inventory, int inventorySlot, Ingredient ingredient, int amount, Collection<InventoryItemsRemoved> collect) {
        Item coin = ItemRegistry.getItem("coin");
        if (ingredient.matchesItem(coin)) {
            final int removed = CoinPouch.removeCoins(item, amount);
            if (removed > 0 && collect != null) {
                collect.add(new InventoryItemsRemoved(inventory, inventorySlot, new InventoryItem("coin"), removed){

                    @Override
                    public void revert() {
                        CoinPouch.setCurrentCoins(item, CoinPouch.getCurrentCoins(item) + removed);
                    }
                });
            }
            return removed;
        }
        return super.removeInventoryAmount(level, player, item, inventory, inventorySlot, ingredient, amount, collect);
    }

    public static int removeCoins(InventoryItem item, int amount) {
        int currentCoins = CoinPouch.getCurrentCoins(item);
        int removedAmount = Math.min(currentCoins, amount);
        CoinPouch.setCurrentCoins(item, currentCoins -= removedAmount);
        return removedAmount;
    }

    public static int getCurrentCoins(InventoryItem item) {
        return item.getGndData().getInt("coins");
    }

    public static void setCurrentCoins(InventoryItem item, int coins) {
        item.getGndData().setInt("coins", coins);
    }
}

