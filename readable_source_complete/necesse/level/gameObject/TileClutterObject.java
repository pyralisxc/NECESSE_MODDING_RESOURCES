/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TileClutterObject
extends GameObject {
    protected String textureName;
    public ObjectDamagedTextureArray texture;
    private final GameRandom drawRandom;

    public TileClutterObject(String textureName, Color mapColor) {
        this.textureName = textureName;
        this.toolType = ToolType.ALL;
        this.mapColor = mapColor;
        this.stackSize = 250;
        this.objectHealth = 10;
        this.canPlaceOnShore = true;
        this.isLightTransparent = true;
        this.displayMapTooltip = true;
        this.hoverHitbox = new Rectangle(2, 2, 28, 28);
        this.drawRandom = new GameRandom();
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int sprite;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(TileClutterObject.getTileSeed(tileX, tileY, 512)).nextInt(texture.getWidth() / 32);
        }
        TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(sprite, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY);
        tileList.add(tm -> drawOptions.draw());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int sprite;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(TileClutterObject.getTileSeed(tileX, tileY, 512)).nextInt(texture.getWidth() / 32);
        }
        texture.initDraw().sprite(sprite, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY);
    }
}

