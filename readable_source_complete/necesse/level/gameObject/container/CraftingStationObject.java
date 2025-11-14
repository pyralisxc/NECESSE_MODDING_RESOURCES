/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.packet.PacketPlaceObject;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.entity.levelEvent.SmokePuffCloudLevelEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.container.CraftingStationUpgrade;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.multiTile.MultiTile;

public class CraftingStationObject
extends FurnitureObject
implements SettlementWorkstationObject {
    public CraftingStationObject() {
        this.displayMapTooltip = true;
        this.setItemCategory("objects", "craftingstations");
        this.setCraftingCategory("craftingstations");
        this.replaceCategories.add("workstation");
        this.canReplaceCategories.add("workstation");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("furniture");
    }

    public CraftingStationObject(Rectangle collision) {
        super(collision);
        this.displayMapTooltip = true;
        this.setItemCategory("objects", "craftingstations");
        this.setCraftingCategory("craftingstations");
        this.replaceCategories.add("workstation");
        this.canReplaceCategories.add("workstation");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("furniture");
    }

    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.NONE};
    }

    public GameMessage getCraftingHeader() {
        return this.getLocalization();
    }

    public int getCraftingCategoryDepth() {
        return 1;
    }

    public HashSet<ItemCategory> getForcedSoloCraftingCategories() {
        return new HashSet<ItemCategory>();
    }

    public int getCraftingFormWidth() {
        return 684;
    }

    public int getCraftingFormXOffset() {
        return 0;
    }

    public boolean allowHighlightOption() {
        return true;
    }

    public CraftingStationUpgrade getStationUpgrade() {
        return null;
    }

    public void performUpgrade(GameObject upgradeObject, Level level, int tileX, int tileY, ServerClient client) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        MultiTile multiTile = this.getMultiTile(level, 0, tileX, tileY);
        ArrayList lastObjects = multiTile.streamObjects(tileX, tileY).filter(e -> level.getObjectID(e.tileX, e.tileY) == ((GameObject)e.value).getID()).map(e -> level.getLevelObject(e.tileX, e.tileY)).collect(Collectors.toCollection(ArrayList::new));
        for (LevelObject lastObject : lastObjects) {
            level.setObject(lastObject.tileX, lastObject.tileY, 0);
        }
        String canPlace = upgradeObject.canPlace(level, 0, tileX, tileY, rotation, true, false);
        if (canPlace == null) {
            upgradeObject.placeObject(level, 0, tileX, tileY, rotation, true);
            level.getServer().network.sendToClientsWithTile(new PacketPlaceObject(level, null, 0, tileX, tileY, upgradeObject.getID(), rotation, true), level, tileX, tileY);
            Rectangle levelRectangle = upgradeObject.getMultiTile(rotation).getLevelRectangle(tileX, tileY);
            level.entityManager.events.add(new SmokePuffCloudLevelEvent(levelRectangle, upgradeObject.mapColor));
        } else {
            InventoryItem upgradeItem = upgradeObject.getObjectItem().getDefaultItem(client.playerMob, 1);
            level.entityManager.pickups.add(upgradeItem.getPickupEntity(level, tileX * 32 + 16, tileY * 32 + 16));
            level.entityManager.events.add(new SmokePuffCloudLevelEvent(new Rectangle(tileX * 32, tileY * 32, 32, 32), upgradeObject.mapColor));
        }
        for (LevelObject lastObject : lastObjects) {
            level.sendObjectUpdatePacket(lastObject.tileX, lastObject.tileY);
        }
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        if (this.isMultiTileMaster()) {
            if (level.isServer()) {
                CraftingStationContainer.openAndSendContainer(ContainerRegistry.CRAFTING_STATION_CONTAINER, player.getServerClient(), level, x, y);
            }
        } else {
            this.getMultiTile(level.getObjectRotation(x, y)).getMasterLevelObject(level, 0, x, y).ifPresent(e -> e.interact(player));
        }
    }

    public int getCraftingObjectID() {
        return this.getMultiTile(0).getMasterObject().getID();
    }

    @Override
    public Stream<Recipe> streamSettlementRecipes(Level level, int tileX, int tileY) {
        Tech[] techs = this.getCraftingTechs();
        return Recipes.streamRecipes().filter(r -> r.matchesTechs(techs));
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return new SoundSettings(GameResources.chestopen);
    }
}

