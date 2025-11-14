/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import necesse.engine.GameTileRange;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.entity.objectEntity.UpgradeStationObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.UpgradableItemContainerSlot;
import necesse.inventory.item.upgradeUtils.UpgradableItem;
import necesse.inventory.item.upgradeUtils.UpgradedItem;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.Level;
import necesse.level.maps.multiTile.MultiTile;

public class UpgradeStationContainer
extends Container {
    public final int UPGRADE_SLOT;
    public final UpgradeStationObjectEntity upgradeEntity;
    public final EmptyCustomAction upgradeButton;
    public final GameTileRange ingredientRange;
    private final LinkedHashSet<Inventory> nearbyInventories = new LinkedHashSet();

    public UpgradeStationContainer(final NetworkClient client, int uniqueSeed, UpgradeStationObjectEntity upgradeEntity, PacketReader reader) {
        super(client, uniqueSeed);
        this.upgradeEntity = upgradeEntity;
        this.UPGRADE_SLOT = this.addSlot(new UpgradableItemContainerSlot(upgradeEntity.inventory, 0));
        this.addInventoryQuickTransfer(this.UPGRADE_SLOT, this.UPGRADE_SLOT);
        MultiTile multiTile = upgradeEntity.getLevelObject().getMultiTile();
        Rectangle tileRectangle = multiTile.getTileRectangle(0, 0);
        this.ingredientRange = new GameTileRange(CraftingStationContainer.nearbyCraftTileRange, tileRectangle);
        this.nearbyInventories.addAll(this.craftInventories);
        for (InventoryRange inventoryRange : this.getNearbyInventories(upgradeEntity.getLevel(), upgradeEntity.tileX, upgradeEntity.tileY, this.ingredientRange, OEInventory::canUseForNearbyCrafting)) {
            this.nearbyInventories.add(inventoryRange.inventory);
        }
        this.upgradeButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                UpgradedItem upgradedItem = UpgradeStationContainer.this.getUpgradedItem();
                if (upgradedItem != null && UpgradeStationContainer.this.canUpgrade(upgradedItem, false).canCraft()) {
                    ArrayList<InventoryItemsRemoved> removed = Recipe.craft(upgradedItem.cost, client.playerMob.getLevel(), client.playerMob, UpgradeStationContainer.this.getCraftInventories());
                    if (UpgradeStationContainer.this.getSlot(UpgradeStationContainer.this.CLIENT_DRAGGING_SLOT).isClear()) {
                        UpgradeStationContainer.this.getSlot(UpgradeStationContainer.this.CLIENT_DRAGGING_SLOT).setItem(upgradedItem.upgradedItem);
                        UpgradeStationContainer.this.getSlot(UpgradeStationContainer.this.UPGRADE_SLOT).setItem(null);
                    } else {
                        UpgradeStationContainer.this.getSlot(UpgradeStationContainer.this.UPGRADE_SLOT).setItem(upgradedItem.upgradedItem);
                    }
                    if (client.isServer()) {
                        client.getServerClient().newStats.items_upgraded.increment(1);
                    }
                }
            }
        });
    }

    public CanCraft canUpgrade(UpgradedItem upgradedItem, boolean countAllIngredients) {
        if (upgradedItem != null) {
            Recipe recipe = new Recipe("air", RecipeTechRegistry.NONE, upgradedItem.cost);
            return this.canCraftRecipe(recipe, this.getCraftInventories(), countAllIngredients);
        }
        return null;
    }

    private boolean useNearbyInventories() {
        if (this.client.isServer()) {
            return this.client.craftingUsesNearbyInventories;
        }
        return Settings.craftingUseNearby.get();
    }

    @Override
    public Collection<Inventory> getCraftInventories() {
        if (this.useNearbyInventories()) {
            return this.nearbyInventories;
        }
        return super.getCraftInventories();
    }

    public UpgradedItem getUpgradedItem() {
        ContainerSlot slot = this.getSlot(this.UPGRADE_SLOT);
        if (!slot.isClear()) {
            InventoryItem slotItem = slot.getItem();
            if (slotItem.item instanceof UpgradableItem && ((UpgradableItem)((Object)slotItem.item)).getCanBeUpgradedError(slotItem) == null) {
                return ((UpgradableItem)((Object)slotItem.item)).getUpgradedItem(slotItem);
            }
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.client.isClient() && Settings.craftingUseNearby.get().booleanValue()) {
            boolean updateCraftable = false;
            for (Inventory inv : this.nearbyInventories) {
                if (inv.isDirty()) {
                    updateCraftable = true;
                }
                inv.clean();
            }
            if (updateCraftable) {
                GlobalData.updateCraftable();
            }
        }
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        return !this.upgradeEntity.removed() && this.upgradeEntity.getLevelObject().isInInteractRange(client.playerMob);
    }

    public static void openAndSendContainer(int containerID, ServerClient client, Level level, int tileX, int tileY, Packet extraContent) {
        if (!level.isServer()) {
            throw new IllegalStateException("Level must be a server level");
        }
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        if (extraContent != null) {
            writer.putNextContentPacket(extraContent);
        }
        PacketOpenContainer p = PacketOpenContainer.LevelObject(containerID, tileX, tileY, packet);
        ContainerRegistry.openAndSendContainer(client, p);
    }

    public static void openAndSendContainer(int containerID, ServerClient client, Level level, int tileX, int tileY) {
        UpgradeStationContainer.openAndSendContainer(containerID, client, level, tileX, tileY, null);
    }
}

