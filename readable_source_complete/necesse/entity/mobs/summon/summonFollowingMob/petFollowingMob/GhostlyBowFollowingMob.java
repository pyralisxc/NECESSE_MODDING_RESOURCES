/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.ai.behaviourTree.BehaviourTreeAI;
import necesse.entity.mobs.ai.behaviourTree.leaves.PlayerFollowerAINode;
import necesse.entity.mobs.ai.behaviourTree.util.FlyingAIMover;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.setBonusBuffs.DeepfrostSetBonusBuff;
import necesse.entity.mobs.summon.summonFollowingMob.petFollowingMob.PetFollowingMob;
import necesse.entity.particle.Particle;
import necesse.gfx.ThemeColorRegistry;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class GhostlyBowFollowingMob
extends PetFollowingMob {
    protected final int textureWidth = 30;
    protected final int textureHeight = 52;
    protected float displayAngle;
    protected float targetAngle;
    protected int rotationDisableDuration = 20;
    protected int rotationDisabledTimer = 0;

    public GhostlyBowFollowingMob() {
        super(10);
        this.setFriction(2.0f);
        this.moveAccuracy = 10;
        this.setSpeed(70.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -16, 24, 28);
        this.selectBox = new Rectangle(-16, -40, 32, 50);
        this.swimMaskMove = 22;
        this.swimMaskOffset = 16;
        this.swimSinkOffset = -12;
    }

    @Override
    public void init() {
        super.init();
        this.ai = new BehaviourTreeAI<GhostlyBowFollowingMob>(this, new PlayerFollowerAINode(480, 96), new FlyingAIMover());
    }

    public float getDesiredHeight() {
        float perc = GameUtils.getAnimFloat(this.getLevel().getWorldEntity().getTime(), 3000);
        return GameMath.sin(perc * 360.0f) * 10.0f - 20.0f;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (!this.isClient()) {
            return;
        }
        this.updateBowRotation();
        if (this.rotationDisabledTimer > 0) {
            --this.rotationDisabledTimer;
        }
        if (this.moveX == 0.0f && this.moveY == 0.0f && !GameRandom.globalRandom.getEveryXthChance(4)) {
            return;
        }
        this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f), this.y + GameRandom.globalRandom.getFloatBetween(-15.0f, 15.0f) + this.getDesiredHeight(), Particle.GType.IMPORTANT_COSMETIC).movesConstant(0.0f, -8.0f).color(ThemeColorRegistry.DEEPFROST.getRandomColor()).sizeFades(7, 12).lifeTime(400);
    }

    protected void updateBowRotation() {
        Point mousePos;
        if (this.rotationDisabledTimer > 0) {
            return;
        }
        Mob followingMob = this.getFollowingMob();
        if (followingMob != null) {
            ActiveBuff buff = followingMob.buffManager.getBuff(BuffRegistry.SetBonuses.GHOSTLY_ARCHER);
            if (buff != null && followingMob.isPlayer) {
                mousePos = DeepfrostSetBonusBuff.getMousePos(buff);
            } else {
                Point dirVector = followingMob.getDirVector();
                mousePos = new Point(followingMob.getX() + dirVector.x * 400, followingMob.getY() + dirVector.y * 350);
            }
        } else {
            mousePos = new Point();
        }
        this.setBowRotation(mousePos.x, mousePos.y);
        this.displayAngle = this.lerpAngle(0.25f, this.displayAngle, this.targetAngle);
    }

    protected void setBowRotation(int targetLevelX, int targetLevelY) {
        this.targetAngle = GameMath.getAngle(GameMath.normalize(this.x - (float)targetLevelX, this.y - (float)targetLevelY));
    }

    public float lerpAngle(float t, float a, float b) {
        a = (a % 360.0f + 360.0f) % 360.0f;
        b = (b % 360.0f + 360.0f) % 360.0f;
        float diff = (b - a + 540.0f) % 360.0f - 180.0f;
        return a + diff * t;
    }

    @Override
    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        super.showAttack(x, y, seed, showAllDirections);
        this.setBowRotation(x, y);
        this.displayAngle = GameMath.getAngle(GameMath.normalize(this.x - (float)x, this.y - (float)y));
        this.rotationDisabledTimer = this.rotationDisableDuration;
    }

    @Override
    public CollisionFilter getLevelCollisionFilter() {
        return null;
    }

    @Override
    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        GameLight light = level.getLightLevel(GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y));
        GameTexture texture = MobRegistry.Textures.ghostlyBow;
        int drawX = camera.getDrawX(x) - 15;
        int drawY = camera.getDrawY(y) - 26 + (int)this.getDesiredHeight();
        int spriteX = GameUtils.getAnim(this.getTime(), 10, 1000);
        final TextureDrawOptionsEnd options = texture.initDraw().sprite(spriteX, 0, 30, 52).rotate(this.displayAngle).light(light).pos(drawX, drawY).size(90);
        list.add(new MobDrawable(){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public int getRockSpeed() {
        return 10;
    }

    @Override
    public Attacker getAttackerDamageProxy() {
        Mob maLeader = this.getFollowingMob();
        if (maLeader != null) {
            return maLeader;
        }
        return super.getAttackerDamageProxy();
    }
}

