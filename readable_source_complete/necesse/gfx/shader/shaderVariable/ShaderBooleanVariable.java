/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.shader.shaderVariable;

import java.util.function.Function;
import java.util.function.Predicate;
import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.shaderVariable.ShaderVariable;

public class ShaderBooleanVariable
extends ShaderVariable<Integer> {
    public Predicate<Integer> toActive;
    public Function<Boolean, Integer> toValue;

    public ShaderBooleanVariable(String varName, Predicate<Integer> toActive, Function<Boolean, Integer> toValue) {
        super(varName);
        this.toActive = toActive;
        this.toValue = toValue;
    }

    public ShaderBooleanVariable(String varName) {
        this(varName, i -> i > 0, b -> b != false ? 1 : 0);
    }

    @Override
    public void addInputs(GameShader shader, ComponentListContainer<? super FormComponent> form, int x, FormFlow yFlow, int width) {
        int value = shader.get1i(this.varName);
        FormCheckBox checkbox = form.addComponent(new FormCheckBox(this.varName, x, yFlow.next(16), this.toActive.test(value)));
        checkbox.onClicked(e -> {
            shader.use();
            shader.pass1i(this.varName, this.toValue.apply(((FormCheckBox)e.from).checked));
            shader.stop();
        });
    }
}

