/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.ParticleOption;
import necesse.entity.projectile.BombProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.TorchObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.DecorDrawOffset;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.gameObject.TorchHolderInterface;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TorchObject
extends GameObject {
    public float flameHue = ParticleOption.defaultFlameHue;
    public float smokeHue = ParticleOption.defaultSmokeHue;
    public boolean disableParticles = false;
    protected String textureName;
    public GameTexture texture;
    public GameTexture texture_off;
    public GameTexture texture_decor;
    public GameTexture texture_decor_off;
    public int particleStartHeight = 12;
    public boolean canPlaceOnHolders;
    public int holderDrawXOffset;
    public int holderDrawYOffset;
    protected String wallPlaceObjectStringID;

    public TorchObject(String textureName, ToolType toolType, Color mapColor, float lightHue, float lightSat, boolean canPlaceOnHolders, int holderDrawYOffset) {
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.lightHue = lightHue;
        this.lightSat = lightSat;
        this.canPlaceOnHolders = canPlaceOnHolders;
        this.holderDrawYOffset = holderDrawYOffset;
        this.setItemCategory("objects", "lighting");
        this.setCraftingCategory("objects", "lighting");
        this.objectHealth = 1;
        this.lightLevel = 150;
        this.stackSize = 1000;
        this.displayMapTooltip = true;
        this.isLightTransparent = true;
        this.canPlaceOnShore = true;
        if (canPlaceOnHolders) {
            this.validObjectLayers.add(ObjectLayerRegistry.FENCE_AND_TABLE_DECOR);
        }
        this.replaceRotations = false;
        this.roomProperties.add("lights");
        this.replaceCategories.add("torch");
        this.canReplaceCategories.add("torch");
        this.canReplaceCategories.add("furniture");
        this.canReplaceCategories.add("column");
    }

    public TorchObject(String textureName, ToolType toolType, Color mapColor, float lightHue, float lightSat) {
        this(textureName, toolType, mapColor, lightHue, lightSat, true, 0);
    }

    public TorchObject(String textureName, Color mapColor, float lightHue, float lightSat) {
        this(textureName, ToolType.ALL, mapColor, lightHue, lightSat);
    }

    public TorchObject setWallPlaceObjectStringID(String wallTorchStringID) {
        if (ObjectRegistry.instance.isClosed()) {
            throw new IllegalStateException("Cannot set torch wall place once object registry is closed");
        }
        this.wallPlaceObjectStringID = wallTorchStringID;
        return this;
    }

    @Override
    public ArrayList<ObjectPlaceOption> getPlaceOptions(Level level, int levelX, int levelY, PlayerMob playerMob, int playerDir, boolean offsetMultiTile) {
        GameObject wallPlaceObject;
        ArrayList<ObjectPlaceOption> placeOptions = super.getPlaceOptions(level, levelX, levelY, playerMob, playerDir, offsetMultiTile);
        if (this.wallPlaceObjectStringID != null && (wallPlaceObject = ObjectRegistry.getObject(this.wallPlaceObjectStringID)) != null) {
            Rectangle wallPlaceUpRectangle;
            Rectangle wallPlaceRightRectangle;
            int tileY;
            int placeRectangleSize;
            int tileX = GameMath.getTileCoordinate(levelX);
            Rectangle wallPlaceLeftRectangle = new Rectangle(tileX * 32 + (32 - (placeRectangleSize = 12)), (tileY = GameMath.getTileCoordinate(levelY)) * 32, placeRectangleSize, 32);
            if (wallPlaceLeftRectangle.contains(levelX, levelY)) {
                placeOptions.add(0, new ObjectPlaceOption(tileX, tileY, wallPlaceObject, 3, true));
            }
            if ((wallPlaceRightRectangle = new Rectangle(tileX * 32, tileY * 32, placeRectangleSize, 32)).contains(levelX, levelY)) {
                placeOptions.add(0, new ObjectPlaceOption(tileX, tileY, wallPlaceObject, 1, true));
            }
            if ((wallPlaceUpRectangle = new Rectangle(tileX * 32, tileY * 32 + (32 - placeRectangleSize), 32, placeRectangleSize)).contains(levelX, levelY)) {
                placeOptions.add(0, new ObjectPlaceOption(tileX, tileY, wallPlaceObject, 0, true));
            }
            GameObject currentObject = level.getObject(tileX, tileY);
            if (currentObject.isWall || currentObject.isRock) {
                placeOptions.add(0, new ObjectPlaceOption(tileX, tileY + 1, wallPlaceObject, 2, true));
            }
        }
        return placeOptions;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + this.textureName);
        this.texture_off = GameTexture.fromFile("objects/" + this.textureName + "_off");
        try {
            this.texture_decor = GameTexture.fromFileRaw("objects/" + this.textureName + "_decor");
        }
        catch (FileNotFoundException e) {
            this.texture_decor = this.texture;
        }
        try {
            this.texture_decor_off = GameTexture.fromFileRaw("objects/" + this.textureName + "_decor_off");
        }
        catch (FileNotFoundException e) {
            this.texture_decor_off = this.texture_off;
        }
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("controls", "torchplacetip"));
        return tooltips;
    }

    @Override
    protected ObjectHoverHitbox getHoverHitbox(Level level, int layerID, int tileX, int tileY) {
        TorchHolderInterface holder;
        ObjectHoverHitbox hitbox = super.getHoverHitbox(level, layerID, tileX, tileY);
        if (layerID == ObjectLayerRegistry.FENCE_AND_TABLE_DECOR && (holder = this.getTorchHolder(level, tileX, tileY)) != null) {
            int offset = holder.getTorchDrawOffset((Level)level, (int)tileX, (int)tileY).yOffset + this.holderDrawYOffset;
            hitbox.y += offset;
            hitbox.height -= offset;
        }
        return hitbox;
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        if (this.disableParticles) {
            return;
        }
        if (GameRandom.globalRandom.getEveryXthChance(40) && this.isActive(level, layerID, tileX, tileY)) {
            TorchHolderInterface holder;
            int xOffset = 16;
            int startHeight = this.particleStartHeight + (int)(GameRandom.globalRandom.nextGaussian() * 2.0);
            if (layerID == ObjectLayerRegistry.FENCE_AND_TABLE_DECOR && (holder = this.getTorchHolder(level, tileX, tileY)) != null) {
                DecorDrawOffset drawOffset = holder.getTorchDrawOffset(level, tileX, tileY);
                xOffset += drawOffset.xOffset + this.holderDrawXOffset;
                startHeight -= drawOffset.yOffset + this.holderDrawYOffset;
            }
            BombProjectile.spawnFuseParticle(level, tileX * 32 + xOffset, tileY * 32 + 16 + 2, startHeight, this.flameHue, this.smokeHue);
        }
    }

    @Override
    public void addLayerDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int layerID, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        TorchHolderInterface holder;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        boolean active = this.isActive(level, layerID, tileX, tileY);
        GameTexture texture = active ? this.texture : this.texture_off;
        int sortY = 16;
        if (layerID == ObjectLayerRegistry.FENCE_AND_TABLE_DECOR && (holder = this.getTorchHolder(level, tileX, tileY)) != null) {
            DecorDrawOffset drawOffset = holder.getTorchDrawOffset(level, tileX, tileY);
            drawX += drawOffset.xOffset + this.holderDrawXOffset;
            drawY += drawOffset.yOffset + this.holderDrawYOffset;
            sortY = drawOffset.sortY;
            if (!drawOffset.useShadowTexture) {
                texture = active ? this.texture_decor : this.texture_decor_off;
            }
        }
        int rotation = level.getObjectRotation(layerID, tileX, tileY) % (texture.getWidth() / 32);
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(rotation, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32);
        final int finalSortY = sortY;
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return finalSortY;
            }

            @Override
            public void draw(TickManager tickManager) {
                Performance.record((PerformanceTimerManager)tickManager, "torchDraw", options::draw);
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture;
        TorchHolderInterface holder = this.getTorchHolder(level, tileX, tileY);
        if (holder != null) {
            DecorDrawOffset drawOffset = holder.getTorchDrawOffset(level, tileX, tileY);
            drawX += drawOffset.xOffset + this.holderDrawXOffset;
            drawY += drawOffset.yOffset + this.holderDrawYOffset;
            if (!drawOffset.useShadowTexture) {
                texture = this.texture_decor;
            }
        }
        rotation = (byte)(rotation % (texture.getWidth() / 32));
        texture.initDraw().sprite(rotation, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    @Override
    public Item generateNewObjectItem() {
        return new TorchObjectItem(this, false);
    }

    @Override
    public int getLightLevel(Level level, int layerID, int tileX, int tileY) {
        return this.isActive(level, layerID, tileX, tileY) ? this.lightLevel : 0;
    }

    public boolean isActive(Level level, int layerID, int x, int y) {
        return this.getMultiTile(level, layerID, x, y).streamIDs(x, y).noneMatch(c -> level.wireManager.isWireActiveAny(c.tileX, c.tileY));
    }

    @Override
    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
        Rectangle rect = this.getMultiTile(level, layerID, tileX, tileY).getTileRectangle(tileX, tileY);
        level.lightManager.updateStaticLight(rect.x, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 1, true);
    }

    public TorchHolderInterface getTorchHolder(Level level, int tileX, int tileY) {
        if (!this.canPlaceOnHolders) {
            return null;
        }
        GameObject object = level.getObject(tileX, tileY);
        if (object instanceof TorchHolderInterface) {
            return (TorchHolderInterface)((Object)object);
        }
        return null;
    }

    @Override
    public boolean canBePlacedOn(Level level, int layerID, int x, int y, GameObject newObject, boolean ignoreOtherLayers) {
        return false;
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        TorchHolderInterface holder;
        if (!(layerID != ObjectLayerRegistry.FENCE_AND_TABLE_DECOR || (holder = this.getTorchHolder(level, x, y)) != null && holder.canPlaceTorch(level, x, y))) {
            return "occupied";
        }
        return super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        if (layerID == ObjectLayerRegistry.FENCE_AND_TABLE_DECOR) {
            TorchHolderInterface holder = this.getTorchHolder(level, x, y);
            return holder != null && holder.canPlaceTorch(level, x, y);
        }
        return super.isValid(level, layerID, x, y);
    }
}

