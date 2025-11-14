/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.window.WindowManager;
import necesse.entity.Entity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.FruitGrowerObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameObject.ForestryJobObject;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.TreeObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.jobs.HarvestFruitLevelJob;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.light.GameLight;

public class FruitTreeObject
extends GameObject
implements ForestryJobObject {
    public int weaveTime = 250;
    public float weaveAmount = 0.02f;
    public int leavesCenterWidth;
    public int leavesMinHeight;
    public int leavesMaxHeight;
    public String leavesTextureName;
    public Supplier<GameTextureSection> leavesTexture;
    public float minGrowTimeSeconds;
    public float maxGrowTimeSeconds;
    public float fruitPerStage;
    public int maxStage;
    public String logStringID;
    public String seedStringID;
    public String fruitStringID;
    protected String textureName;
    protected ObjectDamagedTextureArray texture;
    protected final GameRandom drawRandom;

    public FruitTreeObject(String textureName, String logStringID, String seedStringID, float minGrowTimeSeconds, float maxGrowTimeSeconds, String fruitStringID, float fruitPerStage, int maxStage, Color mapColor, int leavesCenterWidth, int leavesMinHeight, int leavesMaxHeight, String leavesTextureName) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.logStringID = logStringID;
        this.seedStringID = seedStringID;
        this.minGrowTimeSeconds = minGrowTimeSeconds;
        this.maxGrowTimeSeconds = maxGrowTimeSeconds;
        this.fruitStringID = fruitStringID;
        this.fruitPerStage = fruitPerStage;
        this.maxStage = maxStage;
        this.mapColor = mapColor;
        this.leavesCenterWidth = leavesCenterWidth;
        this.leavesMinHeight = leavesMinHeight;
        this.leavesMaxHeight = leavesMaxHeight;
        this.leavesTextureName = leavesTextureName;
        this.debrisColor = new Color(86, 69, 40);
        this.displayMapTooltip = true;
        this.isTree = true;
        this.drawDamage = false;
        this.toolType = ToolType.AXE;
        this.drawRandom = new GameRandom();
        this.replaceCategories.add("tree");
        this.replaceRotations = false;
        this.setItemCategory("objects", "landscaping", "plants");
    }

    @Override
    public List<LevelJob> getLevelJobs(Level level, int tileX, int tileY) {
        if (this.getFruitStage(level, tileX, tileY) > 0) {
            return Collections.singletonList(new HarvestFruitLevelJob(tileX, tileY));
        }
        return super.getLevelJobs(level, tileX, tileY);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
        try {
            GameTexture particleTexture = GameTexture.fromFileRaw("particles/" + this.leavesTextureName);
            int leavesRes = particleTexture.getHeight();
            int leafSprites = particleTexture.getWidth() / leavesRes;
            GameTextureSection particleSection = GameResources.particlesTextureGenerator.addTexture(particleTexture);
            this.leavesTexture = () -> particleSection.sprite(GameRandom.globalRandom.nextInt(leafSprites), 0, leavesRes);
        }
        catch (FileNotFoundException e) {
            this.leavesTexture = null;
        }
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        LootTable lootTable = new LootTable();
        if (this.seedStringID != null) {
            lootTable.items.add(new LootItem(this.seedStringID).preventLootMultiplier());
        }
        if (this.logStringID != null) {
            lootTable.items.add(LootItem.between(this.logStringID, 4, 5).splitItems(5));
        }
        return lootTable;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        Performance.record((PerformanceTimerManager)tickManager, "treeSetup", () -> {
            boolean mirror;
            GameLight light = level.getLightLevel(tileX, tileY);
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            float alpha = 1.0f;
            if (perspective != null && !Settings.hideUI && !Settings.hideCursor) {
                Rectangle alphaRec = new Rectangle(tileX * 32 - 48, tileY * 32 - 100, 128, 100);
                if (perspective.getCollision().intersects(alphaRec)) {
                    alpha = 0.5f;
                } else if (alphaRec.contains(camera.getX() + WindowManager.getWindow().mousePos().sceneX, camera.getY() + WindowManager.getWindow().mousePos().sceneY)) {
                    alpha = 0.5f;
                }
            }
            int spriteRes = 128;
            GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
            int spriteX = 0;
            if (texture.getWidth() > spriteRes && level.getTileID(tileX, tileY) == TileRegistry.snowID) {
                spriteX = 1;
            }
            int spritesHeight = texture.getHeight() / spriteRes;
            GameRandom gameRandom = this.drawRandom;
            synchronized (gameRandom) {
                this.drawRandom.setSeed(FruitTreeObject.getTileSeed(tileX, tileY));
                mirror = this.drawRandom.nextBoolean();
            }
            Consumer<TextureDrawOptionsPositionMod> waveChange = GameResources.waveShader.setupGrassWaveMod(level, tileX, tileY, this.weaveTime, this.weaveAmount, 2, this.drawRandom, FruitTreeObject.getTileSeed(tileX, tileY, 0), mirror, 3.0f);
            int spriteY = Math.min(this.getFruitStage(level, tileX, tileY), spritesHeight - 1);
            final TextureDrawOptionsEnd options = ((TextureDrawOptionsEnd)texture.initDraw().sprite(spriteX, spriteY, spriteRes).alpha(alpha).light(light).mirror(mirror, false).addPositionMod((Consumer)waveChange)).pos(drawX - 48, drawY - 96);
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return 16;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "treeDraw", options::draw);
                }
            });
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        boolean mirror;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int spriteRes = 128;
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        int spriteX = 0;
        if (texture.getWidth() > spriteRes && level.getTileID(tileX, tileY) == TileRegistry.snowID) {
            spriteX = 1;
        }
        int spritesHeight = texture.getHeight() / spriteRes;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(FruitTreeObject.getTileSeed(tileX, tileY));
            mirror = this.drawRandom.nextBoolean();
        }
        int spriteY = Math.min(this.getFruitStage(level, tileX, tileY), spritesHeight - 1);
        texture.initDraw().sprite(spriteX, spriteY, spriteRes).alpha(alpha).light(light).alpha(alpha).mirror(mirror, false).draw(drawX - 48, drawY - 96);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        super.onDestroyed(level, layerID, x, y, attacker, client, itemsDropped);
        if (!level.isServer()) {
            int leaves = GameRandom.globalRandom.getIntBetween(15, 20);
            this.spawnLeafParticles(level, x, y, 20, leaves, new Point2D.Double(), 0.0f);
        }
    }

    @Override
    public boolean onDamaged(Level level, int layerID, int x, int y, int damage, Attacker attacker, ServerClient client, boolean showEffect, int mouseX, int mouseY) {
        boolean out = super.onDamaged(level, layerID, x, y, damage, attacker, client, showEffect, mouseX, mouseY);
        if (showEffect) {
            level.makeGrassWeave(x, y, this.weaveTime, true);
            if (!level.isServer()) {
                int leaves = GameRandom.globalRandom.getIntBetween(0, 2);
                this.spawnLeafParticles(level, x, y, this.leavesMinHeight, leaves, new Point2D.Double(), 0.0f);
            }
        }
        return out;
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        float windAmount;
        super.tickEffect(level, layerID, tileX, tileY);
        if (!Settings.windEffects) {
            return;
        }
        float windSpeed = level.weatherLayer.getWindSpeed();
        if (windSpeed > 0.2f && (windAmount = level.weatherLayer.getWindAmount(tileX, tileY) * 3.0f) > 0.5f) {
            Point2D.Double windDir = level.weatherLayer.getWindDirNormalized();
            for (float buffer = 0.016666668f * windAmount * windSpeed; buffer >= 1.0f || GameRandom.globalRandom.getChance(buffer); buffer -= 1.0f) {
                this.spawnLeafParticles(level, tileX, tileY, this.leavesMinHeight, 1, windDir, windAmount * windSpeed);
            }
        }
    }

    public void spawnLeafParticles(Level level, int x, int y, int minStartHeight, int amount, Point2D.Double windDir, float windSpeed) {
        if (this.leavesTexture == null) {
            return;
        }
        TreeObject.spawnLeafParticles(level, x, y, this.leavesCenterWidth, minStartHeight, this.leavesMaxHeight, amount, windDir, windSpeed, this.leavesTexture);
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        GameObject[] adj;
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        }
        for (GameObject obj : adj = level.getAdjacentObjects(x, y)) {
            if (!obj.isTree) continue;
            return "treenear";
        }
        return null;
    }

    @Override
    public boolean isValid(Level level, int layerID, int x, int y) {
        if (super.isValid(level, layerID, x, y)) {
            GameObject[] adj;
            for (GameObject obj : adj = level.getAdjacentObjects(x, y)) {
                if (!obj.isTree) continue;
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        if (this.getFruitStage(level, x, y) > 0) {
            return Localization.translate("controls", "harvesttip");
        }
        return null;
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        this.harvest(level, x, y, player);
    }

    public void harvest(Level level, int x, int y, PlayerMob player) {
        FruitGrowerObjectEntity ent = this.getFruitObjectEntity(level, x, y);
        if (ent != null) {
            ent.harvest(player);
        }
        if (!level.isServer() && !level.isGrassWeaving(x, y)) {
            SoundManager.playSound(SoundSettingsRegistry.leavesBreakAction, SoundEffect.effect(x * 32 + 16, y * 32 + 16));
            this.spawnLeafParticles(level, x, y, this.leavesMinHeight, 2, new Point2D.Double(), 0.0f);
            level.makeGrassWeave(x, y, this.weaveTime, false);
        }
    }

    public static Point getItemDropPos(int tileX, int tileY, Entity from) {
        Point2D.Float delta;
        int xOffset = 0;
        int yOffset = 0;
        Point2D.Float float_ = delta = from == null ? new Point2D.Float(0.0f, 1.0f) : GameMath.normalize(from.getX() - tileX * 32 - 16, from.getY() - tileY * 32 - 16);
        if (Math.abs(delta.x) - Math.abs(delta.y) <= 0.0f) {
            yOffset = delta.y < 0.0f ? -1 : 1;
        } else {
            xOffset = delta.x < 0.0f ? -1 : 1;
        }
        return new Point(tileX * 32 + 16 + xOffset * 32, tileY * 32 + 16 + yOffset * 32);
    }

    public FruitGrowerObjectEntity getFruitObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (objectEntity instanceof FruitGrowerObjectEntity) {
            return (FruitGrowerObjectEntity)objectEntity;
        }
        return null;
    }

    public int getFruitStage(Level level, int tileX, int tileY) {
        FruitGrowerObjectEntity ent = this.getFruitObjectEntity(level, tileX, tileY);
        if (ent != null) {
            return ent.getStage();
        }
        return 0;
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new FruitGrowerObjectEntity(level, x, y, (int)(this.minGrowTimeSeconds * 1000.0f), (int)(this.maxGrowTimeSeconds * 1000.0f), this.maxStage, this.fruitStringID, this.fruitPerStage);
    }

    @Override
    public String getSaplingStringID() {
        return this.seedStringID;
    }
}

