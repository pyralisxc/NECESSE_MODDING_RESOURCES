/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.commandcenter.settings;

import java.util.function.Consumer;
import java.util.function.Supplier;
import medievalsim.commandcenter.settings.SettingType;

public class AdminSetting<T> {
    private final String id;
    private final String displayName;
    private final String description;
    private final String category;
    private final SettingType type;
    private final T defaultValue;
    private final T minValue;
    private final T maxValue;
    private final boolean requiresRestart;
    private final Supplier<T> getter;
    private final Consumer<T> setter;

    private AdminSetting(Builder<T> builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.category = builder.category;
        this.type = builder.type;
        this.defaultValue = builder.defaultValue;
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
        this.requiresRestart = builder.requiresRestart;
        this.getter = builder.getter;
        this.setter = builder.setter;
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCategory() {
        return this.category;
    }

    public SettingType getType() {
        return this.type;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public T getMinValue() {
        return this.minValue;
    }

    public T getMaxValue() {
        return this.maxValue;
    }

    public boolean requiresRestart() {
        return this.requiresRestart;
    }

    public T getValue() {
        return this.getter != null ? this.getter.get() : this.defaultValue;
    }

    public boolean setValue(T value) {
        if (this.setter == null) {
            return false;
        }
        if (!(this.type != SettingType.INTEGER && this.type != SettingType.FLOAT || this.isValidRange(value))) {
            return false;
        }
        this.setter.accept(value);
        return true;
    }

    private boolean isValidRange(T value) {
        if (value instanceof Integer) {
            int intValue = (Integer)value;
            int min = this.minValue != null ? (Integer)this.minValue : Integer.MIN_VALUE;
            int max = this.maxValue != null ? (Integer)this.maxValue : Integer.MAX_VALUE;
            return intValue >= min && intValue <= max;
        }
        if (value instanceof Float) {
            float floatValue = ((Float)value).floatValue();
            float min = this.minValue != null ? ((Float)this.minValue).floatValue() : Float.MIN_VALUE;
            float max = this.maxValue != null ? ((Float)this.maxValue).floatValue() : Float.MAX_VALUE;
            return floatValue >= min && floatValue <= max;
        }
        if (value instanceof Long) {
            long longValue = (Long)value;
            long min = this.minValue != null ? (Long)this.minValue : Long.MIN_VALUE;
            long max = this.maxValue != null ? (Long)this.maxValue : Long.MAX_VALUE;
            return longValue >= min && longValue <= max;
        }
        return true;
    }

    public boolean isReadOnly() {
        return this.setter == null;
    }

    public static class Builder<T> {
        private String id;
        private String displayName;
        private String description = "";
        private String category = "General";
        private SettingType type;
        private T defaultValue;
        private T minValue;
        private T maxValue;
        private boolean requiresRestart = false;
        private Supplier<T> getter;
        private Consumer<T> setter;

        public Builder(String id, String displayName, SettingType type) {
            this.id = id;
            this.displayName = displayName;
            this.type = type;
        }

        public Builder<T> description(String description) {
            this.description = description;
            return this;
        }

        public Builder<T> category(String category) {
            this.category = category;
            return this;
        }

        public Builder<T> defaultValue(T value) {
            this.defaultValue = value;
            return this;
        }

        public Builder<T> range(T min, T max) {
            this.minValue = min;
            this.maxValue = max;
            return this;
        }

        public Builder<T> requiresRestart(boolean requires) {
            this.requiresRestart = requires;
            return this;
        }

        public Builder<T> requiresRestart() {
            return this.requiresRestart(true);
        }

        public Builder<T> getter(Supplier<T> getter) {
            this.getter = getter;
            return this;
        }

        public Builder<T> setter(Consumer<T> setter) {
            this.setter = setter;
            return this;
        }

        public AdminSetting<T> build() {
            return new AdminSetting(this);
        }
    }
}

