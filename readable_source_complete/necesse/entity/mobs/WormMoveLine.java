/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.WormMoveLineSpawnData;

public class WormMoveLine
extends Line2D.Float {
    public final boolean isMoveJump;
    public boolean isUnderground;
    public final float movedDist;
    private boolean calculatedDist;
    private boolean calculatedDir;
    private double dist;
    private Point2D.Float dir;

    public WormMoveLine(Point2D lastPos, Point2D newPos, boolean isMoveJump, float movedDist, boolean isUnderground) {
        super(lastPos, newPos);
        this.isMoveJump = isMoveJump;
        this.movedDist = movedDist;
        this.isUnderground = isUnderground;
    }

    public WormMoveLine(PacketReader reader, WormMoveLineSpawnData data) {
        this.x1 = reader.getNextFloat();
        this.y1 = reader.getNextFloat();
        Point2D.Float nextPoint = new Point2D.Float(this.x1, this.y1);
        if (data.lastPoint == null) {
            data.lastPoint = nextPoint;
        }
        this.isUnderground = reader.getNextBoolean();
        this.isMoveJump = reader.getNextBoolean();
        if (this.isMoveJump) {
            this.x2 = nextPoint.x;
            this.y2 = nextPoint.y;
            this.movedDist = reader.getNextFloat();
            data.lastPoint = null;
        } else {
            this.x2 = data.lastPoint.x;
            this.y2 = data.lastPoint.y;
            this.movedDist = data.movedDist;
            data.movedDist = (float)((double)data.movedDist + this.getP1().distance(this.getP2()));
            data.lastPoint = nextPoint;
        }
    }

    public void writeSpawnPacket(PacketWriter writer, float x, float y, float extraMovedDist) {
        writer.putNextFloat(x);
        writer.putNextFloat(y);
        writer.putNextBoolean(this.isUnderground);
        writer.putNextBoolean(this.isMoveJump);
        if (this.isMoveJump) {
            writer.putNextFloat(this.movedDist + extraMovedDist);
        }
    }

    public Point2D.Float dir() {
        if (!this.calculatedDir) {
            this.dir = GameMath.normalize(this.x2 - this.x1, this.y2 - this.y1);
            this.calculatedDir = true;
        }
        return this.dir;
    }

    public double dist() {
        if (!this.calculatedDist) {
            this.dist = this.getP1().distance(this.getP2());
            this.calculatedDist = true;
        }
        return this.dist;
    }
}

