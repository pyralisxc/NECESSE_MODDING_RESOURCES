/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.util.GameUtils
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerCollisionChaserAI
 *  necesse.entity.mobs.ai.behaviourTree.util.AIMover
 *  necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FlyingAttackingFollowingMob
 *  necesse.entity.particle.FleshParticle
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.summon;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFlyingFollowerCollisionChaserAI;
import necesse.entity.mobs.ai.behaviourTree.util.AIMover;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.FlyingAttackingFollowingMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class Onyx
extends FlyingAttackingFollowingMob {
    public static GameTexture texture;
    public int count;

    public Onyx() {
        super(10);
        this.accelerationMod = 1.0f;
        this.moveAccuracy = 10;
        this.setSpeed(100.0f);
        this.setFriction(1.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-14, -12, 28, 24);
        this.selectBox = new Rectangle(-14, -41, 28, 40);
    }

    public GameDamage getCollisionDamage(Mob target, boolean fromPacket, ServerClient packetSubmitter) {
        float damagePercent = 0.05f;
        if (target.isBoss()) {
            damagePercent /= 50.0f;
        } else if (target.isPlayer || target.isHuman) {
            damagePercent /= 5.0f;
        }
        return new GameDamage((float)target.getMaxHealth() * damagePercent, 1000000.0f);
    }

    public int getCollisionKnockback(Mob target) {
        return 5;
    }

    public void handleCollisionHit(Mob target, GameDamage damage, int knockback) {
        Mob owner = this.getAttackOwner();
        if (owner != null) {
            target.isServerHit(damage, target.x - owner.x, target.y - owner.y, (float)knockback, (Attacker)this);
            this.collisionHitCooldowns.startCooldown(target);
            if (target.isHostile) {
                this.getLevel().entityManager.events.add((LevelEvent)new MobHealthChangeEvent(owner, (int)Math.max((float)owner.getMaxHealth() * 0.01f, 1.0f)));
            }
        }
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new PlayerFlyingFollowerCollisionChaserAI(576, null, 15, 500, 640, 32), (AIMover)new FlyingAIMover());
        this.count = 0;
    }

    public void serverTick() {
        super.serverTick();
        ++this.count;
        if (this.count >= 100) {
            if (this.isFollowing()) {
                ((ItemAttackerMob)this.getFollowingMob()).serverFollowersManager.removeFollower((Mob)this, false, false);
            }
            this.remove();
        }
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle((Particle)new FleshParticle(this.getLevel(), texture, i, 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32).minLevelCopy(100.0f);
        int drawX = camera.getDrawX(x) - 32;
        int drawY = camera.getDrawY(y) - 55;
        int dir = this.getDir();
        Point sprite = this.getAnimSprite(x, y, dir);
        float bobbing = GameUtils.getBobbing((long)this.getWorldEntity().getTime(), (int)1000) * 5.0f;
        drawY = (int)((float)drawY + bobbing);
        final TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY += this.getLevel().getTile(x / 32, y / 32).getMobSinkingAmount((Mob)this));
        list.add(new MobDrawable(){

            public void draw(TickManager tickManager) {
                drawOptions.draw();
            }
        });
        this.addShadowDrawables(tileList, level, x, y, light, camera);
    }

    public Point getAnimSprite(int x, int y, int dir) {
        return new Point(GameUtils.getAnim((long)this.getWorldEntity().getTime(), (int)4, (int)300), dir);
    }
}

