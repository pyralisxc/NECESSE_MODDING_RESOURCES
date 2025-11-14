/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.level.gameObject.PaintingObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BrokenPaintingObject
extends PaintingObject {
    protected final GameRandom drawRandom = new GameRandom();

    public BrokenPaintingObject(Item.Rarity rarity) {
        super(rarity);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int sortY;
        int rndX;
        GameLight light = level.getLightLevel(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            rndX = this.drawRandom.seeded(BrokenPaintingObject.getTileSeed(tileX, tileY)).nextInt(texture.getWidth() / 32);
        }
        byte rotation = level.getObjectRotation(tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            sortY = 32;
            options.add(texture.initDraw().sprite(rndX, 2, 32, 32).light(light).pos(drawX, drawY + 8));
        } else if (rotation == 1) {
            sortY = 24;
            options.add(texture.initDraw().sprite(rndX, 3, 32, 32).light(light).pos(drawX, drawY - 16));
        } else if (rotation == 2) {
            sortY = 0;
            options.add(texture.initDraw().sprite(rndX, 0, 32, 32).light(light).pos(drawX, drawY - 32));
        } else {
            sortY = 24;
            options.add(texture.initDraw().sprite(rndX, 1, 32, 32).light(light).pos(drawX, drawY - 16));
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return sortY;
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
        int rndX;
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            rndX = this.drawRandom.seeded(BrokenPaintingObject.getTileSeed(tileX, tileY)).nextInt(texture.getWidth() / 32);
        }
        if (rotation == 0) {
            texture.initDraw().sprite(rndX, 2, 32, 32).alpha(alpha).draw(drawX, drawY + 8);
        } else if (rotation == 1) {
            texture.initDraw().sprite(rndX, 3, 32, 32).alpha(alpha).draw(drawX, drawY - 16);
        } else if (rotation == 2) {
            texture.initDraw().sprite(rndX, 0, 32, 32).alpha(alpha).draw(drawX, drawY - 32);
        } else {
            texture.initDraw().sprite(rndX, 1, 32, 32).alpha(alpha).draw(drawX, drawY - 16);
        }
    }
}

