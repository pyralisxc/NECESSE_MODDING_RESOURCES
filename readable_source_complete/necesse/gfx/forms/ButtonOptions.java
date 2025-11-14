/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public class ButtonOptions {
    public GameMessage text;
    public Runnable pressed;

    public ButtonOptions(GameMessage text, Runnable pressed) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(pressed);
        this.text = text;
        this.pressed = pressed;
    }

    public ButtonOptions(String category, String key, Runnable pressed) {
        this(new LocalMessage(category, key), pressed);
    }

    public static ButtonOptions continueButton(Runnable pressed) {
        return new ButtonOptions("ui", "continuebutton", pressed);
    }

    public static ButtonOptions confirmButton(Runnable pressed) {
        return new ButtonOptions("ui", "confirmbutton", pressed);
    }

    public static ButtonOptions backButton(Runnable pressed) {
        return new ButtonOptions("ui", "backbutton", pressed);
    }

    public static ButtonOptions cancelButton(Runnable pressed) {
        return new ButtonOptions("ui", "cancelbutton", pressed);
    }

    public static ButtonOptions closeButton(Runnable pressed) {
        return new ButtonOptions("ui", "closebutton", pressed);
    }
}

