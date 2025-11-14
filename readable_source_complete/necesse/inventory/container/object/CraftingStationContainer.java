/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import necesse.engine.GameTileRange;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.settlement.SettlementContainerObjectStatusManager;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.container.CraftingStationObject;
import necesse.level.gameObject.container.CraftingStationUpgrade;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.multiTile.MultiTile;

public class CraftingStationContainer
extends SettlementDependantContainer {
    public static int nearbyCraftTileRange = 9;
    public final GameMessage header;
    public final Tech[] techs;
    public final int categoryDepth;
    public HashSet<ItemCategory> forceCategorySolo;
    public final CraftingStationUpgrade upgrade;
    public final int objectX;
    public final int objectY;
    public final CraftingStationObject craftingStationObject;
    public final GameTileRange range;
    private final LinkedHashSet<Inventory> nearbyInventories = new LinkedHashSet();
    public SettlementContainerObjectStatusManager settlementObjectManager;
    public final EmptyCustomAction upgradeStationAction;

    public CraftingStationContainer(final NetworkClient client, int uniqueSeed, SettlementDataEvent settlement, final LevelObject levelObject, PacketReader reader) {
        super(client, uniqueSeed, settlement, true);
        this.objectX = levelObject.tileX;
        this.objectY = levelObject.tileY;
        this.craftingStationObject = (CraftingStationObject)levelObject.object;
        this.header = this.craftingStationObject.getCraftingHeader();
        this.techs = this.craftingStationObject.getCraftingTechs();
        this.categoryDepth = Math.max(this.craftingStationObject.getCraftingCategoryDepth(), 0);
        this.forceCategorySolo = this.craftingStationObject.getForcedSoloCraftingCategories();
        this.upgrade = this.craftingStationObject.getStationUpgrade();
        this.settlementObjectManager = new SettlementContainerObjectStatusManager(this, levelObject.level, levelObject.tileX, levelObject.tileY, reader);
        Recipes.streamRecipes().filter(r -> Arrays.stream(this.techs).anyMatch(r::matchTech)).forEach(this::addRecipe);
        MultiTile multiTile = levelObject.getMultiTile();
        Rectangle tileRectangle = multiTile.getTileRectangle(0, 0);
        this.range = new GameTileRange(nearbyCraftTileRange, tileRectangle);
        this.nearbyInventories.addAll(this.craftInventories);
        for (InventoryRange inventoryRange : this.getNearbyInventories(levelObject.level, this.objectX, this.objectY, this.range, OEInventory::canUseForNearbyCrafting)) {
            this.nearbyInventories.add(inventoryRange.inventory);
        }
        this.upgradeStationAction = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                if (CraftingStationContainer.this.upgrade == null) {
                    return;
                }
                Collection<Inventory> craftInventories = CraftingStationContainer.this.getCraftInventories();
                if (CraftingStationContainer.this.canCraftRecipe(CraftingStationContainer.this.upgrade.cost, craftInventories, true).canCraft()) {
                    if (client.isServer()) {
                        CraftingStationContainer.this.craftingStationObject.performUpgrade(CraftingStationContainer.this.upgrade.upgradeObject, levelObject.level, levelObject.tileX, levelObject.tileY, client.getServerClient());
                    }
                    ArrayList<InventoryItemsRemoved> arrayList = Recipe.craft(CraftingStationContainer.this.upgrade.cost, client.playerMob.getLevel(), client.playerMob, craftInventories);
                }
                CraftingStationContainer.this.close();
            }
        });
    }

    @Override
    public Collection<Inventory> getCraftInventories() {
        if (this.useNearbyInventories()) {
            return this.nearbyInventories;
        }
        return super.getCraftInventories();
    }

    private boolean useNearbyInventories() {
        if (this.client.isServer()) {
            return this.client.craftingUsesNearbyInventories;
        }
        return Settings.craftingUseNearby.get();
    }

    @Override
    public void init() {
        super.init();
        if (this.client.isClient()) {
            GlobalData.updateRecipes();
        }
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
        Level level = client.getLevel();
        return level.getObjectID(this.objectX, this.objectY) == this.craftingStationObject.getID() && level.getObject(this.objectX, this.objectY).isInInteractRange(level, this.objectX, this.objectY, client.playerMob);
    }

    public static void openAndSendContainer(int containerID, ServerClient client, Level level, int tileX, int tileY, Packet extraContent) {
        if (!level.isServer()) {
            throw new IllegalStateException("Level must be a server level");
        }
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        ServerSettlementData settlement = SettlementsWorldData.getSettlementsData(level).getServerDataAtTile(level.getIdentifier(), tileX, tileY);
        SettlementContainerObjectStatusManager.writeContent(settlement, level, tileX, tileY, writer);
        if (extraContent != null) {
            writer.putNextContentPacket(extraContent);
        }
        PacketOpenContainer p = PacketOpenContainer.SettlementLevelObject(containerID, settlement, tileX, tileY, packet);
        ContainerRegistry.openAndSendContainer(client, p);
    }

    public static void openAndSendContainer(int containerID, ServerClient client, Level level, int tileX, int tileY) {
        CraftingStationContainer.openAndSendContainer(containerID, client, level, tileX, tileY, null);
    }
}

