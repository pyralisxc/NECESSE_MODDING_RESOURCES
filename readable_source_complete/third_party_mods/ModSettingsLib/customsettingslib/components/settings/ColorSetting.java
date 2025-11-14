/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.TickManager
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 *  necesse.engine.util.GameMath
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.Renderer
 *  necesse.gfx.forms.Form
 *  necesse.gfx.forms.components.FormButton
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormSlider
 *  necesse.gfx.forms.components.localComponents.FormLocalLabel
 *  necesse.gfx.forms.components.localComponents.FormLocalTextButton
 *  necesse.gfx.forms.events.FormEventListener
 *  necesse.gfx.forms.events.FormInputEvent
 *  necesse.gfx.forms.floatMenu.ColorSelectorFloatMenu
 *  necesse.gfx.forms.floatMenu.FloatMenu
 *  necesse.gfx.gameFont.FontOptions
 *  necesse.gfx.ui.ButtonColor
 */
package customsettingslib.components.settings;

import customsettingslib.components.CustomModSetting;
import customsettingslib.components.vanillaimproved.SwitchableFormLocalSlider;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.floatMenu.ColorSelectorFloatMenu;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public class ColorSetting
extends CustomModSetting<Integer> {
    public final AtomicReference<Color> color = new AtomicReference();

    public ColorSetting(String id, Color defaultValue) {
        super(id, defaultValue.getRGB());
    }

    @Override
    public void addSaveData(SaveData saveData) {
        saveData.addInt(this.id, ((Integer)this.value).intValue());
    }

    @Override
    public void applyLoadData(LoadData loadData) {
        this.value = loadData.getInt(this.id, ((Integer)this.defaultValue).intValue());
    }

    @Override
    public void setupPacket(PacketWriter writer) {
        writer.putNextInt(((Integer)this.value).intValue());
    }

    @Override
    public Integer applyPacket(PacketReader reader) {
        return reader.getNextInt();
    }

    @Override
    public int addComponents(int y, int n) {
        this.color.set(this.getColor());
        Color firstShownColor = new Color((Integer)this.getTrueValue(), true);
        boolean isEnabled = this.isEnabled();
        int width = ColorSetting.getWidth();
        settingsForm.addComponent((FormComponent)new FormLocalLabel("settingsui", this.id, new FontOptions(12), 0, (width - 80) / 2, y + 4, width));
        final SwitchableFormLocalSlider red = (SwitchableFormLocalSlider)settingsForm.addComponent((FormComponent)new SwitchableFormLocalSlider("ui", "colorred", 4, y + 20, firstShownColor.getRed(), 0, 255, width - 80, new FontOptions(12)));
        red.onChanged((FormEventListener<FormInputEvent<FormSlider>>)((FormEventListener)e -> {
            Color tempColor = this.color.get();
            this.color.set(new Color(((FormSlider)e.from).getValue(), tempColor.getGreen(), tempColor.getBlue(), tempColor.getAlpha()));
        }));
        final SwitchableFormLocalSlider green = (SwitchableFormLocalSlider)settingsForm.addComponent((FormComponent)new SwitchableFormLocalSlider("ui", "colorgreen", 4, red.getY() + red.getTotalHeight() + 4, firstShownColor.getGreen(), 0, 255, width - 80, new FontOptions(12)));
        green.onChanged((FormEventListener<FormInputEvent<FormSlider>>)((FormEventListener)e -> {
            Color tempColor = this.color.get();
            this.color.set(new Color(tempColor.getRed(), ((FormSlider)e.from).getValue(), tempColor.getBlue(), tempColor.getAlpha()));
        }));
        final SwitchableFormLocalSlider blue = (SwitchableFormLocalSlider)settingsForm.addComponent((FormComponent)new SwitchableFormLocalSlider("ui", "colorblue", 4, green.getY() + green.getTotalHeight() + 4, firstShownColor.getBlue(), 0, 255, width - 80, new FontOptions(12)));
        blue.onChanged((FormEventListener<FormInputEvent<FormSlider>>)((FormEventListener)e -> {
            Color tempColor = this.color.get();
            this.color.set(new Color(tempColor.getRed(), tempColor.getGreen(), ((FormSlider)e.from).getValue(), tempColor.getAlpha()));
        }));
        SwitchableFormLocalSlider alpha = (SwitchableFormLocalSlider)settingsForm.addComponent((FormComponent)new SwitchableFormLocalSlider("ui", "alpha", 4, blue.getY() + blue.getTotalHeight() + 4, firstShownColor.getAlpha(), 0, 255, width - 80, new FontOptions(12)));
        alpha.onChanged((FormEventListener<FormInputEvent<FormSlider>>)((FormEventListener)e -> {
            Color tempColor = this.color.get();
            this.color.set(new Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), ((FormSlider)e.from).getValue()));
        }));
        int nextY = alpha.getY() + alpha.getTotalHeight() + 4;
        FormButton selectColor = ((FormLocalTextButton)settingsForm.addComponent((FormComponent)new FormLocalTextButton("ui", "selectcolor", 4, nextY, width, FormInputSize.SIZE_20, ButtonColor.BASE))).onClicked(e -> {
            Color startColor = this.color.get();
            ((FormButton)e.from).getManager().openFloatMenu((FloatMenu)new ColorSelectorFloatMenu(e.from, startColor){

                public void onApplied(Color newColor) {
                    if (newColor != null) {
                        newColor = new Color(GameMath.limit((int)newColor.getRed(), (int)0, (int)255), GameMath.limit((int)newColor.getGreen(), (int)0, (int)255), GameMath.limit((int)newColor.getBlue(), (int)0, (int)255));
                        red.setValue(newColor.getRed());
                        green.setValue(newColor.getGreen());
                        blue.setValue(newColor.getBlue());
                        ColorSetting.this.color.set(new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), ColorSetting.this.color.get().getAlpha()));
                    }
                }

                public void onSelected(Color newColor) {
                    ColorSetting.this.color.set(new Color(GameMath.limit((int)newColor.getRed(), (int)0, (int)255), GameMath.limit((int)newColor.getGreen(), (int)0, (int)255), GameMath.limit((int)newColor.getBlue(), (int)0, (int)255), ColorSetting.this.color.get().getAlpha()));
                }
            });
        });
        settingsForm.addComponent((FormComponent)new ColorPreview(width - 64, y + (nextY - y) / 2 - 32));
        red.setActive(isEnabled);
        green.setActive(isEnabled);
        blue.setActive(isEnabled);
        alpha.setActive(isEnabled);
        selectColor.setActive(isEnabled);
        return nextY - y + 20 + 4;
    }

    @Override
    public void onSave() {
        this.changeValue(this.color.get().getRGB());
    }

    public Color getColor() {
        return new Color((Integer)this.value, true);
    }

    public class ColorPreview
    extends Form {
        public ColorPreview(int x, int y) {
            super(64, 64);
            this.setPosition(x, y);
        }

        public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
            super.draw(tickManager, perspective, renderBox);
            Renderer.initQuadDraw((int)56, (int)56).color(ColorSetting.this.isEnabled() ? ColorSetting.this.color.get() : new Color((Integer)ColorSetting.this.getTrueValue())).draw(this.getX() + 4, this.getY() + 4);
        }
    }
}

