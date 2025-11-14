/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameUtils;
import necesse.gfx.forms.components.FormPlayerStatComponent;

public class FormPlayerStatLongComponent
extends FormPlayerStatComponent<Long> {
    private final String suffix;

    public FormPlayerStatLongComponent(int x, int y, int width, GameMessage displayName, Supplier<Long> statSupplier, String suffix) {
        super(x, y, width, displayName, statSupplier, (T s) -> GameUtils.metricNumber(s) + suffix);
        this.suffix = suffix;
    }

    public FormPlayerStatLongComponent(int x, int y, int width, GameMessage displayName, Supplier<Long> statSupplier) {
        this(x, y, width, displayName, statSupplier, "");
    }

    @Override
    public String getTooltip(boolean couldFitData, Long value, String formattedValue) {
        if (!couldFitData || !formattedValue.equals(String.valueOf(value) + this.suffix)) {
            return this.displayName.translate() + ": " + GameUtils.formatNumber(value);
        }
        return null;
    }
}

