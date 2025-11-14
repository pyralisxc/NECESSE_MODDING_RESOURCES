/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.gameLoop.tickManager.TicksPerSecond
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.entity.Entity
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.MobDrawable
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.WormMobHead
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.hostile.bosses.BossWormMobBody
 *  necesse.entity.particle.Particle$GType
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawables.Drawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.mobs.bosses.minions.babylon;

import aphorea.mobs.bosses.minions.babylon.BabylonHead;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameLoop.tickManager.TicksPerSecond;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.WormMobHead;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.hostile.bosses.BossWormMobBody;
import necesse.entity.particle.Particle;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.Drawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameSprite;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BabylonBody
extends BossWormMobBody<BabylonHead, BabylonBody> {
    public int spriteY;
    public TicksPerSecond particleSpawner = TicksPerSecond.ticksPerSecond((int)50);

    public BabylonBody() {
        super(1000);
        this.isSummoned = true;
        this.collision = new Rectangle(-30, -25, 60, 50);
        this.hitBox = new Rectangle(-40, -35, 80, 70);
        this.selectBox = new Rectangle(-40, -60, 80, 80);
    }

    public GameMessage getLocalization() {
        BabylonHead head = (BabylonHead)this.master.get(this.getLevel());
        return head != null ? head.getLocalization() : new StaticMessage("babylonbody");
    }

    public void init() {
        super.init();
    }

    public GameDamage getCollisionDamage(Mob target) {
        return BabylonHead.bodyCollisionDamage;
    }

    public boolean canCollisionHit(Mob target) {
        return this.height < 45.0f && super.canCollisionHit(target);
    }

    public void clientTick() {
        super.clientTick();
        if (this.isVisible()) {
            this.particleSpawner.gameTick();
            while (this.particleSpawner.shouldTick()) {
                this.getLevel().entityManager.addParticle(this.x + GameRandom.globalRandom.floatGaussian() * 45.0f, this.y + GameRandom.globalRandom.floatGaussian() * 30.0f + 5.0f, Particle.GType.COSMETIC).movesConstant(GameRandom.globalRandom.floatGaussian() * 6.0f, GameRandom.globalRandom.floatGaussian() * 3.0f).sizeFades(5, 10).givesLight().heightMoves(this.height + 10.0f, this.height + GameRandom.globalRandom.getFloatBetween(30.0f, 40.0f)).lifeTime(1000);
            }
        }
    }

    public boolean canTakeDamage() {
        return false;
    }

    public Stream<ModifierValue<?>> getDefaultModifiers() {
        return Stream.of(new ModifierValue(BuffModifiers.SLOW, (Object)Float.valueOf(0.0f)).max((Object)Float.valueOf(0.0f)));
    }

    protected void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
        if (this.isVisible()) {
            GameLight light = level.getLightLevel((Entity)this);
            int drawX = camera.getDrawX(x) - 112;
            int drawY = camera.getDrawY(y);
            if (this.next != null) {
                Point2D.Float dir = new Point2D.Float(((BabylonBody)this.next).x - (float)x, ((BabylonBody)this.next).y - ((BabylonBody)this.next).height - ((float)y - this.height));
                float angle = GameMath.fixAngle((float)GameMath.getAngle((Point2D.Float)dir));
                final MobDrawable drawOptions = WormMobHead.getAngledDrawable((GameSprite)new GameSprite(BabylonHead.texture, 0, this.spriteY, 224), null, (GameLight)light.minLevelCopy(100.0f), (int)((int)this.height), (float)angle, (int)drawX, (int)drawY, (int)130);
                MobDrawable drawOptionsShadow = WormMobHead.getAngledDrawable((GameSprite)new GameSprite(BabylonHead.texture_shadow, 0, this.spriteY, 224), null, (GameLight)light, (int)((int)this.height), (float)angle, (int)drawX, (int)(drawY + 40), (int)130);
                topList.add((Drawable)new MobDrawable(){

                    public void draw(TickManager tickManager) {
                        drawOptions.draw(tickManager);
                    }
                });
                Objects.requireNonNull(drawOptionsShadow);
                tileList.add((Drawable)drawOptionsShadow);
            }
        }
    }
}

