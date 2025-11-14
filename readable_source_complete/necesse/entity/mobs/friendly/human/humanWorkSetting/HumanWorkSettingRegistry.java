/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.human.humanWorkSetting;

import java.util.ArrayList;
import necesse.entity.mobs.friendly.human.humanWorkSetting.HumanWorkSetting;

public class HumanWorkSettingRegistry {
    private boolean registryOpen = true;
    private ArrayList<HumanWorkSetting<?>> actions = new ArrayList();

    public void closeRegistry() {
        this.registryOpen = false;
    }

    public final HumanWorkSetting<?> getSetting(int id) {
        if (id < 0 || id >= this.actions.size()) {
            System.err.println("Could not find HumanWorkSetting with id " + id);
            return null;
        }
        return this.actions.get(id);
    }

    public final <C extends HumanWorkSetting<?>> C registerSetting(C action) {
        if (!this.registryOpen) {
            throw new IllegalStateException("Cannot register HumanWorkSetting after initialization, must be done in constructor.");
        }
        if (this.actions.size() >= Short.MAX_VALUE) {
            throw new IllegalStateException("Cannot register any more HumanWorkSetting");
        }
        this.actions.add(action);
        action.onRegister(this, this.actions.size() - 1);
        return action;
    }

    public boolean isEmpty() {
        return this.actions.isEmpty();
    }

    public Iterable<HumanWorkSetting<?>> getSettings() {
        return this.actions;
    }
}

