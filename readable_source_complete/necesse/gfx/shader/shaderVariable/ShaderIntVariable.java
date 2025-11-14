/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader.shaderVariable;

import necesse.engine.util.EventVariable;
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

public class ShaderIntVariable
extends ShaderVariable<Integer> {
    protected boolean hasSlider;
    protected int min;
    protected int max;

    public ShaderIntVariable(String varName) {
        super(varName);
    }

    public ShaderIntVariable(String varName, int min, int max) {
        this(varName);
        this.hasSlider = true;
        this.min = min;
        this.max = max;
    }

    @Override
    public void addInputs(GameShader shader, ComponentListContainer<? super FormComponent> form, int x, FormFlow yFlow, int width) {
        EventVariable<Integer> value = new EventVariable<Integer>(shader.get1i(this.varName));
        form.addComponent(new FormLabel(this.varName, new FontOptions(16), -1, x, yFlow.next(18), width));
        FormTextInput input = form.addComponent(new FormTextInput(x, yFlow.next(22), FormInputSize.SIZE_20, width, 100));
        input.setText(Integer.toString(value.get()));
        input.onChange(e -> {
            try {
                int newValue = Integer.parseInt(input.getText());
                value.set(newValue);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        });
        value.addChangeListener(newValue -> input.setText(Integer.toString(newValue)), input::isDisposed);
        if (this.hasSlider) {
            FormSlider slider = form.addComponent(yFlow.nextY(new FormSlider("", x, 0, value.get(), this.min, this.max, width), 5));
            slider.drawValue = false;
            slider.onChanged(e -> value.set(((FormSlider)e.from).getValue()));
            value.addChangeListener(newValue -> slider.setValue((Integer)value.get()), slider::isDisposed);
        }
        value.addChangeListener(newValue -> {
            shader.use();
            shader.pass1i(this.varName, (int)newValue);
            shader.stop();
        }, () -> false);
    }
}

