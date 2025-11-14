/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.input.controller;

import necesse.engine.input.InputSource;

public abstract class ControllerHandle {
    public final InputSource inputSource;
    public final ControllerType type;

    public ControllerHandle(InputSource inputSource, ControllerType type) {
        this.inputSource = inputSource;
        this.type = type;
    }

    public static enum ControllerType {
        Unknown,
        Xbox,
        PlayStation4,
        PlayStation5,
        XboxGeneric,
        XboxLinuxWireless,
        XboxLinuxWired;

    }
}

