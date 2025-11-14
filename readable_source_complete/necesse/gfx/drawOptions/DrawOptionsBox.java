/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions;

import java.awt.Rectangle;
import necesse.gfx.drawOptions.DrawOptions;

public interface DrawOptionsBox
extends DrawOptions {
    public Rectangle getBoundingBox();

    public static DrawOptionsBox concat(final DrawOptionsBox ... drawOptionsBoxes) {
        if (drawOptionsBoxes.length == 0) {
            throw new IllegalArgumentException("Must have at least one argument");
        }
        return new DrawOptionsBox(){

            @Override
            public void draw() {
                for (DrawOptionsBox drawOptionsBox : drawOptionsBoxes) {
                    drawOptionsBox.draw();
                }
            }

            @Override
            public Rectangle getBoundingBox() {
                Rectangle box = null;
                for (DrawOptionsBox drawOptionsBox : drawOptionsBoxes) {
                    box = box == null ? drawOptionsBox.getBoundingBox() : box.union(drawOptionsBox.getBoundingBox());
                }
                return box;
            }
        };
    }
}

