/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class StormingIncursionModifierProjectile
extends Projectile {
    private float xChangeCounter = 0.1f;
    private float initialX;

    public StormingIncursionModifierProjectile() {
    }

    public StormingIncursionModifierProjectile(Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance) {
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
        this.setDamage(new GameDamage(0.0f));
        this.knockback = 0;
    }

    @Override
    public void init() {
        super.init();
        this.height = 40.0f;
        this.piercing = 999;
        this.isSolid = false;
        this.heightBasedOnDistance = true;
        this.trailOffset = -25.0f;
        this.removeIfOutOfBounds = false;
        this.canBreakObjects = false;
        this.setWidth(6.0f, false);
        this.initialX = this.x;
    }

    @Override
    public boolean canHit(Mob mob) {
        return false;
    }

    @Override
    public void onMoveTick(Point2D.Float startPos, double movedDist) {
        super.onMoveTick(startPos, movedDist);
        GameRandom random = new GameRandom();
        if (this.traveledDistance > (float)this.distance * this.xChangeCounter) {
            float rndCounterChange = random.getFloatBetween(0.025f, 0.1f);
            this.xChangeCounter += rndCounterChange;
            float rndX = random.getIntBetween(-8, 8);
            this.x += rndX;
        } else if (this.traveledDistance > (float)this.distance * 0.75f && this.x < this.initialX) {
            this.x += (float)random.getIntBetween(1, 2);
        } else if (this.traveledDistance > (float)this.distance * 0.75f && this.x > this.initialX) {
            this.x -= (float)random.getIntBetween(1, 2);
        }
    }

    @Override
    public Color getParticleColor() {
        return new Color(63, 210, 229);
    }

    @Override
    public Trail getTrail() {
        GameRandom random = new GameRandom();
        int rndColor = random.getIntBetween(130, 200);
        Trail trail = new Trail(this, this.getLevel(), new Color(rndColor, 210, 210), 14.0f, 500, this.getHeight());
        trail.drawOnTop = true;
        return trail;
    }

    @Override
    public float getTrailThickness() {
        float v = ((float)this.distance - this.traveledDistance) / 16.0f;
        if (v < 5.0f) {
            return 5.0f;
        }
        if (v > 20.0f) {
            return 20.0f;
        }
        return v;
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y);
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light.minLevelCopy(Math.min(light.getLevel() + 100.0f, 150.0f))).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY);
        topList.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }
}

