/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object.fallenAltar;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.LinkedHashSet;
import necesse.engine.GameTileRange;
import necesse.engine.Settings;
import necesse.engine.incursionPerkTree.IncursionPerk;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.world.ReturnedObjects;
import necesse.engine.world.worldData.incursions.OpenIncursion;
import necesse.entity.objectEntity.FallenAltarObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.IntCustomAction;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.container.slots.GatewayTabletContainerSlot;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.multiTile.MultiTile;

public class FallenAltarContainer
extends Container {
    public FallenAltarObjectEntity altarEntity;
    public int TABLET_SLOT;
    public IntCustomAction openIncursion;
    public EmptyCustomAction enterIncursion;
    public BooleanCustomAction closeIncursion;
    public IntCustomAction obtainPerk;
    public IntCustomAction respecPerk;
    public EmptyCustomAction resetPerkTree;
    public final GameTileRange ingredientRange;
    private final LinkedHashSet<Inventory> nearbyInventories = new LinkedHashSet();

    public FallenAltarContainer(final NetworkClient client, int uniqueSeed, final FallenAltarObjectEntity altarEntity, Packet content) {
        super(client, uniqueSeed);
        this.altarEntity = altarEntity;
        this.TABLET_SLOT = this.addSlot(new GatewayTabletContainerSlot(altarEntity.inventory, 0));
        this.addInventoryQuickTransfer(slot -> !altarEntity.hasOpenIncursion(), this.TABLET_SLOT, this.TABLET_SLOT);
        MultiTile multiTile = altarEntity.getLevelObject().getMultiTile();
        Rectangle tileRectangle = multiTile.getTileRectangle(0, 0);
        this.ingredientRange = new GameTileRange(CraftingStationContainer.nearbyCraftTileRange, tileRectangle);
        this.nearbyInventories.addAll(this.craftInventories);
        for (InventoryRange inventoryRange : this.getNearbyInventories(altarEntity.getLevel(), altarEntity.tileX, altarEntity.tileY, this.ingredientRange, OEInventory::canUseForNearbyCrafting)) {
            this.nearbyInventories.add(inventoryRange.inventory);
        }
        this.checkAchievements();
        PacketReader reader = new PacketReader(content);
        this.openIncursion = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                IncursionData incursionData;
                if (!client.isServer()) {
                    return;
                }
                if (FallenAltarContainer.this.getSlot(FallenAltarContainer.this.TABLET_SLOT).getItem() != null && (incursionData = GatewayTabletItem.getIncursionData(FallenAltarContainer.this.getSlot(FallenAltarContainer.this.TABLET_SLOT).getItem())) != null) {
                    FallenAltarContainer.this.open(incursionData);
                }
            }
        });
        this.enterIncursion = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (!client.isServer()) {
                    return;
                }
                OpenIncursion openIncursion = altarEntity.getOpenIncursion();
                if (openIncursion != null) {
                    altarEntity.enterIncursion(client.getServerClient());
                }
            }
        });
        this.closeIncursion = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean isComplete) {
                if (!client.isServer()) {
                    return;
                }
                OpenIncursion openIncursion = altarEntity.getOpenIncursion();
                if (openIncursion != null) {
                    ServerClient serverClient = client.getServerClient();
                    if (openIncursion.canComplete == isComplete) {
                        ReturnedObjects returnedObjects = new ReturnedObjects();
                        if (openIncursion.canComplete) {
                            altarEntity.completeOpenIncursion(FallenAltarContainer.this, serverClient, returnedObjects);
                            altarEntity.altarData.addCompletedIncursion(openIncursion.incursionData);
                        } else {
                            altarEntity.closeOpenIncursion(FallenAltarContainer.this, serverClient, returnedObjects);
                        }
                        for (InventoryItem returnedItem : returnedObjects.items) {
                            serverClient.playerMob.getInv().addItemsDropRemaining(returnedItem, "addback", serverClient.playerMob, true, true);
                        }
                        returnedObjects.items.clear();
                        returnedObjects.returnObjectsToTile(altarEntity.getLevel(), altarEntity.tileX, altarEntity.tileY);
                    }
                }
            }
        });
        final FallenAltarContainer altarContainer = this;
        this.obtainPerk = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                AltarData altarData;
                IncursionPerk perk = IncursionPerksRegistry.getPerk(value);
                if (perk.canObtainPerk(altarData = altarEntity.altarData) && perk.canAffordToBuyPerk(altarContainer)) {
                    Recipe.craft(perk.buyPerkIngredientCost, client.playerMob.getLevel(), client.playerMob, FallenAltarContainer.this.getCraftInventories());
                    altarData.obtainPerk(perk);
                    altarEntity.markDirty();
                    FallenAltarContainer.this.checkAchievements();
                }
            }
        });
        this.respecPerk = this.registerAction(new IntCustomAction(){

            @Override
            protected void run(int value) {
                AltarData altarData;
                IncursionPerk perk = IncursionPerksRegistry.getPerk(value);
                if (perk.canRespecPerk(altarData = altarEntity.altarData) && perk.canAffordToRespecPerk(altarContainer)) {
                    Recipe.craft(perk.respecIngredientCost, client.playerMob.getLevel(), client.playerMob, FallenAltarContainer.this.getCraftInventories());
                    InventoryItem altarDust = new InventoryItem("altardust", perk.perkCost);
                    client.playerMob.getInv().addItemsDropRemaining(altarDust, "addback", client.playerMob, !client.isServer(), true, true);
                    altarData.removePerk(perk);
                    altarEntity.markDirty();
                }
            }
        });
        this.resetPerkTree = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                AltarData altarData = altarEntity.altarData;
                if (FallenAltarContainer.this.canAffordFullPerkTreeRespec(altarData).canCraft()) {
                    Recipe.craft(FallenAltarContainer.this.getFullAltarRespecIngredientCost(altarData), client.playerMob.getLevel(), client.playerMob, FallenAltarContainer.this.getCraftInventories());
                    InventoryItem altarDust = new InventoryItem("altardust", FallenAltarContainer.this.getAltarDustAmountFromFullPerkTreeRespec(altarData));
                    client.playerMob.getInv().addItemsDropRemaining(altarDust, "addback", client.playerMob, !client.isServer(), true, true);
                    altarEntity.altarData.obtainedPerkIDs.clear();
                    altarEntity.markDirty();
                }
            }
        });
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        return !this.altarEntity.removed() && this.altarEntity.getLevelObject().isInInteractRange(client.playerMob);
    }

    public boolean open(IncursionData incursion) {
        if (incursion.getCanOpenError(this) == null) {
            if (this.client.isServer()) {
                this.altarEntity.openIncursion(this, incursion, this.client.getServerClient());
            }
            return true;
        }
        return false;
    }

    public static Packet getContainerContent(Server server, FallenAltarObjectEntity altar) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        return packet;
    }

    public CanCraft canAffordPerk(IncursionPerk perk) {
        Recipe recipe = new Recipe("air", RecipeTechRegistry.NONE, perk.buyPerkIngredientCost);
        return this.canCraftRecipe(recipe, this.getCraftInventories(), true);
    }

    public CanCraft canAffordPerkRespec(IncursionPerk perk) {
        Recipe recipe = new Recipe("air", RecipeTechRegistry.NONE, perk.respecIngredientCost);
        return this.canCraftRecipe(recipe, this.getCraftInventories(), true);
    }

    public CanCraft canAffordFullPerkTreeRespec(AltarData altarData) {
        Recipe recipe = new Recipe("air", RecipeTechRegistry.NONE, this.getFullAltarRespecIngredientCost(altarData));
        return this.canCraftRecipe(recipe, this.getCraftInventories(), true);
    }

    public Ingredient[] getFullAltarRespecIngredientCost(AltarData altarData) {
        int respecCostInCoins = altarData.obtainedPerkIDs.size() * 1000;
        return new Ingredient[]{new Ingredient("coin", respecCostInCoins)};
    }

    public int getAltarDustAmountFromFullPerkTreeRespec(AltarData altarData) {
        int dust = 0;
        for (Integer obtainedPerkID : altarData.obtainedPerkIDs) {
            dust += IncursionPerksRegistry.getPerk((int)obtainedPerkID.intValue()).perkCost;
        }
        return dust;
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

    public void checkAchievements() {
        if (!this.client.isServer()) {
            return;
        }
        ServerClient serverClient = this.client.getServerClient();
        if (!serverClient.achievementsLoaded()) {
            return;
        }
        if (this.altarEntity.altarData.obtainedPerkIDs.size() >= 40) {
            serverClient.achievements().HAVE_40_PERKS.markCompleted(serverClient);
        }
    }
}

