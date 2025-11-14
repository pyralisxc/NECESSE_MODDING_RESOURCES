/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GroundPillar;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.StationaryPlayerShooterAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.FrostSentryProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class FrostSentryMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(randomMapDrop, LootItem.between("frostshard", 1, 2));
    public static GameDamage damage = new GameDamage(17.0f);

    public FrostSentryMob() {
        super(120);
        this.setSpeed(0.0f);
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.0f);
        this.setArmor(5);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -23, 28, 32);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<FrostSentryMob>(this, new StationaryPlayerShooterAI<FrostSentryMob>(320){

            @Override
            public void shootTarget(FrostSentryMob mob, Mob target) {
                FrostSentryProjectile projectile = new FrostSentryProjectile(FrostSentryMob.this.getLevel(), mob, mob.x, mob.y, target.x, target.y, 75.0f, 512, damage, 50);
                projectile.x -= projectile.dx * 20.0f;
                projectile.y -= projectile.dy * 20.0f;
                FrostSentryMob.this.attack((int)(mob.x + projectile.dx * 100.0f), (int)(mob.y + projectile.dy * 100.0f), false);
                FrostSentryMob.this.getLevel().entityManager.projectiles.add(projectile);
            }
        });
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
    }

    @Override
    public boolean canBePushed(Mob other) {
        return false;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.frostSentry, 1 + GameRandom.globalRandom.nextInt(5), 0, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        float animProgress;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(FrostSentryMob.getTileCoordinate(x), FrostSentryMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 15;
        int drawY = camera.getDrawY(y) - 26;
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(FrostSentryMob.getTileCoordinate(x), FrostSentryMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        if (this.inLiquid(x, y)) {
            drawY -= 10;
        }
        float wiggle = (animProgress = GameMath.limit(this.getAttackAnimProgress(), 0.0f, 1.0f)) < 0.5f ? animProgress * 2.0f : Math.abs((animProgress - 0.5f) * 2.0f - 1.0f);
        int pixelChange = (int)(wiggle * 5.0f);
        final TextureDrawOptionsEnd body = MobRegistry.Textures.frostSentry.initDraw().sprite(0, 0, 32).size(32 - pixelChange * 2, 32 - pixelChange).light(light).pos(drawX + pixelChange, drawY + pixelChange);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                body.draw();
            }
        });
        if (this.inLiquid(x, y)) {
            y -= 10;
        }
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        drawY += this.getBobbing(x, y);
        return shadowTexture.initDraw().sprite(0, 0, res).light(light).pos(drawX, drawY += level.getTile(FrostSentryMob.getTileCoordinate(x), FrostSentryMob.getTileCoordinate(y)).getMobSinkingAmount(this));
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.jingle, (SoundEffect)SoundEffect.effect(this).pitch(1.2f));
        }
    }

    public static class FrostPillar
    extends GroundPillar {
        public GameTextureSection texture;
        public boolean mirror = GameRandom.globalRandom.nextBoolean();

        public FrostPillar(int x, int y, double spawnDistance, long spawnTime) {
            super(x, y, spawnDistance, spawnTime);
            this.texture = MobRegistry.Textures.cryoQueen == null ? null : GameRandom.globalRandom.getOneOf(new GameTextureSection(MobRegistry.Textures.frostSentry).sprite(1, 0, 32), new GameTextureSection(MobRegistry.Textures.frostSentry).sprite(2, 0, 32), new GameTextureSection(MobRegistry.Textures.frostSentry).sprite(3, 0, 32), new GameTextureSection(MobRegistry.Textures.frostSentry).sprite(4, 0, 32), new GameTextureSection(MobRegistry.Textures.frostSentry).sprite(5, 0, 32));
            this.behaviour = new GroundPillar.TimedBehaviour(300, 200, 800);
        }

        @Override
        public DrawOptions getDrawOptions(Level level, long currentTime, double distanceMoved, GameCamera camera) {
            GameLight light = level.getLightLevel(Entity.getTileCoordinate(this.x), Entity.getTileCoordinate(this.y));
            int drawX = camera.getDrawX(this.x);
            int drawY = camera.getDrawY(this.y);
            double height = this.getHeight(currentTime, distanceMoved);
            int endY = (int)(height * (double)this.texture.getHeight());
            return this.texture.section(0, this.texture.getWidth(), 0, endY).initDraw().mirror(this.mirror, false).light(light).pos(drawX - this.texture.getWidth() / 2, drawY - endY);
        }
    }
}

