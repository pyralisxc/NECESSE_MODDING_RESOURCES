/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug;

import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.lists.FormShaderList;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.shader.GameShader;
import necesse.gfx.shader.shaderVariable.ShaderVariable;

public class DebugShadersForm
extends Form {
    public FormShaderList shaderList;
    public FormContentBox inputBox;
    public FormTextButton shadersBack;
    public final DebugForm parent;

    public DebugShadersForm(String name, DebugForm parent) {
        super(name, 320, 400);
        this.parent = parent;
        this.addComponent(new FormLabel("Shaders", new FontOptions(20), 0, this.getWidth() / 2, 10));
        this.shaderList = this.addComponent(new FormShaderList(0, 40, this.getWidth(), 150));
        this.shaderList.onShaderSelect(e -> this.updateShaderInputs(e.shader));
        this.inputBox = this.addComponent(new FormContentBox(0, 190, this.getWidth(), this.getHeight() - 150 - 80));
        this.shadersBack = this.addComponent(new FormTextButton("Back", 0, this.getHeight() - 40, this.getWidth()));
        this.shadersBack.onClicked(e -> parent.makeCurrent(parent.mainMenu));
    }

    private void updateShaderInputs(GameShader shader) {
        this.inputBox.clearComponents();
        FormFlow flow = new FormFlow(5);
        ArrayList<ShaderVariable<?>> variables = shader.getVariables();
        for (ShaderVariable<?> variable : variables) {
            variable.addInputs(shader, this.inputBox, 5, flow, this.inputBox.getWidth() - 18);
        }
        this.inputBox.setContentBox(new Rectangle(this.getWidth(), flow.next() + 5));
    }
}

