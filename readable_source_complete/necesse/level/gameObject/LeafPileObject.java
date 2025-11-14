/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.packet.PacketHitObject;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettingsRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.window.WindowManager;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsPositionMod;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class LeafPileObject
extends GameObject {
    public int weaveTime = 250;
    public float weaveAmount = 0.08f;
    protected String textureName = "leafpile";
    protected GameTexture[][] textures;
    protected final GameRandom drawRandom;

    public LeafPileObject() {
        this.mapColor = new Color(184, 75, 26);
        this.displayMapTooltip = true;
        this.drawDamage = false;
        this.objectHealth = 1;
        this.toolType = ToolType.ALL;
        this.drawRandom = new GameRandom();
        this.attackThrough = true;
        this.replaceRotations = false;
        this.setItemCategory("objects", "landscaping", "plants");
        this.setCraftingCategory("objects", "landscaping", "plants");
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
    }

    @Override
    public void tick(Mob mob, Level level, int x, int y) {
        super.tick(mob, level, x, y);
        if (Settings.wavyGrass && mob.getFlyingHeight() < 10 && (mob.dx != 0.0f || mob.dy != 0.0f)) {
            level.makeGrassWeave(x, y, this.weaveTime, false);
        }
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        if (level.objectLayer.isPlayerPlaced(tileX, tileY)) {
            return super.getLootTable(level, layerID, tileX, tileY);
        }
        return new LootTable();
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        GameTexture texture = GameTexture.fromFile("objects/" + this.textureName);
        this.textures = new GameTexture[texture.getWidth() / 64][texture.getHeight() / 64];
        for (int i = 0; i < this.textures.length; ++i) {
            for (int j = 0; j < this.textures[i].length; ++j) {
                this.textures[i][j] = new GameTexture(texture, 64 * i, 64 * j, 64, 64);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        boolean mirror;
        double yGaussian;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        float alpha = 1.0f;
        if (perspective != null && !Settings.hideUI && !Settings.hideCursor) {
            Rectangle alphaRec = new Rectangle(tileX * 32 - 12, tileY * 32 - 24, 56, 32);
            if (perspective.getCollision().intersects(alphaRec)) {
                alpha = 0.5f;
            } else if (alphaRec.contains(camera.getX() + WindowManager.getWindow().mousePos().sceneX, camera.getY() + WindowManager.getWindow().mousePos().sceneY)) {
                alpha = 0.5f;
            }
        }
        int spriteX = this.drawRandom.seeded(LeafPileObject.getTileSeed(tileX, tileY)).nextInt(4);
        if (this.textures.length > 1 && level.getTileID(tileX, tileY) == TileRegistry.snowID) {
            spriteX = 1;
        }
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            yGaussian = this.drawRandom.seeded(LeafPileObject.getTileSeed(tileX, tileY, 0)).nextFloat() * 2.0f - 1.0f;
            this.drawRandom.setSeed(LeafPileObject.getTileSeed(tileX, tileY));
            mirror = this.drawRandom.nextBoolean();
        }
        Consumer<TextureDrawOptionsPositionMod> waveChange = GameResources.waveShader.setupGrassWaveMod(level, tileX, tileY, 400L, this.weaveAmount, 2, this.drawRandom, LeafPileObject.getTileSeed(tileX, tileY, 0), mirror, 2.0f);
        GameTexture texture = this.textures[spriteX][0];
        int offset = 28 + (int)(yGaussian * 4.0);
        final TextureDrawOptionsEnd options = ((TextureDrawOptionsEnd)texture.initDraw().alpha(alpha).light(light).mirror(mirror, false).addPositionMod((Consumer)waveChange)).pos(drawX - 32 + 16, drawY - texture.getHeight() + offset);
        final int sortY = offset - 16;
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
        boolean mirror;
        double yGaussian;
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int spriteX = this.drawRandom.seeded(LeafPileObject.getTileSeed(tileX, tileY)).nextInt(4);
        if (this.textures.length > 1 && level.getTileID(tileX, tileY) == TileRegistry.snowID) {
            spriteX = 1;
        }
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            yGaussian = this.drawRandom.seeded(LeafPileObject.getTileSeed(tileX, tileY, 0)).nextFloat() * 2.0f - 1.0f;
            this.drawRandom.setSeed(LeafPileObject.getTileSeed(tileX, tileY));
            mirror = this.drawRandom.nextBoolean();
        }
        int spriteY = Math.min(0, this.textures[spriteX].length - 1);
        GameTexture texture = this.textures[spriteX][spriteY];
        int offset = 28 + (int)(yGaussian * 4.0);
        texture.initDraw().alpha(alpha).light(light).mirror(mirror, false).alpha(alpha).draw(drawX - 32 + 16, drawY - texture.getHeight() + offset);
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage) {
        super.attackThrough(level, x, y, damage);
        level.makeGrassWeave(x, y, 300, false);
        if (damage.damage > 0.0f) {
            this.spawnDestroyedParticles(level, x, y);
        }
    }

    @Override
    public void attackThrough(Level level, int x, int y, GameDamage damage, Attacker attacker) {
        if (!level.objectLayer.isPlayerPlaced(x, y)) {
            super.attackThrough(level, x, y, damage, attacker);
            this.playDamageSound(level, x, y, true);
        } else {
            level.getServer().network.sendToClientsWithTile(new PacketHitObject(level, x, y, this, damage), level, x, y);
        }
    }

    @Override
    public void spawnDestroyedParticles(Level level, int tileX, int tileY) {
        GameRandom random = GameRandom.globalRandom;
        Point2D.Double windDir = level.weatherLayer.getWindDirNormalized();
        float windSpeed = level.weatherLayer.getWindAmount(tileX * 32, tileY * 32) * 3.0f * level.weatherLayer.getWindSpeed();
        int particleCount = !level.objectLayer.isPlayerPlaced(tileX, tileY) ? 25 : 5;
        ParticleTypeSwitcher typeSwitcher = new ParticleTypeSwitcher(Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC);
        boolean alternate = GameRandom.globalRandom.nextBoolean();
        for (int i = 0; i < particleCount; ++i) {
            float posX = (float)(tileX * 32 + 16) + (alternate ? GameRandom.globalRandom.getFloatBetween(-1.0f, 0.0f) : GameRandom.globalRandom.getFloatBetween(0.0f, 1.0f)) * 32.0f;
            alternate = !alternate;
            float posY = tileY * 32;
            float startHeight = GameRandom.globalRandom.getFloatBetween(0.0f, 32.0f);
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
            ParticleOption particle = level.entityManager.addParticle(posX, posY, typeSwitcher.next()).sprite(GameResources.mapleLeafParticles.sprite(random.nextInt(4), 0, 20)).fadesAlphaTime(0, timeToFadeOut).sizeFadesInAndOut(15, 20, 100, 0).height(heightMover).onMoveTick((delta, lifeTime, timeAlive, lifePercent) -> {
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
            SoundManager.playSound(SoundSettingsRegistry.leavesBreakAction, SoundEffect.effect(posX, posY));
        }
    }

    @Override
    public void playDamageSound(Level level, int x, int y, boolean damageDone) {
        SoundManager.playSound(GameResources.leafpiledestroy, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16));
    }
}

