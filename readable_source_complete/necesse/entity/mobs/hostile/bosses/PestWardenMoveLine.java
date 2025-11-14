/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.hostile.bosses;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.WormMoveLine;
import necesse.entity.mobs.WormMoveLineSpawnData;

public class PestWardenMoveLine
extends WormMoveLine {
    public boolean isHardened;

    public PestWardenMoveLine(Point2D lastPos, Point2D newPos, boolean isMoveJump, float movedDist, boolean isUnderground, boolean isHardened) {
        super(lastPos, newPos, isMoveJump, movedDist, isUnderground);
        this.isHardened = isHardened;
    }

    public PestWardenMoveLine(PacketReader reader, WormMoveLineSpawnData data) {
        super(reader, data);
        this.isHardened = reader.getNextBoolean();
    }

    @Override
    public void writeSpawnPacket(PacketWriter writer, float x, float y, float extraMovedDist) {
        super.writeSpawnPacket(writer, x, y, extraMovedDist);
        writer.putNextBoolean(this.isHardened);
    }
}

