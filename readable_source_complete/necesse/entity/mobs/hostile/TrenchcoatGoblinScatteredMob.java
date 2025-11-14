/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.Supplier;
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
import necesse.entity.mobs.hostile.GoblinMob;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.mobs.hostile.TrenchcoatGoblinChestplateMob;
import necesse.entity.mobs.hostile.TrenchcoatGoblinHelmetMob;
import necesse.entity.mobs.hostile.TrenchcoatGoblinShoesMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class TrenchcoatGoblinScatteredMob
extends HostileMob {
    public static LootTable lootTable = GoblinMob.lootTable;
    private final GameTexture goblinTexture;

    public TrenchcoatGoblinScatteredMob(TrenchCoatGoblinType goblinType) {
        super(75);
        this.setSpeed(40.0f);
        this.setFriction(3.0f);
        this.goblinTexture = goblinType.texture.get();
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -32, 32, 38);
        this.swimMaskMove = 14;
        this.swimMaskOffset = 0;
        this.swimSinkOffset = 4;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<TrenchcoatGoblinScatteredMob>(this, new ConfusedCollisionPlayerChaserWandererAI<TrenchcoatGoblinScatteredMob>(() -> !this.getLevel().isCave && !this.getWorldEntity().isNight() && this.canDespawn, 384, new GameDamage(9.0f), 100, 40000){

            @Override
            public boolean attackTarget(TrenchcoatGoblinScatteredMob mob, Mob target) {
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
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), this.goblinTexture, i, 4, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(TrenchcoatGoblinScatteredMob.getTileCoordinate(x), TrenchcoatGoblinScatteredMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 56;
        Point sprite = this.getAnimSprite(x, y, this.getDir());
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd drawOptions = this.goblinTexture.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(TrenchcoatGoblinScatteredMob.getTileCoordinate(x), TrenchcoatGoblinScatteredMob.getTileCoordinate(y)).getMobSinkingAmount(this));
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                swimMask.use();
                drawOptions.draw();
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

    public static enum TrenchCoatGoblinType {
        Helmet(32, 2.0f, 1.0f, () -> MobRegistry.Textures.trenchcoatgoblin_helmet, TrenchcoatGoblinHelmetMob::new),
        Chestplate(16, 1.5f, 1.2f, () -> MobRegistry.Textures.trenchcoatgoblin_chestplate, TrenchcoatGoblinChestplateMob::new),
        Shoes(0, 1.0f, 1.4f, () -> MobRegistry.Textures.trenchcoatgoblin_shoes, TrenchcoatGoblinShoesMob::new);

        public final int startHeight;
        public final float maxTileHeight;
        public final float speedMultiplier;
        public final Supplier<GameTexture> texture;
        public final Supplier<Mob> goblinMob;

        private TrenchCoatGoblinType(int startHeight, float maxTileHeight, float speedMultiplier, Supplier<GameTexture> texture, Supplier<Mob> goblinMob) {
            this.startHeight = startHeight;
            this.maxTileHeight = maxTileHeight;
            this.speedMultiplier = speedMultiplier;
            this.texture = texture;
            this.goblinMob = goblinMob;
        }
    }
}

