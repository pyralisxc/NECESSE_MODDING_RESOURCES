/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.window;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import necesse.engine.GlobalData;
import necesse.engine.SceneColorSetting;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.platforms.Platform;
import necesse.engine.postProcessing.PostProcessGaussBlur;
import necesse.engine.postProcessing.PostProcessShaderStage;
import necesse.engine.postProcessing.PostProcessShockwaveStage;
import necesse.engine.postProcessing.PostProcessStage;
import necesse.engine.state.State;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindowIcon;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.gameTexture.GameTexture;

public abstract class GameWindow {
    public static float[] interfaceSizes = new float[]{1.0f, 1.1f, 1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 2.0f};
    public static int minAdjustedInterfaceHeight = 1080;
    public static int minAdjustedInterfaceWidth = minAdjustedInterfaceHeight * 16 / 9;
    public static int expectedAdjustedInterfaceHeight = 1080;
    public static int expectedAdjustedInterfaceWidth = expectedAdjustedInterfaceHeight * 16 / 9;
    public static int minAdjustedSceneHeight = 1080;
    public static int minAdjustedSceneWidth = minAdjustedSceneHeight * 16 / 9;
    public static int expectedAdjustedSceneHeight = 1080;
    public static int expectedAdjustedSceneWidth = expectedAdjustedSceneHeight * 16 / 9;
    public static float minSceneSize = 1.0f;
    public static float maxSceneSize = 4.0f;
    public FBOType FBO_CAPABILITIES = null;
    private final Input input;
    protected int windowWidth;
    protected int windowHeight;
    protected int windowFrameWidth;
    protected int windowFrameHeight;
    protected boolean hasResized;
    protected GameFrameBuffer hudBuffer;
    protected float hudSize = 1.0f;
    private boolean hudSizeChanged = false;
    private final ArrayList<PostProcessStage> scenePostProcessing;
    private GameFrameBuffer sceneBuffer;
    private GameFrameBuffer sceneOverlayBuffer;
    protected float sceneSize = 1.0f;
    private boolean sceneSizeChanged = false;
    protected GameFrameBuffer[] applyDrawPool = new GameFrameBuffer[3];
    protected int applyDrawUse = 0;
    private final GameFrameBuffer windowBuffer;
    protected GameFrameBuffer currentBuffer;
    private float sceneRed = 1.0f;
    private float sceneGreen = 1.0f;
    private float sceneBlue = 1.0f;
    private float sceneDarkness;
    private int sceneDarknessMinViewDist = 0;
    private int sceneDarknessFadeDist = 1000;
    private boolean active;
    private boolean wasResizedThisFrame;

    public GameWindow() {
        this.scenePostProcessing = new ArrayList();
        this.input = new Input(this);
        this.currentBuffer = this.windowBuffer = this.getNewFrameBuffer(this.getFrameWidth(), this.getFrameHeight());
    }

    public static float getClosestSize(float size, float[] sizes) {
        return IntStream.range(0, sizes.length).mapToObj(i -> Float.valueOf(sizes[i])).min(Comparator.comparingDouble(s -> Math.abs(size - s.floatValue()))).orElse(Float.valueOf(size)).floatValue();
    }

    public static float getAdjustedSize(int minWidth, int minHeight, int expectedWidth, int expectedHeight, int windowWidth, int windowHeight) {
        if (windowHeight <= minHeight || windowWidth <= minWidth) {
            return 1.0f;
        }
        return Math.min((float)windowHeight / (float)expectedHeight, (float)windowWidth / (float)expectedWidth);
    }

    public void createWindow(boolean updateSize) {
        this.createWindow(null, updateSize);
    }

    public void createWindow(GameWindow parent, boolean updateSize) {
        if (parent != null) {
            this.hudSize = parent.hudSize;
            this.sceneSize = parent.sceneSize;
        }
    }

    public void initInput() {
        Platform.getInputManager().initializeInputSources(this);
    }

    public abstract void updateWindowSize();

    public void setupPostProcessing() {
        this.scenePostProcessing.forEach(PostProcessStage::dispose);
        this.scenePostProcessing.clear();
        this.scenePostProcessing.add(new PostProcessShaderStage(this, GameResources.debugColorShader));
        this.scenePostProcessing.add(new PostProcessShaderStage(this, GameResources.colorSettingShader){

            @Override
            public boolean isEnabled() {
                return Settings.sceneColors != SceneColorSetting.Normal;
            }

            @Override
            protected void setShaderVariables() {
                super.setShaderVariables();
                Settings.sceneColors.shaderSetup.accept(GameResources.colorSettingShader);
            }
        });
        this.scenePostProcessing.add(new PostProcessShaderStage(this, GameResources.brightnessShader){

            @Override
            protected void setShaderVariables() {
                super.setShaderVariables();
                GameResources.brightnessShader.pass1f("brightness", Settings.brightness > 1.0f ? GameMath.lerp(Settings.brightness - 1.0f, 1.0f, 1.5f) : Settings.brightness);
            }
        });
        this.scenePostProcessing.add(new PostProcessShaderStage(this, GameResources.windowColorShader){

            @Override
            protected void setShaderVariables() {
                GameResources.windowColorShader.pass1f("red", GameWindow.this.sceneRed);
                GameResources.windowColorShader.pass1f("green", GameWindow.this.sceneGreen);
                GameResources.windowColorShader.pass1f("blue", GameWindow.this.sceneBlue);
            }
        });
        this.scenePostProcessing.add(new PostProcessGaussBlur(this, 1.0f));
        this.scenePostProcessing.add(new PostProcessShaderStage(this, GameResources.darknessShader){

            @Override
            public boolean isEnabled() {
                return GameWindow.this.sceneDarkness > 0.0f;
            }

            @Override
            protected void setShaderVariables() {
                GameResources.darknessShader.pass1f("intensity", GameWindow.this.sceneDarkness);
                GameResources.darknessShader.pass1i("minViewDist", GameWindow.this.sceneDarknessMinViewDist);
                GameResources.darknessShader.pass1i("fadeDist", GameWindow.this.sceneDarknessFadeDist);
            }
        });
        this.scenePostProcessing.add(new PostProcessShockwaveStage(this, GameResources.shockwaveShader));
        this.scenePostProcessing.add(new PostProcessShaderStage(this, GameResources.shockwaveTestShader){

            @Override
            protected void setShaderVariables() {
                this.shader.pass1i("drawX", GameWindow.this.getSceneWidth() / 2);
                this.shader.pass1i("drawY", GameWindow.this.getSceneHeight() / 2);
            }
        });
        this.scenePostProcessing.add(new PostProcessShaderStage(this, GameResources.pixelScalingShader){

            @Override
            public void updateFrameBufferSize() {
                if (this.frameBuffer == null || this.frameBuffer.getWidth() != this.window.getFrameWidth() || this.frameBuffer.getHeight() != this.window.getFrameHeight()) {
                    if (this.frameBuffer != null) {
                        this.frameBuffer.dispose();
                    }
                    this.frameBuffer = this.window.getNewFrameBuffer(this.window.getFrameWidth(), this.window.getFrameHeight());
                }
            }

            @Override
            protected void setShaderVariables() {
                super.setShaderVariables();
                GameResources.pixelScalingShader.pass1f("smoothing_factor", 1.0f);
                GameResources.pixelScalingShader.pass2f("texture_pixel_size", 1.0f / (float)this.window.getSceneWidth(), 1.0f / (float)this.window.getSceneHeight());
            }
        });
    }

    public boolean tickResize() {
        boolean out = false;
        if (this.hasResized) {
            if (this.windowWidth > 1 && this.windowHeight > 1) {
                this.updateResize();
            }
            out = true;
        }
        if (this.sceneSizeChanged) {
            this.updateSceneResize();
            out = true;
        }
        if (this.hudSizeChanged) {
            this.updateHudResize();
            out = true;
        }
        return out;
    }

    private void updateSceneResize() {
        float sceneSizeMod = 1.0f / this.sceneSize;
        int width = (int)((float)this.getWidth() * sceneSizeMod);
        int height = (int)((float)this.getHeight() * sceneSizeMod);
        if (this.sceneBuffer == null || this.sceneBuffer.getWidth() != width || this.sceneBuffer.getHeight() != height) {
            if (this.sceneBuffer != null) {
                this.sceneBuffer.dispose();
            }
            this.sceneBuffer = this.getNewFrameBuffer(width, height);
            if (this.sceneOverlayBuffer != null) {
                this.sceneOverlayBuffer.dispose();
            }
            this.sceneOverlayBuffer = this.getNewFrameBuffer(this.sceneBuffer.getWidth(), this.sceneBuffer.getHeight());
            this.updateView();
        }
        this.scenePostProcessing.forEach(PostProcessStage::updateFrameBufferSize);
        this.sceneSizeChanged = false;
    }

    private void updateHudResize() {
        float hudSizeMod = 1.0f / this.hudSize;
        int width = (int)((float)this.getWidth() * hudSizeMod);
        int height = (int)((float)this.getHeight() * hudSizeMod);
        if (this.hudBuffer == null || this.hudBuffer.getWidth() != width || this.hudBuffer.getHeight() != height) {
            if (this.hudBuffer != null) {
                this.hudBuffer.dispose();
            }
            this.hudBuffer = this.getNewFrameBuffer(width, height);
            this.updateView();
        }
        this.hudSizeChanged = false;
    }

    private void updateFrameResize() {
        boolean changed = false;
        for (int i = 0; i < this.applyDrawPool.length; ++i) {
            GameFrameBuffer current = this.applyDrawPool[i];
            if (current != null && current.getWidth() == this.getWidth() && current.getHeight() == this.getHeight()) continue;
            if (current != null) {
                current.dispose();
            }
            this.applyDrawPool[i] = this.getNewFrameBuffer(this.getWidth(), this.getHeight());
            changed = true;
        }
        if (changed) {
            this.updateView();
        }
    }

    public void updateResize() {
        this.updateSceneResize();
        this.updateHudResize();
        this.updateFrameResize();
        this.hasResized = false;
    }

    public void updateSizeAndShow() {
        this.updateWindowSize();
        this.setVSync(Settings.vSyncEnabled);
        this.setSceneSize(Settings.sceneSize);
        this.setHudSize(Settings.interfaceSize);
        this.setIcon(GameWindowIcon.getIcon());
        this.initInput();
        this.show();
    }

    public abstract void updateView();

    public void updateHudSize() {
        float size = 1.0f;
        if (Settings.adjustInterfaceOnHighResolution) {
            size = GameWindow.getAdjustedSize(minAdjustedInterfaceWidth, minAdjustedInterfaceHeight, expectedAdjustedInterfaceWidth, expectedAdjustedInterfaceHeight, this.getFrameWidth(), this.getFrameHeight());
        }
        this.setHudSize(size * Settings.interfaceSize);
        this.input.updateNextMousePos();
        this.input.submitNextMoveEvent();
        ControllerInput.submitNextRefreshFocusEvent();
    }

    public void updateSceneSize() {
        float size = 1.0f;
        if (Settings.adjustZoomOnHighResolution) {
            size = GameWindow.getAdjustedSize(minAdjustedSceneWidth, minAdjustedSceneHeight, expectedAdjustedSceneWidth, expectedAdjustedSceneHeight, this.getFrameWidth(), this.getFrameHeight());
        }
        this.setSceneSize(size * Settings.sceneSize);
        this.input.updateNextMousePos();
        this.input.submitNextMoveEvent();
        ControllerInput.submitNextRefreshFocusEvent();
    }

    public abstract void update();

    public abstract void preloadUpdate();

    public abstract void makeCurrent();

    public void startHudDraw() {
        this.hudBuffer.bindFrameBuffer();
        this.hudBuffer.clearColor();
        this.hudBuffer.clearDepth();
    }

    public void endHudDraw() {
        this.hudBuffer.unbindFrameBuffer();
    }

    public abstract void renderHud(float var1);

    public void startSceneDraw() {
        this.sceneBuffer.bindFrameBuffer();
        this.sceneBuffer.clearColor();
        this.sceneBuffer.clearDepth();
    }

    public void endSceneDraw() {
        this.sceneBuffer.unbindFrameBuffer();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void applyDraw(Consumer<GameFrameBuffer> drawLogic, Consumer<GameFrameBuffer> glModifyLogic, Consumer<GameFrameBuffer> glEndLogic) {
        GameFrameBuffer startBuffer = this.currentBuffer;
        ++this.applyDrawUse;
        GameFrameBuffer useBuffer = this.applyDrawPool[this.applyDrawUse % this.applyDrawPool.length];
        try {
            useBuffer.bindFrameBuffer();
            useBuffer.clearColor();
            useBuffer.clearDepth();
            drawLogic.accept(useBuffer);
        }
        catch (Throwable throwable) {
            try {
                startBuffer.bindFrameBuffer();
                Renderer.useShader(null);
                Runnable preDrawLogic = glModifyLogic == null ? null : () -> glModifyLogic.accept(useBuffer);
                Runnable postDrawLogic = glEndLogic == null ? null : () -> glEndLogic.accept(useBuffer);
                GameFrameBuffer.drawSimple(useBuffer.getColorBufferTextureID(), 0, 0, useBuffer.getWidth(), useBuffer.getHeight(), preDrawLogic, postDrawLogic);
            }
            finally {
                Renderer.stopShader(null);
            }
            throw throwable;
        }
        try {
            startBuffer.bindFrameBuffer();
            Renderer.useShader(null);
            Runnable preDrawLogic = glModifyLogic == null ? null : () -> glModifyLogic.accept(useBuffer);
            Runnable postDrawLogic = glEndLogic == null ? null : () -> glEndLogic.accept(useBuffer);
            GameFrameBuffer.drawSimple(useBuffer.getColorBufferTextureID(), 0, 0, useBuffer.getWidth(), useBuffer.getHeight(), preDrawLogic, postDrawLogic);
        }
        finally {
            Renderer.stopShader(null);
        }
        --this.applyDrawUse;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void applyDraw(Runnable drawLogic, Runnable glModifyLogic, Runnable glEndLogic) {
        GameFrameBuffer startBuffer = this.currentBuffer;
        ++this.applyDrawUse;
        GameFrameBuffer useBuffer = this.applyDrawPool[this.applyDrawUse % this.applyDrawPool.length];
        try {
            useBuffer.bindFrameBuffer();
            useBuffer.clearColor();
            useBuffer.clearDepth();
            drawLogic.run();
        }
        finally {
            try {
                startBuffer.bindFrameBuffer();
                Renderer.useShader(null);
                GameFrameBuffer.drawSimple(useBuffer.getColorBufferTextureID(), 0, 0, useBuffer.getWidth(), useBuffer.getHeight(), glModifyLogic, glEndLogic);
            }
            finally {
                Renderer.stopShader(null);
            }
        }
        --this.applyDrawUse;
    }

    public void startSceneOverlayDraw() {
        this.sceneOverlayBuffer.bindFrameBuffer();
        this.sceneOverlayBuffer.clearColor();
        this.sceneOverlayBuffer.clearDepth();
    }

    public void endSceneOverlayDraw() {
        this.sceneOverlayBuffer.unbindFrameBuffer();
    }

    public void renderScene(GameFrameBuffer buffer) {
        GameFrameBuffer finalScene = this.sceneBuffer;
        for (PostProcessStage postProcessStage : this.scenePostProcessing) {
            if (!postProcessStage.isEnabled()) continue;
            finalScene = postProcessStage.doPostProcessing(finalScene);
        }
        if (buffer != null) {
            buffer.bindFrameBuffer();
        }
        GameFrameBuffer.draw(finalScene.getColorBufferTextureID(), 0, 0, this.getFrameWidth(), this.getFrameHeight(), null, null);
        GameFrameBuffer.draw(this.sceneOverlayBuffer.getColorBufferTextureID(), 0, 0, this.getFrameWidth(), this.getFrameHeight(), null, null);
    }

    public abstract void show();

    public abstract void requestClose();

    public void destroy() {
        if (this.isCreated()) {
            for (GameFrameBuffer buffer : this.applyDrawPool) {
                if (buffer == null) continue;
                buffer.dispose();
            }
            this.sceneBuffer.dispose();
            this.scenePostProcessing.forEach(PostProcessStage::dispose);
            this.sceneOverlayBuffer.dispose();
            this.hudBuffer.dispose();
            this.input.dispose();
        }
    }

    public abstract void printCapabilities(PrintStream var1);

    public abstract void requestFocus();

    public InputPosition mousePos() {
        if (this.input == null) {
            return InputPosition.dummyPos();
        }
        return this.input.mousePos();
    }

    public void submitNextMoveEvent() {
        this.input.submitNextMoveEvent();
        ControllerInput.submitNextRefreshFocusEvent();
    }

    public void preGameTick(TickManager tickManager) {
        this.wasResizedThisFrame = Performance.record((PerformanceTimerManager)tickManager, "other", () -> {
            boolean focus;
            int maxFPS = Settings.maxFPS;
            if (Settings.savePerformanceOnFocusLoss && !this.active) {
                maxFPS = 30;
            }
            if (tickManager.getMaxFPS() != maxFPS) {
                tickManager.setMaxFPS(maxFPS);
            }
            if ((focus = this.isFocused()) != this.active) {
                this.active = focus;
            }
            return this.tickWindowResize(false);
        });
    }

    public boolean tickWindowResize(boolean forced) {
        if (this.tickResize() || forced) {
            State state;
            if (Settings.adjustZoomOnHighResolution) {
                this.updateSceneSize();
            }
            if (Settings.adjustInterfaceOnHighResolution) {
                this.updateHudSize();
            }
            if ((state = GlobalData.getCurrentState()) != null) {
                state.onWindowResized(this);
            }
            return true;
        }
        return false;
    }

    public abstract boolean isCloseRequested();

    public abstract boolean isFocused();

    public abstract boolean isCreated();

    public boolean isKeyDown(int key) {
        if (this.input == null) {
            return false;
        }
        return this.input.isKeyDown(key);
    }

    public boolean wasResizedThisFrame() {
        return this.wasResizedThisFrame;
    }

    public abstract GameFrameBuffer getNewFrameBuffer(int var1, int var2);

    protected void setCurrentBuffer(GameFrameBuffer frameBuffer) {
        boolean updateView = this.currentBuffer != frameBuffer;
        this.currentBuffer = frameBuffer == null ? this.windowBuffer : frameBuffer;
        if (updateView) {
            this.updateView();
        }
    }

    public Input getInput() {
        return this.input;
    }

    public abstract void setVSync(boolean var1);

    public int getWidth() {
        return this.windowWidth;
    }

    public int getHeight() {
        return this.windowHeight;
    }

    public int getFrameWidth() {
        return this.windowFrameWidth;
    }

    public int getFrameHeight() {
        return this.windowFrameHeight;
    }

    public int getSceneWidth() {
        return this.sceneBuffer == null ? 1 : this.sceneBuffer.getWidth();
    }

    public int getSceneHeight() {
        return this.sceneBuffer == null ? 1 : this.sceneBuffer.getHeight();
    }

    public int getHudWidth() {
        return this.hudBuffer == null ? 1 : this.hudBuffer.getWidth();
    }

    public int getHudHeight() {
        return this.hudBuffer == null ? 1 : this.hudBuffer.getHeight();
    }

    public void setSceneSize(float size) {
        if ((size = GameMath.toDecimals(size, 2)) != this.sceneSize) {
            this.sceneSize = size;
            this.sceneSizeChanged = true;
        }
    }

    public void setHudSize(float size) {
        if ((size = GameMath.toDecimals(size, 2)) != this.hudSize) {
            this.hudSize = size;
            this.hudSizeChanged = true;
        }
    }

    public GameFrameBuffer getCurrentBuffer() {
        return this.currentBuffer;
    }

    public void setSceneShade(float red, float green, float blue) {
        this.sceneRed = red;
        this.sceneGreen = green;
        this.sceneBlue = blue;
    }

    public void setSceneDarkness(float percent, int minViewDist, int fadeDist) {
        this.sceneDarkness = percent;
        this.sceneDarknessMinViewDist = minViewDist;
        this.sceneDarknessFadeDist = fadeDist;
    }

    public void setSceneShade(Color color) {
        this.setSceneShade((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f);
    }

    public abstract void requestAttention();

    public abstract void setIcon(GameWindowIcon var1);

    public abstract void setCursor(CURSOR var1);

    public abstract CURSOR getCursor();

    public abstract void setCursorMode(int var1);

    public abstract void putClipboard(String var1);

    public abstract String getClipboard();

    public FBOType getFBOCapabilities() {
        return this.FBO_CAPABILITIES;
    }

    public void putClipboardDefault(String str) {
        StringSelection s = new StringSelection(str);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, s);
    }

    public String getClipboardDefault() {
        try {
            return (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        }
        catch (UnsupportedFlavorException | IOException e) {
            System.err.println("Could not get clipboard string: " + e.getMessage());
            return "";
        }
    }

    public abstract void loadCursor(CURSOR var1, GameTexture var2, int var3);

    public abstract void destroyCursor(long var1);

    public static enum FBOType {
        GL30,
        EXT;

    }

    public static enum CURSOR {
        DEFAULT(0),
        INVISIBLE(-1),
        INTERACT(1),
        GRAB_OFF(2),
        GRAB_ON(3),
        LOCK(4),
        UNLOCK(5),
        CARET(6, 4, 8),
        TRASH(7),
        ARROWS_DIAGONAL1(8, 7, 7),
        ARROWS_DIAGONAL2(9, 7, 7),
        ARROWS_VERTICAL(10, 4, 8),
        ARROWS_HORIZONTAL(11, 8, 4);

        private long cursor;
        public final int spriteX;
        public final int xOffset;
        public final int yOffset;
        private GameTexture texture;

        private CURSOR(int spriteX, int xOffset, int yOffset) {
            this.spriteX = spriteX;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.cursor = 0L;
        }

        private CURSOR(int spriteX) {
            this(spriteX, 0, 0);
        }

        public long getCursor() {
            return this.cursor;
        }

        public void setCursor(long cursor) {
            this.cursor = cursor;
        }

        public GameTexture getTexture() {
            return this.texture;
        }

        public void setTexture(GameTexture texture) {
            this.texture = texture;
        }
    }
}

