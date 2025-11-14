/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity.interfaces;

import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public interface OEVicinityBuff {
    default public void tickVicinityBuff(ObjectEntity oe) {
        Level level = oe.getLevel();
        int posX = oe.tileX * 32 + 16;
        int posY = oe.tileY * 32 + 16;
        this.tickVicinityBuff(level, posX, posY);
    }

    default public void tickVicinityBuff(Level level, int posX, int posY) {
        int range = this.getBuffRange();
        if (range < 0) {
            throw new IllegalStateException("OEVicinityBuff must have a range higher than 0");
        }
        if (this.shouldBuffPlayers()) {
            level.entityManager.players.streamInRegionsShape(GameUtils.rangeBounds(posX, posY, this.getBuffRange() + 1), 0).filter(p -> !p.removed()).filter(p -> p.getDistance(posX, posY) <= (float)range).filter(p -> this.buffPlayersFilter().test((PlayerMob)p)).forEach(this::applyBuffs);
        }
        if (this.shouldBuffMobs()) {
            Stream<Mob> mobsStream = level.entityManager.mobs.streamInRegionsShape(GameUtils.rangeBounds(posX, posY, this.getBuffRange() + 1), 0);
            mobsStream.filter(m -> !m.removed()).filter(m -> m.getDistance(posX, posY) <= (float)range).filter(m -> this.buffMobsFilter().test((Mob)m)).forEach(this::applyBuffs);
        }
    }

    default public void applyBuffs(Mob mob) {
        for (Buff buff : this.getBuffs()) {
            if (buff == null) continue;
            ActiveBuff ab = new ActiveBuff(buff, mob, 100, null);
            mob.buffManager.addBuff(ab, false);
        }
    }

    public Buff[] getBuffs();

    public int getBuffRange();

    public boolean shouldBuffPlayers();

    default public Predicate<PlayerMob> buffPlayersFilter() {
        return c -> true;
    }

    public boolean shouldBuffMobs();

    default public Predicate<Mob> buffMobsFilter() {
        return m -> true;
    }
}

