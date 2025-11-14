/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameUtils;
import necesse.gfx.forms.components.FormPlayerStatComponent;

public class FormPlayerStatIntComponent
extends FormPlayerStatComponent<Integer> {
    private final String suffix;

    public FormPlayerStatIntComponent(int x, int y, int width, GameMessage displayName, Supplier<Integer> statSupplier, String suffix) {
        super(x, y, width, displayName, statSupplier, (T s) -> GameUtils.metricNumber(s.intValue()) + suffix);
        this.suffix = suffix;
    }

    public FormPlayerStatIntComponent(int x, int y, int width, GameMessage displayName, Supplier<Integer> statSupplier) {
        this(x, y, width, displayName, statSupplier, "");
    }

    @Override
    public String getTooltip(boolean couldFitData, Integer value, String formattedValue) {
        if (!couldFitData || !formattedValue.equals(String.valueOf(value) + this.suffix)) {
            return this.displayName.translate() + ": " + GameUtils.formatNumber(value.intValue());
        }
        return null;
    }
}

