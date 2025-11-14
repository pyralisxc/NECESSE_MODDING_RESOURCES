/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.DecorDrawOffset;
import necesse.level.gameObject.DecorationHolderInterface;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.LargeTableDecorationObject2;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class LargeTableDecorationObject
extends GameObject {
    protected String textureName;
    public ObjectDamagedTextureArray texture;
    public ObjectDamagedTextureArray texture_decor;
    public int decorationWidth;
    public int decorationHeight;
    public int holderDrawXOffset;
    public int holderDrawYOffset;
    protected int counterID;
    public GameMessage customLocalization;

    public LargeTableDecorationObject(String textureName, ToolType toolType, Color mapColor, int decorationWidth, int decorationHeight, int holderDrawXOffset, int holderDrawYOffset) {
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.decorationWidth = decorationWidth;
        this.decorationHeight = decorationHeight;
        this.holderDrawXOffset = holderDrawXOffset;
        this.holderDrawYOffset = holderDrawYOffset;
        this.setItemCategory("objects", "landscaping", "tabledecorations");
        this.setCraftingCategory("objects", "landscaping", "tabledecorations");
        this.objectHealth = 50;
        this.stackSize = 100;
        this.displayMapTooltip = true;
        this.isLightTransparent = true;
        this.canPlaceOnShore = true;
        this.validObjectLayers.add(ObjectLayerRegistry.FENCE_AND_TABLE_DECOR);
    }

    public LargeTableDecorationObject setCustomLocalization(GameMessage name) {
        this.customLocalization = name;
        return this;
    }

    public LargeTableDecorationObject setCustomLocalization(String category, String key) {
        return this.setCustomLocalization(new LocalMessage(category, key));
    }

    public LargeTableDecorationObject setCustomLocalization(String key) {
        return this.setCustomLocalization("object", key);
    }

    @Override
    public GameMessage getNewLocalization() {
        if (this.customLocalization != null) {
            return this.customLocalization;
        }
        return super.getNewLocalization();
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", "tabledecorationtip"));
        tooltips.add(Localization.translate("itemtooltip", "placetip"));
        return tooltips;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
        try {
            this.texture_decor = ObjectDamagedTextureArray.loadAndApplyOverlayRaw(this, "objects/" + this.textureName + "_decor");
        }
        catch (FileNotFoundException e) {
            this.texture_decor = this.texture;
        }
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(0, 0, 2, 1, true, this.getID(), this.counterID);
    }

    @Override
    protected ObjectHoverHitbox getHoverHitbox(Level level, int layerID, int tileX, int tileY) {
        DecorationHolderInterface holder;
        ObjectHoverHitbox hitbox = super.getHoverHitbox(level, layerID, tileX, tileY);
        if (layerID == ObjectLayerRegistry.FENCE_AND_TABLE_DECOR && (holder = this.getDecorationHolder(level, tileX, tileY)) != null) {
            int offset = holder.getDecorationDrawOffset((Level)level, (int)tileX, (int)tileY, (GameObject)this).yOffset + this.holderDrawYOffset;
            hitbox.y += offset;
            hitbox.height -= offset;
        }
        return hitbox;
    }

    @Override
    public void addLayerDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int layerID, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        DecorationHolderInterface holder;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, layerID, tileX, tileY);
        GameTexture textureDecor = this.texture_decor.getDamagedTexture(this, level, layerID, tileX, tileY);
        int sortY = 16;
        if (layerID == ObjectLayerRegistry.FENCE_AND_TABLE_DECOR && (holder = this.getDecorationHolder(level, tileX, tileY)) != null) {
            DecorDrawOffset drawOffset = holder.getDecorationDrawOffset(level, tileX, tileY, this);
            drawX += drawOffset.xOffset + this.holderDrawXOffset;
            drawY += drawOffset.yOffset + this.holderDrawYOffset;
            sortY = drawOffset.sortY;
            if (!drawOffset.useShadowTexture) {
                texture = textureDecor;
            }
        }
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(0, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32);
        final int finalSortY = sortY;
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return finalSortY;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        GameTexture textureDecor = this.texture_decor.getDamagedTexture(this, level, tileX, tileY);
        DecorationHolderInterface holder = this.getDecorationHolder(level, tileX, tileY);
        if (holder != null) {
            DecorDrawOffset drawOffset = holder.getDecorationDrawOffset(level, tileX, tileY, this);
            drawX += drawOffset.xOffset + this.holderDrawXOffset;
            drawY += drawOffset.yOffset + this.holderDrawYOffset;
            if (!drawOffset.useShadowTexture) {
                texture = textureDecor;
            }
        }
        texture.initDraw().sprite(0, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    public DecorationHolderInterface getDecorationHolder(Level level, int tileX, int tileY) {
        GameObject object = level.getObject(tileX, tileY);
        if (object instanceof DecorationHolderInterface) {
            return (DecorationHolderInterface)((Object)object);
        }
        return null;
    }

    @Override
    public ArrayList<ObjectPlaceOption> getPlaceOptions(Level level, int levelX, int levelY, PlayerMob playerMob, int playerDir, boolean offsetMultiTile) {
        int testTileY;
        ArrayList<ObjectPlaceOption> placeOptions = super.getPlaceOptions(level, levelX, levelY, playerMob, playerDir, offsetMultiTile);
        int testTileX = GameMath.getTileCoordinate(levelX);
        GameObject belowObject = level.getObject(testTileX, (testTileY = GameMath.getTileCoordinate(levelY)) + 1);
        if (belowObject instanceof DecorationHolderInterface && belowObject.getHoverHitboxes(level, 0, testTileX, testTileY + 1).stream().anyMatch(hitbox -> hitbox.contains(levelX, levelY))) {
            Point offset = offsetMultiTile ? this.getPlaceOffset(playerDir) : null;
            int tileX = GameMath.getTileCoordinate(levelX + (offset == null ? 0 : offset.x));
            int tileY = GameMath.getTileCoordinate(levelY + (offset == null ? 0 : offset.y));
            return new ArrayList<ObjectPlaceOption>(Collections.singleton(new ObjectPlaceOption(tileX, tileY + 1, this, playerDir, false)));
        }
        return placeOptions;
    }

    @Override
    public boolean canBePlacedOn(Level level, int layerID, int x, int y, GameObject newObject, boolean ignoreOtherLayers) {
        return false;
    }

    public boolean attachesToObject(Level level, int tileX, int tileY) {
        DecorationHolderInterface holder = this.getDecorationHolder(level, tileX, tileY);
        if (holder == null) {
            return false;
        }
        return holder.canPlaceDecoration(level, tileX, tileY);
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String childError;
        MultiTile multiTile = this.getMultiTile(rotation);
        if (multiTile.isMaster && (childError = multiTile.streamOtherObjects(x, y).map(e -> ((GameObject)e.value).canPlace(level, layerID, e.tileX, e.tileY, rotation, byPlayer, ignoreOtherLayers)).reduce(null, (prev, e) -> prev != null ? prev : e)) != null) {
            return childError;
        }
        if (layerID == ObjectLayerRegistry.FENCE_AND_TABLE_DECOR) {
            DecorationHolderInterface holder = this.getDecorationHolder(level, x, y);
            if (holder == null) {
                return "nodecorholder";
            }
            if (!holder.canPlaceDecoration(level, x, y)) {
                return "occupied";
            }
            Dimension maxSize = holder.getMaxDecorationSize(level, x, y);
            if (this.decorationWidth > maxSize.width || this.decorationHeight > maxSize.height) {
                return "decortoobig";
            }
            if (this.attachesToObject(level, x + 1, y)) {
                return null;
            }
        }
        return super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        byte rotation = level.getObjectRotation(layerID, x, y);
        if (layerID == ObjectLayerRegistry.FENCE_AND_TABLE_DECOR) {
            DecorationHolderInterface holder = this.getDecorationHolder(level, x, y);
            if (holder == null) {
                return false;
            }
            Dimension maxSize = holder.getMaxDecorationSize(level, x, y);
            if (this.decorationWidth > maxSize.width || this.decorationHeight > maxSize.height) {
                return false;
            }
        }
        return this.getMultiTile(rotation).streamOtherIDs(x, y).allMatch(e -> level.getObjectID(layerID, e.tileX, e.tileY) == ((Integer)e.value).intValue());
    }

    public static int[] registerLargeTableDecoration(String stringID, Color mapColor, int decorationWidth, int decorationHeight, int holderDrawXOffset, int holderDrawYOffset, float brokerValue, boolean isObtainable, boolean countInStats) {
        int i2;
        LargeTableDecorationObject obj1 = new LargeTableDecorationObject(stringID, ToolType.ALL, mapColor, decorationWidth, decorationHeight, holderDrawXOffset, holderDrawYOffset);
        LargeTableDecorationObject2 obj2 = new LargeTableDecorationObject2(stringID, ToolType.ALL, mapColor, decorationWidth, decorationHeight, holderDrawXOffset, holderDrawYOffset);
        int i1 = ObjectRegistry.registerObject(stringID, (GameObject)obj1, brokerValue, isObtainable, countInStats, new String[0]);
        obj1.counterID = i2 = ObjectRegistry.registerObject(stringID + "2", obj2, 0.0f, false);
        obj2.counterID = i1;
        return new int[]{i1, i2};
    }
}

