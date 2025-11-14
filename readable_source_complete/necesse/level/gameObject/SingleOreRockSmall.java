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
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.MergeFunction;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SingleOreRockSmall
extends GameObject {
    public String droppedStone;
    public long oreHash;
    public String rockLocalizationKey;
    public String oreLocalizationKey;
    public String rockTextureName;
    public String oreTextureName;
    public ObjectDamagedTextureArray rockTexture;
    public ObjectDamagedTextureArray oreTexture;
    public String droppedOre;
    public int droppedOreMin;
    public int droppedOreMax;
    public int placedDroppedOre;
    protected final GameRandom drawRandom;

    public SingleOreRockSmall(String droppedStone, float toolTier, String rockLocalizationKey, String oreLocalizationKey, String rockTextureName, String oreTextureName, Color oreColor, String droppedOre, int droppedOreMin, int droppedOreMax, int placedDroppedOre, boolean isIncursionExtractionObject, String ... category) {
        super(new Rectangle(2, 10, 28, 20));
        this.droppedStone = droppedStone;
        this.toolTier = toolTier;
        this.rockLocalizationKey = rockLocalizationKey;
        this.oreLocalizationKey = oreLocalizationKey;
        this.rockTextureName = rockTextureName;
        this.oreTextureName = oreTextureName;
        this.mapColor = oreColor;
        this.droppedOre = droppedOre;
        this.droppedOreMin = droppedOreMin;
        this.droppedOreMax = droppedOreMax;
        this.placedDroppedOre = placedDroppedOre;
        this.isIncursionExtractionObject = isIncursionExtractionObject;
        this.oreHash = oreColor.hashCode();
        this.isOre = true;
        this.displayMapTooltip = true;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
        this.canPlaceOnLiquid = true;
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "landscaping", "rocksandores");
            this.setCraftingCategory("objects", "landscaping", "rocksandores");
        }
    }

    public SingleOreRockSmall(String droppedStone, float toolTier, String rockLocalizationKey, String oreLocalizationKey, String rockTextureName, String oreTextureName, Color oreColor, String droppedOre, int droppedOreMin, int droppedOreMax, int placedDroppedOre, String ... category) {
        this(droppedStone, toolTier, rockLocalizationKey, oreLocalizationKey, rockTextureName, oreTextureName, oreColor, droppedOre, droppedOreMin, droppedOreMax, placedDroppedOre, true, category);
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "rockoreformat", "rock", new LocalMessage("object", this.rockLocalizationKey), "ore", new LocalMessage("object", this.oreLocalizationKey));
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.rockTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.rockTextureName);
        this.oreTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.oreTextureName);
    }

    @Override
    public GameTexture generateItemTexture() {
        GameTexture rockTexture = new GameTexture(GameTexture.fromFile("items/" + this.rockTextureName));
        GameTexture oreTexture = GameTexture.fromFile("items/" + this.oreTextureName);
        rockTexture.merge(oreTexture, 0, 0, MergeFunction.NORMAL);
        rockTexture.makeFinal();
        return rockTexture;
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        LootTable lootTable = new LootTable(LootItem.between(this.droppedOre, this.droppedOreMin, this.droppedOreMax).splitItems(5));
        if (this.droppedStone != null) {
            lootTable.items.add(LootItem.between(this.droppedStone, 15, 25).splitItems(5));
        }
        return lootTable;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getRandomYOffset(int tileX, int tileY) {
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            return (int)((this.drawRandom.seeded(SingleOreRockSmall.getTileSeed(tileX, tileY, 1)).nextFloat() * 2.0f - 1.0f) * 8.0f) - 4;
        }
    }

    @Override
    protected Rectangle getCollision(Level level, int x, int y, int rotation) {
        Rectangle collision = super.getCollision(level, x, y, rotation);
        collision.y += this.getRandomYOffset(x, y);
        return collision;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        boolean mirror;
        int sprite;
        GameLight light;
        GameLight oreLight = light = level.getLightLevel(tileX, tileY);
        if (this.isIncursionExtractionObject && level.isIncursionLevel) {
            float anim = GameUtils.getAnimFloatContinuous(Math.abs(level.getTime() + 2500L * this.oreHash), 2500);
            oreLight = light.minLevelCopy(GameMath.lerp(anim, 80, 100));
        }
        boolean hasSpelunker = perspective != null && perspective.buffManager.getModifier(BuffModifiers.SPELUNKER) != false;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture rockTexture = this.rockTexture.getDamagedTexture(this, level, tileX, tileY);
        GameTexture oreTexture = this.oreTexture.getDamagedTexture(this, level, tileX, tileY);
        final int randomYOffset = this.getRandomYOffset(tileX, tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(SingleOreRockSmall.getTileSeed(tileX, tileY)).nextInt(rockTexture.getWidth() / 32);
            mirror = this.drawRandom.nextBoolean();
        }
        final DrawOptionsList drawOptions = new DrawOptionsList();
        drawOptions.add(rockTexture.initDraw().sprite(sprite, 0, 32, rockTexture.getHeight()).mirror(mirror, false).light(light).pos(drawX, (drawY += randomYOffset) - rockTexture.getHeight() + 32));
        drawOptions.add(oreTexture.initDraw().sprite(sprite, 0, 32, oreTexture.getHeight()).mirror(mirror, false).spelunkerLight(oreLight, hasSpelunker, this.oreHash, level).pos(drawX, drawY - oreTexture.getHeight() + 32));
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16 + randomYOffset;
            }

            @Override
            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        boolean mirror;
        int sprite;
        GameLight light;
        GameLight oreLight = light = level.getLightLevel(tileX, tileY);
        if (this.isIncursionExtractionObject && level.isIncursionLevel) {
            float anim = GameUtils.getAnimFloatContinuous(Math.abs(level.getTime() + 2500L * this.oreHash), 2500);
            oreLight = light.minLevelCopy(GameMath.lerp(anim, 80, 100));
        }
        boolean hasSpelunker = player != null && player.buffManager.getModifier(BuffModifiers.SPELUNKER) != false;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture rockTexture = this.rockTexture.getDamagedTexture(0.0f);
        GameTexture oreTexture = this.oreTexture.getDamagedTexture(0.0f);
        int randomYOffset = this.getRandomYOffset(tileX, tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(SingleOreRockSmall.getTileSeed(tileX, tileY)).nextInt(rockTexture.getWidth() / 32);
            mirror = this.drawRandom.nextBoolean();
        }
        rockTexture.initDraw().sprite(sprite, 0, 32, rockTexture.getHeight()).mirror(mirror, false).light(light).alpha(alpha).draw(drawX, (drawY += randomYOffset) - rockTexture.getHeight() + 32);
        oreTexture.initDraw().sprite(sprite, 0, 32, oreTexture.getHeight()).mirror(mirror, false).spelunkerLight(oreLight, hasSpelunker, this.oreHash, level).alpha(alpha).draw(drawX, drawY - oreTexture.getHeight() + 32);
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        return !level.isShore(x, y);
    }
}

