/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.input.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.FloatDimension;
import necesse.gfx.GameBackground;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FairMobGlyph
implements FairGlyph {
    public final int size;
    public final List<Integer> mobIDs;
    protected int yOffset;
    private boolean isHovering;

    public FairMobGlyph(int size, List<Integer> mobIDs) {
        this.size = size;
        this.mobIDs = mobIDs;
    }

    public FairMobGlyph(int size, int mobID) {
        this(size, Collections.singletonList(mobID));
    }

    public FairMobGlyph offsetY(int offset) {
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
        int mobID = this.getCurrentMobID();
        this.drawIcon(mobID, x + 1.0f, y - (float)this.size - 6.0f + (float)this.yOffset, this.size + 8, (float)defaultColor.getAlpha() / 255.0f);
        if (this.isHovering && (tooltip = this.getTooltip(mobID)) != null) {
            GameTooltipManager.addTooltip(tooltip, GameBackground.getItemTooltipBackground(), TooltipLocation.FORM_FOCUS);
        }
    }

    public void drawIcon(int mobID, float x, float y, int size, float alpha) {
        GameTexture mobIcon = MobRegistry.getMobIcon(mobID);
        Color color = MobRegistry.textColoredIcons.contains(mobID) ? Settings.UI.activeTextColor : new Color(255, 255, 255);
        mobIcon.initDraw().size(size).color(color).alpha(alpha).draw((int)x, (int)y);
    }

    public GameTooltips getTooltip(int mobID) {
        GameMessage localization = MobRegistry.getLocalization(mobID);
        if (localization != null) {
            return new StringTooltips(localization.translate());
        }
        return null;
    }

    public long getTime() {
        return System.currentTimeMillis();
    }

    public int getCycleTime() {
        return 1000;
    }

    public int getCurrentMobID() {
        long time = this.getTime();
        int cycleTime = this.getCycleTime();
        int currentCycle = (int)(time / (long)cycleTime);
        int currentIndex = Math.floorMod(currentCycle, this.mobIDs.size());
        return this.mobIDs.get(currentIndex);
    }

    @Override
    public FairGlyph getTextBoxCharacter() {
        return this;
    }

    @Override
    public String getParseString() {
        return TypeParsers.getMobParseString(this.getCurrentMobID());
    }
}

