/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.summon.FollowingWormMobBody;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.RubyDragonHeadFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RubyDragonBodyFollowingMob
extends FollowingWormMobBody<RubyDragonHeadFollowingMob, RubyDragonBodyFollowingMob> {
    ParticleTypeSwitcher pTypeSwitcher = new ParticleTypeSwitcher(Particle.GType.COSMETIC, Particle.GType.IMPORTANT_COSMETIC);
    public int spriteY;
    private final TicksPerSecond particleSpawner = TicksPerSecond.ticksPerSecond(20);
    public ModifierValue<?>[] modifiers = new ModifierValue[0];
    public GameDamage collisionDamage = new GameDamage(0.0f);

    public RubyDragonBodyFollowingMob() {
        super(1200);
        this.setArmor(15);
        this.collision = new Rectangle(-20, -15, 40, 30);
        this.hitBox = new Rectangle(-25, -20, 50, 40);
        this.selectBox = new Rectangle(-32, -60, 64, 64);
    }

    @Override
    public GameMessage getLocalization() {
        return new LocalMessage("mob", "rubydragon");
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return this.collisionDamage;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isVisible()) {
            this.particleSpawner.gameTick();
            while (this.particleSpawner.shouldTick()) {
                ComputedValue<GameObject> obj = new ComputedValue<GameObject>(() -> this.getLevel().getObject(this.getTileX(), this.getTileY()));
                if (this.height < 20.0f && (obj.get().isWall || obj.get().isRock)) {
                    this.getLevel().entityManager.addTopParticle(this.x + GameRandom.globalRandom.floatGaussian() * 10.0f, this.y + GameRandom.globalRandom.floatGaussian() * 7.0f + 5.0f, this.pTypeSwitcher.next()).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0f, GameRandom.globalRandom.floatGaussian() * 3.0f).smokeColor().heightMoves(10.0f, GameRandom.globalRandom.getFloatBetween(30.0f, 40.0f)).lifeTime(200);
                    continue;
                }
                if (!(this.height < 0.0f)) continue;
                this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 10.0f, this.y + GameRandom.globalRandom.floatGaussian() * 7.0f + 5.0f, this.pTypeSwitcher.next()).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0f, GameRandom.globalRandom.floatGaussian() * 3.0f).smokeColor().heightMoves(10.0f, GameRandom.globalRandom.getFloatBetween(30.0f, 40.0f)).lifeTime(200);
            }
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        if (!this.isVisible()) {
            return;
        }
        for (int i = 0; i < 3; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.rubyDragon, 2, GameRandom.globalRandom.nextInt(6), 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y);
        if (this.next != null) {
            Point2D.Float dir = new Point2D.Float(((RubyDragonBodyFollowingMob)this.next).x - (float)x, ((RubyDragonBodyFollowingMob)this.next).y - ((RubyDragonBodyFollowingMob)this.next).height - ((float)y - this.height));
            float angle = GameMath.fixAngle(GameMath.getAngle(dir));
            MobDrawable drawOptions = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.rubyDragon, 0, this.spriteY, 64), null, light, (int)this.height, angle, drawX, drawY, 96);
            topList.add(drawOptions);
        }
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.sandWorm_shadow;
        int drawX = camera.getDrawX(x) - shadowTexture.getWidth() / 2;
        int drawY = camera.getDrawY(y) - shadowTexture.getHeight() / 2;
        return shadowTexture.initDraw().light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        if (this.modifiers == null) {
            return super.getDefaultModifiers();
        }
        return Stream.of(this.modifiers);
    }
}

