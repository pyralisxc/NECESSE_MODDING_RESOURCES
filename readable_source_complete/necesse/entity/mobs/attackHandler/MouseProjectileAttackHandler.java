/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Objects;
import necesse.engine.input.Input;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.network.PacketReader;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.MousePositionAttackHandler;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class MouseProjectileAttackHandler
extends MousePositionAttackHandler {
    protected final ArrayList<FollowingProjectile> projectiles;
    protected final int travelDistanceAfter;
    protected final int travelDistanceDuring;
    protected float currentAimX;
    protected float currentAimY;

    public MouseProjectileAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, int travelDistanceDuring, int travelDistanceAfter, FollowingProjectile ... projectiles) {
        super(attackerMob, slot, 50);
        if (projectiles.length == 0) {
            throw new IllegalArgumentException("Must give at least one projectile");
        }
        Objects.requireNonNull(projectiles);
        for (FollowingProjectile projectile : projectiles) {
            Objects.requireNonNull(projectile);
        }
        this.projectiles = new ArrayList<FollowingProjectile>(Arrays.asList(projectiles));
        this.travelDistanceAfter = travelDistanceAfter;
        this.travelDistanceDuring = travelDistanceDuring;
        for (FollowingProjectile projectile : projectiles) {
            if (travelDistanceDuring >= 0) {
                projectile.setDistance(travelDistanceDuring);
            } else {
                projectile.setDistance(10000);
            }
            projectile.traveledDistance = 0.0f;
        }
        FollowingProjectile projectile = projectiles[0];
        this.currentAimX = (float)projectile.getX() + projectile.dx * 64.0f;
        this.currentAimY = (float)projectile.getY() + projectile.dy * 64.0f;
    }

    public MouseProjectileAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, int travelDistanceAfter, FollowingProjectile ... projectiles) {
        this(attackerMob, slot, -1, travelDistanceAfter, projectiles);
    }

    public void addProjectiles(FollowingProjectile ... projectiles) {
        this.projectiles.ensureCapacity(this.projectiles.size() + projectiles.length);
        for (FollowingProjectile projectile : projectiles) {
            this.projectiles.add(projectile);
            if (this.travelDistanceDuring >= 0) {
                projectile.setDistance(this.travelDistanceDuring);
            } else {
                projectile.setDistance(10000);
            }
            projectile.traveledDistance = 0.0f;
            this.adjustNonPlayerProjectileTarget(projectile, this.lastItemAttackerTarget, false);
        }
    }

    public int getCurrentProjectilesCount() {
        return this.projectiles.size();
    }

    @Override
    public Point getNextClientLevelPos(PlayerMob player, GameCamera camera) {
        float currentHeight = 0.0f;
        if (!this.projectiles.isEmpty()) {
            currentHeight = this.projectiles.get(0).getHeight();
        }
        if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
            return new Point((int)this.currentAimX, (int)(this.currentAimY + currentHeight));
        }
        Point next = super.getNextClientLevelPos(player, camera);
        return new Point(next.x, next.y + (int)currentHeight);
    }

    @Override
    public void onUpdatePacket(PacketReader reader) {
        super.onUpdatePacket(reader);
        for (FollowingProjectile projectile : this.projectiles) {
            projectile.targetPos = new Point(this.lastX, this.lastY);
            projectile.target = null;
        }
        this.sendTargetUpdates();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.attackerMob.isPlayer && this.attackerMob.isClient() && Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
            float speed = 0.0f;
            if (!this.projectiles.isEmpty()) {
                FollowingProjectile first = this.projectiles.get(0);
                speed = Math.max(first.speed, first.getOriginalSpeed());
            }
            float change = speed * (float)this.updateInterval / 250.0f;
            this.currentAimX += ControllerInput.getAimX() * change;
            this.currentAimY += ControllerInput.getAimY() * change;
        }
        if (!this.attackerMob.isPlayer) {
            for (FollowingProjectile projectile : this.projectiles) {
                this.adjustNonPlayerProjectileTarget(projectile, this.lastItemAttackerTarget, true);
            }
        }
        ListIterator<FollowingProjectile> li = this.projectiles.listIterator();
        while (li.hasNext()) {
            FollowingProjectile projectile = li.next();
            if (projectile.removed()) {
                li.remove();
                continue;
            }
            if (this.travelDistanceDuring >= 0) continue;
            projectile.traveledDistance = 0.0f;
        }
        if (this.projectiles.isEmpty()) {
            this.attackerMob.endAttackHandler(false);
        }
    }

    @Override
    public void onItemAttackerTargetUpdate(Mob lastTarget, Mob newTarget) {
        super.onItemAttackerTargetUpdate(lastTarget, newTarget);
        for (FollowingProjectile projectile : this.projectiles) {
            this.adjustNonPlayerProjectileTarget(projectile, newTarget, true);
        }
    }

    public void adjustNonPlayerProjectileTarget(FollowingProjectile projectile, Mob target, boolean sendTargetUpdateIfChanged) {
        boolean shouldSendUpdate = false;
        if (target != null) {
            float skillPercent = this.attackerMob.getWeaponSkillPercent(this.item);
            if (skillPercent < 1.0f) {
                Point lastPos = projectile.targetPos;
                projectile.targetPos = new Point(this.lastX, this.lastY);
                projectile.target = null;
                shouldSendUpdate = lastPos == null || lastPos.x != this.lastX || lastPos.y != this.lastY;
            } else {
                projectile.targetPos = null;
                Entity lastTarget = projectile.target;
                projectile.target = target;
                shouldSendUpdate = lastTarget != target;
            }
        } else {
            projectile.targetPos = new Point(this.lastX, this.lastY);
            projectile.target = null;
        }
        if (sendTargetUpdateIfChanged && shouldSendUpdate) {
            this.sendTargetUpdate(projectile);
        }
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        boolean sendTargetUpdate = false;
        for (FollowingProjectile projectile : this.projectiles) {
            projectile.targetPos = null;
            projectile.target = null;
            if (this.travelDistanceAfter >= 0 && !projectile.returningToOwner()) {
                if (this.travelDistanceAfter == 0) {
                    projectile.remove();
                } else {
                    projectile.setDistance(this.travelDistanceAfter);
                    projectile.traveledDistance = 0.0f;
                }
            }
            if (projectile.removed()) continue;
            sendTargetUpdate = true;
        }
        if (sendTargetUpdate) {
            this.sendTargetUpdates();
        }
    }

    protected void sendTargetUpdates() {
        for (FollowingProjectile projectile : this.projectiles) {
            this.sendTargetUpdate(projectile);
        }
    }

    protected void sendTargetUpdate(FollowingProjectile projectile) {
        if (projectile.handlingClient != null) {
            if (this.attackerMob.isClient()) {
                projectile.sendClientTargetUpdate();
            }
        } else if (this.attackerMob.isServer()) {
            projectile.sendServerTargetUpdate(!this.attackerMob.isPlayer);
        }
    }

    @Override
    public boolean canRunAttack(Level level, int attackX, int attackY, ItemAttackerMob attackerMob, InventoryItem item, ItemAttackSlot slot) {
        return this.isFrom(item, slot);
    }

    @Override
    public void drawControllerAimPos(GameCamera camera, Level level, PlayerMob player, InventoryItem item) {
    }
}

