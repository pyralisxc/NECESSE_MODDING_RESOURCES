/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.followingProjectile;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.EntityDrawable;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class EvilsProtectorAttack2Projectile
extends FollowingProjectile {
    public EvilsProtectorAttack2Projectile() {
    }

    public EvilsProtectorAttack2Projectile(Level level, Mob owner, float x, float y, Mob target, GameDamage damage) {
        this.setLevel(level);
        this.x = x;
        this.y = y;
        this.speed = 60.0f;
        this.setTarget(target.x, target.y);
        this.target = target;
        this.setDamage(damage);
        this.knockback = 0;
        this.setDistance(2000);
        this.setOwner(owner);
    }

    public EvilsProtectorAttack2Projectile(Level level, Mob owner, Mob target, GameDamage damage) {
        this(level, owner, owner.x, owner.y, target, damage);
    }

    @Override
    public void init() {
        super.init();
        this.turnSpeed = 0.13f;
        this.givesLight = true;
        this.height = 16.0f;
        this.piercing = 0;
        this.isSolid = false;
        this.particleDirOffset = -24.0f;
        this.trailOffset = -24.0f;
        this.setWidth(8.0f);
    }

    @Override
    public float getTurnSpeed(int targetX, int targetY, float delta) {
        return this.getTurnSpeed(delta) * this.invDynamicTurnSpeedMod(targetX, targetY, this.getTurnRadius());
    }

    @Override
    public Color getParticleColor() {
        return new Color(50, 0, 45);
    }

    @Override
    public Trail getTrail() {
        return new Trail(this, this.getLevel(), new Color(50, 0, 45), 6.0f, 500, 18.0f);
    }

    @Override
    public void updateTarget() {
        if (this.isClient()) {
            return;
        }
        if (this.target != null && this.target != this.getOwner()) {
            if (!this.isSamePlace(this.target) || ((Mob)this.target).getDistance(this.getOwner()) > 960.0f) {
                this.target = this.getOwner();
                this.sendServerTargetUpdate(false);
            }
        } else {
            ServerClient target = GameUtils.streamServerClients(this.getLevel()).min(Comparator.comparing(m -> Float.valueOf(m.playerMob.getDistance(this.getOwner())))).orElse(null);
            if (target != null && target.playerMob != this.getOwner() && target.playerMob.getDistance(this.getOwner()) < 960.0f) {
                this.target = target.playerMob;
                this.sendServerTargetUpdate(false);
            }
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        if (this.removed()) {
            return;
        }
        GameLight light = level.getLightLevel(this);
        int drawX = camera.getDrawX(this.x) - this.texture.getWidth() / 2;
        int drawY = camera.getDrawY(this.y);
        final TextureDrawOptionsEnd options = this.texture.initDraw().light(light).rotate(this.getAngle(), this.texture.getWidth() / 2, 0).pos(drawX, drawY - (int)this.getHeight());
        list.add(new EntityDrawable(this){

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
        this.addShadowDrawables(tileList, drawX, drawY, light, this.getAngle(), 0);
    }
}

