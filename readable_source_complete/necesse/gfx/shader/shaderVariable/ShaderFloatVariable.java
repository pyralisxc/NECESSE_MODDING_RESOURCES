/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader.shaderVariable;

import necesse.engine.util.EventVariable;
import necesse.engine.util.GameMath;
import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.shaderVariable.ShaderVariable;

public class ShaderFloatVariable
extends ShaderVariable<Float> {
    protected boolean hasSlider;
    protected float min;
    protected float max;
    protected int scale;

    public ShaderFloatVariable(String varName) {
        super(varName);
    }

    public ShaderFloatVariable(String varName, float min, float max, int scale) {
        this(varName);
        this.hasSlider = true;
        this.min = min;
        this.max = max;
        this.scale = scale;
    }

    public ShaderFloatVariable(String varName, float min, float max) {
        this(varName, min, max, 100);
    }

    @Override
    public void addInputs(GameShader shader, ComponentListContainer<? super FormComponent> form, int x, FormFlow yFlow, int width) {
        EventVariable<Float> value = new EventVariable<Float>(Float.valueOf(shader.get1f(this.varName)));
        form.addComponent(new FormLabel(this.varName, new FontOptions(16), -1, x, yFlow.next(18), width));
        FormTextInput input = form.addComponent(new FormTextInput(x, yFlow.next(22), FormInputSize.SIZE_20, width, 100));
        input.setText(Float.toString(value.get().floatValue()));
        input.onChange(e -> {
            try {
                float newValue = Float.parseFloat(input.getText());
                value.set(Float.valueOf(newValue));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        });
        value.addChangeListener(newValue -> input.setText(Float.toString(newValue.floatValue())), input::isDisposed);
        if (this.hasSlider) {
            FormSlider slider = form.addComponent(yFlow.nextY(new FormSlider("", x, 10, (int)((value.get().floatValue() - this.min) * (float)this.scale / (this.max - this.min)), 0, this.scale, width), 5));
            slider.drawValue = false;
            slider.onChanged(e -> {
                float var = this.min + (float)((FormSlider)e.from).getValue() / (float)this.scale * (this.max - this.min);
                var = GameMath.toDecimals(var, (int)Math.ceil(Math.log(this.scale)));
                value.set(Float.valueOf(var));
            });
            value.addChangeListener(newValue -> slider.setValue((int)((newValue.floatValue() - this.min) * (float)this.scale / (this.max - this.min))), slider::isDisposed);
        }
        value.addChangeListener(newValue -> {
            shader.use();
            shader.pass1f(this.varName, newValue.floatValue());
            shader.stop();
        }, () -> false);
    }
}

