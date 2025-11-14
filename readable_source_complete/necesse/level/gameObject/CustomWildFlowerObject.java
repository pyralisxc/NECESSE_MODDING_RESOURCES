/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketHitObject;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.TileRegistry;
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
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemMultiplierIgnored;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CustomWildFlowerObject
extends GameObject {
    protected String textureName;
    public String seedStringID;
    public String productStringID;
    public GameTexture texture;
    public int textureSpriteX;
    public int maxProductAmount;
    public String[] growTiles;
    protected final GameRandom drawRandom;

    public CustomWildFlowerObject(String textureName, int textureSpriteX, String seedStringID, String productStringID, int maxProductAmount, Color mapColor, String ... growTiles) {
        super(new Rectangle(0, 0));
        this.textureName = textureName;
        this.textureSpriteX = textureSpriteX;
        this.seedStringID = seedStringID;
        this.productStringID = productStringID;
        this.maxProductAmount = maxProductAmount;
        this.growTiles = growTiles;
        this.mapColor = mapColor;
        this.displayMapTooltip = true;
        this.drawDamage = false;
        this.objectHealth = 1;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.attackThrough = true;
        this.drawRandom = new GameRandom();
    }

    public CustomWildFlowerObject(String textureName, int textureSpriteX, String seedStringID, String productStringID, int maxProductAmount, Color debrisColor) {
        this(textureName, textureSpriteX, seedStringID, productStringID, maxProductAmount, debrisColor, "grasstile", "overgrowngrasstile");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        GameTexture spriteTexture = GameTexture.fromFile("objects/" + this.textureName);
        this.texture = new GameTexture("objects/" + this.textureName + " weave", 64, spriteTexture.getHeight());
        this.texture.copy(spriteTexture, 16, 0, this.textureSpriteX * 32, 0, 32, spriteTexture.getHeight());
        this.texture.resetTexture();
        spriteTexture.makeFinal();
        this.texture.makeFinal();
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return new LootTable(new LootItemMultiplierIgnored(new LootItem(this.productStringID)));
        }
        return new LootTable(LootItem.between(this.productStringID, 1, this.maxProductAmount), LootItem.between(this.seedStringID, 1, 2));
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
            level.makeGrassWeave(x, y, 1000, false);
        } else {
            this.playDamageSound(level, x, y, true);
        }
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.grass, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16));
    }

    @Override
    public void tick(Mob mob, Level level, int x, int y) {
        super.tick(mob, level, x, y);
        if (Settings.wavyGrass && mob.getFlyingHeight() < 10 && (mob.dx != 0.0f || mob.dy != 0.0f)) {
            level.makeGrassWeave(x, y, 1000, false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        boolean mirrored;
        double nextGaus2;
        double nextGaus;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            nextGaus = this.drawRandom.seeded(CustomWildFlowerObject.getTileSeed(tileX, tileY)).nextGaussian();
            nextGaus2 = this.drawRandom.nextGaussian();
            mirrored = this.drawRandom.nextBoolean();
        }
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        Consumer<TextureDrawOptionsPositionMod> waveChange = GameResources.waveShader.setupGrassWaveMod(level, tileX, tileY, 1000L, 0.07f, 1, this.drawRandom, CustomWildFlowerObject.getTileSeed(tileX, tileY), mirrored, 2.0f);
        int offsetY = 16 + (int)(nextGaus * 4.0);
        final TextureDrawOptionsEnd options = ((TextureDrawOptionsEnd)this.texture.initDraw().light(light).mirror(mirrored, false).addPositionMod((Consumer)waveChange)).pos(drawX - 16 + (int)(nextGaus2 * 4.0), drawY - this.texture.getHeight() + offsetY);
        final int sortY = offsetY - 6;
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return sortY;
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
        boolean mirrored;
        double nextGaus2;
        double nextGaus;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            nextGaus = this.drawRandom.seeded(CustomWildFlowerObject.getTileSeed(tileX, tileY)).nextGaussian();
            nextGaus2 = this.drawRandom.nextGaussian();
            mirrored = this.drawRandom.nextBoolean();
        }
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int offsetY = 16 + (int)(nextGaus * 4.0);
        this.texture.initDraw().light(light).mirror(mirrored, false).alpha(alpha).draw(drawX - 16 + (int)(nextGaus2 * 4.0), drawY - this.texture.getHeight() + offsetY);
    }

    @Override
    public boolean canPlaceOn(Level level, int layerID, int x, int y, GameObject other) {
        return other.getID() == 0 || !other.getValidObjectLayers().contains(ObjectLayerRegistry.TILE_LAYER);
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String superCanPlace = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (superCanPlace != null) {
            return superCanPlace;
        }
        if (level.getObjectID(ObjectLayerRegistry.TILE_LAYER, x, y) != 0) {
            return "occupied";
        }
        GameTile tile = level.getTile(x, y);
        if (Arrays.stream(this.growTiles).anyMatch(sID -> tile.getID() == TileRegistry.getTileID(sID))) {
            return null;
        }
        if (byPlayer && tile.isOrganic) {
            return null;
        }
        return "invalidtile";
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        if (level.getObjectID(ObjectLayerRegistry.TILE_LAYER, x, y) != 0) {
            return false;
        }
        GameTile tile = level.getTile(x, y);
        boolean validGrowTile = Arrays.stream(this.growTiles).anyMatch(sID -> tile.getID() == TileRegistry.getTileID(sID));
        return validGrowTile || level.objectLayer.isPlayerPlaced(layerID, x, y) && tile.isOrganic;
    }
}

