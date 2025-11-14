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
 *  necesse.gfx.forms.components.FormTextInput
 *  necesse.gfx.forms.components.localComponents.FormLocalLabel
 *  necesse.gfx.gameFont.FontOptions
 */
package customsettingslib.components.settings;

import customsettingslib.components.CustomModSetting;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;

public class StringSetting
extends CustomModSetting<String> {
    public int maxLength;
    public boolean large;
    public final AtomicReference<String> newValue = new AtomicReference();

    public StringSetting(String id, String defaultValue, int maxLength, boolean large) {
        super(id, defaultValue);
        this.maxLength = maxLength;
        this.large = large;
    }

    @Override
    public boolean isValidValue(Object value) {
        return super.isValidValue(value) && (this.maxLength == 0 || ((String)value).length() <= this.maxLength);
    }

    @Override
    public void addSaveData(SaveData saveData) {
        saveData.addSafeString(this.id, (String)this.value);
    }

    @Override
    public void applyLoadData(LoadData loadData) {
        this.value = loadData.getSafeString(this.id, (String)this.defaultValue);
    }

    @Override
    public void setupPacket(PacketWriter writer) {
        writer.putNextString((String)this.value);
    }

    @Override
    public String applyPacket(PacketReader reader) {
        return reader.getNextString();
    }

    @Override
    public int addComponents(int y, int n) {
        this.newValue.set((String)this.value);
        int inputWidth = this.large ? 192 : 128;
        settingsForm.addComponent((FormComponent)new FormLocalLabel("settingsui", this.id, new FontOptions(16), -1, 4 + inputWidth + 16, y + 2));
        FormTextInput input = (FormTextInput)settingsForm.addComponent((FormComponent)new FormTextInput(4, y, FormInputSize.SIZE_20, inputWidth, this.maxLength));
        input.onChange(e -> {
            FormTextInput formTextInput = (FormTextInput)e.from;
            String text = formTextInput.getText();
            this.newValue.set(text);
        });
        input.setText((String)this.getTrueValue());
        input.setActive(this.isEnabled());
        return 20;
    }

    @Override
    public void onSave() {
        this.changeValue(this.newValue.get());
    }
}

