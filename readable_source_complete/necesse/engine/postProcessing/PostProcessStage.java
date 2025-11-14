/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.postProcessing;

import necesse.engine.window.GameWindow;
import necesse.gfx.gameTexture.GameFrameBuffer;

public abstract class PostProcessStage {
    public final GameWindow window;

    public PostProcessStage(GameWindow window) {
        this.window = window;
    }

    public boolean isEnabled() {
        return true;
    }

    public abstract GameFrameBuffer doPostProcessing(GameFrameBuffer var1);

    public abstract void updateFrameBufferSize();

    public abstract void dispose();
}

