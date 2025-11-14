/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package necesse.gfx.drawables;

import java.util.ArrayList;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.gameTexture.GameTexture;
import org.lwjgl.opengl.GL11;

public class CustomGLDrawOptionsList {
    public Runnable setupRunnable;
    private final ArrayList<Runnable> runs;

    public CustomGLDrawOptionsList(int initialCapacity) {
        this.runs = new ArrayList(initialCapacity);
    }

    public CustomGLDrawOptionsList() {
        this(0);
    }

    public synchronized CustomGLDrawOptionsList add(Runnable runnable) {
        this.runs.add(runnable);
        return this;
    }

    public int size() {
        return this.runs.size();
    }

    public synchronized DrawOptions pos(int drawMode) {
        return this.pos(drawMode, SharedTextureDrawOptions.MAX_VERTEX_CALLS_PER_DRAW_CALL);
    }

    public synchronized DrawOptions pos(int drawMode, int maxElementsPerCall) {
        return () -> {
            GameTexture.unbindTexture();
            int drawCounter = 0;
            GL11.glLoadIdentity();
            if (this.setupRunnable != null) {
                this.setupRunnable.run();
            }
            GL11.glBegin((int)drawMode);
            for (Runnable run : this.runs) {
                if (drawCounter >= maxElementsPerCall) {
                    GL11.glEnd();
                    GL11.glBegin((int)drawMode);
                    drawCounter = 0;
                }
                run.run();
                ++drawCounter;
            }
            GL11.glEnd();
        };
    }

    public synchronized void draw(int drawMode) {
        this.draw(drawMode, SharedTextureDrawOptions.MAX_VERTEX_CALLS_PER_DRAW_CALL);
    }

    public synchronized void draw(int drawMode, int maxElementsPerCall) {
        GameTexture.unbindTexture();
        int drawCounter = 0;
        GL11.glLoadIdentity();
        if (this.setupRunnable != null) {
            this.setupRunnable.run();
        }
        GL11.glBegin((int)drawMode);
        for (Runnable run : this.runs) {
            if (drawCounter >= maxElementsPerCall) {
                GL11.glEnd();
                GL11.glBegin((int)drawMode);
                drawCounter = 0;
            }
            run.run();
            ++drawCounter;
        }
        GL11.glEnd();
    }
}

