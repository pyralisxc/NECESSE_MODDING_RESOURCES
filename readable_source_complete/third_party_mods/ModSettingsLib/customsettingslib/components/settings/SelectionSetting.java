/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.localization.message.StaticMessage
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 *  necesse.gfx.forms.components.FormComponent
 *  necesse.gfx.forms.components.FormDropdownSelectionButton
 *  necesse.gfx.forms.components.FormInputSize
 *  necesse.gfx.ui.ButtonColor
 */
package customsettingslib.components.settings;

import customsettingslib.components.CustomModSetting;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.ui.ButtonColor;

public class SelectionSetting
extends CustomModSetting<Integer> {
    public Option[] options;
    public final AtomicReference<Integer> newValue = new AtomicReference();

    public SelectionSetting(String id, int defaultValue, Option ... options) {
        super(id, defaultValue);
        this.options = options;
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
        return 0 <= value && value <= this.options.length;
    }

    @Override
    public int addComponents(int y, int n) {
        this.newValue.set((Integer)this.value);
        int width = SelectionSetting.getWidth();
        FormDropdownSelectionButton selectionForm = (FormDropdownSelectionButton)settingsForm.addComponent((FormComponent)new FormDropdownSelectionButton(4, y, FormInputSize.SIZE_20, ButtonColor.BASE, width));
        for (int i = 0; i < this.options.length; ++i) {
            selectionForm.options.add((Object)i, this.options[i].getDisplayName());
        }
        selectionForm.setSelected((Object)((Integer)this.getTrueValue()), this.options[(Integer)this.getTrueValue()].getDisplayName());
        selectionForm.onSelected(e -> this.newValue.set((Integer)e.value));
        selectionForm.setActive(this.isEnabled());
        return 20;
    }

    @Override
    public void onSave() {
        this.changeValue(this.newValue.get());
    }

    public static class Option {
        public String name;
        public Object value;
        public boolean staticMessage;

        public Option(String name, Object value, boolean staticMessage) {
            this.name = name;
            this.value = value;
            this.staticMessage = staticMessage;
        }

        public Option(String name, Object value) {
            this(name, value, false);
        }

        public Option(String name) {
            this(name, name, false);
        }

        public GameMessage getDisplayName() {
            return this.staticMessage ? new StaticMessage(this.name) : new LocalMessage("settingsui", this.name);
        }
    }
}

