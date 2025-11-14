/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.MaskShaderOptions;
import necesse.entity.mobs.MaxHealthGetter;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.CrystalGolemMob;
import necesse.entity.mobs.hostile.bosses.ascendedWizard.AscendedWizardMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.laserProjectile.AscendedGolemBeamProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class AscendedGolemMob
extends CrystalGolemMob {
    protected int lifeTime = 0;
    protected int deathTime = 20000;
    public static MaxHealthGetter MAX_HEALTH = new MaxHealthGetter(400, 750, 1000, 1300, 1800);

    public AscendedGolemMob() {
        this.difficultyChanges.setMaxHealth(MAX_HEALTH);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.lifeTime += 50;
        if (this.lifeTime >= this.deathTime) {
            this.remove(0.0f, 0.0f, null, true);
        }
    }

    @Override
    public void spawnChargeParticles() {
        for (int i = 0; i < 2; ++i) {
            int angle = GameRandom.globalRandom.nextInt(360);
            Point2D.Float dir = GameMath.getAngleDir(angle);
            float range = GameRandom.globalRandom.getFloatBetween(25.0f, 40.0f);
            float startX = this.x + dir.x * range;
            float startY = this.y + 4.0f;
            float endHeight = 29.0f;
            float startHeight = endHeight + dir.y * range;
            int lifeTime = GameRandom.globalRandom.getIntBetween(200, 500);
            float speed = dir.x * range * 250.0f / (float)lifeTime;
            Color color1 = new Color(255, 0, 231);
            Color color2 = new Color(207, 32, 190);
            Color color3 = new Color(163, 52, 152);
            Color color = GameRandom.globalRandom.getOneOf(color1, color2, color3);
            this.getLevel().entityManager.addParticle(startX, startY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.puffParticles.sprite(GameRandom.globalRandom.nextInt(5), 0, 12)).sizeFades(10, 16).rotates().heightMoves(startHeight, endHeight).movesConstant(-speed, 0.0f).color(color).givesLight(285.0f, 1.0f).ignoreLight(true).fadesAlphaTime(100, 50).lifeTime(lifeTime);
        }
    }

    @Override
    public Color getWarningBeamColor(int alpha) {
        return new Color(227, 0, 255, alpha);
    }

    @Override
    public Projectile getProjectile(int targetX, int targetY, int distance) {
        return new AscendedGolemBeamProjectile(this.getLevel(), this, this.x, this.y, targetX, targetY, distance, AscendedWizardMob.crystalGolemDamage, 20);
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        GameRandom random = GameRandom.globalRandom;
        float anglePerParticle = 36.0f;
        for (int i = 0; i < 10; ++i) {
            int angle = (int)((float)i * anglePerParticle + random.nextFloat() * anglePerParticle);
            float dx = (float)Math.sin(Math.toRadians(angle)) * 20.0f;
            float dy = (float)Math.cos(Math.toRadians(angle)) * 20.0f;
            this.getLevel().entityManager.addParticle(this, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.ascendedParticle.sprite(0, 0, 20)).sizeFades(10, 20).ignoreLight(true).heightMoves(0.0f, -30.0f).movesFriction(dx * 2.0f, dy * 2.0f, 0.8f).lifeTime(1500);
        }
    }

    @Override
    protected SoundSettings getHitDeathSound() {
        return new SoundSettings(GameResources.fadedeath2).fallOffDistance(1000).basePitch(1.5f);
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 22 - 10;
        int drawY = camera.getDrawY(y) - 44 - 7 - 6;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        drawY += this.getBobbing(x, y);
        drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount(this);
        if (this.isAttacking) {
            sprite.x = 0;
        }
        final MaskShaderOptions swimMask = this.getSwimMaskShaderOptions(this.inLiquidFloat(x, y));
        final TextureDrawOptionsEnd drawOptions = MobRegistry.Textures.ascendedGolem.initDraw().sprite(sprite.x, sprite.y, 64).addMaskShader(swimMask).light(light.minLevelCopy(150.0f)).pos(drawX, drawY);
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
}

