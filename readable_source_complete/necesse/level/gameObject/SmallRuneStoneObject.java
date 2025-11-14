/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SmallRuneStoneObject
extends GameObject {
    protected GameTexture texture;
    protected GameTexture lightTexture;
    protected GameTexture shadowTexture;
    protected final GameRandom drawRandom;

    public SmallRuneStoneObject() {
        super(new Rectangle(4, 8, 24, 16));
        this.mapColor = new Color(136, 214, 255);
        this.toolType = ToolType.PICKAXE;
        this.toolTier = 1.0f;
        this.rarity = Item.Rarity.UNCOMMON;
        this.setItemCategory("objects", "landscaping", "plainsrocksandores");
        this.setCraftingCategory("objects", "landscaping", "plainsrocksandores");
        this.objectHealth = 80;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
        this.hoverHitbox = new Rectangle(4, -4, 24, 24);
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return new LootTable(new LootItem("runestone", GameRandom.globalRandom.getIntBetween(1, 2)));
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/smallrunestone");
        this.lightTexture = GameTexture.fromFile("objects/smallrunestone_light");
        this.shadowTexture = GameTexture.fromFile("objects/smallrunestone_shadow");
    }

    public float getDesiredHeight(Level level, int tileX, int tileY) {
        int seededOffset = this.drawRandom.seeded(SmallRuneStoneObject.getTileSeed(tileX, tileY)).nextInt(3000);
        float perc = GameUtils.getAnimFloat(level.getWorldEntity().getTime() + (long)seededOffset, 3000);
        return GameMath.sin(perc * 360.0f) * 5.0f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        boolean mirrored;
        int sprite;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(SmallRuneStoneObject.getTileSeed(tileX, tileY)).nextInt(this.texture.getWidth() / 32);
            mirrored = this.drawRandom.seeded(SmallRuneStoneObject.getTileSeed(tileX, tileY, 5)).nextBoolean();
        }
        boolean spelunker = perspective != null && perspective.buffManager.getModifier(BuffModifiers.SPELUNKER) != false;
        final TextureDrawOptionsEnd drawOptions = this.texture.initDraw().sprite(sprite, 0, 32, 64).mirror(mirrored, false).spelunkerLight(light, spelunker, this.getID(), level).pos(drawX, drawY - (int)this.getDesiredHeight(level, tileX, tileY) - (this.texture.getHeight() - 32));
        final TextureDrawOptionsEnd lightDrawOptions = this.lightTexture.initDraw().sprite(sprite, 0, 32, 64).mirror(mirrored, false).light(light.minLevelCopy(150.0f)).pos(drawX, drawY - (int)this.getDesiredHeight(level, tileX, tileY) - (this.texture.getHeight() - 32));
        final TextureDrawOptionsEnd shadowDrawOptions = this.shadowTexture.initDraw().sprite(sprite, 0, 32, 64).mirror(mirrored, false).spelunkerLight(light, spelunker, this.getID(), level).pos(drawX, drawY - (this.texture.getHeight() - 32));
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                shadowDrawOptions.draw();
                drawOptions.draw();
                lightDrawOptions.draw();
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        boolean mirrored;
        int sprite;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(SmallRuneStoneObject.getTileSeed(tileX, tileY)).nextInt(this.texture.getWidth() / 32);
            mirrored = this.drawRandom.seeded(SmallRuneStoneObject.getTileSeed(tileX, tileY, 5)).nextBoolean();
        }
        this.texture.initDraw().sprite(sprite, 0, 32, 64).mirror(mirrored, false).alpha(alpha).draw(drawX, drawY - (this.texture.getHeight() - 32));
    }

    @Override
    public boolean shouldSnapSmartMining(Level level, int x, int y) {
        return true;
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        return !level.isShore(x, y);
    }
}

