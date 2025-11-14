/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import java.awt.geom.Point2D;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.util.GameMath;

public interface SoundEmitter
extends PrimitiveSoundEmitter {
    public float getSoundPositionX();

    public float getSoundPositionY();

    @Override
    default public float getSoundDistance(float listenerX, float listenerY) {
        return (float)new Point2D.Float(this.getSoundPositionX(), this.getSoundPositionY()).distance(listenerX, listenerY);
    }

    @Override
    default public Point2D.Float getSoundDirection(float listenerX, float listenerY) {
        return GameMath.normalize(this.getSoundPositionX() - listenerX, this.getSoundPositionY() - listenerY);
    }
}

