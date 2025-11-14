/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.localization;

import necesse.engine.localization.Language;

public abstract class LocalizationChangeListener {
    public abstract void onChange(Language var1);

    public abstract boolean isDisposed();
}

