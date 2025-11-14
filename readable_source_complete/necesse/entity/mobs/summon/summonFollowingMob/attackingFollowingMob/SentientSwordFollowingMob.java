/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ability.BooleanMobAbility;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.Blackboard;
import necesse.entity.mobs.ai.behaviourTree.leaves.CooldownAttackTargetAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerChargeChaserAI;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FlyingAttackingFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SentientSwordFollowingMob
extends FlyingAttackingFollowingMob {
    public Trail trail;
    public float moveAngle;
    public float currentAngle;
    private float toMove;
    protected boolean inAttackState;
    public BooleanMobAbility setAttackState;

    public SentientSwordFollowingMob() {
        super(10);
        this.moveAccuracy = 15;
        this.setSpeed(120.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-18, -15, 36, 30);
        this.hitBox = new Rectangle(-18, -15, 36, 36);
        this.selectBox = new Rectangle();
        this.setAttackState = this.registerAbility(new BooleanMobAbility(){

            @Override
            protected void run(boolean value) {
                SentientSwordFollowingMob.this.inAttackState = value;
            }
        });
    }

    @Override
    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        return this.summonDamage;
    }

    @Override
    public int getCollisionKnockback(Mob target) {
        return 20;
    }

    @Override
    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        Mob owner = this.getAttackOwner();
        if (owner != null && target != null) {
            target.isServerHit(damage, target.x - owner.x, target.y - owner.y, knockback, this);
            this.collisionHitCooldowns.startCooldown(target);
        }
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<SentientSwordFollowingMob>(this, new SentientSwordAI(384, CooldownAttackTargetAINode.CooldownTimer.TICK, 1000, 100, 1024, 64));
        if (this.isClient()) {
            this.trail = new Trail(this, this.getLevel(), new Color(150, 130, 170), 16.0f, 200, 0.0f);
            this.trail.drawOnTop = true;
            this.trail.removeOnFadeOut = false;
            this.getLevel().entityManager.addTrail(this.trail);
        }
    }

    @Override
    public void tickMovement(float delta) {
        this.toMove += delta;
        while (this.toMove > 4.0f) {
            float oldX = this.x;
            float oldY = this.y;
            super.tickMovement(4.0f);
            this.toMove -= 4.0f;
            Point2D.Float d = GameMath.normalize(oldX - this.x, oldY - this.y);
            this.moveAngle = (float)Math.toDegrees(Math.atan2(d.y, d.x));
            this.updateCurrentAngle();
            if (this.trail == null) continue;
            float trailOffset = 5.0f;
            this.trail.addPoint(new TrailVector(this.x + d.x * trailOffset, this.y + d.y * trailOffset, -d.x, -d.y, this.trail.thickness, 0.0f));
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        float particleAngle = this.currentAngle + 45.0f - 90.0f;
        this.getLevel().entityManager.addParticle(this.x, this.y, Particle.GType.COSMETIC).sprite(GameResources.sentientSword.sprite(0, 0, 54)).color((options, lifeTime, timeAlive, lifePercent) -> {
            float rMod = GameMath.lerp(lifePercent, 0, 110);
            float gMod = GameMath.lerp(lifePercent, 0, 75);
            float bMod = GameMath.lerp(lifePercent, 0, 35);
            options.color(new Color((int)(158.0f - rMod), (int)(48.0f + gMod), (int)(123.0f + bMod)));
            options.alpha(1.0f - 1.0f * lifePercent);
        }).size((options, lifeTime, timeAlive, lifePercent) -> options.size(54, 54)).rotation((lifeTime, timeAlive, lifePercent) -> particleAngle).lifeTime(200);
    }

    private void updateCurrentAngle() {
        if (this.inAttackState && this.currentSpeed > 10.0f) {
            if (this.moveAngle > this.currentAngle && Math.abs(this.moveAngle - this.currentAngle) >= 180.0f) {
                this.currentAngle = Math.min(this.moveAngle, this.currentAngle - 2.0f) < -180.0f ? 180.0f : this.currentAngle - 2.0f;
            } else if (this.moveAngle < this.currentAngle && Math.abs(this.currentAngle - this.moveAngle) <= 180.0f) {
                this.currentAngle = Math.max(this.moveAngle, this.currentAngle - 2.0f) < -180.0f ? 180.0f : this.currentAngle - 2.0f;
            } else if (this.moveAngle > this.currentAngle && Math.abs(this.moveAngle - this.currentAngle) <= 180.0f) {
                this.currentAngle = Math.min(this.moveAngle, this.currentAngle + 2.0f) > 180.0f ? -180.0f : this.currentAngle + 2.0f;
            } else if (this.moveAngle < this.currentAngle && Math.abs(this.currentAngle - this.moveAngle) >= 180.0f) {
                this.currentAngle = Math.max(this.moveAngle, this.currentAngle + 2.0f) > 180.0f ? -180.0f : this.currentAngle + 2.0f;
            }
        } else {
            this.currentAngle = this.currentAngle + 2.0f > 180.0f ? -180.0f : this.currentAngle + 2.0f;
        }
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.sentientSword, i, 2, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public void playDeathSound() {
        SoundManager.playSound(GameResources.cling, (SoundEffect)SoundEffect.effect(this).volume(0.2f).pitch(1.5f));
        SoundManager.playSound(GameResources.crackdeath, (SoundEffect)SoundEffect.effect(this).volume(0.3f));
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(SentientSwordFollowingMob.getTileCoordinate(x), SentientSwordFollowingMob.getTileCoordinate(y));
        int drawX = camera.getDrawX(x) - 26;
        int drawY = camera.getDrawY(y) - 26;
        TextureDrawOptionsEnd options = MobRegistry.Textures.sentientSword.initDraw().sprite(0, 0, 54).light(light).rotate(this.currentAngle + 45.0f - 90.0f, 26, 26).pos(drawX, drawY);
        topList.add(tm -> options.draw());
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    @Override
    protected void addShadowDrawables(OrderableDrawables list, Level level, int x, int y, GameLight light, GameCamera camera) {
        int drawX = camera.getDrawX(x) - 26;
        int drawY = camera.getDrawY(y) - 26 + 10;
        final TextureDrawOptionsEnd options = MobRegistry.Textures.sentientSword_shadow.initDraw().sprite(0, 0, 54).light(light).rotate(this.currentAngle + 45.0f - 90.0f, 26, 26).pos(drawX, drawY);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.trail != null) {
            this.trail.removeOnFadeOut = true;
        }
    }

    static class SentientSwordAI<T extends SentientSwordFollowingMob>
    extends PlayerFlyingFollowerChargeChaserAI<T> {
        public SentientSwordAI(int searchDistance, CooldownAttackTargetAINode.CooldownTimer cooldownTimer, int chargeCooldown, int targetStoppingDistance, int teleportDistance, int stoppingDistance) {
            super(searchDistance, cooldownTimer, chargeCooldown, targetStoppingDistance, teleportDistance, stoppingDistance);
        }

        @Override
        public void onRootSet(AINode<T> root, T mob, Blackboard<T> blackboard) {
            super.onRootSet(root, mob, blackboard);
            blackboard.onGlobalTick(event -> {
                boolean shouldBeInAttackState;
                boolean bl = shouldBeInAttackState = blackboard.getObject(Mob.class, "currentTarget") != null;
                if (mob.inAttackState != shouldBeInAttackState) {
                    mob.setAttackState.runAndSend(shouldBeInAttackState);
                }
            });
        }
    }
}

