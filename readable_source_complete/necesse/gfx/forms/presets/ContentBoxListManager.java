/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.util.LinkedList;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.position.FormPositionContainer;

public class ContentBoxListManager {
    public final FormContentBox contentBox;
    private final LinkedList<FormPositionContainer> components = new LinkedList();
    private FormFlow flow;

    public ContentBoxListManager(FormContentBox contentBox) {
        this.contentBox = contentBox;
        this.flow = new FormFlow();
    }

    public <T extends FormPositionContainer> T add(T component) {
        return this.add(component, 0);
    }

    public <T extends FormPositionContainer> T add(T component, int extraSpace) {
        this.contentBox.addComponent((FormComponent)((Object)component));
        this.components.add(component);
        return this.flow.nextY(component, extraSpace);
    }

    public void clear() {
        for (FormPositionContainer component : this.components) {
            this.contentBox.removeComponent((FormComponent)((Object)component));
        }
        this.components.clear();
        this.flow = new FormFlow();
    }

    public int size() {
        return this.components.size();
    }

    public boolean isEmpty() {
        return this.components.isEmpty();
    }

    public void updatePositions() {
        this.flow = new FormFlow();
        for (FormPositionContainer component : this.components) {
            this.flow.nextY(component);
        }
    }

    public void fit(int padding) {
        this.contentBox.fitContentBoxToComponents(padding);
    }

    public void fit(int leftPadding, int rightPadding, int topPadding, int botPadding) {
        this.contentBox.fitContentBoxToComponents(leftPadding, rightPadding, topPadding, botPadding);
    }

    public void fit() {
        this.contentBox.fitContentBoxToComponents();
    }
}

