/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.job;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.registries.RegistryClosedException;

public class JobType
implements IDDataContainer {
    public final IDData idData = new IDData();
    public boolean canChangePriority;
    public boolean defaultDisabledBySettler;
    public GameMessage displayName;
    public GameMessage tooltip;

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    @Override
    public final int getID() {
        return this.idData.getID();
    }

    @Override
    public String getStringID() {
        return this.idData.getStringID();
    }

    public JobType(boolean canChangePriority, boolean defaultDisabledBySettler, GameMessage displayName, GameMessage tooltip) {
        if (JobTypeRegistry.instance.isClosed()) {
            throw new RegistryClosedException("Cannot construct JobType objects when job type registry is closed, since they are a static registered objects. Use JobTypeRegistry.getJobType(...) to get job types.");
        }
        this.canChangePriority = canChangePriority;
        this.defaultDisabledBySettler = defaultDisabledBySettler;
        this.displayName = displayName;
        this.tooltip = tooltip;
    }

    public void onJobTypeRegistryClosed() {
    }
}

