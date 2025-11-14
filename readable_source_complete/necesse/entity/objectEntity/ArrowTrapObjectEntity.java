/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.entity.mobs.GameDamage;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.entity.projectile.TrapArrowProjectile;
import necesse.level.maps.Level;

public class ArrowTrapObjectEntity
extends TrapObjectEntity {
    public static GameDamage damage = new GameDamage(40.0f, 100.0f, 0.0f, 2.0f, 1.0f);

    public ArrowTrapObjectEntity(Level level, int x, int y) {
        super(level, x, y, 1000L);
        this.shouldSave = false;
    }

    @Override
    public void triggerTrap(int wireID, int dir) {
        if (this.isClient() || this.onCooldown()) {
            return;
        }
        if (this.otherWireActive(wireID)) {
            return;
        }
        Point position = this.getPos(this.tileX, this.tileY, dir);
        Point targetDir = this.getDir(dir);
        int xPos = position.x * 32;
        if (targetDir.x == 0) {
            xPos += 16;
        } else if (targetDir.x == -1) {
            xPos += 30;
        } else if (targetDir.x == 1) {
            xPos += 2;
        }
        int yPos = position.y * 32;
        if (targetDir.y == 0) {
            yPos += 16;
        } else if (targetDir.y == -1) {
            yPos += 30;
        } else if (targetDir.y == 1) {
            yPos += 2;
        }
        this.getLevel().entityManager.projectiles.add(new TrapArrowProjectile(xPos, yPos, xPos + targetDir.x, yPos + targetDir.y, damage, null));
        this.startCooldown();
    }

    public Point getDir(int dir) {
        if (dir == 0) {
            return new Point(0, -1);
        }
        if (dir == 1) {
            return new Point(1, 0);
        }
        if (dir == 2) {
            return new Point(0, 1);
        }
        if (dir == 3) {
            return new Point(-1, 0);
        }
        return new Point();
    }
}

