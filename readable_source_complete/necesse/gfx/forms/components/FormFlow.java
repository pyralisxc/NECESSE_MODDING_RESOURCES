/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.position.FormPositionContainer;

public class FormFlow {
    private int y;
    private int lastY;

    public FormFlow(int y) {
        this.y = y;
        this.lastY = y;
    }

    public FormFlow() {
        this(0);
    }

    public int next(int add) {
        int out = this.y;
        this.lastY = this.y;
        this.y += add;
        return out;
    }

    public int next() {
        return this.next(0);
    }

    @Deprecated
    public <T extends FormPositionContainer> T next(T component) {
        return this.nextY(component, 0);
    }

    @Deprecated
    public <T extends FormPositionContainer> T next(T component, int extra) {
        return this.nextY(component, extra);
    }

    public <T extends FormPositionContainer> T nextY(T component) {
        return this.nextY(component, 0);
    }

    public <T extends FormPositionContainer> T nextY(T component, int extra) {
        Rectangle boundingBox = ((FormComponent)((Object)component)).getBoundingBox();
        component.setY(this.next(boundingBox.height + extra));
        return component;
    }

    public <T extends FormPositionContainer> T nextX(T component) {
        return this.nextX(component, 0);
    }

    public <T extends FormPositionContainer> T nextX(T component, int extra) {
        Rectangle boundingBox = ((FormComponent)((Object)component)).getBoundingBox();
        component.setX(this.next(boundingBox.width + extra));
        return component;
    }

    public int prev(int add) {
        this.lastY = this.y;
        this.y -= add;
        return this.y;
    }

    public <T extends FormPositionContainer> T prevY(T component) {
        return this.prevY(component, 0);
    }

    public <T extends FormPositionContainer> T prevY(T component, int extra) {
        this.lastY = this.y;
        Rectangle boundingBox = ((FormComponent)((Object)component)).getBoundingBox();
        component.setY(this.prev(boundingBox.height));
        this.y -= extra;
        return component;
    }

    public <T extends FormPositionContainer> T prevX(T component) {
        return this.prevX(component, 0);
    }

    public <T extends FormPositionContainer> T prevX(T component, int extra) {
        Rectangle boundingBox = ((FormComponent)((Object)component)).getBoundingBox();
        component.setX(this.prev(boundingBox.width));
        this.y -= extra;
        return component;
    }

    public FormFlow split(int add) {
        this.next(add);
        return new FormFlow(this.y);
    }

    public FormFlow split() {
        return this.split(0);
    }

    public <T extends FormPositionContainer> T sameY(T component) {
        return this.sameY(component, 0);
    }

    public <T extends FormPositionContainer> T sameY(T component, int extra) {
        this.lastY = this.y;
        Rectangle boundingBox = ((FormComponent)((Object)component)).getBoundingBox();
        component.setY(this.lastY - boundingBox.height + extra);
        return component;
    }
}

