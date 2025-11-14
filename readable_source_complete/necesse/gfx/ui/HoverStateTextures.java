/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui;

import java.io.FileNotFoundException;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.GameInterfaceStyle;

public class HoverStateTextures {
    public GameTexture active;
    public GameTexture highlighted;

    public HoverStateTextures(GameInterfaceStyle style, String path) {
        this.active = style.fromFile(path);
        this.highlighted = this.fromFile(style, path, "_highlighted", this.active);
    }

    protected GameTexture fromFile(GameInterfaceStyle style, String basePath, String addedPath, GameTexture defaultReturn) {
        try {
            return style.fromFileRaw(basePath + addedPath);
        }
        catch (FileNotFoundException e) {
            try {
                return style.fromDefaultFileRaw(basePath + addedPath);
            }
            catch (FileNotFoundException ex) {
                return defaultReturn;
            }
        }
    }
}

