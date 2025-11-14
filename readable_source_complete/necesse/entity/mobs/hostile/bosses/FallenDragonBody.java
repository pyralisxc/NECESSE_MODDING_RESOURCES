/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.bosses.BossWormMobBody;
import necesse.entity.mobs.hostile.bosses.FallenDragonHead;
import necesse.entity.mobs.hostile.bosses.FallenWizardMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FallenDragonBody
extends BossWormMobBody<FallenDragonHead, FallenDragonBody> {
    public int shadowSprite = 0;
    public int spriteY;
    public boolean spawnsParticles;
    public TicksPerSecond particleSpawner = TicksPerSecond.ticksPerSecond(10);

    public FallenDragonBody() {
        super(1000);
        this.isSummoned = true;
        this.collision = new Rectangle(-18, -15, 36, 30);
        this.hitBox = new Rectangle(-25, -20, 50, 40);
        this.selectBox = new Rectangle(-32, -80, 64, 84);
    }

    @Override
    public GameMessage getLocalization() {
        FallenDragonHead head = (FallenDragonHead)this.master.get(this.getLevel());
        if (head != null) {
            return head.getLocalization();
        }
        return new StaticMessage("flyingspiritsbody");
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return FallenWizardMob.dragonBodyDamage;
    }

    @Override
    public boolean canCollisionHit(Mob target) {
        return this.height < 45.0f && super.canCollisionHit(target);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.spawnsParticles && this.isVisible()) {
            this.particleSpawner.gameTick();
            while (this.particleSpawner.shouldTick()) {
                this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 15.0f, this.y + GameRandom.globalRandom.floatGaussian() * 10.0f + 5.0f, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0f, GameRandom.globalRandom.floatGaussian() * 3.0f).sizeFades(15, 25).color(new Color(51, 46, 59)).heightMoves(this.height + 10.0f, this.height + GameRandom.globalRandom.getFloatBetween(30.0f, 40.0f)).lifeTime(350);
            }
        }
    }

    @Override
    public int getFlyingHeight() {
        return 20;
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.POISON_DAMAGE_FLAT, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.FIRE_DAMAGE_FLAT, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.FROST_DAMAGE_FLAT, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.FIRE_DAMAGE, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)));
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        if (!this.isVisible()) {
            return;
        }
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.fallenWizardDragon, 2, GameRandom.globalRandom.nextInt(6), 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
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
            Point2D.Float dir = new Point2D.Float(((FallenDragonBody)this.next).x - (float)x, ((FallenDragonBody)this.next).y - ((FallenDragonBody)this.next).height - ((float)y - this.height));
            float angle = GameMath.fixAngle(GameMath.getAngle(dir));
            MobDrawable drawOptions = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.fallenWizardDragon, 0, this.spriteY, 64), null, light, (int)this.height, angle, drawX, drawY, 96);
            topList.add(drawOptions);
        }
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.swampGuardian_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(this.shadowSprite, 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }
}

