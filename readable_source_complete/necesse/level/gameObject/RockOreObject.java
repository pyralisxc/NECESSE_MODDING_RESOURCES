/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.RockObject;
import necesse.level.maps.Level;

public class RockOreObject
extends RockObject {
    protected String oreMaskTextureName;
    protected String oreTextureName;
    public RockObject parentRock;
    public ObjectDamagedTextureArray[] oreTextures;
    public long oreHash;
    public String droppedOre;
    public int droppedOreMin;
    public int droppedOreMax;
    public int placedDroppedOre;
    protected final GameRandom oreTextureRandom;

    public RockOreObject(RockObject parentRock, String oreMaskTextureName, String oreTextureName, Color oreColor, String droppedOre, int droppedOreMin, int droppedOreMax, int placedDroppedOre, boolean isIncursionExtractionObject, String ... category) {
        super("rock", parentRock.mapColor, parentRock.droppedStone, new String[0]);
        this.parentRock = parentRock;
        this.toolTier = parentRock.toolTier;
        this.isOre = true;
        this.oreMaskTextureName = oreMaskTextureName;
        this.oreTextureName = oreTextureName;
        this.mapColor = oreColor;
        this.oreHash = oreColor.hashCode();
        this.droppedOre = droppedOre;
        this.droppedOreMin = droppedOreMin;
        this.droppedOreMax = droppedOreMax;
        this.placedDroppedOre = placedDroppedOre;
        this.oreTextureRandom = new GameRandom();
        this.isIncursionExtractionObject = isIncursionExtractionObject;
        this.displayMapTooltip = true;
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "landscaping", "rocksandores");
            this.setCraftingCategory("objects", "landscaping", "rocksandores");
        }
    }

    public RockOreObject(RockObject parentRock, String oreMaskTextureName, String oreTextureName, Color oreColor, String droppedOre, int droppedOreMin, int droppedOreMax, int placedDroppedOre, String ... category) {
        this(parentRock, oreMaskTextureName, oreTextureName, oreColor, droppedOre, droppedOreMin, droppedOreMax, placedDroppedOre, true, category);
    }

    public RockOreObject(RockObject parentRock, String oreMaskTextureName, String oreTextureName, Color oreColor, String droppedOre, String ... category) {
        this(parentRock, oreMaskTextureName, oreTextureName, oreColor, droppedOre, 1, 3, 2, category);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "rockoreformat", "rock", this.parentRock.getNewLocalization(), "ore", new LocalMessage("object", this.oreTextureName));
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        LootTable out = this.parentRock.getLootTable(level, layerID, tileX, tileY);
        if (this.droppedOre != null) {
            out.items.add(LootItem.between(this.droppedOre, this.droppedOreMin, this.droppedOreMax).splitItems(5));
        }
        return out;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        this.addRockDrawables(list, level, tileX, tileY, this.parentRock.rockTextures.getDamagedTexture(this, level, tileX, tileY), this.getOreTexture(level, tileX, tileY), this.oreHash, tickManager, camera, perspective);
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        LinkedList<LevelSortedDrawable> list = new LinkedList<LevelSortedDrawable>();
        this.addRockDrawables(list, level, tileX, tileY, this.parentRock.rockTextures.getDamagedTexture(0.0f), this.getOreTexture(tileX, tileY, 0.0f), this.oreHash, Float.valueOf(alpha), null, camera, player);
        list.forEach(e -> e.draw(null));
    }

    @Override
    public void loadTextures() {
        GameTexture oreMask;
        super.loadTextures();
        GameTexture oreTemp = GameTexture.fromFile("objects/" + this.oreTextureName);
        this.oreTextures = new ObjectDamagedTextureArray[oreTemp.getWidth() / 32];
        try {
            oreMask = GameTexture.fromFileRaw("objects/" + this.oreMaskTextureName);
        }
        catch (FileNotFoundException e) {
            oreMask = GameTexture.fromFile("objects/oremask");
        }
        for (int i = 0; i < this.oreTextures.length; ++i) {
            GameTexture oreTexture = new GameTexture(oreMask);
            for (int x = 0; x < oreTexture.getWidth(); x += 32) {
                for (int y = 0; y < oreTexture.getHeight(); y += 32) {
                    oreTexture.merge(oreTemp, x, y, i * 32, 0, 32, 32, (cCol, mCol) -> {
                        float cRed = (float)cCol.getRed() / 255.0f;
                        float cGreen = (float)cCol.getGreen() / 255.0f;
                        float cBlue = (float)cCol.getBlue() / 255.0f;
                        float cAlpha = (float)cCol.getAlpha() / 255.0f;
                        float mRed = (float)mCol.getRed() / 255.0f;
                        float mGreen = (float)mCol.getGreen() / 255.0f;
                        float mBlue = (float)mCol.getBlue() / 255.0f;
                        float mAlpha = (float)mCol.getAlpha() / 255.0f;
                        return new Color(cRed * mRed, cGreen * mGreen, cBlue * mBlue, cAlpha * mAlpha);
                    });
                }
            }
            this.oreTextures[i] = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, oreTexture);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected GameTexture getOreTexture(Level level, int tileX, int tileY) {
        int oreTexture;
        GameRandom gameRandom = this.oreTextureRandom;
        synchronized (gameRandom) {
            oreTexture = this.oreTextureRandom.seeded(RockOreObject.getTileSeed(tileX, tileY) * 8629L).nextInt(this.oreTextures.length);
        }
        return this.oreTextures[oreTexture].getDamagedTexture(this, level, tileX, tileY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected GameTexture getOreTexture(int tileX, int tileY, float damagePercent) {
        int oreTexture;
        GameRandom gameRandom = this.oreTextureRandom;
        synchronized (gameRandom) {
            oreTexture = this.oreTextureRandom.seeded(RockOreObject.getTileSeed(tileX, tileY) * 8629L).nextInt(this.oreTextures.length);
        }
        return this.oreTextures[oreTexture].getDamagedTexture(damagePercent);
    }

    @Override
    public GameTexture generateItemTexture() {
        GameTexture oreMask;
        GameTexture oreTemp = GameTexture.fromFile("objects/" + this.oreTextureName);
        try {
            oreMask = GameTexture.fromFileRaw("items/" + this.oreMaskTextureName);
        }
        catch (FileNotFoundException e) {
            oreMask = GameTexture.fromFile("items/oremask");
        }
        GameTexture oreTexture = new GameTexture(oreMask);
        oreTexture.merge(oreTemp, 0, 0, 0, 0, 32, 32, (cCol, mCol) -> {
            float cRed = (float)cCol.getRed() / 255.0f;
            float cGreen = (float)cCol.getGreen() / 255.0f;
            float cBlue = (float)cCol.getBlue() / 255.0f;
            float cAlpha = (float)cCol.getAlpha() / 255.0f;
            float mRed = (float)mCol.getRed() / 255.0f;
            float mGreen = (float)mCol.getGreen() / 255.0f;
            float mBlue = (float)mCol.getBlue() / 255.0f;
            float mAlpha = (float)mCol.getAlpha() / 255.0f;
            return new Color(cRed * mRed, cGreen * mGreen, cBlue * mBlue, cAlpha * mAlpha);
        });
        GameTexture itemTexture = new GameTexture(this.parentRock.generateItemTexture());
        itemTexture.merge(oreTexture, 0, 0, MergeFunction.NORMAL);
        itemTexture.makeFinal();
        return itemTexture;
    }
}

