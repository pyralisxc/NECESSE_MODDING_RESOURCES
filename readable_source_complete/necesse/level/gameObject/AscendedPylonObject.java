/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.AscendedPylonObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AscendedPylonObject
extends GameObject {
    public ObjectDamagedTextureArray texture;
    public GameTexture effectsTexture;
    public GameTexture shieldTexture;

    public AscendedPylonObject() {
        super(new Rectangle(0, 0, 32, 32));
        this.mapColor = new Color(227, 27, 255);
        this.displayMapTooltip = true;
        this.objectHealth = 300;
        this.toolType = ToolType.UNBREAKABLE;
        this.isLightTransparent = true;
        this.lightHue = 310.0f;
        this.lightSat = 0.5f;
        this.lightLevel = 150;
        this.hoverHitbox = new Rectangle(0, -24, 32, 56);
    }

    @Override
    public List<Rectangle> getProjectileCollisions(Level level, int x, int y, int rotation) {
        return Collections.emptyList();
    }

    @Override
    public boolean shouldGenerateDamageOverlayTextures() {
        return true;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/ascendedpylon");
        this.effectsTexture = GameTexture.fromFile("objects/ascendedpyloneffects");
        this.shieldTexture = GameTexture.fromFile("objects/pylonshield");
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
        int levelX = tileX * 32 + 16;
        int levelY = tileY * 32 + 16;
        for (int i = 0; i < 50; ++i) {
            int lifeTime = GameRandom.globalRandom.getIntBetween(500, 5000);
            float lifePerc = (float)lifeTime / 5000.0f;
            float startHeight = 10.0f;
            float height = startHeight + (float)GameRandom.globalRandom.getIntBetween(70, 150) * lifePerc;
            level.entityManager.addParticle((float)levelX + GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f), (float)levelY + GameRandom.globalRandom.getFloatBetween(-10.0f, 10.0f), Particle.GType.IMPORTANT_COSMETIC).sizeFades(20, 30).movesFriction(GameRandom.globalRandom.getFloatBetween(-40.0f, 40.0f), GameRandom.globalRandom.getFloatBetween(-20.0f, 20.0f), 0.5f).heightMoves(startHeight, height).colorRandom(320.0f, 0.8f, 0.5f, 10.0f, 0.1f, 0.1f).givesLight(300.0f, 0.5f).lifeTime(lifeTime);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        DrawOptions effectsOptions;
        TextureDrawOptionsEnd shieldOptions;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        float damagePercent = 0.0f;
        AscendedPylonObjectEntity objectEntity = this.getCurrentObjectEntity(level, tileX, tileY, AscendedPylonObjectEntity.class);
        if (objectEntity != null) {
            damagePercent = objectEntity.getDamagePercent();
            if (objectEntity.isShielded()) {
                int shieldSpriteRes = this.shieldTexture.getHeight();
                shieldOptions = this.shieldTexture.initDraw().sprite((int)(level.getLocalTime() / 100L) % 4, 0, shieldSpriteRes).alpha(0.5f).pos(drawX + 16 - shieldSpriteRes / 2, drawY - shieldSpriteRes / 2 - 32);
            } else {
                shieldOptions = null;
            }
            effectsOptions = objectEntity.getEffectDrawOptions(this.effectsTexture, tileX, tileY, light, camera);
        } else {
            shieldOptions = null;
            effectsOptions = null;
        }
        GameTexture damagedTexture = this.texture.getDamagedTexture(damagePercent);
        final TextureDrawOptionsEnd options = damagedTexture.initDraw().light(light).pos(drawX + 16 - damagedTexture.getWidth() / 2, drawY - damagedTexture.getHeight() + 32 + 8);
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
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 32;
            }

            @Override
            public void draw(TickManager tickManager) {
                if (shieldOptions != null) {
                    shieldOptions.draw();
                }
                if (effectsOptions != null) {
                    effectsOptions.draw();
                }
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - 16;
        int drawY = camera.getTileDrawY(tileY) - 32;
        this.texture.getDamagedTexture(0.0f).initDraw().sprite(0, 0, 64).light(light).alpha(alpha).draw(drawX, drawY);
    }

    @Override
    public void onMouseHover(Level level, int x, int y, GameCamera camera, PlayerMob perspective, boolean debug) {
        super.onMouseHover(level, x, y, camera, perspective, debug);
        AscendedPylonObjectEntity objectEntity = this.getCurrentObjectEntity(level, x, y, AscendedPylonObjectEntity.class);
        GameTooltips hoverTooltip = objectEntity.getPylonHoverTooltip(perspective);
        if (hoverTooltip != null) {
            GameTooltipManager.addTooltip(hoverTooltip, TooltipLocation.INTERACT_FOCUS);
        } else {
            GameTooltipManager.addTooltip(new StringTooltips(this.getDisplayName()), TooltipLocation.INTERACT_FOCUS);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new AscendedPylonObjectEntity(level, x, y);
    }
}

