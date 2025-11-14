/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
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
import necesse.level.gameObject.CrystalClusterRObject;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.StaticMultiTile;

public class CrystalClusterObject
extends GameObject {
    protected int counterID;
    protected String dropItem;
    protected int minDropAmount;
    protected int maxDropAmount;
    protected int placedDropAmount;
    private final String textureName;
    public ObjectDamagedTextureArray texture;
    protected final GameRandom drawRandom;

    public static void registerCrystalCluster(String textureName, Color mapColor, float glowHue, String dropItem, int minDropAmount, int maxDropAmount, int placedDropAmount, float brokerValue, boolean isObtainable, String ... category) {
        CrystalClusterObject o1 = new CrystalClusterObject(textureName, mapColor, glowHue, dropItem, minDropAmount, maxDropAmount, placedDropAmount, category);
        int id1 = ObjectRegistry.registerObject(textureName, o1, brokerValue, isObtainable);
        CrystalClusterRObject o2 = new CrystalClusterRObject(textureName, mapColor, glowHue);
        o1.counterID = ObjectRegistry.registerObject(textureName + "r", o2, 0.0f, false);
        o2.counterID = id1;
    }

    public static void registerCrystalCluster(String textureName, Color mapColor, float glowHue, String dropItem, float brokerValue, boolean isObtainable, String ... category) {
        CrystalClusterObject.registerCrystalCluster(textureName, mapColor, glowHue, dropItem, 2, 3, 2, brokerValue, isObtainable, category);
    }

    protected CrystalClusterObject(String textureName, Color mapColor, float glowHue, String dropItem, int minDropAmount, int maxDropAmount, int placedDropAmount, String ... category) {
        super(new Rectangle(14, 14, 18, 10));
        this.mapColor = mapColor;
        this.textureName = textureName;
        this.dropItem = dropItem;
        this.minDropAmount = minDropAmount;
        this.maxDropAmount = maxDropAmount;
        this.placedDropAmount = placedDropAmount;
        this.drawRandom = new GameRandom();
        this.isLightTransparent = true;
        this.canPlaceOnLiquid = false;
        this.lightLevel = 150;
        this.lightSat = 1.0f;
        this.lightHue = glowHue;
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "landscaping", "rocksandores");
            this.setCraftingCategory("objects", "landscaping", "rocksandores");
        }
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        if (this.dropItem != null && this.maxDropAmount > 0) {
            return new LootTable(LootItem.between(this.dropItem, this.minDropAmount, this.maxDropAmount).splitItems(5));
        }
        return new LootTable();
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", this.textureName);
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new StaticMultiTile(0, 0, 2, 1, true, this.getID(), this.counterID);
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
            sprite = this.drawRandom.seeded(CrystalClusterObject.getTileSeed(tileX, tileY)).nextInt(texture.getWidth() / 64);
        }
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(sprite * 2, 0, 32, texture.getHeight()).light(light.minLevelCopy(150.0f)).pos(drawX, drawY - texture.getHeight() + 32);
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
            sprite = this.drawRandom.seeded(CrystalClusterObject.getTileSeed(tileX, tileY)).nextInt(texture.getWidth() / 64);
        }
        texture.initDraw().sprite(sprite * 2, 0, 32, texture.getHeight()).light(light.minLevelCopy(150.0f)).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.crystalHit1, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16).volume(2.0f).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.1f)));
    }
}

