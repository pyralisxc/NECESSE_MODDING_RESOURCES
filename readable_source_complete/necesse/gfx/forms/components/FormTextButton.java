/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;

public class FormTextButton
extends FormButton
implements FormPositionContainer {
    private FormPosition position;
    private int width;
    private String text;
    private String drawText;
    private String tooltip;
    public int tooltipMaxWidth = 400;
    public FormInputSize size;
    public ButtonColor color;
    private int fullTextWidth;

    public FormTextButton(String text, String tooltip, int x, int y, int width, FormInputSize size, ButtonColor color) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.size = size;
        this.color = color;
        this.setText(text);
        this.setTooltip(tooltip);
    }

    public FormTextButton(String text, String tooltip, int x, int y, int width) {
        this(text, tooltip, x, y, width, FormInputSize.SIZE_32_TO_40, ButtonColor.BASE);
    }

    public FormTextButton(String text, int x, int y, int width, FormInputSize size, ButtonColor color) {
        this(text, null, x, y, width, size, color);
    }

    public FormTextButton(String text, int x, int y, int width) {
        this(text, null, x, y, width);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        boolean useDownTexture;
        Color drawCol = this.getDrawColor();
        ButtonState state = this.getButtonState();
        int textOffset = 0;
        boolean bl = useDownTexture = this.isDown() && this.isHovering();
        if (useDownTexture) {
            this.size.getButtonDownDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
            textOffset = this.size.buttonDownContentDrawOffset;
        } else {
            this.size.getButtonDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
        }
        Rectangle contentRect = this.size.getContentRectangle(this.width);
        FormShader.FormShaderState textState = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(contentRect.x, contentRect.y, contentRect.width, contentRect.height));
        try {
            FontOptions fontOptions = this.size.getFontOptions().color(this.getTextColor());
            String drawText = this.getDrawText();
            int drawX = this.width / 2 - FontManager.bit.getWidthCeil(drawText, fontOptions) / 2;
            FontManager.bit.drawString(drawX, textOffset + this.size.fontDrawOffset, drawText, fontOptions);
        }
        finally {
            textState.end();
        }
        if (useDownTexture) {
            this.size.getButtonDownEdgeDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
        } else {
            this.size.getButtonEdgeDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
        }
        if (this.isHovering()) {
            this.addTooltips(perspective);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormTextButton.singleBox(new Rectangle(this.getX(), this.getY() + this.size.textureDrawOffset, this.width, this.size.height));
    }

    public String getText() {
        return this.text;
    }

    public String getDrawText() {
        return this.drawText;
    }

    protected void addTooltips(PlayerMob perspective) {
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

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
        this.updateDrawText();
    }

    public void setText(String text) {
        this.text = text;
        this.updateDrawText();
    }

    private void updateDrawText() {
        this.drawText = this.text;
        int maxWidth = this.getMaxTextWidth();
        int textWidth = this.fullTextWidth = this.text.isEmpty() ? 0 : FontManager.bit.getWidthCeil(this.text, this.size.getFontOptions().color(this.getInterfaceStyle().activeTextColor));
        while (!this.drawText.isEmpty() && textWidth > maxWidth) {
            this.drawText = this.drawText.substring(0, this.drawText.length() - 1);
            textWidth = this.drawText.isEmpty() ? 0 : FontManager.bit.getWidthCeil(this.drawText, this.size.getFontOptions().color(this.getInterfaceStyle().activeTextColor));
        }
    }

    protected int getMaxTextWidth() {
        return this.width;
    }

    public int getWantedWidth() {
        return this.fullTextWidth + this.width - this.size.getContentRectangle((int)this.width).width + 5;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }
}

