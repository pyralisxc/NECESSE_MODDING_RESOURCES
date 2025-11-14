/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.event.ConfuseWanderAIEvent;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedCollisionPlayerChaserWandererAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.mobs.hostile.ZombieMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ConditionLootItemList;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class GoblinMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(new ConditionLootItemList((r, o) -> {
        Mob self = LootTable.expectExtra(Mob.class, o, 0);
        return self == null || self.getLevel().isCave;
    }, ZombieMob.belowSurfaceDrop), randomMapDrop, LootItem.between("coin", 5, 15));

    public GoblinMob() {
        super(50);
        this.setSpeed(30.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -32, 32, 38);
        this.swimMaskMove = 12;
        this.swimMaskOffset = 26;
        this.swimSinkOffset = 0;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<GoblinMob>(this, new ConfusedCollisionPlayerChaserWandererAI<GoblinMob>(() -> !this.getLevel().isCave && !this.getWorldEntity().isNight() && this.canDespawn, 384, new GameDamage(9.0f), 100, 40000){

            @Override
            public boolean attackTarget(GoblinMob mob, Mob target) {
                boolean success = super.attackTarget(mob, target);
                Point2D.Float runAwayDir = GameMath.normalize(mob.x - target.x, mob.y - target.y);
                int confuseTime = GameRandom.globalRandom.getIntBetween(2000, 3000);
                this.getBlackboard().submitEvent("confuseWander", new ConfuseWanderAIEvent(confuseTime, runAwayDir));
                return success;
            }
        });
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.goblin, i, 4, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(GoblinMob.getTileCoordinate(x), GoblinMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 28;
        Point sprite = this.getAnimSprite(x, y, this.getDir());
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.goblin.initDraw().sprite(sprite.x, sprite.y, 32).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(GoblinMob.getTileCoordinate(x), GoblinMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                options.draw();
                swimMask.stop();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_baby_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(this.getDir(), 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public int getRockSpeed() {
        return 10;
    }
}

