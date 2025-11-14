/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedCollisionPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.bosses.BossMob;
import necesse.entity.mobs.hostile.bosses.QueenSpiderMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderHatchlingMob
extends BossMob {
    public static LootTable lootTable = new LootTable();
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(15, 20, 25, 30, 35);
    public long deathTime;

    public SpiderHatchlingMob() {
        super(100);
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
        this.isSummoned = true;
        this.setSpeed(40.0f);
        this.setFriction(2.0f);
        this.attackCooldown = 500;
        this.collision = new Rectangle(-11, -8, 22, 16);
        this.hitBox = new Rectangle(-14, -16, 28, 28);
        this.selectBox = new Rectangle(-14, -16, 28, 28);
        this.swimMaskMove = 8;
        this.swimMaskOffset = 26;
        this.swimSinkOffset = 0;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<SpiderHatchlingMob>(this, new ConfusedCollisionPlayerChaserWandererAI(null, 1600, QueenSpiderMob.hatchlingDamage, 100, 10000));
        this.deathTime = this.getWorldEntity().getTime() + 20000L;
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.deathTime <= this.getWorldEntity().getTime()) {
            this.setHealth(0);
        }
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.spiderHatchling.body, 12, i, 16, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SpiderHatchlingMob.getTileCoordinate(x), SpiderHatchlingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 15;
        int drawY = camera.getDrawY(y) - 22;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.spiderHatchling.body.initDraw().sprite(sprite.x, sprite.y, 32).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(SpiderHatchlingMob.getTileCoordinate(x), SpiderHatchlingMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.spiderHatchling.shadow.initDraw().sprite(sprite.x, sprite.y, 32).light(light).pos(drawX, drawY);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public int getRockSpeed() {
        return 7;
    }
}

