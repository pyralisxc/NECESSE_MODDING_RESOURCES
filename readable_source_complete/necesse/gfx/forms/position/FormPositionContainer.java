/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.position;

import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;

public interface FormPositionContainer {
    public FormPosition getPosition();

    public void setPosition(FormPosition var1);

    default public int getX() {
        return this.getPosition().getX();
    }

    default public int getY() {
        return this.getPosition().getY();
    }

    default public void setX(int x) {
        this.setPosition(new FormFixedPosition(x, this.getY()));
    }

    default public void setY(int y) {
        this.setPosition(new FormFixedPosition(this.getX(), y));
    }

    default public void setPosition(int x, int y) {
        this.setPosition(new FormFixedPosition(x, y));
    }

    default public void addPosition(int x, int y) {
        this.setX(this.getX() + x);
        this.setY(this.getY() + y);
    }
}

