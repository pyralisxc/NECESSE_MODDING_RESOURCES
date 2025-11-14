/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL13
 */
package necesse.gfx.drawOptions.texture;

import necesse.gfx.drawOptions.texture.ShaderSpriteAbstract;
import org.lwjgl.opengl.GL13;

public class StaticShaderSprite4i
extends ShaderSpriteAbstract {
    protected int x;
    protected int y;
    protected int z;
    protected int w;

    public StaticShaderSprite4i(int pos, int x, int y, int z, int w) {
        super(pos);
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    @Override
    public void startTopLeft() {
        GL13.glMultiTexCoord4i((int)this.glPos, (int)this.x, (int)this.y, (int)this.z, (int)this.w);
    }

    @Override
    public void startTopRight() {
    }

    @Override
    public void startBotRight() {
    }

    @Override
    public void startBotLeft() {
    }
}

