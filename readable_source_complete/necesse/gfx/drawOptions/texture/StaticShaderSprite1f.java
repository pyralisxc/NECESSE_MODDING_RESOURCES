/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL13
 */
package necesse.gfx.drawOptions.texture;

import necesse.gfx.drawOptions.texture.ShaderSpriteAbstract;
import org.lwjgl.opengl.GL13;

public class StaticShaderSprite1f
extends ShaderSpriteAbstract {
    protected float x;

    public StaticShaderSprite1f(int pos, float x) {
        super(pos);
        this.x = x;
    }

    @Override
    public void startTopLeft() {
        GL13.glMultiTexCoord1f((int)this.glPos, (float)this.x);
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

