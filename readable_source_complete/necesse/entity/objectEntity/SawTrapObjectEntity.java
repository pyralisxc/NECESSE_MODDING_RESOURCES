/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.summon.SawBladeMob;
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.TrapTrackObject;
import necesse.level.maps.Level;

public class SawTrapObjectEntity
extends TrapObjectEntity {
    public SawTrapObjectEntity(Level level, int x, int y) {
        super(level, x, y, 900L);
        this.shouldSave = false;
    }

    @Override
    public void triggerTrap(int wireID, int dir) {
        if (this.isClient() || this.onCooldown()) {
            return;
        }
        Point d = this.getDir(dir);
        GameObject objectInFront = this.getLevel().getObject(this.tileX + d.x, this.tileY + d.y);
        if (objectInFront instanceof TrapTrackObject) {
            Mob sawblade = MobRegistry.getMob("sawblade", this.getLevel());
            sawblade.setFacingDir(d.x, d.y);
            ((SawBladeMob)sawblade).sawDir = dir;
            ((SawBladeMob)sawblade).sawSpeed = 10.0f;
            this.getLevel().entityManager.addMob(sawblade, (this.tileX + d.x) * 32 + 16, (this.tileY + d.y) * 32 + 16);
            this.sendClientTriggerPacket();
            this.startCooldown();
        }
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
        return new Point(0, 0);
    }

    @Override
    public void onClientTrigger() {
        super.onClientTrigger();
        this.startCooldown();
    }
}

