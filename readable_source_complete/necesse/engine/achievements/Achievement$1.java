/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.achievements;

import java.awt.Rectangle;
import necesse.gfx.drawOptions.DrawOptionsBox;
import necesse.gfx.drawOptions.DrawOptionsList;

static class Achievement.1
implements DrawOptionsBox {
    final /* synthetic */ int val$drawX;
    final /* synthetic */ int val$drawY;
    final /* synthetic */ int val$width;
    final /* synthetic */ int val$finalHeight;
    final /* synthetic */ DrawOptionsList val$drawOptions;

    Achievement.1() {
        this.val$drawX = n;
        this.val$drawY = n2;
        this.val$width = n3;
        this.val$finalHeight = n4;
        this.val$drawOptions = drawOptionsList;
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(this.val$drawX, this.val$drawY, this.val$width, this.val$finalHeight);
    }

    @Override
    public void draw() {
        this.val$drawOptions.draw();
    }
}
