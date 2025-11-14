/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.util.function.Supplier;
import necesse.gfx.gameTooltips.GameTooltips;

public class CompareGameTooltips
implements GameTooltips {
    private GameTooltips left;
    private GameTooltips right;
    private int margin;
    private boolean snapToBottom;

    public CompareGameTooltips(GameTooltips left, GameTooltips right, int margin, boolean snapToBottom) {
        this.left = left;
        this.right = right;
        this.margin = margin;
        this.snapToBottom = snapToBottom;
    }

    @Override
    public int getHeight() {
        return Math.max(this.left.getHeight(), this.right.getHeight());
    }

    @Override
    public int getWidth() {
        return this.left.getWidth() + this.right.getWidth() + this.margin;
    }

    @Override
    public int getDrawXOffset() {
        return -this.left.getWidth() - this.margin;
    }

    @Override
    public void draw(int x, int y, Supplier<Color> defaultColor) {
        if (this.snapToBottom) {
            int leftHeight = this.left.getHeight();
            int rightHeight = this.right.getHeight();
            int maxHeight = Math.max(leftHeight, rightHeight);
            this.left.draw(x, y + maxHeight - leftHeight, defaultColor);
            this.right.draw(x + this.left.getWidth() + this.margin, y + maxHeight - rightHeight, defaultColor);
        } else {
            this.left.draw(x, y, defaultColor);
            this.right.draw(x + this.left.getWidth() + this.margin, y, defaultColor);
        }
    }

    @Override
    public int getDrawOrder() {
        return 0;
    }

    @Override
    public boolean matchesSearch(String search) {
        return this.left.matchesSearch(search) || this.right.matchesSearch(search);
    }
}

