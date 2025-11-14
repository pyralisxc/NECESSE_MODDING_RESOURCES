/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedCollisionPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.mobs.hostile.ZombieMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.light.GameLight;

public class IncursionCrawlingZombieMob
extends HostileMob {
    public static LootTable lootTable = ZombieMob.lootTable;

    public IncursionCrawlingZombieMob() {
        super(250);
        this.setSpeed(55.0f);
        this.setFriction(4.0f);
        this.isSummoned = true;
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-18, -24, 36, 36);
        this.swimMaskMove = 16;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = 0;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<IncursionCrawlingZombieMob>(this, new ConfusedCollisionPlayerChaserWandererAI(() -> this.getLevel().buffManager.getModifier(LevelModifiers.ENEMIES_RETREATING), 576, new GameDamage(50.0f), 100, 40000));
    }

    @Override
    public DeathMessageTable getDeathMessages() {
        return this.getDeathMessages("zombie", 3);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.crawlingZombie.body, 12, i, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(IncursionCrawlingZombieMob.getTileCoordinate(x), IncursionCrawlingZombieMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 40;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd body = MobRegistry.Textures.crawlingZombie.body.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(IncursionCrawlingZombieMob.getTileCoordinate(x), IncursionCrawlingZombieMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                body.draw();
            }
        });
        TextureDrawOptionsEnd shadow = MobRegistry.Textures.crawlingZombie.shadow.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX + swimMask.drawXOffset, drawY + swimMask.drawYOffset);
        tileList.add(tm -> shadow.draw());
    }

    @Override
    public int getSwimMaskOffset() {
        int dir = this.getDir();
        if (dir == 0 || dir == 2) {
            return super.getSwimMaskOffset() - 4;
        }
        return super.getSwimMaskOffset();
    }

    @Override
    public int getRockSpeed() {
        return 15;
    }
}

