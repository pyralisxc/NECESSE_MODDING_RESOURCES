/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.registries.MobRegistry$Textures
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode
 *  necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.particle.FleshParticle
 *  necesse.entity.particle.Particle
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.runicsummons;

import aphorea.mobs.runicsummons.RunicAttackingFollowingMob;
import aphorea.mobs.summon.BabyUnstableGelSlime;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.CollisionChaserAINode;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class RunicUnstableGelSlime
extends RunicAttackingFollowingMob {
    public static GameTexture texture = BabyUnstableGelSlime.texture;
    public int count;

    public RunicUnstableGelSlime() {
        super(5);
        this.setSpeed(40.0f);
        this.setFriction(2.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-13, -14, 26, 24);
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new PlayerFollowerCollisionChaserAI<Mob>(Integer.MAX_VALUE, new GameDamage(0.0f), 30, 1000, Integer.MAX_VALUE, 64){

            public boolean attackTarget(Mob mob, Mob target) {
                float damagePercent = 0.05f;
                if (target.isBoss()) {
                    damagePercent /= 50.0f;
                } else if (target.isPlayer || target.isHuman) {
                    damagePercent /= 5.0f;
                }
                return CollisionChaserAINode.simpleAttack((Mob)mob, (Mob)target, (GameDamage)new GameDamage((float)target.getMaxHealth() * damagePercent, 1000000.0f), (int)this.knockback);
            }
        });
        this.count = 0;
    }

    public void serverTick() {
        super.serverTick();
        ++this.count;
        if ((float)this.count >= 20.0f * this.effectNumber) {
            if (this.isFollowing()) {
                ((ItemAttackerMob)this.getFollowingMob()).serverFollowersManager.removeFollower((Mob)this, false, false);
            }
            this.remove();
        }
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle((Particle)new FleshParticle(this.getLevel(), texture, GameRandom.globalRandom.nextInt(5), 8, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(this.getTileX(), this.getTileY());
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 25;
        Point sprite = this.getAnimSprite(x, y, this.getDir());
        drawY += this.getBobbing(x, y);
        TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(sprite.x, sprite.y, 32).light(light).pos(drawX, drawY += this.getLevel().getTile(this.getTileX(), this.getTileY()).getMobSinkingAmount((Mob)this));
        list.add(new MobDrawable((DrawOptions)drawOptions){
            final /* synthetic */ DrawOptions val$drawOptions;
            {
                this.val$drawOptions = drawOptions;
            }

            public void draw(TickManager tickManager) {
                this.val$drawOptions.draw();
            }
        });
        if (!this.isWaterWalking()) {
            this.addShadowDrawables(tileList, level, x, y, light, camera);
        }
    }

    protected TextureDrawOptions getShadowDrawOptions(Level level, int x, int y, GameLight light, GameCamera camera) {
        GameTexture shadowTexture = MobRegistry.Textures.human_baby_shadow;
        int res = shadowTexture.getHeight();
        int drawX = camera.getDrawX(x) - res / 2;
        int drawY = camera.getDrawY(y) - res / 2;
        return shadowTexture.initDraw().sprite(this.getDir(), 0, res).light(light).pos(drawX, drawY += this.getBobbing(x, y));
    }

    public int getRockSpeed() {
        return 20;
    }
}

