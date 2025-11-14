/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

class CrystalClusterRObject
extends GameObject {
    protected int counterID;
    private final String textureName;
    public ObjectDamagedTextureArray texture;
    protected final GameRandom drawRandom;

    protected CrystalClusterRObject(String textureName, Color mapColor, float glowHue) {
        super(new Rectangle(0, 14, 18, 10));
        this.textureName = textureName;
        this.mapColor = mapColor;
        this.drawRandom = new GameRandom();
        this.isLightTransparent = true;
        this.canPlaceOnLiquid = false;
        this.lightLevel = 150;
        this.lightSat = 0.3f;
        this.lightHue = glowHue;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(1, 0, 2, 1, false, this.counterID, this.getID());
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
            sprite = this.drawRandom.seeded(CrystalClusterRObject.getTileSeed(tileX - 1, tileY)).nextInt(texture.getWidth() / 64);
        }
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(sprite * 2 + 1, 0, 32, texture.getHeight()).light(light.minLevelCopy(150.0f)).pos(drawX, drawY - texture.getHeight() + 32);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int sprite;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(CrystalClusterRObject.getTileSeed(tileX - 1, tileY)).nextInt(texture.getWidth() / 64);
        }
        texture.initDraw().sprite(sprite * 2 + 1, 0, 32, texture.getHeight()).light(light.minLevelCopy(150.0f)).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.crystalHit1, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16).volume(2.0f).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
    }
}

