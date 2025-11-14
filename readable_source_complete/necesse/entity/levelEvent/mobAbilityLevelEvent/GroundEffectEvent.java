/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.mobAbilityLevelEvent;

import java.awt.Point;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.PointSetAbstract;
import necesse.entity.levelEvent.mobAbilityLevelEvent.HitboxEffectEvent;
import necesse.entity.mobs.Mob;

public abstract class GroundEffectEvent
extends HitboxEffectEvent {
    protected int x;
    protected int y;

    public GroundEffectEvent() {
    }

    public GroundEffectEvent(Mob owner, int x, int y, GameRandom uniqueIDRandom) {
        super(owner, uniqueIDRandom);
        this.x = x;
        this.y = y;
    }

    @Override
    public void setupSpawnPacket(PacketWriter writer) {
        super.setupSpawnPacket(writer);
        writer.putNextInt(this.x);
        writer.putNextInt(this.y);
    }

    @Override
    public void applySpawnPacket(PacketReader reader) {
        super.applySpawnPacket(reader);
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
    }

    @Override
    public PointSetAbstract<?> getRegionPositions() {
        PointSetAbstract<?> superPositions = super.getRegionPositions();
        PointHashSet regionPositions = new PointHashSet(superPositions.size() + 1);
        regionPositions.addAll(superPositions);
        regionPositions.add(this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.x)), this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.y)));
        return regionPositions;
    }

    @Override
    public Point getSaveToRegionPos() {
        return new Point(this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.x)), this.level.regionManager.getRegionCoordByTile(GameMath.getTileCoordinate(this.y)));
    }
}

