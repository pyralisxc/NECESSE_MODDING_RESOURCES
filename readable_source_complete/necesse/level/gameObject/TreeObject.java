/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
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
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TreeObject
extends GameObject
implements ForestryJobObject {
    public int weaveTime = 250;
    public float weaveAmount = 0.02f;
    public int leavesCenterWidth;
    public int leavesMinHeight;
    public int leavesMaxHeight;
    public String leavesTextureName;
    public Supplier<GameTextureSection> leavesTexture;
    public String logStringID;
    public String saplingStringID;
    protected String textureName;
    public ObjectDamagedTextureArray texture;
    protected final GameRandom drawRandom;

    public TreeObject(String textureName, String logStringID, String saplingStringID, Color mapColor, int leavesCenterWidth, int leavesMinHeight, int leavesMaxHeight, String leavesTextureName) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.logStringID = logStringID;
        this.saplingStringID = saplingStringID;
        this.mapColor = mapColor;
        this.leavesCenterWidth = leavesCenterWidth;
        this.leavesMinHeight = leavesMinHeight;
        this.leavesMaxHeight = leavesMaxHeight;
        this.leavesTextureName = leavesTextureName;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
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
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
        if (this.leavesTextureName != null) {
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
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        LootTable lootTable = new LootTable();
        if (this.saplingStringID != null) {
            lootTable.items.add(LootItem.between(this.saplingStringID, 1, 2).splitItems(5));
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
            int spriteY = 0;
            GameRandom gameRandom = this.drawRandom;
            synchronized (gameRandom) {
                this.drawRandom.setSeed(TreeObject.getTileSeed(tileX, tileY));
                if (spritesHeight > 1) {
                    spriteY = this.drawRandom.nextInt(spritesHeight);
                }
                mirror = this.drawRandom.nextBoolean();
            }
            Consumer<TextureDrawOptionsPositionMod> waveChange = GameResources.waveShader.setupGrassWaveMod(level, tileX, tileY, this.weaveTime, this.weaveAmount, 2, this.drawRandom, TreeObject.getTileSeed(tileX, tileY, 0), mirror, 3.0f);
            final TextureDrawOptionsEnd options = ((TextureDrawOptionsEnd)texture.initDraw().sprite(spriteX, spriteY, spriteRes).alpha(alpha).light(light).mirror(mirror, false).addPositionMod((Consumer)waveChange)).pos(drawX - 48, drawY - 96);
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return 16;
                }

                @Override
                public void draw(TickManager tickManager) {
                    Performance.record((PerformanceTimerManager)tickManager, "treeDraw", () -> options.draw());
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
        int spriteY = 0;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            this.drawRandom.setSeed(TreeObject.getTileSeed(tileX, tileY));
            if (spritesHeight > 1) {
                spriteY = this.drawRandom.nextInt(spritesHeight);
            }
            mirror = this.drawRandom.nextBoolean();
        }
        texture.initDraw().sprite(spriteX, spriteY, spriteRes).alpha(alpha).light(light).mirror(mirror, false).draw(drawX - 48, drawY - 96);
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

    public static void spawnLeafParticles(Level level, int x, int y, int centerWidth, int minStartHeight, int maxStartHeight, int amount, Point2D.Double windDir, float windSpeed, Supplier<GameTextureSection> textureSupplier) {
        boolean alternate = GameRandom.globalRandom.nextBoolean();
        for (int i = 0; i < amount; ++i) {
            float posX = (float)(x * 32 + 16) + (alternate ? GameRandom.globalRandom.getFloatBetween(-1.0f, 0.0f) : GameRandom.globalRandom.getFloatBetween(0.0f, 1.0f)) * (float)centerWidth;
            alternate = !alternate;
            float posY = y * 32 + 32;
            float startHeight = GameRandom.globalRandom.getFloatBetween(minStartHeight, maxStartHeight);
            float startHeightSpeed = GameRandom.globalRandom.getFloatBetween(0.0f, 60.0f);
            float endHeight = GameRandom.globalRandom.getFloatBetween(-10.0f, -5.0f);
            float gravity = GameRandom.globalRandom.getFloatBetween(8.0f, 20.0f);
            boolean mirror = GameRandom.globalRandom.nextBoolean();
            float rotation = GameRandom.globalRandom.getFloatBetween(-100.0f, 100.0f);
            float moveX = GameRandom.globalRandom.floatGaussian() * 5.0f + (float)windDir.x * windSpeed * 10.0f;
            float moveY = GameRandom.globalRandom.floatGaussian() * 2.0f + (float)windDir.y * windSpeed * 10.0f;
            ParticleOption.FrictionMover frictionMover = new ParticleOption.FrictionMover(moveX, moveY, 0.0f);
            ParticleOption.CollisionMover mover = new ParticleOption.CollisionMover(level, frictionMover, new CollisionFilter().mobCollision().addFilter(tp -> tp.object().object.isWall));
            int timeToLive = GameRandom.globalRandom.getIntBetween(3000, 8000);
            int timeToFadeOut = GameRandom.globalRandom.getIntBetween(1000, 2000);
            int totalTime = timeToLive + timeToFadeOut;
            ParticleOption.HeightMover heightMover = new ParticleOption.HeightMover(startHeight, startHeightSpeed, gravity, 2.0f, endHeight, 0.0f);
            AtomicReference<Float> floatingTime = new AtomicReference<Float>(Float.valueOf(0.0f));
            ParticleOption particle = level.entityManager.addParticle(posX, posY, Particle.GType.COSMETIC).sprite(textureSupplier.get()).fadesAlphaTime(0, timeToFadeOut).sizeFadesInAndOut(15, 20, 100, 0).height(heightMover).onMoveTick((delta, lifeTime, timeAlive, lifePercent) -> {
                if (heightMover.currentHeight > endHeight) {
                    floatingTime.set(Float.valueOf(((Float)floatingTime.get()).floatValue() + delta));
                }
            }).modify((options, lifeTime, timeAlive, lifePercent) -> {
                float angle = GameMath.sin(((Float)floatingTime.get()).floatValue() / 5.0f) * rotation;
                options.rotate(angle, 10, -4);
            }).moves((pos, delta, lifeTime, timeAlive, lifePercent) -> {
                if (heightMover.currentHeight > endHeight) {
                    mover.tick(pos, delta, lifeTime, timeAlive, lifePercent);
                }
            }).modify((options, lifeTime, timeAlive, lifePercent) -> options.mirror(mirror, false)).lifeTime(totalTime);
            if (particle.isRemoved()) continue;
            SoundManager.playSound(SoundSettingsRegistry.leavesBreakAmbient, SoundEffect.effect(posX, posY));
        }
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
    public String getSaplingStringID() {
        return this.saplingStringID;
    }
}

