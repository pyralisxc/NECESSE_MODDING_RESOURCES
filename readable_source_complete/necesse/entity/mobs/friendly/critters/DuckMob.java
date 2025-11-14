/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.critters;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.CritterAI;
import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.light.GameLight;

public class DuckMob
extends CritterMob {
    public static LootTable lootTable = new LootTable(new LootItem("duckbreast"));

    public DuckMob() {
        this.setSpeed(15.0f);
        this.setFriction(3.0f);
        this.setSwimSpeed(1.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -28, 32, 34);
        this.swimMaskMove = 10;
        this.swimMaskOffset = 4;
        this.swimSinkOffset = 4;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<DuckMob>(this, new CritterAI());
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public int getTileWanderPriority(TilePosition pos, Biome baseBiome) {
        if (pos.tileID() == TileRegistry.waterID) {
            return 1000;
        }
        int height = pos.level.liquidManager.getHeight(pos.tileX, pos.tileY);
        if (height >= 0 && height <= 3) {
            return 1000;
        }
        return super.getTileWanderPriority(pos, baseBiome);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 5; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.duck.body, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(DuckMob.getTileCoordinate(x), DuckMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 30;
        int drawY = camera.getDrawY(y) - 48;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.duck.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(DuckMob.getTileCoordinate(x), DuckMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.duck.shadow.initDraw().sprite(0, dir, 64).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public int getSwimMaskMove() {
        if (this.getDir() == 0) {
            return super.getSwimMaskMove() - 4;
        }
        return super.getSwimMaskMove();
    }

    @Override
    public int getSwimSinkOffset() {
        if (this.getDir() == 0) {
            return super.getSwimSinkOffset() - 6;
        }
        return super.getSwimSinkOffset();
    }

    @Override
    protected int getRockSpeed() {
        return 8;
    }

    @Override
    public MobSpawnLocation checkSpawnLocation(MobSpawnLocation location) {
        return location.checkNotLevelCollides().checkTile((tileX, tileY) -> {
            int tileID = this.getLevel().getTileID((int)tileX, (int)tileY);
            if (tileID == TileRegistry.waterID) {
                return true;
            }
            int height = this.getLevel().liquidManager.getHeight((int)tileX, (int)tileY);
            return height >= 0 && height <= 3;
        });
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameRandom.globalRandom.getOneOf(GameResources.duckAmbients)).volume(0.6f);
    }

    @Override
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.duckHurt).volume(0.3f);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.duckDeath).volume(0.2f);
    }
}

