/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.util.function.Consumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.gfx.shader.ColorShader;

public enum SceneColorSetting {
    Normal(new LocalMessage("settingsui", "colornormal"), shader -> {
        shader.pass1f("brightness", 1.0f);
        shader.pass1f("contrast", 1.0f);
        shader.pass1f("gamma", 1.0f);
        shader.pass1f("vibrance", 0.0f);
        shader.pass1f("vibranceRedBalance", 1.0f);
        shader.pass1f("vibranceGreenBalance", 1.0f);
        shader.pass1f("vibranceBlueBalance", 1.0f);
        shader.pass1f("red", 1.0f);
        shader.pass1f("green", 1.0f);
        shader.pass1f("blue", 1.0f);
    }),
    Vibrant(new LocalMessage("settingsui", "colorvibrant"), shader -> {
        shader.pass1f("brightness", 1.0f);
        shader.pass1f("contrast", 1.1f);
        shader.pass1f("gamma", 1.0f);
        shader.pass1f("vibrance", 0.2f);
        shader.pass1f("vibranceRedBalance", 1.0f);
        shader.pass1f("vibranceGreenBalance", 1.0f);
        shader.pass1f("vibranceBlueBalance", 1.0f);
        shader.pass1f("red", 1.0f);
        shader.pass1f("green", 1.0f);
        shader.pass1f("blue", 1.0f);
    });

    public final GameMessage displayName;
    public final Consumer<ColorShader> shaderSetup;

    private SceneColorSetting(GameMessage displayName, Consumer<ColorShader> shaderSetup) {
        this.displayName = displayName;
        this.shaderSetup = shaderSetup;
    }
}

