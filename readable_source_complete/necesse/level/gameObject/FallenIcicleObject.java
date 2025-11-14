/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
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
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FallenIcicleObject
extends GameObject {
    public static GameTexture texture;
    public static GameTexture shadowTexture;
    protected ObjectDamagedTextureArray damagedTextureArray;
    protected static final GameRandom drawRandom;

    public FallenIcicleObject() {
        super(new Rectangle(4, 4, 24, 24));
        this.mapColor = new Color(0, 174, 255);
        this.isLightTransparent = true;
        this.stackSize = 500;
        this.setItemCategory("objects", "landscaping", "snowrocksandores");
        this.setCraftingCategory("objects", "landscaping", "snowrocksandores");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        texture = GameTexture.fromFile("objects/fallingicespikes");
        shadowTexture = GameTexture.fromFile("objects/fallingicespikes_shadow");
        this.damagedTextureArray = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, texture);
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.iceHit, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16).pitch(damageDone ? GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f) : 2.0f));
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, level, tileX, tileY, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.damagedTextureArray.getDamagedTexture(this, level, tileX, tileY);
        int sprite = FallenIcicleObject.getGeneratedSpriteIndex(tileX, tileY);
        int endYOffGround = level.objectLayer.isPlayerPlaced(tileX, tileY) ? 23 : FallenIcicleObject.getGeneratedYOffset(tileX, tileY);
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(sprite, 0, 64, 64 - endYOffGround).pos(drawX - 16, drawY - 32 + endYOffGround - 6).light(light);
        TextureDrawOptionsEnd shadowOptions = shadowTexture.initDraw().section(18, 47, 42, 59).posMiddle(drawX + 16, drawY + 23).light(light);
        tileList.add(tm -> shadowOptions.draw());
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
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.damagedTextureArray.getDamagedTexture(this, level, tileX, tileY);
        int sprite = FallenIcicleObject.getGeneratedSpriteIndex(tileX, tileY);
        int endYOffGround = 23;
        shadowTexture.initDraw().section(18, 47, 42, 59).posMiddle(drawX + 16, drawY + 23).light(light).alpha(alpha).draw();
        texture.initDraw().sprite(sprite, 0, 64, 64 - endYOffGround).pos(drawX - 16, drawY - 32 + endYOffGround - 6).light(light).alpha(alpha).draw();
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return new LootTable(LootItem.between("deepsnowstone", 1, 3).splitItems(2));
    }

    public static synchronized int getGeneratedSpriteIndex(int tileX, int tileY) {
        drawRandom.setSeed(FallenIcicleObject.getTileSeed(tileX, tileY));
        return drawRandom.nextInt(4);
    }

    public static synchronized int getGeneratedYOffset(int tileX, int tileY) {
        drawRandom.setSeed(FallenIcicleObject.getTileSeed(tileX, tileY));
        return drawRandom.nextInt(17) - 8 + 25;
    }

    public static synchronized float getStartShadowSize(int tileX, int tileY) {
        drawRandom.setSeed(FallenIcicleObject.getTileSeed(tileX, tileY));
        return drawRandom.getFloatBetween(0.15f, 0.25f);
    }

    public static synchronized Point getStartOffset(int tileX, int tileY) {
        drawRandom.setSeed(FallenIcicleObject.getTileSeed(tileX, tileY));
        return new Point(drawRandom.getIntBetween(-10, 10), drawRandom.getIntBetween(-10, 10));
    }

    static {
        drawRandom = new GameRandom();
    }
}

