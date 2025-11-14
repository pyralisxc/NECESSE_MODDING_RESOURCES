/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.util.ArrayList;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.ContinueComponent;

public abstract class ContinueForm
extends Form
implements ContinueComponent {
    private ArrayList<Runnable> continueEvents = new ArrayList();
    private boolean isContinued = false;

    public ContinueForm(String name, int width, int height) {
        super(name, width, height);
    }

    @Override
    public final void onContinue(Runnable continueEvent) {
        if (continueEvent != null) {
            this.continueEvents.add(continueEvent);
        }
    }

    @Override
    public final void applyContinue() {
        if (this.canContinue()) {
            this.continueEvents.forEach(Runnable::run);
            this.isContinued = true;
        }
    }

    @Override
    public boolean canContinue() {
        return true;
    }

    @Override
    public final boolean isContinued() {
        return this.isContinued;
    }
}

