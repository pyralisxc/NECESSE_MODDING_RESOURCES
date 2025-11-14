/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.StoneCoffin2Object;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class CoffinObject
extends GameObject {
    protected String textureName;
    protected ObjectDamagedTextureArray texture;
    protected ObjectDamagedTextureArray openTexture;
    protected int counterID;
    protected final GameRandom drawRandom;
    protected String droppedItemStringID;

    public static int[] registerCoffinObject(String stringID, String textureName, String droppedItemStringID, ToolType toolType, Color mapColor, float brokerValue, boolean isObtainable) {
        int i2;
        CoffinObject obj1 = new CoffinObject(textureName, droppedItemStringID, toolType, mapColor);
        StoneCoffin2Object obj2 = new StoneCoffin2Object(textureName, droppedItemStringID, toolType, mapColor);
        int i1 = ObjectRegistry.registerObject(stringID, obj1, brokerValue, isObtainable);
        obj1.counterID = i2 = ObjectRegistry.registerObject(stringID + "2", obj2, 0.0f, false);
        obj2.counterID = i1;
        return new int[]{i1, i2};
    }

    protected CoffinObject(String textureName, String droppedItemStringID, ToolType toolType, Color mapColor) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.droppedItemStringID = droppedItemStringID;
        this.mapColor = mapColor;
        this.toolType = toolType;
        this.displayMapTooltip = true;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
        this.setItemCategory("objects", "landscaping", "masonry");
        this.setCraftingCategory("objects", "landscaping", "masonry");
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        if (this.droppedItemStringID != null) {
            return new LootTable(LootItem.between(this.droppedItemStringID, 10, 20).splitItems(5).preventLootMultiplier());
        }
        return new LootTable();
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new MultiTile(0, 1, 1, 2, rotation, true, this.counterID, this.getID());
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
        try {
            this.openTexture = ObjectDamagedTextureArray.loadAndApplyOverlayRaw(this, "objects/" + this.textureName + "_open");
        }
        catch (FileNotFoundException e) {
            this.openTexture = null;
        }
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 6, y * 32, 20, 26);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32 + 6, y * 32 + 6, 26, 20);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 6, y * 32 + 6, 20, 26);
        }
        return new Rectangle(x * 32, y * 32 + 6, 26, 20);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        ObjectEntity ent;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        boolean treasureHunter = perspective != null && perspective.buffManager.getModifier(BuffModifiers.TREASURE_HUNTER) != false;
        ObjectDamagedTextureArray usedTexture = this.texture;
        if (this.openTexture != null && (ent = level.entityManager.getObjectEntity(tileX, tileY)) != null && ent.implementsOEUsers() && ((OEUsers)((Object)ent)).isInUse()) {
            usedTexture = this.openTexture;
        }
        GameTexture texture = usedTexture.getDamagedTexture(this, level, tileX, tileY);
        final SharedTextureDrawOptions draws = new SharedTextureDrawOptions(texture);
        if (rotation == 0) {
            draws.addSprite(0, texture.getHeight() / 32 - 1, 32).spelunkerLight(light, treasureHunter, this.getID(), level).pos(drawX, drawY);
        } else if (rotation == 1) {
            draws.addSprite(1, 0, 32, texture.getHeight()).spelunkerLight(light, treasureHunter, this.getID(), level).pos(drawX, drawY - texture.getHeight() + 32);
        } else if (rotation == 2) {
            draws.addSprite(3, 0, 32, texture.getHeight() - 32).spelunkerLight(light, treasureHunter, this.getID(), level).pos(drawX, drawY - texture.getHeight() + 64);
        } else {
            draws.addSprite(5, 0, 32, texture.getHeight()).spelunkerLight(light, treasureHunter, this.getID(), level).pos(drawX, drawY - texture.getHeight() + 32);
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                draws.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        if (rotation == 0) {
            texture.initDraw().sprite(0, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - 32);
        } else if (rotation == 1) {
            texture.initDraw().sprite(1, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
            texture.initDraw().sprite(2, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX + 32, drawY - texture.getHeight() + 32);
        } else if (rotation == 2) {
            texture.initDraw().sprite(3, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY);
        } else {
            texture.initDraw().sprite(4, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX - 32, drawY - texture.getHeight() + 32);
            texture.initDraw().sprite(5, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
        }
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        if (level.isServer()) {
            OEInventoryContainer.openAndSendContainer(ContainerRegistry.OE_INVENTORY_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        if (!this.getMultiTile((int)0).isMaster) {
            return null;
        }
        return new InventoryObjectEntity(level, x, y, 10){

            @Override
            public boolean canSetInventoryName() {
                return false;
            }
        };
    }
}

