/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import necesse.engine.network.packet.PacketTrapTriggered;
import necesse.engine.util.GameMath;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class TrapObjectEntity
extends ObjectEntity {
    public long cooldown;
    protected long startCooldownTime;

    public TrapObjectEntity(Level level, int x, int y, long cooldownInMs) {
        super(level, "trap", x, y);
        this.cooldown = cooldownInMs;
        this.startCooldownTime = 0L;
    }

    @Override
    public boolean shouldRequestPacket() {
        return false;
    }

    public void triggerTrap(int wireID, int dir) {
    }

    public boolean onCooldown() {
        return this.startCooldownTime + this.cooldown > this.getWorldEntity().getTime();
    }

    public float getCooldownPercent() {
        return GameMath.limit((float)(this.getWorldEntity().getTime() - this.startCooldownTime) / (float)this.cooldown, 0.0f, 1.0f);
    }

    public void startCooldown() {
        this.startCooldownTime = this.getWorldEntity().getTime();
    }

    public long getTimeSinceActivated() {
        return this.getWorldEntity().getTime() - this.startCooldownTime;
    }

    public void onClientTrigger() {
    }

    public void sendClientTriggerPacket() {
        if (!this.isServer()) {
            return;
        }
        this.getLevel().getServer().network.sendToClientsWithTile(new PacketTrapTriggered(this.tileX, this.tileY), this.getLevel(), this.tileX, this.tileY);
    }

    public Point getPos(int x, int y, int dir) {
        if (dir == 0) {
            return new Point(x, y - 1);
        }
        if (dir == 1) {
            return new Point(x + 1, y);
        }
        if (dir == 2) {
            return new Point(x, y + 1);
        }
        if (dir == 3) {
            return new Point(x - 1, y);
        }
        return new Point(x, y);
    }

    public boolean otherWireActive(int exceptionWireID) {
        for (int i = 0; i < 4; ++i) {
            if (i == exceptionWireID || !this.getLevel().wireManager.isWireActive(this.tileX, this.tileY, i)) continue;
            return true;
        }
        return false;
    }
}

