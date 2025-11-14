/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.StaticMultiObject;
import necesse.level.maps.Level;

public class CowSkeletonObject
extends StaticMultiObject {
    protected ParticleTypeSwitcher particleTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC, Particle.GType.CRITICAL);

    protected CowSkeletonObject(int multiX, int multiY, int multiWidth, int multiHeight, int[] multiIDs, Rectangle fullCollision) {
        super(multiX, multiY, multiWidth, multiHeight, multiIDs, fullCollision, "cowskeleton");
        this.mapColor = new Color(158, 101, 32);
        this.objectHealth = 50;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.setItemCategory("objects", "misc");
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return new LootTable();
    }

    @Override
    public void spawnDestroyedParticles(Level level, int tileX, int tileY) {
        super.spawnDestroyedParticles(level, tileX, tileY);
        for (int i = 0; i < 10; ++i) {
            level.entityManager.addParticle(tileX * 32 + GameRandom.globalRandom.getIntBetween(0, 32), tileY * 32 + GameRandom.globalRandom.getIntBetween(0, 32), this.particleTypeSwitcher.next()).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 12)).movesFriction(GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), GameRandom.globalRandom.getFloatBetween(-3.0f, -8.0f), 0.05f).color(new Color(16774102)).sizeFades(10, 20).lifeTime(2000);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        GameSprite sprite = new GameSprite(texture, 0, 0, 64);
        final DrawOptions options = this.getMultiTextureDrawOptions(sprite, level, tileX, tileY, camera);
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
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        GameSprite sprite = new GameSprite(texture, 0, 0, 64);
        this.drawMultiTexturePreview(sprite, tileX, tileY, alpha, camera);
    }

    public static int[] registerCowSkeleton() {
        int[] ids = new int[4];
        Rectangle collision = new Rectangle(16, 20, 20, 28);
        ids[0] = ObjectRegistry.registerObject("cowskeleton", new CowSkeletonObject(0, 0, 2, 2, ids, collision), 0.0f, true);
        ids[1] = ObjectRegistry.registerObject("cowskeleton2", new CowSkeletonObject(1, 0, 2, 2, ids, collision), 0.0f, false);
        ids[2] = ObjectRegistry.registerObject("cowskeleton3", new CowSkeletonObject(0, 1, 2, 2, ids, collision), 0.0f, false);
        ids[3] = ObjectRegistry.registerObject("cowskeleton4", new CowSkeletonObject(1, 1, 2, 2, ids, collision), 0.0f, false);
        return ids;
    }
}

