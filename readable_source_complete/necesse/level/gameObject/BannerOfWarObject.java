/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.BannerOfWarObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;
import necesse.level.maps.light.GameLight;

public class BannerOfWarObject
extends GameObject {
    public ObjectDamagedTextureArray texture;
    protected final GameRandom drawRandom;
    final int animTime = 1200;

    public BannerOfWarObject() {
        super(new Rectangle(6, 6, 20, 20));
        this.displayMapTooltip = true;
        this.stackSize = 1;
        this.objectHealth = 100;
        this.isLightTransparent = true;
        this.rarity = Item.Rarity.LEGENDARY;
        this.drawRandom = new GameRandom();
        this.setItemCategory("objects", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/bannerofwar");
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
        int sprite = GameUtils.getAnim(level.getWorldEntity().getTime() + Math.abs(BannerOfWarObject.getTileSeed(tileX, tileY, 52)), 4, 1200);
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(BannerOfWarObject.getTileSeed(tileX, tileY));
            waveChange = GameResources.waveShader.setupGrassWaveMod(level, tileX, tileY, 1000L, 0.02f, 2, this.drawRandom, BannerOfWarObject.getTileSeed(tileX, tileY, 0), false, 3.0f);
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
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        if (level.isIncursionLevel && !((IncursionLevel)level).incursionData.currentIncursionPerkIDs.contains(IncursionPerksRegistry.ENABLE_BANNER_OF_WAR.getID())) {
            return "cantplaceinincursions";
        }
        return super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
    }

    @Override
    public void attemptPlace(Level level, int x, int y, final PlayerMob player, String error) {
        if (level.isClient() && error.equals("cantplaceinincursions")) {
            UniqueFloatText text = new UniqueFloatText(player.getX(), player.getY() - 20, Localization.translate("incursion", "cantplaceinincursions"), new FontOptions(16).outline().color(new Color(200, 100, 100))){

                @Override
                public int getAnchorX() {
                    return player.getX();
                }

                @Override
                public int getAnchorY() {
                    return player.getY() - 20;
                }
            };
            text.riseTime = 500;
            text.fadeTime = 500;
            text.expandTime = 50;
            level.hudManager.addElement(text);
            SoundManager.playSound(GameResources.tick, SoundEffect.ui());
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new BannerOfWarObjectEntity(level, x, y);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "bannerofwartip1"), 400);
        tooltips.add(Localization.translate("itemtooltip", "bannerofwartip2"), 400);
        return tooltips;
    }

    @Override
    public List<ObjectHoverHitbox> getHoverHitboxes(Level level, int layerID, int tileX, int tileY) {
        List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, layerID, tileX, tileY);
        list.add(new ObjectHoverHitbox(layerID, tileX, tileY, 0, -32, 32, 32));
        return list;
    }
}

