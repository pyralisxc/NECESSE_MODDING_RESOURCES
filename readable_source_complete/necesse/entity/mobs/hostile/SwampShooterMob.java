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
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.StationaryPlayerShooterAI;
import necesse.entity.mobs.hostile.HostileMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.SwampBoltProjectile;
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

public class SwampShooterMob
extends HostileMob {
    public static LootTable lootTable = new LootTable(randomMapDrop, ChanceLootItem.between(0.5f, "swampsludge", 1, 1));
    public static GameDamage damage = new GameDamage(40.0f);

    public SwampShooterMob() {
        super(200);
        this.setSpeed(0.0f);
        this.setFriction(3.0f);
        this.setKnockbackModifier(0.0f);
        this.setArmor(10);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 48);
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<SwampShooterMob>(this, new StationaryPlayerShooterAI<SwampShooterMob>(448){

            @Override
            public void shootTarget(SwampShooterMob mob, Mob target) {
                SwampBoltProjectile projectile = new SwampBoltProjectile(SwampShooterMob.this.getLevel(), mob, mob.x, mob.y, target.x, target.y, 100.0f, 640, damage, 50);
                projectile.setTargetPrediction(target);
                SwampShooterMob.this.attack((int)(mob.x + projectile.dx * 100.0f), (int)(mob.y + projectile.dy * 100.0f), false);
                projectile.x += Math.signum(SwampShooterMob.this.attackDir.x) * 10.0f;
                projectile.y += SwampShooterMob.this.attackDir.y * 6.0f;
                SwampShooterMob.this.getLevel().entityManager.projectiles.add(projectile);
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
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.swampShooter, 10, i, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int spriteY;
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SwampShooterMob.getTileCoordinate(x), SwampShooterMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 56;
        drawY += this.getBobbing(x, y);
        drawY += level.getTile(SwampShooterMob.getTileCoordinate(x), SwampShooterMob.getTileCoordinate(y)).getMobSinkingAmount(this);
        boolean mirror = false;
        if (this.attackDir != null) {
            float threshold = 0.4f;
            if (Math.abs(this.attackDir.x) - Math.abs(this.attackDir.y) <= threshold) {
                int n = spriteY = this.attackDir.y < 0.0f ? 0 : 2;
                if (this.attackDir.x < 0.0f) {
                    mirror = true;
                }
            } else {
                spriteY = this.attackDir.x < 0.0f ? 3 : 1;
            }
        } else {
            int dir = this.getDir();
            spriteY = dir == 0 || dir == 1 ? 1 : 3;
        }
        float animProgress = this.getAttackAnimProgress();
        int spriteX = this.isAttacking ? 1 + Math.min((int)(animProgress * 4.0f), 3) : 0;
        final TextureDrawOptionsEnd body = MobRegistry.Textures.swampShooter.initDraw().sprite(spriteX, spriteY, 64).mirror(mirror, false).light(light).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                body.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        drawY += this.getBobbing(x, y);
        return shadowTexture.initDraw().sprite(0, 0, res).light(light).pos(drawX, drawY += level.getTile(SwampShooterMob.getTileCoordinate(x), SwampShooterMob.getTileCoordinate(y)).getMobSinkingAmount(this));
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        if (this.isClient()) {
            SoundManager.playSound(GameResources.flick, (SoundEffect)SoundEffect.effect(this).pitch(1.2f));
        }
    }
}

