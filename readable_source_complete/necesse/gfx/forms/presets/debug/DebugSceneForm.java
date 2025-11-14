/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import necesse.engine.util.EventVariable;
import necesse.gfx.GameResources;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameFont.FontOptions;

public class DebugSceneForm
extends Form {
    public final DebugForm parent;
    public static EventVariable<Float> brightness = new EventVariable<Float>(Float.valueOf(1.0f));
    public static EventVariable<Float> contrast = new EventVariable<Float>(Float.valueOf(1.0f));
    public static EventVariable<Float> gamma = new EventVariable<Float>(Float.valueOf(1.0f));
    public static EventVariable<Float> vibrance = new EventVariable<Float>(Float.valueOf(0.0f));
    public static EventVariable<Float> vibranceRedBalance = new EventVariable<Float>(Float.valueOf(1.0f));
    public static EventVariable<Float> vibranceGreenBalance = new EventVariable<Float>(Float.valueOf(1.0f));
    public static EventVariable<Float> vibranceBlueBalance = new EventVariable<Float>(Float.valueOf(1.0f));
    public static EventVariable<Float> red = new EventVariable<Float>(Float.valueOf(1.0f));
    public static EventVariable<Float> green = new EventVariable<Float>(Float.valueOf(1.0f));
    public static EventVariable<Float> blue = new EventVariable<Float>(Float.valueOf(1.0f));

    public DebugSceneForm(String name, DebugForm parent) {
        super(name, 240, 400);
        this.parent = parent;
        FormFlow flow = new FormFlow(10);
        this.addComponent(new FormLabel("Scene", new FontOptions(20), 0, this.getWidth() / 2, flow.next(20)));
        final int scale = 20;
        FormSlider brightnessSlider = this.addComponent(flow.nextY(new FormSlider("Brightness", 5, 10, (int)(brightness.get().floatValue() * (float)scale), 0, 2 * scale, this.getWidth() - 10){

            @Override
            public String getValueText() {
                return (int)((float)this.getValue() / (float)scale * 100.0f) + " %";
            }
        }, 5));
        brightnessSlider.onChanged(e -> brightness.set(Float.valueOf((float)((FormSlider)e.from).getValue() / (float)scale)));
        brightness.addChangeListener(value -> brightnessSlider.setValue((int)(value.floatValue() * (float)scale)), this::isDisposed);
        FormSlider contestSlider = this.addComponent(flow.nextY(new FormSlider("Contrast", 5, 10, (int)(contrast.get().floatValue() * (float)scale), 0, 2 * scale, this.getWidth() - 10){

            @Override
            public String getValueText() {
                return (int)((float)this.getValue() / (float)scale * 100.0f) + " %";
            }
        }, 5));
        contestSlider.onChanged(e -> contrast.set(Float.valueOf((float)((FormSlider)e.from).getValue() / (float)scale)));
        contrast.addChangeListener(value -> contestSlider.setValue((int)(value.floatValue() * (float)scale)), this::isDisposed);
        FormSlider gammaSlider = this.addComponent(flow.nextY(new FormSlider("Gamma", 5, 10, (int)(gamma.get().floatValue() * (float)scale), 0, 2 * scale, this.getWidth() - 10){

            @Override
            public String getValueText() {
                return (int)((float)this.getValue() / (float)scale * 100.0f) + " %";
            }
        }, 5));
        gammaSlider.onChanged(e -> gamma.set(Float.valueOf((float)((FormSlider)e.from).getValue() / (float)scale)));
        gamma.addChangeListener(value -> gammaSlider.setValue((int)(value.floatValue() * (float)scale)), this::isDisposed);
        FormSlider vibranceSlider = this.addComponent(flow.nextY(new FormSlider("Vibrance", 5, 10, (int)(vibrance.get().floatValue() * (float)scale), -2 * scale, 2 * scale, this.getWidth() - 10){

            @Override
            public String getValueText() {
                return (int)((float)this.getValue() / (float)scale * 100.0f) + " %";
            }
        }, 5));
        vibranceSlider.onChanged(e -> vibrance.set(Float.valueOf((float)((FormSlider)e.from).getValue() / (float)scale)));
        vibrance.addChangeListener(value -> vibranceSlider.setValue((int)(value.floatValue() * (float)scale)), this::isDisposed);
        FormSlider vibranceRedSlider = this.addComponent(flow.nextY(new FormSlider("Vibrance Red", 5, 10, (int)(vibranceRedBalance.get().floatValue() * (float)scale), 0, 1 * scale, this.getWidth() - 10){

            @Override
            public String getValueText() {
                return (int)((float)this.getValue() / (float)scale * 100.0f) + " %";
            }
        }, 5));
        vibranceRedSlider.onChanged(e -> vibranceRedBalance.set(Float.valueOf((float)((FormSlider)e.from).getValue() / (float)scale)));
        vibranceRedBalance.addChangeListener(value -> vibranceRedSlider.setValue((int)(value.floatValue() * (float)scale)), this::isDisposed);
        FormSlider vibranceGreenSlider = this.addComponent(flow.nextY(new FormSlider("Vibrance Green", 5, 10, (int)(vibranceGreenBalance.get().floatValue() * (float)scale), 0, 1 * scale, this.getWidth() - 10){

            @Override
            public String getValueText() {
                return (int)((float)this.getValue() / (float)scale * 100.0f) + " %";
            }
        }, 5));
        vibranceGreenSlider.onChanged(e -> vibranceGreenBalance.set(Float.valueOf((float)((FormSlider)e.from).getValue() / (float)scale)));
        vibranceGreenBalance.addChangeListener(value -> vibranceGreenSlider.setValue((int)(value.floatValue() * (float)scale)), this::isDisposed);
        FormSlider vibranceBlueSlider = this.addComponent(flow.nextY(new FormSlider("Vibrance Blue", 5, 10, (int)(vibranceBlueBalance.get().floatValue() * (float)scale), 0, 1 * scale, this.getWidth() - 10){

            @Override
            public String getValueText() {
                return (int)((float)this.getValue() / (float)scale * 100.0f) + " %";
            }
        }, 5));
        vibranceBlueSlider.onChanged(e -> vibranceBlueBalance.set(Float.valueOf((float)((FormSlider)e.from).getValue() / (float)scale)));
        vibranceBlueBalance.addChangeListener(value -> vibranceBlueSlider.setValue((int)(value.floatValue() * (float)scale)), this::isDisposed);
        flow.next(10);
        FormSlider redSlider = this.addComponent(flow.nextY(new FormSlider("Red color", 5, 10, (int)(red.get().floatValue() * (float)scale), 0, 2 * scale, this.getWidth() - 10){

            @Override
            public String getValueText() {
                return (int)((float)this.getValue() / (float)scale * 100.0f) + " %";
            }
        }, 5));
        redSlider.onChanged(e -> red.set(Float.valueOf((float)((FormSlider)e.from).getValue() / (float)scale)));
        red.addChangeListener(value -> redSlider.setValue((int)(value.floatValue() * (float)scale)), this::isDisposed);
        FormSlider greenSlider = this.addComponent(flow.nextY(new FormSlider("Green color", 5, 10, (int)(green.get().floatValue() * (float)scale), 0, 2 * scale, this.getWidth() - 10){

            @Override
            public String getValueText() {
                return (int)((float)this.getValue() / (float)scale * 100.0f) + " %";
            }
        }, 5));
        greenSlider.onChanged(e -> green.set(Float.valueOf((float)((FormSlider)e.from).getValue() / (float)scale)));
        green.addChangeListener(value -> greenSlider.setValue((int)(value.floatValue() * (float)scale)), this::isDisposed);
        FormSlider blueSlider = this.addComponent(flow.nextY(new FormSlider("Blue color", 5, 10, (int)(blue.get().floatValue() * (float)scale), 0, 2 * scale, this.getWidth() - 10){

            @Override
            public String getValueText() {
                return (int)((float)this.getValue() / (float)scale * 100.0f) + " %";
            }
        }, 5));
        blueSlider.onChanged(e -> blue.set(Float.valueOf((float)((FormSlider)e.from).getValue() / (float)scale)));
        blue.addChangeListener(value -> blueSlider.setValue((int)(value.floatValue() * (float)scale)), this::isDisposed);
        this.addComponent(new FormTextButton("Back", 0, flow.next(40), this.getWidth())).onClicked(e -> parent.makeCurrent(parent.mainMenu));
        this.setHeight(flow.next());
    }

    static {
        brightness.addChangeListener(value -> {
            GameResources.debugColorShader.use();
            GameResources.debugColorShader.pass1f("brightness", value.floatValue());
            GameResources.debugColorShader.stop();
        }, () -> false);
        contrast.addChangeListener(value -> {
            GameResources.debugColorShader.use();
            GameResources.debugColorShader.pass1f("contrast", value.floatValue());
            GameResources.debugColorShader.stop();
        }, () -> false);
        gamma.addChangeListener(value -> {
            GameResources.debugColorShader.use();
            GameResources.debugColorShader.pass1f("gamma", value.floatValue());
            GameResources.debugColorShader.stop();
        }, () -> false);
        vibrance.addChangeListener(value -> {
            GameResources.debugColorShader.use();
            GameResources.debugColorShader.pass1f("vibrance", value.floatValue());
            GameResources.debugColorShader.stop();
        }, () -> false);
        vibranceRedBalance.addChangeListener(value -> {
            GameResources.debugColorShader.use();
            GameResources.debugColorShader.pass1f("vibranceRedBalance", value.floatValue());
            GameResources.debugColorShader.stop();
        }, () -> false);
        vibranceGreenBalance.addChangeListener(value -> {
            GameResources.debugColorShader.use();
            GameResources.debugColorShader.pass1f("vibranceGreenBalance", value.floatValue());
            GameResources.debugColorShader.stop();
        }, () -> false);
        vibranceBlueBalance.addChangeListener(value -> {
            GameResources.debugColorShader.use();
            GameResources.debugColorShader.pass1f("vibranceBlueBalance", value.floatValue());
            GameResources.debugColorShader.stop();
        }, () -> false);
        red.addChangeListener(value -> {
            GameResources.debugColorShader.use();
            GameResources.debugColorShader.pass1f("red", value.floatValue());
            GameResources.debugColorShader.stop();
        }, () -> false);
        green.addChangeListener(value -> {
            GameResources.debugColorShader.use();
            GameResources.debugColorShader.pass1f("green", value.floatValue());
            GameResources.debugColorShader.stop();
        }, () -> false);
        blue.addChangeListener(value -> {
            GameResources.debugColorShader.use();
            GameResources.debugColorShader.pass1f("blue", value.floatValue());
            GameResources.debugColorShader.stop();
        }, () -> false);
    }
}

