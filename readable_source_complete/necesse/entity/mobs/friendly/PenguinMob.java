/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.FollowerWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.friendly.FriendlyRopableMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class PenguinMob
extends FriendlyRopableMob {
    public static LootTable lootTable = new LootTable(new ChanceLootItem(0.02f, "weticicle"));

    public PenguinMob() {
        super(40);
        this.setSpeed(12.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -30, 32, 36);
        this.swimMaskMove = 12;
        this.swimMaskOffset = 24;
        this.swimSinkOffset = 0;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<FriendlyRopableMob>(this, new FollowerWandererAI<FriendlyRopableMob>(320, 64, 30000){

            @Override
            protected Mob getFollowingMob(FriendlyRopableMob mob) {
                return mob.getRopeMob();
            }
        }, new AIMover());
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.penguinAmbient).volume(0.85f);
    }

    @Override
    protected SoundSettings getHurtSound() {
        return new SoundSettings(GameResources.penguinHurt).volume(0.8f);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.penguinDeath).volume(1.2f);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.penguin, i, 4, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(PenguinMob.getTileCoordinate(x), PenguinMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 28;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd options = MobRegistry.Textures.penguin.initDraw().sprite(sprite.x, sprite.y, 32).addMaskShader(swimMask).light(light).pos(drawX, drawY += level.getTile(PenguinMob.getTileCoordinate(x), PenguinMob.getTileCoordinate(y)).getMobSinkingAmount(this));
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
        return 5;
    }
}

