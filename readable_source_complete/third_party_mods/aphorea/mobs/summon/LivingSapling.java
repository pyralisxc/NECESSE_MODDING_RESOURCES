/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.ai.behaviourTree.AINode
 *  necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI
 *  necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI
 *  necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptions
 *  necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTexture.GameTextureSection
 *  necesse.level.gameObject.TreeObject
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.summon;

import aphorea.mobs.hostile.InfectedTreant;
import aphorea.utils.magichealing.AphMagicHealing;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.AINode;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.trees.PlayerFollowerCollisionChaserAI;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.AttackingFollowingMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameObject.TreeObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class LivingSapling
extends AttackingFollowingMob {
    public static Map<Integer, Integer> hitCount = new HashMap<Integer, Integer>();
    public static GameTexture texture;
    public static GameTexture texture_shadow;
    public static String leavesTextureName;
    public static Supplier<GameTextureSection> leavesTexture;
    public int jump = 0;
    public static float jumpHeight;
    public static int jumpDuration;
    public boolean mirrored;
    public int spriteX;

    public static int getHitCount(Mob mob) {
        return hitCount.getOrDefault(mob.getUniqueID(), 0);
    }

    public static void setHitCount(Mob mob, int amount) {
        hitCount.put(mob.getUniqueID(), amount);
    }

    public LivingSapling() {
        super(100);
        this.setSpeed(40.0f);
        this.setFriction(5.0f);
        this.collision = new Rectangle(-8, -4, 16, 12);
        this.hitBox = new Rectangle(-14, -8, 28, 20);
        this.selectBox = new Rectangle(-16, -16, 32, 32);
        GameRandom random = new GameRandom();
        this.mirrored = random.getChance(0.5f);
        this.spriteX = random.getIntBetween(0, 5);
    }

    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI((Mob)this, (AINode)new PlayerFollowerCollisionChaserAI<LivingSapling>(320, this.summonDamage, 30, 1000, 640, 64){

            public boolean attackTarget(LivingSapling mob, Mob target) {
                if (LivingSapling.this.isServer() && target.isHostile) {
                    int attacks = LivingSapling.getHitCount(LivingSapling.this.getAttackOwner()) + 1;
                    if (attacks >= 10) {
                        attacks = 0;
                        AphMagicHealing.healMob(LivingSapling.this.getAttackOwner(), LivingSapling.this.getAttackOwner(), 4);
                    }
                    LivingSapling.setHitCount(LivingSapling.this.getAttackOwner(), attacks);
                }
                return super.attackTarget((Mob)mob, target);
            }
        });
        this.jump = 0;
    }

    public void clientTick() {
        super.clientTick();
        if (this.dx == 0.0f && this.dy == 0.0f) {
            this.jump = 0;
        } else {
            ++this.jump;
            if (this.jump > jumpDuration) {
                this.jump = 0;
            }
        }
        if (this.jump == 0) {
            this.setFriction(20.0f);
        } else {
            this.setFriction(0.1f);
        }
    }

    public void serverTick() {
        super.serverTick();
        if (this.dx == 0.0f && this.dy == 0.0f) {
            this.jump = 0;
        } else {
            ++this.jump;
            if (this.jump > jumpDuration) {
                this.jump = 0;
            }
        }
        if (this.jump == 0) {
            this.setFriction(20.0f);
        } else {
            this.setFriction(0.1f);
        }
    }

    public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(x / 32, y / 32);
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 16;
        drawY -= (int)(Math.sin((double)((float)this.jump / (float)jumpDuration) * Math.PI) * (double)jumpHeight);
        drawY += this.getBobbing(x, y);
        TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(this.spriteX, 0, 32).light(light).mirror(this.mirrored, false).pos(drawX, drawY += this.getLevel().getTile(this.getTileX(), this.getTileY()).getMobSinkingAmount((Mob)this));
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
        GameTexture shadowTexture = texture_shadow;
        int drawX = camera.getDrawX(x) - 16;
        int drawY = camera.getDrawY(y) - 16;
        return shadowTexture.initDraw().light(light).mirror(this.mirrored, false).pos(drawX, drawY += this.getBobbing(x, y));
    }

    public int getRockSpeed() {
        return 20;
    }

    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        int leaves = GameRandom.globalRandom.getIntBetween(1, 2);
        this.spawnLeafParticles(this.getLevel(), (int)this.x, (int)this.y, 10, leaves, new Point2D.Double(), 0.0f);
    }

    public void spawnLeafParticles(Level level, int x, int y, int minStartHeight, int amount, Point2D.Double windDir, float windSpeed) {
        if (InfectedTreant.leavesTexture != null) {
            TreeObject.spawnLeafParticles((Level)level, (int)x, (int)y, (int)16, (int)minStartHeight, (int)14, (int)amount, (Point2D.Double)windDir, (float)windSpeed, InfectedTreant.leavesTexture);
        }
    }

    static {
        leavesTextureName = "oakleaves";
        jumpHeight = 10.0f;
        jumpDuration = 4;
    }
}

