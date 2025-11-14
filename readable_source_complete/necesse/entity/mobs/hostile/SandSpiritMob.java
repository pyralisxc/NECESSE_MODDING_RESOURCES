/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.ConfusedCollisionPlayerChaserWandererAI;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.hostile.FlyingHostileMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.TopFleshParticle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.LootTable;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SandSpiritMob
extends FlyingHostileMob {
    public static LootTable lootTable = new LootTable();

    public SandSpiritMob() {
        super(150);
        this.setSpeed(30.0f);
        this.setFriction(0.5f);
        this.setKnockbackModifier(0.2f);
        this.setArmor(10);
        this.moveAccuracy = 10;
        this.collision = new Rectangle(-12, -12, 24, 24);
        this.hitBox = new Rectangle(-16, -16, 32, 32);
        this.selectBox = new Rectangle(-18, -40, 36, 54);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<SandSpiritMob>(this, new ConfusedCollisionPlayerChaserWandererAI(null, 384, new GameDamage(48.0f), 100, 40000), new FlyingAIMover());
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            int sprite = GameRandom.globalRandom.nextInt(4);
            this.getLevel().entityManager.addParticle(new TopFleshParticle(this.getLevel(), MobRegistry.Textures.sandSpirit, sprite, 2, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.fadedeath2);
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SandSpiritMob.getTileCoordinate(x), SandSpiritMob.getTileCoordinate(y));
        int bobbing = (int)(GameUtils.getBobbing(level.getWorldEntity().getTime(), 1600) * 5.0f);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 48 + bobbing;
        TextureDrawOptionsEnd body = MobRegistry.Textures.sandSpirit.initDraw().sprite(0, 0, 64).mirror(this.moveX < 0.0f, false).alpha(0.7f).light(light).pos(drawX, drawY);
        int minLight = 100;
        TextureDrawOptionsEnd eyes = MobRegistry.Textures.sandSpirit.initDraw().sprite(1, 0, 64).mirror(this.moveX < 0.0f, false).alpha(0.7f).light(light.minLevelCopy(minLight)).pos(drawX, drawY);
        this.addShadowDrawables(topList, level, x, y, light, camera);
        topList.add(tm -> {
            body.draw();
            eyes.draw();
        });
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2 + 4;
        return shadowTexture.initDraw().sprite(0, 0, res).light(light).pos(drawX, drawY);
    }
}

