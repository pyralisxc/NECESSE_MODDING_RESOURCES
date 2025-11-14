/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketHitObject;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsMods;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.gfx.drawables.Drawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class GrassObject
extends GameObject {
    public int weaveTime = 2000;
    public float weaveAmount = 0.15f;
    public float weaveHeight = 1.0f;
    public float randomXOffset = 8.0f;
    public float randomYOffset = 8.0f;
    protected String textureName;
    protected GameTexture[] textures;
    protected GameTexture[] underGroundTextures;
    protected int undergroundPixels;
    private final GameRandom drawRandom;
    protected int density;
    protected int extraWeaveSpace = 0;

    public GrassObject(String textureName, int undergroundPixels, int density) {
        super(new Rectangle(0, 0));
        this.textureName = textureName;
        this.undergroundPixels = undergroundPixels;
        this.density = density;
        this.drawDamage = false;
        this.isGrass = true;
        this.objectHealth = 1;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.attackThrough = true;
        this.drawRandom = new GameRandom();
        this.canPlaceOnProtectedLevels = true;
        this.setItemCategory("objects", "landscaping", "plants");
        this.setCraftingCategory("objects", "landscaping", "plants");
    }

    public GrassObject(String textureName, int density) {
        this(textureName, 0, density);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        GameTexture spriteTexture = GameTexture.fromFile("objects/" + this.textureName);
        int sprites = spriteTexture.getWidth() / 32;
        int extraWeaveSpaceHalf = this.extraWeaveSpace / 2;
        this.textures = new GameTexture[sprites];
        this.underGroundTextures = new GameTexture[sprites];
        for (int i = 0; i < sprites; ++i) {
            this.textures[i] = new GameTexture("objects/" + this.textureName + " weave" + i, 32 + this.extraWeaveSpace, spriteTexture.getHeight() - this.undergroundPixels);
            this.textures[i].copy(spriteTexture, extraWeaveSpaceHalf, 0, i * 32, 0, 32, spriteTexture.getHeight() - this.undergroundPixels);
            this.textures[i].makeFinal();
            if (this.undergroundPixels <= 0) continue;
            this.underGroundTextures[i] = new GameTexture("objects/" + this.textureName + " underground weave" + i, 32 + this.extraWeaveSpace, this.undergroundPixels);
            this.underGroundTextures[i].copy(spriteTexture, extraWeaveSpaceHalf, 0, i * 32, spriteTexture.getHeight() - this.undergroundPixels, 32, this.undergroundPixels);
            this.underGroundTextures[i].makeFinal();
        }
        spriteTexture.makeFinal();
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage, Attacker attacker) {
        if (level.objectLayer.isPlayerPlaced(x, y)) {
            level.getServer().network.sendToClientsWithTile(new PacketHitObject(level, x, y, this, damage), level, x, y);
        } else {
            super.attackThrough(level, x, y, damage, attacker);
        }
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage) {
        super.attackThrough(level, x, y, damage);
        if (damage.damage <= 0.0f || level.objectLayer.isPlayerPlaced(x, y)) {
            level.makeGrassWeave(x, y, this.weaveTime, false);
        } else {
            this.playDamageSound(level, x, y, true);
        }
    }

    @Override
    public void tick(Mob mob, Level level, int x, int y) {
        super.tick(mob, level, x, y);
        if (Settings.wavyGrass && mob.getFlyingHeight() <= 20 && (mob.dx != 0.0f || mob.dy != 0.0f)) {
            level.makeGrassWeave(x, y, this.weaveTime, false);
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record((PerformanceTimerManager)tickManager, "grassSetup", () -> {
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            GameLight light = level.getLightLevel(tileX, tileY);
            if (Settings.denseGrass) {
                this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 10, -7, 0);
                this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 26, -7, 1);
            } else {
                this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 14, -7, 0);
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameLight light = level.getLightLevel(tileX, tileY);
        LinkedList<LevelSortedDrawable> list = new LinkedList<LevelSortedDrawable>();
        OrderableDrawables tileList = new OrderableDrawables(new TreeMap<Integer, List<Drawable>>());
        if (Settings.denseGrass) {
            this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 10, -7, 0, 0.5f);
            this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 26, -7, 1, 0.5f);
        } else {
            this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, 14, -7, 0, 0.5f);
        }
        tileList.forEach(d -> d.draw(level.tickManager()));
        list.forEach(d -> d.draw(level.tickManager()));
    }

    public void addGrassDrawable(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, int drawX, int drawY, GameLight light, int yOffset, int sortYOffset, int primeIndex) {
        this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, yOffset, sortYOffset, primeIndex, 0, this.textures.length - 1);
    }

    public void addGrassDrawable(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, int drawX, int drawY, GameLight light, int yOffset, int sortYOffset, int primeIndex, float alpha) {
        this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, yOffset, sortYOffset, primeIndex, 0, this.textures.length - 1, alpha);
    }

    public void addGrassDrawable(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, int drawX, int drawY, GameLight light, int yOffset, int sortYOffset, int primeIndex, int minTextureIndex, int maxTextureIndex) {
        this.addGrassDrawable(list, tileList, level, tileX, tileY, drawX, drawY, light, yOffset, sortYOffset, primeIndex, minTextureIndex, maxTextureIndex, 1.0f);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addGrassDrawable(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, int drawX, int drawY, GameLight light, int yOffset, int sortYOffset, int primeIndex, int minTextureIndex, int maxTextureIndex, float alpha) {
        int textureIndex;
        boolean mirror;
        double xGaussian;
        double yGaussian;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            yGaussian = this.drawRandom.seeded(GrassObject.getTileSeed(tileX, tileY, primeIndex)).nextFloat() * 2.0f - 1.0f;
            xGaussian = this.drawRandom.nextFloat() * 2.0f - 1.0f;
            mirror = this.drawRandom.nextBoolean();
            textureIndex = this.drawRandom.getIntBetween(minTextureIndex, maxTextureIndex);
        }
        GameTexture texture = this.textures[textureIndex];
        Consumer<TextureDrawOptionsPositionMod> waveChange = GameResources.waveShader.setupGrassWaveMod(level, tileX, tileY, this.weaveTime, this.weaveAmount, 2, this.drawRandom, GrassObject.getTileSeed(tileX, tileY, primeIndex), mirror, 2.0f);
        int offset = yOffset + (int)(yGaussian * (double)this.randomYOffset);
        TextureDrawOptionsMods next = texture.initDraw().alpha(alpha).mirror(mirror, false);
        if (light != null) {
            next = ((TextureDrawOptionsEnd)next).light(light);
        }
        next = ((TextureDrawOptionsEnd)next).addPositionMod((Consumer)waveChange);
        final TextureDrawOptionsEnd options = ((TextureDrawOptionsEnd)next).pos(drawX + (int)(xGaussian * (double)this.randomXOffset) - this.extraWeaveSpace / 2, drawY - texture.getHeight() + offset);
        final int sortY = offset + sortYOffset;
        GameTexture underGroundTexture = this.underGroundTextures[textureIndex];
        if (underGroundTexture != null && this.shouldDrawUnderground(level, tileX, tileY)) {
            TextureDrawOptionsEnd undergroundNext = underGroundTexture.initDraw().alpha(alpha).mirror(mirror, false);
            if (light != null) {
                undergroundNext.light(light);
            }
            TextureDrawOptionsEnd underground = undergroundNext.pos(drawX + (int)(xGaussian * (double)this.randomXOffset) - this.extraWeaveSpace / 2, drawY + offset);
            tileList.add(tm -> underground.draw());
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return sortY;
            }

            @Override
            public void draw(TickManager tickManager) {
                Performance.record((PerformanceTimerManager)tickManager, "grassDraw", options::draw);
            }
        });
    }

    protected boolean shouldDrawUnderground(Level level, int tileX, int tileY) {
        return level.getTile((int)tileX, (int)tileY).isLiquid && level.getTile((int)tileX, (int)(tileY + 1)).isLiquid;
    }

    @Override
    public boolean shouldSnapControllerMining(Level level, int x, int y) {
        return level.objectLayer.isPlayerPlaced(x, y) || !this.attackThrough;
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        }
        if (!byPlayer && !this.densityCheck(level, x, y)) {
            return "adjspace";
        }
        return null;
    }

    protected boolean densityCheck(Level level, int x, int y) {
        if (this.density > 0) {
            GameObject[] adj = level.getAdjacentObjects(x, y);
            int objs = 0;
            for (GameObject obj : adj) {
                if (obj.isGrass) {
                    ++objs;
                }
                if (objs <= this.density) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.grass, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16));
    }
}

