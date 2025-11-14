/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.geom.Point2D;
import necesse.engine.network.packet.PacketMobAttack;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;

public class AttackAnimMob
extends Mob {
    public int attackAnimTime = 200;
    public Point2D.Float attackDir;
    public int attackSeed;

    public AttackAnimMob(int health) {
        super(health);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.isAttacking) {
            this.getAttackAnimProgress();
        }
    }

    public float getAttackAnimProgress() {
        float progress = (float)(this.getWorldEntity().getTime() - this.attackTime) / (float)this.attackAnimTime;
        if (progress >= 1.0f) {
            this.isAttacking = false;
        }
        return Math.min(1.0f, progress);
    }

    @Override
    public void attack(int x, int y, boolean showAllDirections) {
        super.attack(x, y, showAllDirections);
        this.attackDir = GameMath.normalize(x - this.getX(), y - this.getY());
        this.isAttacking = true;
        if (this.isServer()) {
            this.getLevel().getServer().network.sendToClientsWithEntity(new PacketMobAttack(this, x, y, showAllDirections), this);
        }
    }

    @Override
    public final void showAttack(int x, int y, boolean showAllDirections) {
        this.showAttack(x, y, 0, showAllDirections);
    }

    public void showAttack(int x, int y, int seed, boolean showAllDirections) {
        this.updateAttackDir(x, y, showAllDirections);
        this.isAttacking = true;
        this.attackSeed = seed;
        this.attackTime = this.getTime();
    }

    public void updateAttackDir(int x, int y, boolean showAllDirections) {
        this.attackDir = GameMath.normalize(x - this.getX(), y - this.getY());
        if (showAllDirections) {
            this.setFacingDir(x - this.getX(), y - this.getY());
        } else if (x > this.getX()) {
            this.setDir(1);
        } else {
            this.setDir(3);
        }
    }
}

