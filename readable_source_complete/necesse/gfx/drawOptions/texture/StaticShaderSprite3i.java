/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL13
 */
package necesse.gfx.drawOptions.texture;

import necesse.gfx.drawOptions.texture.ShaderSpriteAbstract;
import org.lwjgl.opengl.GL13;

public class StaticShaderSprite3i
extends ShaderSpriteAbstract {
    protected int x;
    protected int y;
    protected int z;

    public StaticShaderSprite3i(int pos, int x, int y, int z) {
        super(pos);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void startTopLeft() {
        GL13.glMultiTexCoord3i((int)this.glPos, (int)this.x, (int)this.y, (int)this.z);
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

