/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import java.util.HashMap;
import necesse.engine.modLoader.LoadedMod;

public abstract class ControllerState {
    private int id = -1;
    public final LoadedMod mod = LoadedMod.getRunningMod();
    private static int nextId = 0;
    private static final HashMap<Integer, ControllerState> IDControllerState = new HashMap();

    public int getID() {
        return this.id;
    }

    public void init() {
        if (this.id != -1) {
            throw new IllegalStateException("Controller state already initialized");
        }
        this.id = nextId++;
        IDControllerState.put(this.id, this);
    }

    public static ControllerState getStateFromId(int ID) {
        return IDControllerState.get(ID);
    }
}

