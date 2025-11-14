/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.BannerOfPeaceObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BannerOfPeaceObject
extends GameObject {
    public ObjectDamagedTextureArray texture;
    protected final GameRandom drawRandom;
    final int animTime = 1200;

    public BannerOfPeaceObject() {
        super(new Rectangle(6, 6, 20, 20));
        this.displayMapTooltip = true;
        this.stackSize = 1;
        this.objectHealth = 100;
        this.isLightTransparent = true;
        this.rarity = Item.Rarity.RARE;
        this.drawRandom = new GameRandom();
        this.setItemCategory("objects", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/bannerofpeace");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Consumer<TextureDrawOptionsPositionMod> waveChange;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        int sprite = GameUtils.getAnim(level.getWorldEntity().getTime() + Math.abs(BannerOfPeaceObject.getTileSeed(tileX, tileY, 52)), 4, 1200);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(BannerOfPeaceObject.getTileSeed(tileX, tileY));
            waveChange = GameResources.waveShader.setupGrassWaveMod(level, tileX, tileY, 1000L, 0.02f, 2, this.drawRandom, BannerOfPeaceObject.getTileSeed(tileX, tileY, 0), false, 3.0f);
        }
        final TextureDrawOptionsEnd options = ((TextureDrawOptionsEnd)texture.initDraw().sprite(sprite, 0, 64, texture.getHeight()).light(light).addPositionMod((Consumer)waveChange)).pos(drawX - 16, drawY - (texture.getHeight() - 32));
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
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(0, 0, 64, texture.getHeight()).alpha(alpha).draw(drawX - 16, drawY - (texture.getHeight() - 32));
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        float buffer;
        float windAmount;
        super.tickEffect(level, layerID, tileX, tileY);
        if (!Settings.windEffects) {
            return;
        }
        float windSpeed = level.weatherLayer.getWindSpeed();
        if (windSpeed > 0.2f && (windAmount = level.weatherLayer.getWindAmount(tileX, tileY) * 3.0f) > 0.5f && ((buffer = 0.016666668f * windAmount * windSpeed) >= 1.0f || GameRandom.globalRandom.getChance(buffer))) {
            level.makeGrassWeave(tileX, tileY, 1200, false);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new BannerOfPeaceObjectEntity(level, x, y);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "bannerofpeacetip"), 400);
        tooltips.add(Localization.translate("itemtooltip", "placeinanysettlement"));
        return tooltips;
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String superError = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (superError != null) {
            return superError;
        }
        boolean hasSettlement = SettlementsWorldData.getSettlementsData(level).hasSettlementAtTile(level, x, y);
        if (!hasSettlement) {
            return "notsettlement";
        }
        return null;
    }

    @Override
    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int layerID, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, layerID, tileX, tileY);
        list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, -32, 32, 32));
        return list;
    }
}

