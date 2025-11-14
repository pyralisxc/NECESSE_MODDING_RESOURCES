/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package necesse.gfx.gameFont;

import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.gameFont.GameFontGlyphPosition;
import necesse.gfx.gameTexture.GameTexture;
import org.lwjgl.opengl.GL11;

public class GameFontGlyphPositionTexture
extends GameFontGlyphPosition {
    public final GameTexture texture;
    private final float x1;
    private final float x2;
    private final float y1;
    private final float y2;

    public GameFontGlyphPositionTexture(GameTexture texture, int textureX, int textureY, int width, int height) {
        super(textureX, textureY, width, height);
        this.texture = texture;
        this.x1 = TextureDrawOptions.pixel(textureX, texture.getWidth());
        this.x2 = TextureDrawOptions.pixel(textureX + width, texture.getWidth());
        this.y1 = TextureDrawOptions.pixel(textureY, texture.getHeight());
        this.y2 = TextureDrawOptions.pixel(textureY + height, texture.getHeight());
    }

    public GameFontGlyphPositionTexture(GameTexture texture, GameFontGlyphPosition position) {
        this(texture, position.textureX, position.textureY, position.width, position.height);
    }

    public void draw(float drawX, float drawY, float width, float height) {
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)this.x1, (float)this.y1);
        GL11.glVertex2f((float)(drawX += (float)this.drawXOffset), (float)(drawY += (float)this.drawYOffset));
        GL11.glTexCoord2f((float)this.x1, (float)this.y2);
        GL11.glVertex2f((float)drawX, (float)(drawY + height));
        GL11.glTexCoord2f((float)this.x2, (float)this.y2);
        GL11.glVertex2f((float)(drawX + width), (float)(drawY + height));
        GL11.glTexCoord2f((float)this.x2, (float)this.y1);
        GL11.glVertex2f((float)(drawX + width), (float)drawY);
        GL11.glEnd();
    }

    public void drawNoBegin(float drawX, float drawY, float width, float height) {
        GL11.glTexCoord2f((float)this.x1, (float)this.y1);
        GL11.glVertex2f((float)(drawX += (float)this.drawXOffset), (float)(drawY += (float)this.drawYOffset));
        GL11.glTexCoord2f((float)this.x1, (float)this.y2);
        GL11.glVertex2f((float)drawX, (float)(drawY + height));
        GL11.glTexCoord2f((float)this.x2, (float)this.y2);
        GL11.glVertex2f((float)(drawX + width), (float)(drawY + height));
        GL11.glTexCoord2f((float)this.x2, (float)this.y1);
        GL11.glVertex2f((float)(drawX + width), (float)drawY);
    }
}

