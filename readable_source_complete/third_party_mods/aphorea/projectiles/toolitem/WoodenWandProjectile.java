/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.util.GameUtils
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.projectile.followingProjectile.FollowingProjectile
 *  necesse.entity.trails.Trail
 *  necesse.gfx.camera.GameCamera
 *  necesse.gfx.drawables.LevelSortedDrawable
 *  necesse.gfx.drawables.OrderableDrawables
 *  necesse.inventory.InventoryItem
 *  necesse.level.maps.Level
 *  necesse.level.maps.LevelObjectHit
 */
package aphorea.projectiles.toolitem;

import aphorea.items.tools.healing.AphHealingProjectileToolItem;
import aphorea.utils.AphColors;
import aphorea.utils.AphDistances;
import aphorea.utils.magichealing.AphMagicHealing;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.entity.trails.Trail;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class WoodenWandProjectile
extends FollowingProjectile {
    Color color = AphColors.green;
    AphHealingProjectileToolItem toolItem;
    InventoryItem item;
    int healing;

    public WoodenWandProjectile(int healing, AphHealingProjectileToolItem toolItem, InventoryItem item, Level level, Mob owner, float x, float y, float targetX, float targetY, float speed, int distance) {
        this.healing = healing;
        this.toolItem = toolItem;
        this.item = item;
        this.setLevel(level);
        this.setOwner(owner);
        this.x = x;
        this.y = y;
        this.setTarget(targetX, targetY);
        this.speed = speed;
        this.distance = distance;
    }

    public WoodenWandProjectile() {
    }

    public void init() {
        super.init();
        this.turnSpeed = 0.1f;
        this.piercing = 0;
        this.bouncing = 0;
        this.doesImpactDamage = false;
        this.knockback = 0;
        this.canBreakObjects = false;
        this.canHitMobs = true;
        this.givesLight = true;
        this.setWidth(0.0f, 5.0f);
    }

    public boolean canHit(Mob mob) {
        return AphMagicHealing.canHealMob(this.getOwner(), mob) && this.getOwner() != mob;
    }

    public void updateTarget() {
        super.updateTarget();
        if (this.traveledDistance > 20.0f) {
            this.target = null;
            this.target = AphDistances.findClosestMob(this.getLevel(), this.x, this.y, this.distance / 2, this::canHit);
        }
    }

    public Color getParticleColor() {
        return this.color;
    }

    public Trail getTrail() {
        return new Trail((Projectile)this, this.getLevel(), this.color, 26.0f, 500, this.getHeight());
    }

    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    }

    public void doHitLogic(Mob mob, LevelObjectHit object, float x, float y) {
        if (this.isServer() && this.getOwner() != null && mob != null) {
            if (this.toolItem != null) {
                this.toolItem.healMob((ItemAttackerMob)((PlayerMob)this.getOwner()), mob, this.item);
            } else if (this.healing != 0) {
                AphMagicHealing.healMob(this.getOwner(), mob, this.healing, this.item, this.toolItem);
            }
        }
    }

    public void checkHitCollision(Line2D hitLine) {
        this.customCheckCollisions(this.toHitbox(hitLine));
    }

    protected final void customCheckCollisions(Shape hitbox) {
        Mob ownerMob = this.getOwner();
        if (ownerMob != null && this.isBoomerang && this.returningToOwner && hitbox.intersects(ownerMob.getHitBox())) {
            this.remove();
        }
        if (this.isServer() && this.canBreakObjects) {
            ArrayList hits = this.getLevel().getCollisions(hitbox, this.getAttackThroughCollisionFilter());
            for (LevelObjectHit hit : hits) {
                if (hit.invalidPos() || !hit.getObject().attackThrough) continue;
                this.attackThrough(hit);
            }
        }
        if (this.canHitMobs) {
            List targets = this.customStreamTargets(hitbox).filter(m -> this.canHit((Mob)m) && hitbox.intersects(m.getHitBox())).filter(m -> !this.isSolid || m.canHitThroughCollision() || !this.perpLineCollidesWithLevel(m.x, m.y)).collect(Collectors.toCollection(LinkedList::new));
            for (Mob target : targets) {
                this.onHit(target, null, this.x, this.y, false, null);
            }
        }
    }

    protected Stream<Mob> customStreamTargets(Shape hitBounds) {
        return Stream.concat(this.getLevel().entityManager.mobs.streamInRegionsShape(hitBounds, 1), GameUtils.streamNetworkClients((Level)this.getLevel()).filter(c -> !c.isDead() && c.hasSpawned()).map(sc -> sc.playerMob));
    }
}

