/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.postProcessing;

import java.util.Iterator;
import java.util.LinkedList;
import necesse.engine.GlobalData;
import necesse.engine.postProcessing.PostProcessStage;
import necesse.engine.postProcessing.PostProcessingEffects;
import necesse.engine.window.GameWindow;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.shader.ShockwaveShader;

public class PostProcessShockwaveStage
extends PostProcessStage {
    protected LinkedList<CachedFrameBuffer> frameBuffers = new LinkedList();
    public final ShockwaveShader shader;

    public PostProcessShockwaveStage(GameWindow window, ShockwaveShader shader) {
        super(window);
        this.shader = shader;
        this.updateFrameBufferSize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GameFrameBuffer doPostProcessing(GameFrameBuffer inputBuffer) {
        LinkedList<PostProcessingEffects.AbstractShockwaveEffect> effects = PostProcessingEffects.shockwaveEffects;
        if (this.frameBuffers.size() < effects.size()) {
            while (this.frameBuffers.size() < effects.size()) {
                this.frameBuffers.add(new CachedFrameBuffer());
            }
        }
        Iterator effectsIterator = effects.iterator();
        for (CachedFrameBuffer cachedFrameBuffer : this.frameBuffers) {
            if (effectsIterator.hasNext()) {
                cachedFrameBuffer.currentEffect = (PostProcessingEffects.AbstractShockwaveEffect)effectsIterator.next();
                cachedFrameBuffer.update(true);
                continue;
            }
            cachedFrameBuffer.currentEffect = null;
        }
        while (this.frameBuffers.size() > effects.size() && this.frameBuffers.getLast().getTimeSinceLastUsed() > 60000L) {
            this.frameBuffers.removeLast().dispose();
        }
        GameCamera camera = GlobalData.getCurrentState().getCamera();
        if (camera == null) {
            return inputBuffer;
        }
        try {
            this.shader.use();
            for (CachedFrameBuffer cache : this.frameBuffers) {
                if (cache.currentEffect == null) {
                    break;
                }
                try {
                    cache.frameBuffer.bindFrameBuffer();
                    cache.frameBuffer.clearColor();
                    cache.currentEffect.setupShader(this.shader, camera);
                    GameFrameBuffer.draw(inputBuffer.getColorBufferTextureID(), 0, 0, cache.frameBuffer.getWidth(), cache.frameBuffer.getHeight(), null, null);
                }
                finally {
                    cache.frameBuffer.unbindFrameBuffer();
                }
                inputBuffer = cache.frameBuffer;
            }
        }
        finally {
            this.shader.stop();
        }
        return inputBuffer;
    }

    @Override
    public void updateFrameBufferSize() {
        for (CachedFrameBuffer frameBuffer : this.frameBuffers) {
            frameBuffer.update(false);
        }
    }

    @Override
    public void dispose() {
        for (CachedFrameBuffer frameBuffer : this.frameBuffers) {
            frameBuffer.dispose();
        }
        this.frameBuffers.clear();
    }

    protected class CachedFrameBuffer {
        public long lastUsedTime;
        public GameFrameBuffer frameBuffer;
        public PostProcessingEffects.AbstractShockwaveEffect currentEffect;

        protected CachedFrameBuffer() {
        }

        public void update(boolean updateCachedTime) {
            if (updateCachedTime) {
                this.lastUsedTime = System.currentTimeMillis();
            }
            if (this.frameBuffer == null || this.frameBuffer.getWidth() != PostProcessShockwaveStage.this.window.getSceneWidth() || this.frameBuffer.getHeight() != PostProcessShockwaveStage.this.window.getSceneHeight()) {
                if (this.frameBuffer != null) {
                    this.frameBuffer.dispose();
                }
                this.frameBuffer = PostProcessShockwaveStage.this.window.getNewFrameBuffer(PostProcessShockwaveStage.this.window.getSceneWidth(), PostProcessShockwaveStage.this.window.getSceneHeight());
            }
        }

        public long getTimeSinceLastUsed() {
            return System.currentTimeMillis() - this.lastUsedTime;
        }

        public void dispose() {
            if (this.frameBuffer != null) {
                this.frameBuffer.dispose();
            }
        }
    }
}

