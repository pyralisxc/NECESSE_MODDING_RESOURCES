/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.GlobalData
 *  necesse.engine.modLoader.LoadedMod
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.PacketWriter
 *  necesse.engine.save.LoadData
 *  necesse.engine.save.SaveData
 *  necesse.engine.state.MainMenu
 */
package customsettingslib.components;

import customsettingslib.components.SettingsComponents;
import necesse.engine.GlobalData;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.state.MainMenu;

public abstract class CustomModSetting<T>
extends SettingsComponents {
    public final String id;
    public final T defaultValue;
    public final LoadedMod mod;
    public T value;

    public CustomModSetting(String id, T defaultValue, LoadedMod mod) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.mod = mod;
        this.value = defaultValue;
    }

    public CustomModSetting(String id, T defaultValue) {
        this(id, defaultValue, LoadedMod.getRunningMod());
    }

    public void restoreToDefault() {
        this.value = this.defaultValue;
    }

    public Class<?> getValueClass() {
        return this.defaultValue.getClass();
    }

    public abstract void addSaveData(SaveData var1);

    public abstract void applyLoadData(LoadData var1);

    public abstract void setupPacket(PacketWriter var1);

    public abstract T applyPacket(PacketReader var1);

    public T getValue() {
        return this.value;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public T getTrueValue() {
        return (T)customModSettings.getSetting(this.id);
    }

    public boolean isValidValue(Object value) {
        return value.getClass() == this.getValueClass();
    }

    public abstract void onSave();

    public void changeValue(T value) {
        if (this.isValidValue(value)) {
            this.value = value;
        }
    }

    public boolean isEnabled() {
        return !CustomModSetting.customModSettings.serverSettings.contains(this.id) || GlobalData.getCurrentState() instanceof MainMenu;
    }
}

