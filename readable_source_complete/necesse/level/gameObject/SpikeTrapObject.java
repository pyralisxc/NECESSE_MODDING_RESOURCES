/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SpikeTrapObjectEntity;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpikeTrapObject
extends GameObject {
    public GameTexture texture;
    public GameTexture spikeTexture;
    public int animationDuration = 1000;

    public SpikeTrapObject() {
        super(new Rectangle(0, 0, 0, 0));
        this.showsWire = true;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.setItemCategory("objects", "traps");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/spiketrap");
        this.spikeTexture = GameTexture.fromFile("objects/spiketrapspikes");
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        long timeSinceActivated;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        TextureDrawOptionsEnd baseOptions = this.texture.initDraw().sprite(0, 0, 32, 64).light(light).pos(drawX, drawY - 32);
        tileList.add(tm -> baseOptions.draw());
        ObjectEntity ent = level.entityManager.getObjectEntity(tileX, tileY);
        if (ent instanceof TrapObjectEntity && (timeSinceActivated = ((TrapObjectEntity)ent).getTimeSinceActivated()) < (long)this.animationDuration) {
            int timePerFrame = this.animationDuration / 9;
            int frameIndex = (int)(timeSinceActivated / (long)timePerFrame);
            final TextureDrawOptionsEnd spikeOptions = this.spikeTexture.initDraw().sprite(frameIndex, 0, 32, 64).light(light).pos(drawX, drawY - 32);
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return -4;
                }

                @Override
                public void draw(TickManager tickManager) {
                    spikeOptions.draw();
                }
            });
        }
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        this.texture.initDraw().sprite(0, 0, 32, 64).alpha(alpha).draw(drawX, drawY - 32);
    }

    @Override
    public Color getMapColor(Level level, int tileX, int tileY) {
        return level.getTile(tileX, tileY).getMapColor(level, tileX, tileY).darker();
    }

    @Override
    public void onWireUpdate(Level level, int layerID, int tileX, int tileY, int wireID, boolean active) {
        ObjectEntity ent;
        if (active && level.isServer() && (ent = level.entityManager.getObjectEntity(tileX, tileY)) != null) {
            ((TrapObjectEntity)ent).triggerTrap(wireID, level.getObjectRotation(tileX, tileY));
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new SpikeTrapObjectEntity(level, x, y, this.animationDuration);
    }
}

