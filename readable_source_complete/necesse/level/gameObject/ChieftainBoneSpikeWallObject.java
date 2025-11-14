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
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ChieftainBoneSpikeWallObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ChieftainBoneSpikeWallObject
extends GameObject {
    protected int yOffset = -3;
    protected final GameRandom drawRandom;

    public ChieftainBoneSpikeWallObject(Color mapColor, ToolType toolType) {
        super(new Rectangle(32, 32));
        this.mapColor = mapColor;
        this.toolType = toolType;
        this.isLightTransparent = true;
        this.stackSize = 500;
        this.drawRandom = new GameRandom();
        this.canPlaceOnShore = true;
        this.replaceRotations = false;
        this.hoverHitbox = new Rectangle(0, -32, 32, 64);
    }

    @Override
    protected Rectangle getCollision(Level level, int x, int y, int rotation) {
        return super.getCollision(level, x, y, rotation);
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        float heightMultiplier;
        super.tickEffect(level, layerID, tileX, tileY);
        ChieftainBoneSpikeWallObjectEntity objectEntity = this.getCurrentObjectEntity(level, tileX, tileY, ChieftainBoneSpikeWallObjectEntity.class);
        if (objectEntity != null && (heightMultiplier = objectEntity.getAnimationHeightProgress()) > 0.0f && heightMultiplier < 1.0f && GameRandom.globalRandom.getChance(heightMultiplier)) {
            int startHeight = -8 + GameRandom.globalRandom.nextInt(16);
            level.entityManager.addParticle(tileX * 32 + GameRandom.globalRandom.getIntBetween(0, 32), tileY * 32 + 32, Particle.GType.COSMETIC).sizeFades(8, 12).smokeColor().heightMoves(startHeight, startHeight + 32).lifeTime(1000);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        boolean mirror;
        GameLight light = level.getLightLevel(tileX, tileY);
        GameTexture texture = GameResources.smallBoneSpikes;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) + 32;
        int spriteRes = 64;
        int spritesWidth = texture.getWidth() / spriteRes;
        int spriteX = 0;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(ChieftainBoneSpikeWallObject.getTileSeed(tileX, tileY));
            if (spritesWidth > 1) {
                spriteX = this.drawRandom.nextInt(spritesWidth);
            }
            mirror = this.drawRandom.nextBoolean();
        }
        ChieftainBoneSpikeWallObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY, ChieftainBoneSpikeWallObjectEntity.class);
        float heightProgress = 1.0f;
        if (objectEntity != null) {
            heightProgress = objectEntity.getAnimationHeightProgress();
        }
        int endY = (int)(heightProgress * (float)texture.getHeight());
        final TextureDrawOptionsEnd options = texture.initDraw().section(spriteX * spriteRes, spriteX * spriteRes + spriteRes, 0, endY).light(light).mirror(mirror, false).pos(drawX - 16, drawY - endY);
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
        boolean mirror;
        GameLight light = level.getLightLevel(tileX, tileY);
        GameTexture texture = GameResources.smallBoneSpikes;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY) - texture.getHeight() + 32;
        int spriteRes = 64;
        int spritesWidth = texture.getWidth() / spriteRes;
        int spriteX = 0;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(ChieftainBoneSpikeWallObject.getTileSeed(tileX, tileY));
            if (spritesWidth > 1) {
                spriteX = this.drawRandom.nextInt(spritesWidth);
            }
            mirror = this.drawRandom.nextBoolean();
        }
        texture.initDraw().sprite(spriteX, 0, spriteRes).light(light).mirror(mirror, false).alpha(alpha).draw(drawX - 16, drawY);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new ChieftainBoneSpikeWallObjectEntity(level, x, y);
    }

    @Override
    public GameMessage preventsLadderPlacement(Level level, int tileX, int tileY) {
        return new LocalMessage("misc", "blockingexit");
    }
}

