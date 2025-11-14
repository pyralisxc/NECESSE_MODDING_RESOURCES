/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SwampSporeObjectEntity;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SwampSporeObject
extends GameObject {
    public static int[] frameTimes = new int[]{1000, 1000, 1000, 1000, 150, 150, 150, 150};
    public static int totalFrameTime = Arrays.stream(frameTimes).sum();
    public static int burstTime = 4000;
    public GameTexture texture;

    public SwampSporeObject() {
        super(new Rectangle(0, 0, 32, 32));
        this.mapColor = new Color(88, 37, 133);
        this.displayMapTooltip = true;
        this.objectHealth = 300;
        this.toolType = ToolType.UNBREAKABLE;
        this.isLightTransparent = true;
        this.lightHue = 310.0f;
        this.lightSat = 0.5f;
        this.lightLevel = 50;
        this.hoverHitbox = new Rectangle(0, -24, 32, 56);
    }

    @Override
    public List<Rectangle> getProjectileCollisions(Level level, int x, int y, int rotation) {
        return Collections.emptyList();
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/swampspore");
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return new LootTable(LootItem.between("swampsludge", 2, 5).splitItems(5));
    }

    @Override
    public void spawnDestroyedParticles(Level level, int tileX, int tileY) {
        super.spawnDestroyedParticles(level, tileX, tileY);
        for (int i = 0; i < 6; ++i) {
            level.entityManager.addParticle(new FleshParticle(level, this.texture, i, 2, 32, (float)(tileX * 32) + 16.0f, (float)(tileY * 32) + 16.0f, 20.0f, 0.0f, 0.0f), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - 16;
        int drawY = camera.getTileDrawY(tileY) - 32;
        long currentTime = level.getTime() + GameObject.getTileSeed(tileX, tileY, 52);
        int frame = GameUtils.getAnim(Math.floorMod(currentTime, (long)totalFrameTime), frameTimes);
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(frame, 0, 64).light(light).pos(drawX, drawY);
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
        int drawX = camera.getTileDrawX(tileX) - 16;
        int drawY = camera.getTileDrawY(tileY) - 32;
        long currentTime = level.getTime() + GameObject.getTileSeed(tileX, tileY, 52);
        int frame = GameUtils.getAnim(Math.floorMod(currentTime, (long)totalFrameTime), frameTimes);
        this.texture.initDraw().sprite(frame, 0, 64).light(light).alpha(alpha).draw(drawX, drawY);
    }

    @Override
    public void onMouseHover(Level level, int x, int y, GameCamera camera, PlayerMob perspective, boolean debug) {
        super.onMouseHover(level, x, y, camera, perspective, debug);
        GameTooltipManager.addTooltip(new StringTooltips(this.getDisplayName()), TooltipLocation.INTERACT_FOCUS);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new SwampSporeObjectEntity(level, x, y);
    }
}

