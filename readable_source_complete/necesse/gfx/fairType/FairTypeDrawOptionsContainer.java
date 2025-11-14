/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.util.LinkedList;
import java.util.function.Supplier;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.gfx.fairType.FairTypeDrawOptions;

public class FairTypeDrawOptionsContainer {
    private final Supplier<FairTypeDrawOptions> generator;
    private FairTypeDrawOptions drawOptions;
    protected Language currentLang = null;
    protected LinkedList<Runnable> updateEvents = new LinkedList();
    protected LinkedList<Runnable> resetEvents = new LinkedList();

    public FairTypeDrawOptionsContainer(Supplier<FairTypeDrawOptions> generator) {
        this.generator = generator;
    }

    public FairTypeDrawOptionsContainer updateOnLanguageChange() {
        this.currentLang = Localization.getCurrentLang();
        return this;
    }

    public FairTypeDrawOptionsContainer onUpdate(Runnable listener) {
        this.updateEvents.add(listener);
        return this;
    }

    public FairTypeDrawOptionsContainer onReset(Runnable listener) {
        this.resetEvents.add(listener);
        return this;
    }

    public FairTypeDrawOptions get() {
        if (this.drawOptions == null || this.drawOptions.shouldUpdate() || this.currentLang != null && this.currentLang != Localization.getCurrentLang()) {
            if (this.currentLang != null) {
                this.currentLang = Localization.getCurrentLang();
            }
            this.drawOptions = this.generator.get();
            this.updateEvents.forEach(Runnable::run);
        }
        return this.drawOptions;
    }

    public FairTypeDrawOptionsContainer reset() {
        if (this.drawOptions != null) {
            this.drawOptions = null;
            this.resetEvents.forEach(Runnable::run);
        }
        return this;
    }
}

