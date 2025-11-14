/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.forms.components.FormLabel
 *  necesse.gfx.forms.components.FormSlider
 *  necesse.gfx.forms.components.FormTextInput
 *  necesse.gfx.forms.components.localComponents.FormLocalLabel
 *  necesse.gfx.forms.events.FormEventListener
 *  necesse.gfx.forms.events.FormInputEvent
 *  necesse.gfx.gameFont.FontOptions
 */
package customsettingslib.components.settings;

import customsettingslib.components.CustomModSetting;
import customsettingslib.components.vanillaimproved.SwitchableFormLocalSlider;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.gameFont.FontOptions;

public class IntSetting
extends CustomModSetting<Integer> {
    public int min;
    public int max;
    public DisplayMode displayMode;
    public int decimals;
    public final AtomicReference<Integer> newValue = new AtomicReference();

    public IntSetting(String id, int defaultValue, int min, int max, DisplayMode displayMode, int decimals) {
        super(id, defaultValue);
        this.min = min;
        this.max = max;
        this.displayMode = displayMode;
        this.decimals = decimals;
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
    public boolean isValidValue(Object value) {
        return super.isValidValue(value) && this.inBounds((Integer)value);
    }

    public boolean inBounds(int value) {
        return this.min <= value && value <= this.max;
    }

    @Override
    public int addComponents(int y, int n) {
        this.newValue.set((Integer)this.value);
        int width = IntSetting.getWidth();
        boolean isEnabled = this.isEnabled();
        if (this.displayMode == DisplayMode.BAR) {
            boolean onlyBar = this.min == 0 && this.max == 100 && this.decimals == 2;
            AtomicReference<FormLabel> preview = new AtomicReference<FormLabel>();
            SwitchableFormLocalSlider slider = (SwitchableFormLocalSlider)((SwitchableFormLocalSlider)settingsForm.addComponent((FormComponent)new SwitchableFormLocalSlider("settingsui", this.id, 4, y, (Integer)this.getTrueValue(), this.min, this.max, width - (onlyBar ? 0 : 80)), 15)).onChanged((FormEventListener<FormInputEvent<FormSlider>>)((FormEventListener)e -> {
                this.newValue.set(((FormSlider)e.from).getValue());
                if (!onlyBar && preview.get() != null) {
                    ((FormLabel)preview.get()).setText(this.decimals == 0 ? this.newValue.get().toString() : String.valueOf((float)this.newValue.get().intValue() / (float)Math.pow(10.0, this.decimals)));
                }
            }));
            if (!onlyBar) {
                preview.set((FormLabel)settingsForm.addComponent((FormComponent)new FormLabel(((Integer)this.getTrueValue()).toString(), new FontOptions(16), 0, width - 32, y + (slider.getTotalHeight() - 16) / 2, 64)));
            }
            slider.setActive(isEnabled);
            return slider.getTotalHeight();
        }
        if (this.displayMode == DisplayMode.INPUT) {
            settingsForm.addComponent((FormComponent)new FormLocalLabel("settingsui", this.id, new FontOptions(16), -1, 148, y + 2));
            FormTextInput input = (FormTextInput)settingsForm.addComponent((FormComponent)new FormTextInput(4, y, FormInputSize.SIZE_20, 128, Math.max(String.valueOf(this.max).length(), String.valueOf(this.min).length())));
            AtomicBoolean ensure = new AtomicBoolean(true);
            input.onChange(e -> {
                FormTextInput formTextInput = (FormTextInput)e.from;
                String text = formTextInput.getText();
                try {
                    if (this.decimals == 0) {
                        int number = Integer.parseInt(text);
                        if (number < this.min) {
                            number = this.min;
                        }
                        if (number > this.max) {
                            number = this.max;
                        }
                        if (ensure.get()) {
                            ensure.set(false);
                            formTextInput.setText(String.valueOf(number));
                        } else {
                            ensure.set(true);
                        }
                        this.newValue.set(number);
                    } else {
                        float numberF = Float.parseFloat(text);
                        int number = Math.round(numberF * (float)Math.pow(10.0, this.decimals));
                        if (number < this.min) {
                            number = this.min;
                        }
                        if (number > this.max) {
                            number = this.max;
                        }
                        if (ensure.get()) {
                            ensure.set(false);
                            formTextInput.setText(String.valueOf((float)number / (float)Math.pow(10.0, this.decimals)));
                        } else {
                            ensure.set(true);
                        }
                        this.newValue.set(number);
                    }
                }
                catch (RuntimeException ignored) {
                    formTextInput.setText(this.newValue.get().toString());
                    formTextInput.setCaretEnd();
                }
            });
            input.setRegexMatchFull("-?[0-9]+(\\.[0-9]+)?");
            input.setText(((Integer)this.getTrueValue()).toString());
            input.setActive(isEnabled);
            return 20;
        }
        if (this.displayMode == DisplayMode.INPUT_BAR) {
            AtomicReference<FormTextInput> input = new AtomicReference<FormTextInput>();
            SwitchableFormLocalSlider slider = (SwitchableFormLocalSlider)((SwitchableFormLocalSlider)settingsForm.addComponent((FormComponent)new SwitchableFormLocalSlider("settingsui", this.id, 4, y, this.newValue.get(), this.min, this.max, width - 80), 15)).onChanged((FormEventListener<FormInputEvent<FormSlider>>)((FormEventListener)e -> {
                this.newValue.set(((FormSlider)e.from).getValue());
                if (input.get() != null) {
                    ((FormTextInput)input.get()).setText(this.decimals == 0 ? this.newValue.get().toString() : String.valueOf((float)this.newValue.get().intValue() / (float)Math.pow(10.0, this.decimals)));
                }
            }));
            input.set((FormTextInput)settingsForm.addComponent((FormComponent)new FormTextInput(width - 64, y + (slider.getTotalHeight() - 20) / 2, FormInputSize.SIZE_20, 64, Math.max(String.valueOf(this.max).length(), String.valueOf(this.min).length()))));
            ((FormTextInput)input.get()).setRegexMatchFull("-?[0-9]+(\\.[0-9]+)?");
            ((FormTextInput)input.get()).setText(((Integer)this.getTrueValue()).toString());
            AtomicBoolean ensure = new AtomicBoolean(true);
            ((FormTextInput)input.get()).onChange(e -> {
                FormTextInput formTextInput = (FormTextInput)e.from;
                String text = formTextInput.getText();
                try {
                    if (this.decimals == 0) {
                        int number = Integer.parseInt(text);
                        if (number < this.min) {
                            number = this.min;
                        }
                        if (number > this.max) {
                            number = this.max;
                        }
                        if (ensure.get()) {
                            ensure.set(false);
                            formTextInput.setText(String.valueOf(number));
                        } else {
                            ensure.set(true);
                        }
                        this.newValue.set(number);
                        slider.setValue(number);
                    } else {
                        float numberF = Float.parseFloat(text);
                        int number = Math.round(numberF * (float)Math.pow(10.0, this.decimals));
                        if (number < this.min) {
                            number = this.min;
                        }
                        if (number > this.max) {
                            number = this.max;
                        }
                        if (ensure.get()) {
                            ensure.set(false);
                            formTextInput.setText(String.valueOf((float)number / (float)Math.pow(10.0, this.decimals)));
                        } else {
                            ensure.set(true);
                        }
                        this.newValue.set(number);
                        slider.setValue(number);
                    }
                }
                catch (RuntimeException ignored) {
                    formTextInput.setText(this.newValue.get().toString());
                    formTextInput.setCaretEnd();
                }
            });
            slider.setActive(isEnabled);
            ((FormTextInput)input.get()).setActive(isEnabled);
            return slider.getTotalHeight();
        }
        return 0;
    }

    @Override
    public void onSave() {
        this.changeValue(this.newValue.get());
    }

    public static enum DisplayMode {
        INPUT,
        BAR,
        INPUT_BAR;

    }
}

