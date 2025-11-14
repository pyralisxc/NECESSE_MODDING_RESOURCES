/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

public interface ContinueComponent {
    public void onContinue(Runnable var1);

    public void applyContinue();

    default public boolean canContinue() {
        return true;
    }

    public boolean isContinued();
}

