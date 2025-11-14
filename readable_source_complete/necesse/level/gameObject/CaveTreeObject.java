/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.TreeObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CaveTreeObject
extends TreeObject {
    public CaveTreeObject(String textureName, String logStringID, String saplingStringID, Color mapColor, int leavesCenterWidth, int leavesMinHeight, int leavesMaxHeight, String leavesTextureName) {
        super(textureName, logStringID, saplingStringID, mapColor, leavesCenterWidth, leavesMinHeight, leavesMaxHeight, leavesTextureName);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record((PerformanceTimerManager)tickManager, "treeSetup", () -> {
            boolean mirror;
            GameLight light = level.getLightLevel(tileX, tileY);
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            float alpha = 1.0f;
            if (perspective != null && !Settings.hideUI && !Settings.hideCursor) {
                Rectangle alphaRec = new Rectangle(tileX * 32 - 48, tileY * 32 - 100, 128, 100);
                if (perspective.getCollision().intersects(alphaRec)) {
                    alpha = 0.5f;
                } else if (alphaRec.contains(camera.getX() + WindowManager.getWindow().mousePos().sceneX, camera.getY() + WindowManager.getWindow().mousePos().sceneY)) {
                    alpha = 0.5f;
                }
            }
            int spriteRes = 128;
            GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
            int spriteX = 0;
            if (texture.getWidth() > spriteRes && level.getTileID(tileX, tileY) == TileRegistry.snowID) {
                spriteX = 1;
            } else if (texture.getWidth() > spriteRes * 2 && level.getTileID(tileX, tileY) == TileRegistry.basaltRockID) {
                spriteX = 2;
            }
            int spritesHeight = texture.getHeight() / spriteRes;
            int spriteY = 0;
            GameRandom gameRandom = this.drawRandom;
            synchronized (gameRandom) {
                this.drawRandom.setSeed(CaveTreeObject.getTileSeed(tileX, tileY));
                if (spritesHeight > 1) {
                    spriteY = this.drawRandom.nextInt(spritesHeight);
                }
                mirror = this.drawRandom.nextBoolean();
            }
            Consumer<TextureDrawOptionsPositionMod> waveChange = GameResources.waveShader.setupGrassWaveMod(level, tileX, tileY, this.weaveTime, this.weaveAmount, 2, this.drawRandom, CaveTreeObject.getTileSeed(tileX, tileY, 0), mirror, 3.0f);
            final TextureDrawOptionsEnd options = ((TextureDrawOptionsEnd)texture.initDraw().sprite(spriteX, spriteY, spriteRes).alpha(alpha).light(light).mirror(mirror, false).addPositionMod((Consumer)waveChange)).pos(drawX - 48, drawY - 96);
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return 16;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "treeDraw", () -> options.draw());
                }
            });
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        boolean mirror;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int spriteRes = 128;
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int spriteX = 0;
        if (texture.getWidth() > spriteRes && level.getTileID(tileX, tileY) == TileRegistry.snowID) {
            spriteX = 1;
        } else if (texture.getWidth() > spriteRes * 2 && level.getTileID(tileX, tileY) == TileRegistry.basaltRockID) {
            spriteX = 2;
        }
        int spritesHeight = texture.getHeight() / spriteRes;
        int spriteY = 0;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(CaveTreeObject.getTileSeed(tileX, tileY));
            if (spritesHeight > 1) {
                spriteY = this.drawRandom.nextInt(spritesHeight);
            }
            mirror = this.drawRandom.nextBoolean();
        }
        texture.initDraw().sprite(spriteX, spriteY, spriteRes).alpha(alpha).light(light).mirror(mirror, false).draw(drawX - 48, drawY - 96);
    }
}

