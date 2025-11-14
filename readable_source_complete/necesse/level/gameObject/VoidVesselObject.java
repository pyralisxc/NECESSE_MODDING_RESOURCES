/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
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
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class VoidVesselObject
extends GameObject {
    protected GameTexture texture;
    protected GameTexture lightTexture;
    protected GameTexture shadowTexture;
    protected final GameRandom drawRandom;

    public VoidVesselObject() {
        super(new Rectangle(4, 4, 24, 24));
        this.mapColor = new Color(255, 0, 231);
        this.toolType = ToolType.ALL;
        this.rarity = Item.Rarity.UNIQUE;
        this.setItemCategory("objects");
        this.setCraftingCategory("objects");
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
        this.hoverHitbox = new Rectangle(0, 0, 32, 32);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("objects/voidvessel");
        this.shadowTexture = GameTexture.fromFile("objects/voidvessel_shadow");
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "voidvesseltip"));
        return tooltips;
    }

    public float getDesiredHeight(Level level, int tileX, int tileY) {
        int seededOffset = this.drawRandom.seeded(VoidVesselObject.getTileSeed(tileX, tileY)).nextInt(3000);
        float perc = GameUtils.getAnimFloat(level.getWorldEntity().getTime() + (long)seededOffset, 3000);
        return GameMath.sin(perc * 360.0f) * 5.0f + 20.0f;
    }

    private int getSpriteX(Level level) {
        return (int)(level.getWorldEntity().getTime() / 100L % 8L);
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        int x = GameMath.getLevelCoordinate(tileX) + 20;
        int y = GameMath.getLevelCoordinate(tileY) - 30;
        GameRandom random = GameRandom.globalRandom;
        AtomicReference<Float> currentAngle = new AtomicReference<Float>(Float.valueOf(random.nextFloat() * 360.0f));
        float distance = 20.0f;
        if (random.getChance(0.05f)) {
            level.entityManager.addParticle((float)x + GameMath.sin(currentAngle.get().floatValue()) * distance, (float)y + GameMath.cos(currentAngle.get().floatValue()) * distance, Particle.GType.CRITICAL).sprite(GameResources.voidPuffParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 12)).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 5.0f / 250.0f), Float::sum).floatValue();
                pos.x = (float)x + GameMath.sin(angle) * distance;
                pos.y = (float)y + (65.0f + ((float)x - pos.x) - angle / 36.0f + GameMath.cos(angle) / 2.0f * distance) - this.getDesiredHeight(level, tileX, tileY);
            }).lifeTime(5000).height(15.0f).alpha(0.3f).ignoreLight(true).sizeFadesInAndOut(12, 24, 500, 500);
            level.entityManager.addParticle((float)x + GameMath.sin(currentAngle.get().floatValue()) * distance, (float)y + GameMath.cos(currentAngle.get().floatValue()) * distance, Particle.GType.CRITICAL).sprite(GameResources.voidPuffParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 12)).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                float angle = currentAngle.accumulateAndGet(Float.valueOf(delta * 5.0f / 250.0f), Float::sum).floatValue();
                pos.x = (float)x + GameMath.sin(angle) * distance;
                pos.y = (float)y + (65.0f - ((float)x - pos.x) - angle / 36.0f + GameMath.cos(angle) / 2.0f * distance) - this.getDesiredHeight(level, tileX, tileY);
            }).lifeTime(5000).height(15.0f).alpha(0.3f).ignoreLight(true).sizeFadesInAndOut(12, 24, 1000, 1000);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - 4;
        int drawY = camera.getTileDrawY(tileY);
        int seededOffset = this.drawRandom.seeded(VoidVesselObject.getTileSeed(tileX, tileY)).nextInt(3000);
        float perc = GameMath.sin(GameUtils.getAnimFloat(level.getWorldEntity().getTime() + (long)seededOffset, 3000) * 360.0f);
        final TextureDrawOptionsEnd drawOptions = this.texture.initDraw().sprite(this.getSpriteX(level), 0, 40, 40).light(light.minLevelCopy(150.0f)).pos(drawX, drawY - (int)this.getDesiredHeight(level, tileX, tileY) - (this.texture.getHeight() - 32));
        final TextureDrawOptionsEnd shadowDrawOptions = this.shadowTexture.initDraw().sprite(0, 0, 40, 14).light(light.minLevelCopy(150.0f)).alpha(1.0f - perc).pos(drawX, drawY + 16);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                shadowDrawOptions.draw();
                drawOptions.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        this.texture.initDraw().sprite(this.getSpriteX(level), 0, 40, 40).alpha(alpha).draw(camera.getTileDrawX(tileX) - 4, camera.getTileDrawY(tileY) - (int)this.getDesiredHeight(level, tileX, tileY) - (this.texture.getHeight() - 32));
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

