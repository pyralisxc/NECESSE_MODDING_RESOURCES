/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 *  necesse.gfx.forms.components.FormCheckBox
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.localComponents.FormLocalCheckBox
 */
package customsettingslib.components.settings;

import customsettingslib.components.CustomModSetting;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;

public class BooleanSetting
extends CustomModSetting<Boolean> {
    public final AtomicReference<Boolean> newValue = new AtomicReference();

    public BooleanSetting(String id, Boolean defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public void addSaveData(SaveData saveData) {
        saveData.addBoolean(this.id, ((Boolean)this.value).booleanValue());
    }

    @Override
    public void applyLoadData(LoadData loadData) {
        this.value = loadData.getBoolean(this.id, ((Boolean)this.defaultValue).booleanValue());
    }

    @Override
    public void setupPacket(PacketWriter writer) {
        writer.putNextBoolean(((Boolean)this.value).booleanValue());
    }

    @Override
    public Boolean applyPacket(PacketReader reader) {
        return reader.getNextBoolean();
    }

    @Override
    public int addComponents(int y, int n) {
        this.newValue.set((Boolean)this.value);
        int width = BooleanSetting.getWidth();
        FormCheckBox component = ((FormLocalCheckBox)settingsForm.addComponent((FormComponent)new FormLocalCheckBox("settingsui", this.id, 4, y, width), 8)).onClicked(e -> this.newValue.set(this.newValue.get() == false));
        component.checked = (Boolean)this.getTrueValue();
        component.setActive(this.isEnabled());
        return ((Rectangle)component.getHitboxes().get((int)0)).height;
    }

    @Override
    public void onSave() {
        this.changeValue(this.newValue.get());
    }
}

