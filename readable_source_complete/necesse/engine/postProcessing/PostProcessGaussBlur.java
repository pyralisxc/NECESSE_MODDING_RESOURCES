/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.postProcessing;

import necesse.engine.postProcessing.PostProcessStage;
import necesse.engine.window.GameWindow;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameFrameBuffer;

public class PostProcessGaussBlur
extends PostProcessStage {
    public static boolean enabled = false;
    private float size;
    private boolean sizeChanged;
    private GameFrameBuffer horiBuffer;
    private GameFrameBuffer vertBuffer;

    public PostProcessGaussBlur(GameWindow window, float size) {
        super(window);
        this.size = size;
        this.horiBuffer = window.getNewFrameBuffer((int)((float)window.getSceneWidth() * size), (int)((float)window.getSceneHeight() * size));
        this.vertBuffer = window.getNewFrameBuffer((int)((float)window.getSceneWidth() * size), (int)((float)window.getSceneHeight() * size));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setSize(float size) {
        if (this.size == size) {
            return;
        }
        this.size = size;
        this.sizeChanged = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GameFrameBuffer doPostProcessing(GameFrameBuffer inputBuffer) {
        if (enabled) {
            if (this.sizeChanged) {
                int width = (int)((float)this.window.getSceneWidth() * this.size);
                int height = (int)((float)this.window.getSceneHeight() * this.size);
                if (this.horiBuffer.getWidth() != width || this.horiBuffer.getHeight() != height) {
                    this.horiBuffer.dispose();
                    this.horiBuffer = this.window.getNewFrameBuffer(width, height);
                    this.vertBuffer.dispose();
                    this.vertBuffer = this.window.getNewFrameBuffer(width, height);
                }
                this.sizeChanged = false;
            }
            try {
                this.horiBuffer.bindFrameBuffer();
                this.horiBuffer.clearColor();
                GameResources.horizontalGaussShader.use();
                GameResources.horizontalGaussShader.pass1f("pixelSize", 1.0f / (float)this.horiBuffer.getWidth());
                GameFrameBuffer.draw(inputBuffer.getColorBufferTextureID(), 0, 0, this.horiBuffer.getWidth(), this.horiBuffer.getHeight(), null, null);
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
            return this.vertBuffer;
        }
        return inputBuffer;
    }

    @Override
    public void updateFrameBufferSize() {
        this.sizeChanged = true;
    }

    @Override
    public void dispose() {
        this.horiBuffer.dispose();
        this.vertBuffer.dispose();
    }
}

