/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.camera;

import java.awt.geom.Point2D;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameMath;
import necesse.gfx.camera.GameCamera;

public class PanningCamera
extends GameCamera {
    private float xBuffer;
    private float yBuffer;
    private float dx;
    private float dy;
    private float speed;

    public PanningCamera(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public PanningCamera(int x, int y) {
        super(x, y);
    }

    public PanningCamera() {
    }

    public void tickMovement(TickManager tickManager) {
        float v;
        this.xBuffer += this.dx * this.speed * tickManager.getDelta() / 250.0f;
        this.yBuffer += this.dy * this.speed * tickManager.getDelta() / 250.0f;
        while (this.xBuffer >= 1.0f || this.xBuffer <= -1.0f) {
            v = Math.signum(this.xBuffer);
            this.x = (int)((float)this.x + v);
            this.xBuffer -= v;
        }
        while (this.yBuffer >= 1.0f || this.yBuffer <= -1.0f) {
            v = Math.signum(this.yBuffer);
            this.y = (int)((float)this.y + v);
            this.yBuffer -= v;
        }
    }

    public void setDirection(float dx, float dy) {
        Point2D.Float p = GameMath.normalize(dx, dy);
        this.dx = p.x;
        this.dy = p.y;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getXDir() {
        return this.dx;
    }

    public float getYDir() {
        return this.dy;
    }

    public void invertXDir() {
        this.dx = -this.dx;
    }

    public void invertYDir() {
        this.dy = -this.dy;
    }
}

