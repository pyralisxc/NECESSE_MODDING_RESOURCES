/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Objects;
import java.util.function.Supplier;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.gameTooltips.GameTooltips;

public class FairTypeTooltip
implements GameTooltips {
    public FairTypeDrawOptions drawOptions;
    public int xOffset;

    public FairTypeTooltip(FairTypeDrawOptions drawOptions, int xOffset) {
        Objects.requireNonNull(drawOptions);
        this.drawOptions = drawOptions;
        this.xOffset = xOffset;
    }

    public FairTypeTooltip(FairTypeDrawOptions drawOptions) {
        this(drawOptions, 0);
    }

    public FairTypeTooltip(FairType type, int xOffset) {
        this(type.getDrawOptions(FairType.TextAlign.LEFT, 400, false, true), xOffset);
    }

    public FairTypeTooltip(FairType type) {
        this(type, 0);
    }

    @Override
    public int getHeight() {
        return this.drawOptions.getBoundingBox().height;
    }

    @Override
    public int getWidth() {
        return this.drawOptions.getBoundingBox().width + this.xOffset;
    }

    @Override
    public void draw(int x, int y, Supplier<Color> defaultColor) {
        Rectangle boundingBox = this.drawOptions.getBoundingBox();
        this.drawOptions.draw(x - boundingBox.x + this.xOffset, y - boundingBox.y, defaultColor.get());
    }

    @Override
    public int getDrawOrder() {
        return 0;
    }

    @Override
    public boolean matchesSearch(String search) {
        return this.drawOptions.getType().matchesSearch(search);
    }
}

