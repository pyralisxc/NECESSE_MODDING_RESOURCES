/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.util.ComputedValue;
import necesse.engine.util.GameMath;

public interface LinesSoundEmitter
extends PrimitiveSoundEmitter {
    public Iterable<Line2D> getSoundLines();

    @Override
    default public float getSoundDistance(float listenerX, float listenerY) {
        Point2D.Float listenerPoint = new Point2D.Float(listenerX, listenerY);
        float bestDist = -1.0f;
        Iterable<Line2D> soundLines = this.getSoundLines();
        for (Line2D soundLine : soundLines) {
            Point2D p = GameMath.getClosestPointOnLine(soundLine, listenerPoint, false);
            ComputedValue<Float> pDist = new ComputedValue<Float>(() -> Float.valueOf((float)p.distance(listenerPoint)));
            if (bestDist != -1.0f && !(pDist.get().floatValue() < bestDist)) continue;
            bestDist = pDist.get().floatValue();
        }
        return bestDist;
    }

    @Override
    default public Point2D.Float getSoundDirection(float listenerX, float listenerY) {
        Point2D.Float listenerPoint = new Point2D.Float(listenerX, listenerY);
        Point2D bestPoint = null;
        float bestDist = -1.0f;
        Iterable<Line2D> soundLines = this.getSoundLines();
        for (Line2D soundLine : soundLines) {
            Point2D p = GameMath.getClosestPointOnLine(soundLine, listenerPoint, false);
            ComputedValue<Float> pDist = new ComputedValue<Float>(() -> Float.valueOf((float)p.distance(listenerPoint)));
            if (bestPoint != null && !(pDist.get().floatValue() < bestDist)) continue;
            bestPoint = p;
            bestDist = pDist.get().floatValue();
        }
        if (bestPoint == null) {
            return null;
        }
        return GameMath.normalize((float)bestPoint.getX() - listenerX, (float)bestPoint.getY() - listenerY);
    }
}

