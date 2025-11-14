/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;

public abstract class ItemControllerInteract {
    public int levelX;
    public int levelY;

    public ItemControllerInteract(int levelX, int levelY) {
        this.levelX = levelX;
        this.levelY = levelY;
    }

    public abstract DrawOptions getDrawOptions(GameCamera var1);

    public abstract void onCurrentlyFocused(GameCamera var1);
}

