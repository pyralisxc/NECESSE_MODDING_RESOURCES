/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader.shaderVariable;

import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.shaderVariable.ShaderVariable;

public class ShaderInt3Variable
extends ShaderVariable<Integer[]> {
    public ShaderInt3Variable(String varName) {
        super(varName);
    }

    @Override
    public void addInputs(GameShader shader, ComponentListContainer<? super FormComponent> form, int x, FormFlow yFlow, int width) {
        int[] values = shader.get3i(this.varName);
        form.addComponent(new FormLabel(this.varName, new FontOptions(16), -1, x, yFlow.next(16), width));
        this.addTextInput(shader, form, x, yFlow, width, values, 0);
        this.addTextInput(shader, form, x, yFlow, width, values, 1);
        this.addTextInput(shader, form, x, yFlow, width, values, 2);
    }

    protected void addTextInput(GameShader shader, ComponentListContainer<? super FormComponent> form, int x, FormFlow yFlow, int width, int[] values, int index) {
        FormTextInput input = form.addComponent(new FormTextInput(x, yFlow.next(22), FormInputSize.SIZE_20, width, 100));
        input.setText(Integer.toString(values[index]));
        input.onChange(e -> {
            try {
                values[index] = Integer.parseInt(input.getText());
                this.apply(shader, values);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        });
    }

    protected void apply(GameShader shader, int[] variable) {
        shader.use();
        shader.pass3i(this.varName, variable[0], variable[1], variable[2]);
        shader.stop();
    }
}

