/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.Mob;

public class MobHitCooldowns {
    private HashMap<Integer, Long> hitCooldowns = new HashMap();
    public int hitCooldown;

    public MobHitCooldowns(int hitCooldown) {
        this.hitCooldown = hitCooldown;
    }

    public MobHitCooldowns() {
        this(500);
    }

    public void setupPacket(PacketWriter writer, boolean onlyCooldowns, long currentTime) {
        if (onlyCooldowns) {
            List list = this.hitCooldowns.entrySet().stream().filter(e -> (Long)e.getValue() > currentTime).collect(Collectors.toList());
            writer.putNextShortUnsigned(list.size());
            for (Map.Entry e2 : list) {
                writer.putNextInt((Integer)e2.getKey());
                writer.putNextInt((int)((Long)e2.getValue() - currentTime));
            }
        } else {
            writer.putNextShortUnsigned(this.hitCooldowns.size());
            for (Map.Entry<Integer, Long> e3 : this.hitCooldowns.entrySet()) {
                writer.putNextInt(e3.getKey());
                writer.putNextInt((int)(e3.getValue() - currentTime));
            }
        }
    }

    public void applyPacket(PacketReader reader, long currentTime) {
        int size = reader.getNextShortUnsigned();
        for (int i = 0; i < size; ++i) {
            int mobUniqueID = reader.getNextInt();
            long cooldown = currentTime + (long)reader.getNextInt();
            this.hitCooldowns.put(mobUniqueID, cooldown);
        }
    }

    public boolean canHit(Mob target, long currentTime, int toleration) {
        long cooldown = this.hitCooldowns.getOrDefault(target.getHitCooldownUniqueID(), 0L);
        return cooldown - (long)toleration <= currentTime;
    }

    public boolean canHit(Mob target, int toleration) {
        return this.canHit(target, target.getWorldEntity().getTime(), toleration);
    }

    public boolean canHit(Mob target, long currentTime) {
        return this.canHit(target, currentTime, 0);
    }

    public boolean canHit(Mob target) {
        return this.canHit(target, target.getWorldEntity().getTime());
    }

    public void startCooldown(Mob target, long currentTime) {
        this.hitCooldowns.put(target.getHitCooldownUniqueID(), currentTime + (long)this.hitCooldown);
    }

    public void startCooldown(Mob target) {
        this.startCooldown(target, target.getWorldEntity().getTime());
    }

    public void resetCooldown(Mob target) {
        this.hitCooldowns.remove(target.getHitCooldownUniqueID());
    }

    public void resetCooldowns() {
        this.hitCooldowns.clear();
    }
}

