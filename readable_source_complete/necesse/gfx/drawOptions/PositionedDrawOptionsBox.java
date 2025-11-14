/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions;

import java.awt.Rectangle;
import necesse.gfx.drawOptions.DrawOptionsBox;

public interface PositionedDrawOptionsBox {
    public Rectangle getBoundingBox(int var1, int var2);

    public void draw(int var1, int var2);

    default public DrawOptionsBox toDrawOptionsBox(final int drawX, final int drawY) {
        return new DrawOptionsBox(){

            @Override
            public Rectangle getBoundingBox() {
                return PositionedDrawOptionsBox.this.getBoundingBox(drawX, drawY);
            }

            @Override
            public void draw() {
                PositionedDrawOptionsBox.this.draw(drawX, drawY);
            }
        };
    }
}

