/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.postProcessing;

import necesse.engine.postProcessing.PostProcessStage;
import necesse.engine.window.GameWindow;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.shader.GameShader;

public class PostProcessShaderStage
extends PostProcessStage {
    protected GameFrameBuffer frameBuffer;
    public final GameShader shader;

    public PostProcessShaderStage(GameWindow window, GameShader shader) {
        super(window);
        this.shader = shader;
        this.updateFrameBufferSize();
    }

    @Override
    public GameFrameBuffer doPostProcessing(GameFrameBuffer inputBuffer) {
        try {
            this.frameBuffer.bindFrameBuffer();
            this.frameBuffer.clearColor();
            this.shader.use();
            this.setShaderVariables();
            GameFrameBuffer.draw(inputBuffer.getColorBufferTextureID(), 0, 0, this.frameBuffer.getWidth(), this.frameBuffer.getHeight(), null, null);
        }
        finally {
            this.shader.stop();
            this.frameBuffer.unbindFrameBuffer();
        }
        return this.frameBuffer;
    }

    protected void setShaderVariables() {
    }

    @Override
    public void updateFrameBufferSize() {
        if (this.frameBuffer == null || this.frameBuffer.getWidth() != this.window.getSceneWidth() || this.frameBuffer.getHeight() != this.window.getSceneHeight()) {
            if (this.frameBuffer != null) {
                this.frameBuffer.dispose();
            }
            this.frameBuffer = this.window.getNewFrameBuffer(this.window.getSceneWidth(), this.window.getSceneHeight());
        }
    }

    @Override
    public void dispose() {
        this.frameBuffer.dispose();
    }
}

