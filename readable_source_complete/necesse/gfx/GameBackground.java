/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx;

import java.awt.Color;
import necesse.engine.Settings;
import necesse.engine.util.PointSetAbstract;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;

public abstract class GameBackground {
    public static GameBackground itemTooltip = new GameBackground(){

        @Override
        public SharedTextureDrawOptions getOutlineDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.tooltip.getOutlineDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getCenterDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.tooltip.getCenterDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.tooltip.getDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getOutlineEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.tooltip.getOutlineEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getCenterEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.tooltip.getCenterEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.tooltip.getEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getTiledDrawOptions(int x, int y, int xPadding, int yPadding, PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
            return Settings.UI.tooltip.getTiledDrawOptions(x, y, xPadding, yPadding, tiles, tileWidth, tileHeight);
        }

        @Override
        public SharedTextureDrawOptions getTiledEdgeDrawOptions(int x, int y, int xPadding, int yPadding, PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
            return Settings.UI.tooltip.getTiledEdgeDrawOptions(x, y, xPadding, yPadding, tiles, tileWidth, tileHeight);
        }

        @Override
        public Color getCenterColor() {
            return Settings.UI.tooltip.getCenterColor();
        }

        @Override
        public int getContentPadding() {
            return Settings.UI.tooltip.contentPadding;
        }
    };
    public static GameBackground form = new GameBackground(){

        @Override
        public SharedTextureDrawOptions getOutlineDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.form.getOutlineDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getCenterDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.form.getCenterDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.form.getDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getOutlineEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.form.getOutlineEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getCenterEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.form.getCenterEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.form.getEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getTiledDrawOptions(int x, int y, int xPadding, int yPadding, PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
            return Settings.UI.form.getTiledDrawOptions(x, y, xPadding, yPadding, tiles, tileWidth, tileHeight);
        }

        @Override
        public SharedTextureDrawOptions getTiledEdgeDrawOptions(int x, int y, int xPadding, int yPadding, PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
            return Settings.UI.form.getTiledEdgeDrawOptions(x, y, xPadding, yPadding, tiles, tileWidth, tileHeight);
        }

        @Override
        public Color getCenterColor() {
            return Settings.UI.form.getCenterColor();
        }

        @Override
        public int getContentPadding() {
            return Settings.UI.form.contentPadding;
        }
    };
    public static GameBackground textBox = new GameBackground(){

        @Override
        public SharedTextureDrawOptions getOutlineDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.textBox.getOutlineDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getCenterDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.textBox.getCenterDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.textBox.getDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getOutlineEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.textBox.getOutlineEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getCenterEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.textBox.getCenterEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.textBox.getEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getTiledDrawOptions(int x, int y, int xPadding, int yPadding, PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
            return Settings.UI.textBox.getTiledDrawOptions(x, y, xPadding, yPadding, tiles, tileWidth, tileHeight);
        }

        @Override
        public SharedTextureDrawOptions getTiledEdgeDrawOptions(int x, int y, int xPadding, int yPadding, PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
            return Settings.UI.textBox.getTiledEdgeDrawOptions(x, y, xPadding, yPadding, tiles, tileWidth, tileHeight);
        }

        @Override
        public Color getCenterColor() {
            return Settings.UI.textBox.getCenterColor();
        }

        @Override
        public int getContentPadding() {
            return Settings.UI.textBox.contentPadding;
        }
    };
    public static GameBackground indent = new GameBackground(){

        @Override
        public SharedTextureDrawOptions getOutlineDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.indent.getOutlineDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getCenterDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.indent.getCenterDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.indent.getDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getOutlineEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.indent.getOutlineEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getCenterEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.indent.getCenterEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.indent.getEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getTiledDrawOptions(int x, int y, int xPadding, int yPadding, PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
            return Settings.UI.indent.getTiledDrawOptions(x, y, xPadding, yPadding, tiles, tileWidth, tileHeight);
        }

        @Override
        public SharedTextureDrawOptions getTiledEdgeDrawOptions(int x, int y, int xPadding, int yPadding, PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
            return Settings.UI.indent.getTiledEdgeDrawOptions(x, y, xPadding, yPadding, tiles, tileWidth, tileHeight);
        }

        @Override
        public Color getCenterColor() {
            return Settings.UI.indent.getCenterColor();
        }

        @Override
        public int getContentPadding() {
            return Settings.UI.indent.contentPadding;
        }
    };
    public static GameBackground indentBorderless = new GameBackground(){

        @Override
        public SharedTextureDrawOptions getOutlineDrawOptions(int x, int y, int width, int height) {
            return null;
        }

        @Override
        public SharedTextureDrawOptions getCenterDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.indentBorderless.getCenterDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.indentBorderless.getDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getOutlineEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.indentBorderless.getOutlineEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getCenterEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.indentBorderless.getCenterEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getEdgeDrawOptions(int x, int y, int width, int height) {
            return Settings.UI.indentBorderless.getEdgeDrawOptions(x, y, width, height);
        }

        @Override
        public SharedTextureDrawOptions getTiledDrawOptions(int x, int y, int xPadding, int yPadding, PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
            return Settings.UI.indentBorderless.getTiledDrawOptions(x, y, xPadding, yPadding, tiles, tileWidth, tileHeight);
        }

        @Override
        public SharedTextureDrawOptions getTiledEdgeDrawOptions(int x, int y, int xPadding, int yPadding, PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
            return Settings.UI.indentBorderless.getTiledEdgeDrawOptions(x, y, xPadding, yPadding, tiles, tileWidth, tileHeight);
        }

        @Override
        public Color getCenterColor() {
            return Settings.UI.indentBorderless.getCenterColor();
        }

        @Override
        public int getContentPadding() {
            return Settings.UI.indentBorderless.contentPadding;
        }
    };

    public static GameBackground getItemTooltipBackground() {
        if (Settings.showItemTooltipBackground) {
            return itemTooltip;
        }
        return null;
    }

    public abstract SharedTextureDrawOptions getOutlineDrawOptions(int var1, int var2, int var3, int var4);

    public abstract SharedTextureDrawOptions getCenterDrawOptions(int var1, int var2, int var3, int var4);

    public abstract SharedTextureDrawOptions getDrawOptions(int var1, int var2, int var3, int var4);

    public abstract SharedTextureDrawOptions getOutlineEdgeDrawOptions(int var1, int var2, int var3, int var4);

    public abstract SharedTextureDrawOptions getCenterEdgeDrawOptions(int var1, int var2, int var3, int var4);

    public abstract SharedTextureDrawOptions getEdgeDrawOptions(int var1, int var2, int var3, int var4);

    public abstract SharedTextureDrawOptions getTiledDrawOptions(int var1, int var2, int var3, int var4, PointSetAbstract<?> var5, int var6, int var7);

    public abstract SharedTextureDrawOptions getTiledEdgeDrawOptions(int var1, int var2, int var3, int var4, PointSetAbstract<?> var5, int var6, int var7);

    public abstract Color getCenterColor();

    public abstract int getContentPadding();
}

