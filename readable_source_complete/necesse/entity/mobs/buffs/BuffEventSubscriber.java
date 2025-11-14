/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs;

import java.util.function.Consumer;
import necesse.entity.mobs.MobGenericEvent;

public interface BuffEventSubscriber {
    public <T extends MobGenericEvent> void subscribeEvent(Class<T> var1, Consumer<T> var2);
}

