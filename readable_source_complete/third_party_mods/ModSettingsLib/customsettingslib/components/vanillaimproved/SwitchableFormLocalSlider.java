/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.Settings
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.input.InputEvent
 *  necesse.engine.input.controller.ControllerEvent
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.util.GameUtils
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.forms.components.FormSlider
 *  necesse.gfx.forms.components.localComponents.FormLocalSlider
 *  necesse.gfx.forms.controller.ControllerFocusHandler
 *  necesse.gfx.forms.events.FormEventListener
 *  necesse.gfx.forms.events.FormInputEvent
 *  necesse.gfx.gameFont.FontManager
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.gfx.gameTexture.GameSprite
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.gfx.gameTooltips.GameTooltipManager
 *  necesse.gfx.gameTooltips.GameTooltips
 *  necesse.gfx.gameTooltips.StringTooltips
 *  necesse.gfx.gameTooltips.TooltipLocation
 */
package customsettingslib.components.vanillaimproved;

import java.awt.Color;
import java.awt.Rectangle;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.localComponents.FormLocalSlider;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class SwitchableFormLocalSlider
extends FormLocalSlider {
    public boolean active = true;
    public FontOptions fontOptions;
    public int width;

    public SwitchableFormLocalSlider(String category, String key, int x, int y, int startValue, int minValue, int maxValue, int width, FontOptions fontOptions) {
        super((GameMessage)new LocalMessage(category, key), x, y, startValue, minValue, maxValue, width, fontOptions);
        this.fontOptions = fontOptions;
        this.width = width;
        this.allowScroll = false;
    }

    public SwitchableFormLocalSlider(String category, String key, int x, int y, int startValue, int minValue, int maxValue, int width) {
        this(category, key, x, y, startValue, minValue, maxValue, width, new FontOptions(16));
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        super.draw(tickManager, perspective, renderBox);
        Color color = Settings.UI.activeElementColor;
        GameTexture texture = Settings.UI.slider.active;
        if (!this.active) {
            color = Settings.UI.inactiveTextColor;
        } else if (this.isGrabbed()) {
            color = Settings.UI.highlightElementColor;
            texture = Settings.UI.slider.highlighted;
        }
        FontOptions fontOptions = this.fontOptions.defaultColor(Settings.UI.activeTextColor);
        String valueText = this.getValueText();
        int valueTextWidth = FontManager.bit.getWidthCeil(valueText, fontOptions);
        FontManager.bit.drawString((float)(this.getX() + this.width - valueTextWidth), (float)this.getY(), valueText, fontOptions);
        int maxTextWidth = this.width - valueTextWidth;
        String maxString = GameUtils.maxString((String)this.text, (FontOptions)fontOptions, (int)(maxTextWidth - 10));
        if (!maxString.equals(this.text)) {
            maxString = maxString + "...";
            if (this.isControllerFocus((ControllerFocusHandler)this)) {
                GameTooltipManager.addTooltip((GameTooltips)new StringTooltips(this.text), (TooltipLocation)TooltipLocation.FORM_FOCUS);
            }
        }
        FontManager.bit.drawString((float)this.getX(), (float)this.getY(), maxString, fontOptions);
        int textHeight = this.getTextHeight();
        SwitchableFormLocalSlider.drawWidthComponent((GameSprite)new GameSprite(texture, 0, 0, texture.getHeight()), (GameSprite)new GameSprite(texture, 1, 0, texture.getHeight()), (int)this.getX(), (int)(this.getY() + textHeight), (int)this.width);
        texture.initDraw().section(texture.getHeight() * 2, texture.getWidth(), 0, texture.getHeight()).color(color).draw(this.getX() + this.getSliderPixelProgress(texture), this.getY() + this.getTextHeight());
    }

    public boolean isGrabbed() {
        return this.active && super.isGrabbed();
    }

    public boolean isMouseOverSlider(InputEvent event) {
        return this.active && super.isMouseOverSlider(event);
    }

    public boolean isMouseOverBar(InputEvent event) {
        return this.active && super.isMouseOverBar(event);
    }

    public boolean isMouseOverText(InputEvent event) {
        return this.active && super.isMouseOverText(event);
    }

    public FormSlider onChanged(FormEventListener<FormInputEvent<FormSlider>> listener) {
        return super.onChanged(listener);
    }

    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.active) {
            super.handleInputEvent(event, tickManager, perspective);
        }
    }

    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.active) {
            super.handleControllerEvent(event, tickManager, perspective);
        }
    }
}

