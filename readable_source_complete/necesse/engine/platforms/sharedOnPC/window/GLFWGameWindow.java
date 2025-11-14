/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.Callbacks
 *  org.lwjgl.glfw.GLFW
 *  org.lwjgl.glfw.GLFWImage
 *  org.lwjgl.glfw.GLFWImage$Buffer
 *  org.lwjgl.glfw.GLFWVidMode
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.Platform
 *  org.lwjgl.system.Pointer
 */
package necesse.engine.platforms.sharedOnPC.window;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;
import necesse.engine.GameAuth;
import necesse.engine.GameLaunch;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.platforms.Platform;
import necesse.engine.platforms.sharedOnPC.window.GLFWGameError;
import necesse.engine.util.PointerList;
import necesse.engine.window.DisplayMode;
import necesse.engine.window.GameWindow;
import necesse.engine.window.GameWindowCreationException;
import necesse.engine.window.GameWindowIcon;
import necesse.engine.window.WindowManager;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.gameTexture.GameFrameBufferEXT;
import necesse.gfx.gameTexture.GameFrameBufferGL30;
import necesse.gfx.gameTexture.GameTexture;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Pointer;

public class GLFWGameWindow
extends GameWindow {
    private long glfwWindow = 0L;
    private boolean monitorNeedsHDRHack = false;
    private GameWindow.CURSOR cursor;

    @Override
    public void createWindow(GameWindow parent, boolean updateSize) {
        this.createWindow((GLFWGameWindow)parent, updateSize);
    }

    public void createWindow(GLFWGameWindow parent, boolean updateSize) {
        super.createWindow(parent, updateSize);
        GLFW.glfwDefaultWindowHints();
        this.setupWindowHints();
        GLFW.glfwWindowHint((int)131076, (int)0);
        String title = "Necesse v. 1.0.1";
        long authentication = GameAuth.getAuthentication();
        if (authentication != 0L) {
            title = title + " : Auth " + authentication;
        }
        this.glfwWindow = GLFW.glfwCreateWindow((int)1280, (int)720, (CharSequence)title, (long)0L, (long)(parent != null ? parent.glfwWindow : 0L));
        if (this.glfwWindow == 0L) {
            Renderer.queryGLError("windowCreation");
            throw new GameWindowCreationException("Failed to create the GLFW window");
        }
        try (MemoryStack stack = MemoryStack.stackPush();){
            IntBuffer width2 = stack.mallocInt(1);
            IntBuffer height2 = stack.mallocInt(1);
            GLFW.glfwGetWindowSize((long)this.glfwWindow, (IntBuffer)width2, (IntBuffer)height2);
            this.windowWidth = Math.max(width2.get(0), 1);
            this.windowHeight = Math.max(height2.get(0), 1);
            IntBuffer frameWidth = stack.mallocInt(1);
            IntBuffer frameHeight = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize((long)this.glfwWindow, (IntBuffer)frameWidth, (IntBuffer)frameHeight);
            this.windowFrameWidth = Math.max(frameWidth.get(0), 1);
            this.windowFrameHeight = Math.max(frameHeight.get(0), 1);
        }
        GLFW.glfwSetWindowSizeCallback((long)this.glfwWindow, (win, width, height) -> {
            this.windowWidth = Math.max(width, 1);
            this.windowHeight = Math.max(height, 1);
            this.hasResized = true;
        });
        GLFW.glfwSetFramebufferSizeCallback((long)this.glfwWindow, (win, width, height) -> {
            this.windowFrameWidth = Math.max(width, 1);
            this.windowFrameHeight = Math.max(height, 1);
            this.hasResized = true;
        });
        GLFW.glfwSetWindowFocusCallback((long)this.glfwWindow, (window, hasFocus) -> {
            if (hasFocus) {
                this.performHDRHackIfNeeded();
            }
        });
        this.makeCurrent();
        GL.createCapabilities();
        if (GL.getCapabilities().OpenGL30) {
            this.FBO_CAPABILITIES = GameWindow.FBOType.GL30;
        } else if (GL.getCapabilities().GL_EXT_framebuffer_object) {
            this.FBO_CAPABILITIES = GameWindow.FBOType.EXT;
        } else {
            GameLog.warn.println("Could not find framebuffer capabilities, some functions may not work.");
        }
        Renderer.queryGLError("PostWindowGL");
        GL11.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glClearDepth((double)0.0);
        if (updateSize) {
            this.updateWindowSize();
        } else {
            this.updateResize();
        }
        GL11.glEnable((int)3553);
        GL11.glEnable((int)2929);
        GL11.glDepthFunc((int)519);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        this.setVSync(Settings.vSyncEnabled);
        this.monitorNeedsHDRHack = Platform.getWindowManager().doesMonitorNeedHDRHack();
    }

    private void setupWindowHints() {
        org.lwjgl.system.Platform platform = org.lwjgl.system.Platform.get();
        if (platform != org.lwjgl.system.Platform.MACOSX) {
            switch (Settings.displayMode) {
                case Borderless: {
                    GLFW.glfwWindowHint((int)131077, (int)0);
                    GLFW.glfwWindowHint((int)131075, (int)0);
                    break;
                }
                case Fullscreen: {
                    GLFW.glfwWindowHint((int)131077, (int)1);
                    GLFW.glfwWindowHint((int)131075, (int)0);
                    break;
                }
                case Windowed: {
                    GLFW.glfwWindowHint((int)131077, (int)1);
                    GLFW.glfwWindowHint((int)131075, (int)1);
                }
            }
        } else {
            GLFW.glfwWindowHint((int)131075, (int)1);
            GLFW.glfwWindowHint((int)131077, (int)1);
        }
    }

    private void performHDRHackIfNeeded() {
        if (this.monitorNeedsHDRHack) {
            try (MemoryStack stack = MemoryStack.stackPush();){
                long monitor = GLFW.glfwGetWindowMonitor((long)this.glfwWindow);
                if (monitor == 0L) {
                    return;
                }
                GLFWVidMode vidMode = GLFW.glfwGetVideoMode((long)monitor);
                int refreshRate = vidMode != null ? vidMode.refreshRate() : 60;
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                GLFW.glfwGetWindowSize((long)this.glfwWindow, (IntBuffer)width, (IntBuffer)height);
                GLFW.glfwSetWindowMonitor((long)this.glfwWindow, (long)monitor, (int)0, (int)0, (int)width.get(0), (int)height.get(0), (int)(refreshRate + 1));
                GLFW.glfwSetWindowMonitor((long)this.glfwWindow, (long)monitor, (int)0, (int)0, (int)width.get(0), (int)height.get(0), (int)0);
                GLFW.glfwSetWindowMonitor((long)this.glfwWindow, (long)monitor, (int)0, (int)0, (int)width.get(0), (int)height.get(0), (int)refreshRate);
            }
        }
    }

    @Override
    public void updateWindowSize() {
        try (MemoryStack stack = MemoryStack.stackPush();){
            long monitor;
            int monitorIndex = Settings.monitor;
            if (GameLaunch.launchMonitor >= 0) {
                monitorIndex = GameLaunch.launchMonitor;
            }
            if ((monitor = WindowManager.getMonitor(monitorIndex)) == 0L) {
                System.out.println("Could not find monitor from settings, falling back to primary monitor");
                monitor = GLFW.glfwGetPrimaryMonitor();
            }
            if (monitor == 0L) {
                throw new NullPointerException("Could not find monitor");
            }
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode((long)monitor);
            if (vidMode == null) {
                throw new NullPointerException("Could not find monitor video mode");
            }
            int width = Settings.displaySize == null ? vidMode.width() : Settings.displaySize.width;
            int height = Settings.displaySize == null ? vidMode.height() : Settings.displaySize.height;
            IntBuffer x = stack.mallocInt(1);
            IntBuffer y = stack.mallocInt(1);
            GLFW.glfwGetMonitorPos((long)monitor, (IntBuffer)x, (IntBuffer)y);
            DisplayMode modeToApply = Settings.displayMode;
            if (modeToApply == DisplayMode.Borderless && this.monitorNeedsHDRHack) {
                modeToApply = DisplayMode.Fullscreen;
            }
            if (org.lwjgl.system.Platform.get() != org.lwjgl.system.Platform.MACOSX) {
                switch (modeToApply) {
                    case Borderless: {
                        GLFW.glfwSetWindowMonitor((long)this.glfwWindow, (long)0L, (int)x.get(0), (int)y.get(0), (int)vidMode.width(), (int)vidMode.height(), (int)vidMode.refreshRate());
                        GLFW.glfwSetWindowPos((long)this.glfwWindow, (int)x.get(0), (int)y.get(0));
                        break;
                    }
                    case Fullscreen: {
                        if (Platform.getWindowManager().doesMonitorNeedHDRHack()) {
                            GLFW.glfwSetWindowMonitor((long)this.glfwWindow, (long)monitor, (int)0, (int)0, (int)width, (int)height, (int)(vidMode.refreshRate() + 1));
                            GLFW.glfwSetWindowMonitor((long)this.glfwWindow, (long)monitor, (int)0, (int)0, (int)width, (int)height, (int)0);
                        }
                        GLFW.glfwSetWindowMonitor((long)this.glfwWindow, (long)monitor, (int)0, (int)0, (int)width, (int)height, (int)vidMode.refreshRate());
                        this.requestFocus();
                        break;
                    }
                    case Windowed: {
                        int xPos = x.get(0) + (vidMode.width() - width) / 2;
                        int yPos = y.get(0) + (vidMode.height() - height) / 2;
                        GLFW.glfwSetWindowMonitor((long)this.glfwWindow, (long)0L, (int)yPos, (int)yPos, (int)width, (int)height, (int)vidMode.refreshRate());
                        GLFW.glfwSetWindowPos((long)this.glfwWindow, (int)xPos, (int)yPos);
                    }
                }
            } else {
                GLFW.glfwSetWindowMonitor((long)this.glfwWindow, (long)0L, (int)(x.get(0) + (vidMode.width() - width) / 2), (int)(y.get(0) + (vidMode.height() - height) / 2), (int)width, (int)height, (int)vidMode.refreshRate());
            }
        }
        this.updateResize();
    }

    @Override
    public void updateView() {
        GL11.glMatrixMode((int)5889);
        GL11.glLoadIdentity();
        GL11.glOrtho((double)0.0, (double)this.currentBuffer.getWidth(), (double)this.currentBuffer.getHeight(), (double)0.0, (double)0.0, (double)1.0);
        GL11.glMatrixMode((int)5888);
        GL11.glViewport((int)0, (int)0, (int)this.currentBuffer.getWidth(), (int)this.currentBuffer.getHeight());
    }

    @Override
    public void update() {
        GLFW.glfwSwapBuffers((long)this.glfwWindow);
    }

    @Override
    public void preloadUpdate() {
        GLFW.glfwPollEvents();
        GLFW.glfwSwapBuffers((long)this.glfwWindow);
    }

    @Override
    public void makeCurrent() {
        GLFW.glfwMakeContextCurrent((long)this.glfwWindow);
    }

    @Override
    public void renderHud(float alpha) {
        if (GameResources.sharpenShader != null && Settings.sharpenInterface) {
            try {
                GameResources.sharpenShader.use(this.getFrameWidth(), this.getFrameHeight(), this.hudSize - 1.0f);
                GameFrameBuffer.draw(this.hudBuffer.getColorBufferTextureID(), 0, 0, this.getFrameWidth(), this.getFrameHeight(), () -> GL11.glColor4f((float)alpha, (float)alpha, (float)alpha, (float)alpha), null);
            }
            catch (Exception e) {
                GameResources.sharpenShader.stop();
            }
        } else {
            GameFrameBuffer.draw(this.hudBuffer.getColorBufferTextureID(), 0, 0, this.getFrameWidth(), this.getFrameHeight(), () -> GL11.glColor4f((float)alpha, (float)alpha, (float)alpha, (float)alpha), null);
        }
    }

    @Override
    public void show() {
        GLFW.glfwShowWindow((long)this.glfwWindow);
    }

    @Override
    public void requestClose() {
        GLFW.glfwSetWindowShouldClose((long)this.glfwWindow, (boolean)true);
    }

    @Override
    public void destroy() {
        if (this.isCreated()) {
            super.destroy();
            Callbacks.glfwFreeCallbacks((long)this.glfwWindow);
            GLFW.glfwDestroyWindow((long)this.glfwWindow);
            this.glfwWindow = 0L;
        }
    }

    @Override
    public void printCapabilities(PrintStream printStream) {
        GLCapabilities capabilities = GL.getCapabilities();
        Class<?> capClass = capabilities.getClass();
        int maxNameLength = 0;
        for (Field field : capClass.getFields()) {
            maxNameLength = Math.max(maxNameLength, field.getName().length());
        }
        for (Field field : capClass.getFields()) {
            Object o;
            StringBuilder builder = new StringBuilder().append(field.getName());
            int spaces = maxNameLength - field.getName().length();
            for (int i = 0; i < spaces; ++i) {
                builder.append(".");
            }
            builder.append(": ");
            try {
                o = field.get(capabilities);
                if (o instanceof Long) {
                    o = Long.toHexString((Long)o);
                } else if (o instanceof Integer) {
                    o = Integer.toHexString((Integer)o);
                } else if (o instanceof Short) {
                    o = Integer.toHexString(((Short)o).shortValue());
                } else if (o instanceof Byte) {
                    o = Integer.toHexString(((Byte)o).byteValue());
                }
            }
            catch (IllegalAccessException e) {
                o = "ERR";
            }
            builder.append(o);
            printStream.println(builder);
        }
    }

    @Override
    public void requestFocus() {
        GLFW.glfwFocusWindow((long)this.glfwWindow);
    }

    @Override
    public boolean isCloseRequested() {
        return GLFW.glfwWindowShouldClose((long)this.glfwWindow);
    }

    @Override
    public boolean isFocused() {
        return GLFW.glfwGetWindowAttrib((long)this.glfwWindow, (int)131073) == 1;
    }

    @Override
    public boolean isCreated() {
        return this.glfwWindow != 0L;
    }

    @Override
    public GameFrameBuffer getNewFrameBuffer(int width, int height) {
        if (width == 0 || height == 0) {
            return new GlfwWindowFrameBuffer(x$0 -> this.setCurrentBuffer((GameFrameBuffer)x$0));
        }
        if (this.FBO_CAPABILITIES == GameWindow.FBOType.EXT) {
            return new GameFrameBufferEXT(x$0 -> this.setCurrentBuffer((GameFrameBuffer)x$0), width, height);
        }
        Renderer.queryGLError("PostNewEXTFrameBuffer");
        GameFrameBufferGL30 buffer = new GameFrameBufferGL30(x$0 -> this.setCurrentBuffer((GameFrameBuffer)x$0), width, height);
        int error = Renderer.queryGLError(null);
        if (error != 0) {
            System.out.println("Detected error trying to create GL30 frame buffers, trying EXT.");
            Renderer.printGLError(null, error);
            try {
                GameFrameBufferEXT extBuffer = new GameFrameBufferEXT(x$0 -> this.setCurrentBuffer((GameFrameBuffer)x$0), width, height);
                error = Renderer.queryGLError(null);
                if (error != 0) {
                    System.out.println("Could not create EXT frame buffer either, some things may not work correctly :(");
                    Renderer.printGLError(null, error);
                    return buffer;
                }
                this.FBO_CAPABILITIES = GameWindow.FBOType.EXT;
                return extBuffer;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return buffer;
    }

    @Override
    public void setVSync(boolean enable) {
        GLFW.glfwSwapInterval((int)(enable ? 1 : 0));
    }

    @Override
    public void requestAttention() {
        GLFW.glfwRequestWindowAttention((long)this.glfwWindow);
    }

    @Override
    public void setIcon(GameWindowIcon gameIcon) {
        if (org.lwjgl.system.Platform.get() == org.lwjgl.system.Platform.MACOSX) {
            return;
        }
        try (GLFWImage.Buffer buffer = GLFWImage.malloc((int)4);){
            PointerList pointerList = new PointerList(new Pointer[0]);
            pointerList.add(GLFWGameWindow.bufferAndScaleIcon(buffer, 0, gameIcon, 16));
            pointerList.add(GLFWGameWindow.bufferAndScaleIcon(buffer, 1, gameIcon, 32));
            pointerList.add(GLFWGameWindow.bufferAndScaleIcon(buffer, 2, gameIcon, 64));
            pointerList.add(GLFWGameWindow.bufferAndScaleIcon(buffer, 3, gameIcon, 128));
            GLFW.glfwSetWindowIcon((long)this.glfwWindow, (GLFWImage.Buffer)buffer);
            pointerList.freeAll();
        }
    }

    public static ByteBuffer bufferAndScaleIcon(GLFWImage.Buffer buffer, int position, GameWindowIcon icon, int resolution) {
        ByteBuffer image = GameWindowIcon.scaleIcon(icon, resolution);
        ((GLFWImage.Buffer)buffer.position(position)).width(resolution).height(resolution).pixels(image);
        return image;
    }

    @Override
    public void setCursor(GameWindow.CURSOR cursor) {
        this.cursor = cursor;
        long cursorAddress = cursor.getCursor();
        GLFW.glfwSetCursor((long)this.glfwWindow, (long)cursorAddress);
    }

    @Override
    public GameWindow.CURSOR getCursor() {
        return this.cursor;
    }

    @Override
    public void setCursorMode(int mode) {
        GLFW.glfwSetInputMode((long)this.glfwWindow, (int)208897, (int)mode);
    }

    @Override
    public void putClipboard(String str) {
        GLFW.glfwSetClipboardString((long)this.glfwWindow, (CharSequence)str);
    }

    @Override
    public String getClipboard() {
        return GLFWGameError.tryGLFWError(new GLFWGameError.Supplier<String>(new int[]{65545}){

            @Override
            public String run() {
                return GLFW.glfwGetClipboardString((long)GLFWGameWindow.this.glfwWindow);
            }

            @Override
            public String onCatch(GLFWGameError error) {
                return error.errorName;
            }
        });
    }

    @Override
    public void loadCursor(GameWindow.CURSOR cursor, GameTexture texture, int size) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            int resolution;
            ByteBuffer buffer;
            GLFWImage cursorImage = GLFWImage.malloc((MemoryStack)stack);
            int xOffset = 0;
            int yOffset = 0;
            if (cursor.spriteX < 0) {
                buffer = new GameTexture((String)"cursorBuffer", (int)1, (int)1).buffer;
                resolution = 1;
            } else {
                GameTexture sprite;
                resolution = 32;
                xOffset = cursor.xOffset;
                yOffset = cursor.yOffset;
                GameTexture cursorTexture = new GameTexture(texture, cursor.spriteX, 0, resolution);
                cursorTexture = new GameTexture(texture, cursor.spriteX, 0, resolution);
                float zoom = Renderer.getCursorSizeZoom(size);
                if (zoom != 1.0f) {
                    resolution = (int)((float)resolution * zoom);
                    xOffset = (int)((float)xOffset * zoom);
                    yOffset = (int)((float)yOffset * zoom);
                    sprite = cursorTexture.resize(resolution, resolution);
                } else {
                    sprite = cursorTexture;
                }
                buffer = sprite.buffer;
                cursor.setTexture(cursorTexture.clamp(1).makeFinal());
            }
            buffer.position(0);
            cursorImage.width(resolution).height(resolution).pixels(buffer);
            long lastCursor = cursor.getCursor();
            if (lastCursor != 0L) {
                this.destroyCursor(lastCursor);
            }
            cursor.setCursor(GLFW.glfwCreateCursor((GLFWImage)cursorImage, (int)xOffset, (int)yOffset));
        }
    }

    @Override
    public void destroyCursor(long cursor) {
        GLFW.glfwDestroyCursor((long)cursor);
    }

    public long getGlfwWindow() {
        return this.glfwWindow;
    }

    private class GlfwWindowFrameBuffer
    extends GameFrameBuffer {
        public GlfwWindowFrameBuffer(Consumer<GameFrameBuffer> binder) {
            super(binder);
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public int getWidth() {
            return GLFWGameWindow.this.getFrameWidth();
        }

        @Override
        public int getHeight() {
            return GLFWGameWindow.this.getFrameHeight();
        }

        @Override
        public int getColorBufferTextureID() {
            return 0;
        }

        @Override
        public int getDepthBufferTextureID() {
            return 0;
        }

        @Override
        public void glBind() {
            if (GLFWGameWindow.this.FBO_CAPABILITIES == GameWindow.FBOType.EXT) {
                EXTFramebufferObject.glBindFramebufferEXT((int)36160, (int)0);
            } else {
                GL30.glBindFramebuffer((int)36160, (int)0);
            }
        }

        @Override
        public void glUnbind() {
            this.glBind();
        }

        @Override
        public void dispose() {
        }

        @Override
        public GameTexture getTextureAndDisposeFrameBuffer(String textureDebugName) {
            throw new UnsupportedOperationException("Cannot get texture from a window frame buffer");
        }

        @Override
        public void bindTexture(int texturePos) {
            throw new UnsupportedOperationException("Cannot bind a window frame buffer as a texture");
        }
    }
}

