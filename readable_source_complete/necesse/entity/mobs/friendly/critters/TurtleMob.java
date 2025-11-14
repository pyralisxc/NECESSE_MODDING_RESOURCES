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
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.MobSpawnLocation;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.light.GameLight;

public class TurtleMob
extends CritterMob {
    public static LootTable lootTable = new LootTable();

    public TurtleMob() {
        this.setSpeed(6.0f);
        this.setFriction(3.0f);
        this.setSwimSpeed(2.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-19, -20, 38, 28);
        this.swimMaskMove = 8;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = 0;
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
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.turtle.body, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(TurtleMob.getTileCoordinate(x), TurtleMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 30;
        int drawY = camera.getDrawY(y) - 54;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.turtle.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(TurtleMob.getTileCoordinate(x), TurtleMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.turtle.shadow.initDraw().sprite(0, dir, 64).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public int getRockSpeed() {
        return 10;
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
        return new SoundSettings(GameResources.turtleAmbient).volume(0.25f);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.turtleDeath).volume(0.2f);
    }
}

