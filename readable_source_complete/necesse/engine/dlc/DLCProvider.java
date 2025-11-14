/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.dlc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import necesse.engine.dlc.DLC;
import necesse.engine.util.Event;

public abstract class DLCProvider {
    public static DLCProvider instance;
    public static final HashMap<DLC, DLCStatus> DLCStatuses;
    public static final Event<DLC> onDLCInstalled;

    public DLCProvider() {
        instance = this;
        DLC.DLCs.forEach(dlc -> DLCStatuses.put((DLC)dlc, DLCStatus.NOT_OWNED));
    }

    public void checkForNewlyInstalledDLCs() {
        DLCStatuses.forEach((DLC2, value) -> {
            if (value != DLCStatus.INSTALLED) {
                DLCStatus newStatus = this.checkDLCStatus((DLC)DLC2);
                DLCStatuses.put((DLC)DLC2, newStatus);
                if (newStatus == DLCStatus.INSTALLED) {
                    DLC2.onDLCInstalledClient();
                    onDLCInstalled.invoke((DLC)DLC2);
                }
            }
        });
    }

    public static List<DLC> getInstalledDLCs() {
        return DLCStatuses.entrySet().stream().filter(entry -> entry.getValue() == DLCStatus.INSTALLED).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    protected abstract DLCStatus checkDLCStatus(DLC var1);

    static {
        DLCStatuses = new HashMap();
        onDLCInstalled = new Event();
    }

    public static enum DLCStatus {
        NOT_OWNED,
        OWNED,
        INSTALLED;

    }
}

