/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

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
import necesse.entity.mobs.hostile.bosses.CrystalDragonHead;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class CrystalDragonBody
extends BossWormMobBody<CrystalDragonHead, CrystalDragonBody> {
    public int spriteY;
    public TicksPerSecond particleSpawner = TicksPerSecond.ticksPerSecond(50);

    public CrystalDragonBody() {
        super(1000);
        this.isSummoned = true;
        this.collision = new Rectangle(-30, -25, 60, 50);
        this.hitBox = new Rectangle(-40, -35, 80, 70);
        this.selectBox = new Rectangle(-40, -60, 80, 80);
    }

    @Override
    public GameMessage getLocalization() {
        CrystalDragonHead head = (CrystalDragonHead)this.master.get(this.getLevel());
        if (head != null) {
            return head.getLocalization();
        }
        return new StaticMessage("crystaldragonbody");
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return CrystalDragonHead.bodyCollisionDamage;
    }

    @Override
    public boolean canCollisionHit(Mob target) {
        return this.height < 45.0f && super.canCollisionHit(target);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isVisible()) {
            this.particleSpawner.gameTick();
            while (this.particleSpawner.shouldTick()) {
                this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 45.0f, this.y + GameRandom.globalRandom.floatGaussian() * 30.0f + 5.0f, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0f, GameRandom.globalRandom.floatGaussian() * 3.0f).sizeFades(5, 10).givesLight().heightMoves(this.height + 10.0f, this.height + GameRandom.globalRandom.getFloatBetween(30.0f, 40.0f)).lifeTime(1000);
                if (this.spriteY != 7) continue;
                this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 15.0f, this.y + GameRandom.globalRandom.floatGaussian() * 10.0f + 5.0f, Particle.GType.COSMETIC).sprite(GameResources.pearlescentShardParticles.sprite(GameRandom.globalRandom.nextInt(4), 0, 18, 24)).movesFriction(-this.dx * 20.0f, -this.dy * 20.0f, 0.8f).sizeFades(11, 22).ignoreLight(true).givesLight().heightMoves(this.height + 10.0f, this.height + GameRandom.globalRandom.getFloatBetween(30.0f, 40.0f)).lifeTime(2000);
            }
        }
    }

    @Override
    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue<Float>(BuffModifiers.SLOW, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)), new ModifierValue<Float>(BuffModifiers.POISON_DAMAGE_FLAT, Float.valueOf(0.0f)).max(Float.valueOf(0.0f)));
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        if (!this.isVisible()) {
            return;
        }
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.crystalDragon, 4, this.spriteY + GameRandom.globalRandom.getIntBetween(0, 1), 64, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (!this.isVisible()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(x) - 112;
        int drawY = camera.getDrawY(y);
        if (this.next != null) {
            Point2D.Float dir = new Point2D.Float(((CrystalDragonBody)this.next).x - (float)x, ((CrystalDragonBody)this.next).y - ((CrystalDragonBody)this.next).height - ((float)y - this.height));
            float angle = GameMath.fixAngle(GameMath.getAngle(dir));
            final MobDrawable drawOptions = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.crystalDragon, 0, this.spriteY, 224), null, light.minLevelCopy(100.0f), (int)this.height, angle, drawX, drawY, 130);
            MobDrawable drawOptionsShadow = WormMobHead.getAngledDrawable(new GameSprite(MobRegistry.Textures.crystalDragon_shadow, 0, this.spriteY, 224), null, light, (int)this.height, angle, drawX, drawY + 40, 130);
            topList.add(new MobDrawable(){

                @Override
                public void draw(TickManager tickManager) {
                    drawOptions.draw(tickManager);
                }
            });
            tileList.add(drawOptionsShadow::draw);
        }
    }
}

