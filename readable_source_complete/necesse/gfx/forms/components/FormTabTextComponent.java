/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import necesse.engine.Settings;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTabContentComponent;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormTabTextComponent
extends FormTabContentComponent {
    protected static final FontOptions fontOptions = new FontOptions(16);
    private String text;
    private String drawText;
    private String tooltip;
    public int tooltipMaxWidth = 400;

    public FormTabTextComponent(String text, String tooltip, Form form, int x, FormInputSize size) {
        super(form, x, size, FormTabTextComponent.getTextFontWidth(text));
        this.setText(text);
        this.setTooltip(tooltip);
    }

    public FormTabTextComponent(String text, String tooltip, Form form, int x, FormInputSize size, int width) {
        super(form, x, size, width, width);
        this.setText(text);
        this.setTooltip(tooltip);
    }

    @Override
    protected void drawContent(int x, int y, int width, int height, PlayerMob perspective) {
        String drawText = this.getDrawText();
        int drawX = x + width / 2 - FontManager.bit.getWidthCeil(drawText, fontOptions) / 2;
        int drawY = y + height / 2 - fontOptions.getSize() / 2;
        FontManager.bit.drawString(drawX, drawY, drawText, fontOptions);
    }

    @Override
    protected void addTooltips(PlayerMob perspective) {
        super.addTooltips(perspective);
        StringTooltips tooltips = new StringTooltips();
        if (!this.getDrawText().equals(this.getText())) {
            tooltips.add(this.getText());
        }
        if (this.tooltip != null && !this.tooltip.isEmpty()) {
            tooltips.add(this.tooltip, this.tooltipMaxWidth);
        }
        if (tooltips.getSize() != 0) {
            GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
        }
    }

    private static int getTextFontWidth(String text) {
        return FontManager.bit.getWidthCeil(text, fontOptions.color(Settings.UI.activeTextColor));
    }

    public String getText() {
        return this.text;
    }

    public String getDrawText() {
        return this.drawText;
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        this.updateDrawText();
    }

    public void setText(String text) {
        this.text = text;
        this.setWantedWidth(FormTabTextComponent.getTextFontWidth(text) + 10);
        this.updateDrawText();
    }

    private void updateDrawText() {
        this.drawText = this.text;
        while (!this.drawText.isEmpty() && FormTabTextComponent.getTextFontWidth(this.drawText) > this.currentWidth) {
            this.drawText = this.drawText.substring(0, this.drawText.length() - 1);
        }
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
}

