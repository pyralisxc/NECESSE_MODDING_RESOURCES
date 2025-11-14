/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package necesse.gfx.credits;

import java.awt.Dimension;
import necesse.engine.window.GameWindow;
import necesse.gfx.GameResources;
import necesse.gfx.credits.GameCreditsDisplay;
import necesse.gfx.gameTexture.GameFrameBuffer;
import org.lwjgl.opengl.GL11;

public class GameCreditsDrawManager {
    protected GameWindow window;
    protected float size = 0.2f;
    private GameFrameBuffer baseBuffer;
    private GameFrameBuffer horiBuffer;
    private GameFrameBuffer vertBuffer;
    protected GameCreditsDisplay display;
    protected Dimension bounds;
    protected long startTime;
    protected int totalTime;
    protected boolean isPaused;
    protected float speed;
    protected int bufferPadding = 50;

    public GameCreditsDrawManager(GameWindow window, GameCreditsDisplay display) {
        this.window = window;
        this.display = display;
        this.startTime = System.currentTimeMillis();
        this.totalTime = display.initDrawAndGetTotalTimeShown();
        this.bounds = display.getDrawBounds();
        this.isPaused = false;
        this.speed = 1.0f;
        this.onWindowResized(window);
    }

    public void restart() {
        this.startTime = System.currentTimeMillis();
        this.isPaused = false;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public void setPaused(boolean paused) {
        if (this.isPaused == paused) {
            return;
        }
        this.startTime = paused ? (long)this.getCurrentTime() : this.getStartTimeAtSpeed(this.startTime, this.speed);
        this.isPaused = paused;
    }

    public void setSpeed(float speed) {
        if (speed <= 0.0f) {
            throw new IllegalArgumentException("Speed must be greater than 0");
        }
        if (!this.isPaused) {
            this.startTime = this.getStartTimeAtSpeed(this.getCurrentTime(), speed);
        }
        this.speed = speed;
    }

    public int getTotalTime() {
        return this.totalTime;
    }

    public void setProgress(int time) {
        this.startTime = this.isPaused ? (long)time : this.getStartTimeAtSpeed(time, this.speed);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void draw(int x, int y) {
        GameFrameBuffer startBuffer = this.window.getCurrentBuffer();
        int currentTime = this.getCurrentTime();
        try {
            this.baseBuffer.bindFrameBuffer();
            this.baseBuffer.clearColor();
            this.display.draw(currentTime, 50, 50, 1.0f);
        }
        finally {
            this.baseBuffer.unbindFrameBuffer();
        }
        try {
            this.horiBuffer.bindFrameBuffer();
            this.horiBuffer.clearColor();
            GameResources.horizontalGaussShader.use();
            GameResources.horizontalGaussShader.pass1f("pixelSize", 1.0f / (float)this.horiBuffer.getWidth());
            GameFrameBuffer.draw(this.baseBuffer.getColorBufferTextureID(), 0, 0, this.horiBuffer.getWidth(), this.horiBuffer.getHeight(), null, null);
        }
        finally {
            GameResources.horizontalGaussShader.stop();
            this.horiBuffer.unbindFrameBuffer();
        }
        try {
            this.vertBuffer.bindFrameBuffer();
            this.vertBuffer.clearColor();
            GameResources.verticalGaussShader.use();
            GameResources.verticalGaussShader.pass1f("pixelSize", 1.0f / (float)this.vertBuffer.getHeight());
            GameFrameBuffer.draw(this.horiBuffer.getColorBufferTextureID(), 0, 0, this.vertBuffer.getWidth(), this.vertBuffer.getHeight(), null, null);
        }
        finally {
            GameResources.verticalGaussShader.stop();
            this.vertBuffer.unbindFrameBuffer();
        }
        startBuffer.bindFrameBuffer();
        GameFrameBuffer.draw(this.vertBuffer.getColorBufferTextureID(), x - this.bufferPadding, y - this.bufferPadding, this.baseBuffer.getWidth(), this.baseBuffer.getHeight(), () -> GL11.glColor4f((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f), null);
        GameFrameBuffer.draw(this.baseBuffer.getColorBufferTextureID(), x - this.bufferPadding, y - this.bufferPadding, this.baseBuffer.getWidth(), this.baseBuffer.getHeight(), null, null);
    }

    public int getCurrentTime() {
        return this.isPaused ? (int)this.startTime : (int)((float)(System.currentTimeMillis() - this.startTime) * this.speed);
    }

    protected long getStartTimeAtSpeed(long currentTime, float speed) {
        return System.currentTimeMillis() - (long)((float)currentTime / speed);
    }

    public boolean isDone() {
        if (this.isPaused) {
            return false;
        }
        return this.getCurrentTime() >= this.totalTime;
    }

    public Dimension getBounds() {
        return this.bounds;
    }

    public void onWindowResized(GameWindow window) {
        this.window = window;
        if (this.baseBuffer != null) {
            this.baseBuffer.dispose();
        }
        if (this.horiBuffer != null) {
            this.horiBuffer.dispose();
        }
        if (this.vertBuffer != null) {
            this.vertBuffer.dispose();
        }
        this.baseBuffer = window.getNewFrameBuffer(this.bounds.width + this.bufferPadding * 2, this.bounds.height + this.bufferPadding * 2);
        this.horiBuffer = window.getNewFrameBuffer((int)((float)this.baseBuffer.getWidth() * this.size), (int)((float)this.baseBuffer.getHeight() * this.size));
        this.vertBuffer = window.getNewFrameBuffer((int)((float)this.baseBuffer.getWidth() * this.size), (int)((float)this.baseBuffer.getHeight() * this.size));
    }

    public void dispose() {
        this.horiBuffer.dispose();
        this.vertBuffer.dispose();
        this.baseBuffer.dispose();
    }
}

