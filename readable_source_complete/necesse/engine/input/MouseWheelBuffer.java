/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input;

import necesse.engine.input.InputEvent;
import necesse.engine.window.WindowManager;

public class MouseWheelBuffer {
    public boolean convertYToXOnShift;
    public double xBuffer;
    public double yBuffer;

    public MouseWheelBuffer(boolean convertYToXOnShift) {
        this.convertYToXOnShift = convertYToXOnShift;
    }

    public void add(InputEvent event, double speed) {
        double wheelX = event.getMouseWheelX();
        double wheelY = event.getMouseWheelY();
        if (this.convertYToXOnShift && WindowManager.getWindow().isKeyDown(340)) {
            wheelX = wheelY;
            wheelY = 0.0;
        }
        this.xBuffer += wheelX * speed;
        this.yBuffer += wheelY * speed;
    }

    public void add(InputEvent event) {
        this.add(event, 1.0);
    }

    public int useAllScrollX() {
        int used = (int)this.xBuffer;
        this.xBuffer -= (double)used;
        return used;
    }

    public int useAllScrollY() {
        int used = (int)this.yBuffer;
        this.yBuffer -= (double)used;
        return used;
    }

    public void useScrollX(ScrollUser user) {
        int used = this.useAllScrollX();
        boolean positive = used > 0;
        int absUsed = Math.abs(used);
        for (int i = 0; i < absUsed; ++i) {
            user.use(positive);
        }
    }

    public void useScrollY(ScrollUser user) {
        int used = this.useAllScrollY();
        boolean positive = used > 0;
        int absUsed = Math.abs(used);
        for (int i = 0; i < absUsed; ++i) {
            user.use(positive);
        }
    }

    public static interface ScrollUser {
        public void use(boolean var1);
    }
}

