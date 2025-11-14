/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import necesse.engine.input.InputEvent;
import necesse.engine.util.FloatDimension;
import necesse.engine.util.GameBlackboard;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.inventory.InventoryItem;

public class FairItemGlyph
implements FairGlyph {
    public final int size;
    public final List<InventoryItem> items;
    protected boolean onlyShowNameTooltip;
    protected boolean dontShowTooltip;
    protected GameBlackboard tooltipBlackboard;
    protected int yOffset;
    private boolean isHovering;

    public FairItemGlyph(int size, List<InventoryItem> item) {
        this.size = size;
        this.items = item;
    }

    public FairItemGlyph(int size, InventoryItem item) {
        this(size, Collections.singletonList(item));
    }

    public FairItemGlyph dontShowTooltip() {
        this.dontShowTooltip = true;
        return this;
    }

    public FairItemGlyph onlyShowNameTooltip() {
        this.onlyShowNameTooltip = true;
        this.dontShowTooltip = false;
        return this;
    }

    public FairItemGlyph setTooltipBlackboard(GameBlackboard blackboard) {
        this.tooltipBlackboard = blackboard;
        return this;
    }

    public FairItemGlyph offsetY(int offset) {
        this.yOffset = offset;
        return this;
    }

    @Override
    public FloatDimension getDimensions() {
        return new FloatDimension(this.size + 8, this.size + 4);
    }

    @Override
    public void updateDimensions() {
    }

    @Override
    public void handleInputEvent(float drawX, float drawY, InputEvent event) {
        if (event.isMouseMoveEvent()) {
            Dimension dim = this.getDimensions().toInt();
            this.isHovering = new Rectangle((int)drawX + 1, (int)drawY - dim.height + this.yOffset, dim.width, dim.height).contains(event.pos.hudX, event.pos.hudY);
        }
    }

    @Override
    public void draw(float x, float y, Color defaultColor) {
        GameTooltips tooltip;
        InventoryItem currentDrawnItem = this.getCurrentDrawnItem();
        this.drawIcon(currentDrawnItem, x + 1.0f, y - (float)this.size - 6.0f + (float)this.yOffset, this.size + 8, (float)defaultColor.getAlpha() / 255.0f);
        if (this.isHovering && (tooltip = this.getTooltip(currentDrawnItem)) != null) {
            GameTooltipManager.addTooltip(tooltip, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
        }
    }

    public void drawIcon(InventoryItem currentDrawnItem, float x, float y, int size, float alpha) {
        currentDrawnItem.drawIcon(null, (int)x, (int)y, size, new Color(1.0f, 1.0f, 1.0f, alpha));
    }

    public GameTooltips getTooltip(InventoryItem currentDrawnItem) {
        if (this.dontShowTooltip) {
            return null;
        }
        if (this.onlyShowNameTooltip) {
            return new StringTooltips(currentDrawnItem.getItemDisplayName(), currentDrawnItem.item.getRarityColor(currentDrawnItem));
        }
        return currentDrawnItem.getTooltip(null, this.tooltipBlackboard == null ? new GameBlackboard() : this.tooltipBlackboard);
    }

    public long getTime() {
        return System.currentTimeMillis();
    }

    public int getCycleTime() {
        return 1000;
    }

    public InventoryItem getCurrentDrawnItem() {
        long time = this.getTime();
        int cycleTime = this.getCycleTime();
        int currentCycle = (int)(time / (long)cycleTime);
        int currentIndex = Math.floorMod(currentCycle, this.items.size());
        return this.items.get(currentIndex);
    }

    @Override
    public FairGlyph getTextBoxCharacter() {
        return this;
    }

    @Override
    public String getParseString() {
        return TypeParsers.getItemParseString(this.getCurrentDrawnItem());
    }
}

