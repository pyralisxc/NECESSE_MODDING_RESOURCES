/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class PearlescentShardObject
extends GameObject {
    protected String textureName;
    public GameTexture texture;
    protected final GameRandom drawRandom;

    public PearlescentShardObject(String textureName, Color mapColor) {
        super(new Rectangle(0, -14, 32, 46));
        this.textureName = textureName;
        this.mapColor = mapColor;
        this.isIncursionExtractionObject = true;
        this.isOre = true;
        this.displayMapTooltip = true;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
        this.canPlaceOnLiquid = true;
        this.setItemCategory("objects", "landscaping", "incursionrocksandores");
        this.setCraftingCategory("objects", "landscaping", "incursionrocksandores");
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", this.textureName);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/" + this.textureName);
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return new LootTable(LootItem.between("pearlescentdiamond", 1, 2));
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        this.spawnShardParticles(level, GameMath.getLevelCoordinate(tileX) + 16, GameMath.getLevelCoordinate(tileY) - 32);
        float hueMod = (float)level.getWorldEntity().getLocalTime() / 50.0f % 240.0f;
        float glowHue = hueMod < 120.0f ? hueMod + 200.0f : 440.0f - hueMod;
        level.lightManager.refreshParticleLight(tileX, tileY, glowHue, 1.0f, 200);
    }

    public float getDesiredHeight(Level level, int tileX, int tileY) {
        int seededOffset = this.drawRandom.seeded(PearlescentShardObject.getTileSeed(tileX, tileY)).nextInt(3000);
        float perc = GameUtils.getAnimFloat(level.getWorldEntity().getTime() + (long)seededOffset, 3000);
        return GameMath.sin(perc * 360.0f) * 5.0f;
    }

    public void spawnShardParticles(Level level, int x, int y) {
        GameRandom random = GameRandom.globalRandom;
        AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
        float distance = 30.0f;
        if (random.getChance(0.05f)) {
            level.entityManager.addParticle((float)x + GameMath.sin(currentAngle.get().floatValue()) * distance, (float)y + GameMath.cos(currentAngle.get().floatValue()) * distance, Particle.GType.CRITICAL).sprite(GameResources.pearlescentShardParticles.sprite(random.nextInt(4), 0, 18, 24)).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 5.0f / 250.0f), Float::sum).floatValue();
                pos.x = (float)x + GameMath.sin(angle) * distance;
                pos.y = (float)(y + 65) + ((float)x - pos.x) - angle / 36.0f + GameMath.cos(angle) / 2.0f * distance;
            }).lifeTime(5000).height(15.0f).ignoreLight(true).sizeFadesInAndOut(18, 24, 500, 500);
            level.entityManager.addParticle((float)x + GameMath.sin(currentAngle.get().floatValue()) * distance, (float)y + GameMath.cos(currentAngle.get().floatValue()) * distance, Particle.GType.CRITICAL).sprite(GameResources.pearlescentShardParticles.sprite(random.nextInt(4), 0, 18, 24)).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 5.0f / 250.0f), Float::sum).floatValue();
                pos.x = (float)x + GameMath.sin(angle) * distance;
                pos.y = (float)(y + 65) - ((float)x - pos.x) - angle / 36.0f + GameMath.cos(angle) / 2.0f * distance;
            }).lifeTime(5000).height(15.0f).ignoreLight(true).sizeFadesInAndOut(18, 24, 1000, 1000);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int height;
        int sprite;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(PearlescentShardObject.getTileSeed(tileX, tileY)).nextInt(this.texture.getWidth() / 32);
            height = (int)this.getDesiredHeight(level, tileX, tileY);
        }
        final TextureDrawOptionsEnd options = this.texture.initDraw().sprite(sprite, 0, 32, this.texture.getHeight()).pos(drawX, drawY - this.texture.getHeight() + 32 + height);
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
        int height;
        int sprite;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            sprite = this.drawRandom.seeded(PearlescentShardObject.getTileSeed(tileX, tileY)).nextInt(this.texture.getWidth() / 32);
            height = (int)this.getDesiredHeight(level, tileX, tileY);
        }
        this.texture.initDraw().sprite(sprite, 0, 32, this.texture.getHeight()).alpha(alpha).draw(drawX, drawY - this.texture.getHeight() + 32 + height);
    }
}

