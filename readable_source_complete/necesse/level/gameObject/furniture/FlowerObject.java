/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.FlowerObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FlowerObject
extends FurnitureObject {
    protected String textureName;
    public String customDrop;
    public GameTexture texture;
    public GameTexture itemTexture;
    public int spriteX;
    public String wildObjectStringID;
    public int itemSpoilDurationMinutes;

    public FlowerObject(String textureName, int spriteX, int stackSize, int itemSpoilDurationMinutes, String wildObjectStringID, Color mapColor) {
        this(textureName, spriteX, stackSize, itemSpoilDurationMinutes, null, wildObjectStringID, mapColor);
    }

    public FlowerObject(String textureName, int spriteX, int stackSize, int itemSpoilDurationMinutes, String customDrop, String wildObjectStringID, Color mapColor) {
        super(new Rectangle(0, 0));
        this.textureName = textureName;
        this.spriteX = spriteX;
        this.stackSize = stackSize;
        this.itemSpoilDurationMinutes = itemSpoilDurationMinutes;
        this.customDrop = customDrop;
        this.wildObjectStringID = wildObjectStringID;
        this.mapColor = mapColor;
        this.setItemCategory("materials", "flowers");
        this.objectHealth = 50;
        this.toolType = ToolType.ALL;
        this.drawDamage = false;
        this.isLightTransparent = true;
        this.furnitureType = "flower";
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        GameTexture texture = GameTexture.fromFile("objects/" + this.textureName);
        this.itemTexture = new GameTexture(texture, 0, 0, 32);
        this.texture = new GameTexture(texture, this.spriteX * 32, 0, 32, texture.getHeight());
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        LootTable out = new LootTable();
        if (this.customDrop != null) {
            out.items.add(new LootItem(this.customDrop).preventLootMultiplier());
        } else {
            out.items.add(super.getLootTable(level, layerID, tileX, tileY));
        }
        out.items.add(new LootItem("flowerpot").preventLootMultiplier());
        return out;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - 8;
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).pos(drawX, drawY - (this.texture.getHeight() - 32));
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameObject wildObject;
        String canWildPlace;
        if (this.wildObjectStringID != null && (canWildPlace = (wildObject = ObjectRegistry.getObject(this.wildObjectStringID)).canPlace(level, tileX, tileY, rotation, true)) == null) {
            wildObject.drawPreview(level, tileX, tileY, rotation, alpha, player, camera);
            return;
        }
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - 8;
        this.texture.initDraw().alpha(alpha).draw(drawX, drawY - (this.texture.getHeight() - 32));
    }

    @Override
    public void placeObject(Level level, int layerID, int x, int y, int rotation, boolean byPlayer) {
        GameObject wildObject;
        String canWildPlace;
        if (this.wildObjectStringID != null && (canWildPlace = (wildObject = ObjectRegistry.getObject(this.wildObjectStringID)).canPlace(level, layerID, x, y, rotation, byPlayer, false)) == null) {
            wildObject.placeObject(level, layerID, x, y, rotation, byPlayer);
            return;
        }
        super.placeObject(level, layerID, x, y, rotation, byPlayer);
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String canWildPlace;
        if (this.wildObjectStringID != null && (canWildPlace = ObjectRegistry.getObject(this.wildObjectStringID).canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers)) == null) {
            return null;
        }
        if (!level.getObject((int)x, (int)y).isFlowerpot) {
            return "notflowerpot";
        }
        return null;
    }

    @Override
    public boolean canReplace(Level level, int layerID, int tileX, int tileY, int rotation) {
        return false;
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        if (this.wildObjectStringID != null) {
            tooltips.add(Localization.translate("itemtooltip", "wildflowertip"));
        } else {
            tooltips.add(Localization.translate("itemtooltip", "flowertip"));
        }
        return tooltips;
    }

    @Override
    public Item generateNewObjectItem() {
        return new FlowerObjectItem((GameObject)this, () -> this.itemTexture).spoilDuration(this.itemSpoilDurationMinutes).addGlobalIngredient("anycompostable");
    }
}

