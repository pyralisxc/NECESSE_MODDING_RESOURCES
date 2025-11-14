/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class SwampRootObject
extends StaticMultiObject {
    private final GameRandom drawRandom;

    protected SwampRootObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "swamproot");
        this.mapColor = new Color(109, 68, 29);
        this.objectHealth = 50;
        this.toolType = ToolType.AXE;
        this.displayMapTooltip = true;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
        this.setItemCategory("objects", "landscaping", "plants");
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return new LootTable(LootItem.between("willowlog", 1, 2).splitItems(2));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int spriteX;
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        LevelObject masterObject = this.getMultiTile(level.getObjectRotation(tileX, tileY)).getMasterLevelObject(level, 0, tileX, tileY).orElse(null);
        long tileSeed = masterObject == null ? SwampRootObject.getTileSeed(tileX, tileY) : SwampRootObject.getTileSeed(masterObject.tileX, masterObject.tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            spriteX = this.drawRandom.seeded(tileSeed).nextInt(4);
        }
        GameSprite sprite = new GameSprite(texture, spriteX, 0, 64);
        DrawOptions options = this.getMultiTextureDrawOptions(sprite, level, tileX, tileY, camera);
        tileList.add(tm -> options.draw());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int spriteX;
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        Point masterTile = this.getMultiTile(level.getObjectRotation(tileX, tileY)).getMasterTilePos(tileX, tileY).orElse(null);
        long tileSeed = masterTile == null ? SwampRootObject.getTileSeed(tileX, tileY) : SwampRootObject.getTileSeed(masterTile.x, masterTile.y);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            spriteX = this.drawRandom.seeded(tileSeed).nextInt(4);
        }
        GameSprite sprite = new GameSprite(texture, spriteX, 0, 64);
        this.drawMultiTexturePreview(sprite, tileX, tileY, alpha, camera);
    }

    public static int[] registerSwampRoot() {
        int[] ids = new int[4];
        Rectangle collision = new Rectangle(0, 0, 0, 0);
        ids[0] = ObjectRegistry.registerObject("swamproot", new SwampRootObject(0, 0, 2, 2, ids, collision), 0.0f, true);
        ids[1] = ObjectRegistry.registerObject("swamproot2", new SwampRootObject(1, 0, 2, 2, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject("swamproot3", new SwampRootObject(0, 1, 2, 2, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject("swamproot4", new SwampRootObject(1, 1, 2, 2, ids, collision), 0.0f, false);
        return ids;
    }
}

