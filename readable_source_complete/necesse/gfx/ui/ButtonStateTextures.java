/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.ui;

import java.io.FileNotFoundException;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.GameInterfaceStyle;

public class ButtonStateTextures {
    public GameTexture active;
    public GameTexture inactive;
    public GameTexture highlighted;
    public GameTexture downActive;
    public GameTexture downInactive;
    public GameTexture downHighlighted;

    public ButtonStateTextures(GameInterfaceStyle style, String path) {
        this.active = style.fromFile(path);
        this.inactive = this.fromFile(style, path, "_inactive", this.active);
        this.highlighted = this.fromFile(style, path, "_highlighted", this.active);
        this.downActive = this.fromFile(style, path, "_down", this.active);
        this.downInactive = this.fromFile(style, path, "_down_inactive", this.downActive);
        this.downHighlighted = this.fromFile(style, path, "_down_highlighted", this.downActive);
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

