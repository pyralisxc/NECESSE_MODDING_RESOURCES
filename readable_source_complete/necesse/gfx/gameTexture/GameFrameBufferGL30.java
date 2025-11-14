/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.opengl.GL30
 */
package necesse.gfx.gameTexture;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import necesse.gfx.gameTexture.GameFrameBuffer;
import necesse.gfx.gameTexture.GameTexture;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class GameFrameBufferGL30
extends GameFrameBuffer {
    public final int width;
    public final int height;
    public final int frameBufferObject;
    public final int textureID;
    private final boolean complete;

    public GameFrameBufferGL30(Consumer<GameFrameBuffer> binder, int width, int height) {
        super(binder);
        this.width = width;
        this.height = height;
        this.frameBufferObject = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer((int)36160, (int)this.frameBufferObject);
        this.textureID = GL30.glGenTextures();
        GL30.glBindTexture((int)3553, (int)this.textureID);
        GL30.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL30.glTexParameteri((int)3553, (int)10243, (int)33071);
        GL30.glTexImage2D((int)3553, (int)0, (int)32856, (int)Math.max(1, width), (int)Math.max(1, height), (int)0, (int)6408, (int)5121, (ByteBuffer)null);
        GL30.glTexParameteri((int)3553, (int)10241, (int)9729);
        GL30.glTexParameteri((int)3553, (int)10240, (int)9729);
        GL30.glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)this.textureID, (int)0);
        this.complete = this.checkStatus();
        if (this.complete) {
            GL30.glBindFramebuffer((int)36160, (int)this.frameBufferObject);
            GL30.glClearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)0.0f);
            GL30.glClearDepth((double)0.0);
            GL30.glViewport((int)0, (int)0, (int)width, (int)height);
            GL30.glMatrixMode((int)5889);
            GL30.glLoadIdentity();
            GL30.glOrtho((double)0.0, (double)width, (double)height, (double)0.0, (double)0.0, (double)1.0);
            GL30.glMatrixMode((int)5888);
            GL30.glEnable((int)2929);
            GL30.glDepthFunc((int)519);
            GL30.glEnable((int)3553);
            GL30.glEnable((int)3042);
            GL14.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)771);
        }
        this.glUnbind();
    }

    private boolean checkStatus() {
        int status = GL30.glCheckFramebufferStatus((int)36160);
        if (status != 36053) {
            System.err.println("Could not create frame buffer");
            switch (status) {
                case 36054: {
                    System.err.println("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
                    break;
                }
                case 36055: {
                    System.err.println("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
                    break;
                }
                case 36059: {
                    System.err.println("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
                    break;
                }
                case 36060: {
                    System.err.println("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
                    break;
                }
                case 36061: {
                    System.err.println("GL_FRAMEBUFFER_UNSUPPORTED");
                    break;
                }
                case 36182: {
                    System.err.println("GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE");
                    break;
                }
                case 33305: {
                    System.err.println("GL_FRAMEBUFFER_UNDEFINED");
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getColorBufferTextureID() {
        return this.textureID;
    }

    @Override
    public int getDepthBufferTextureID() {
        return 0;
    }

    @Override
    protected void glBind() {
        GL30.glBindFramebuffer((int)36160, (int)this.frameBufferObject);
    }

    @Override
    protected void glUnbind() {
        GL30.glBindFramebuffer((int)36160, (int)0);
    }

    @Override
    public void dispose() {
        GL30.glDeleteTextures((int)this.textureID);
        GL30.glDeleteFramebuffers((int)this.frameBufferObject);
    }

    @Override
    public GameTexture getTextureAndDisposeFrameBuffer(String textureDebugName) {
        GL30.glDeleteFramebuffers((int)this.frameBufferObject);
        return GameTexture.fromTextureID(textureDebugName, this.textureID, this.width, this.height);
    }

    @Override
    public void bindTexture(int texturePos) {
        GL30.glActiveTexture((int)texturePos);
        GL30.glBindTexture((int)3553, (int)this.textureID);
        GL30.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL30.glTexParameteri((int)3553, (int)10243, (int)33071);
    }
}

