/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.dlc;

import java.util.ArrayList;
import java.util.HashMap;
import necesse.engine.GameLog;
import necesse.engine.localization.Localization;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;

public class DLC
implements IDDataContainer {
    public static final DLC SUPPORTER_PACK;
    public static final ArrayList<DLC> DLCs;
    private static final HashMap<String, Integer> stringIDToIDMap;
    public final IDData idData = new IDData();
    public final int steamAppID;

    public DLC(int steamAppID) {
        this.steamAppID = steamAppID;
    }

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    @Override
    public final int getID() {
        return this.idData.getID();
    }

    @Override
    public final String getStringID() {
        return this.idData.getStringID();
    }

    private static DLC addDLC(DLC dlc, String stringID) {
        dlc.idData.setData(DLCs.size(), stringID);
        stringIDToIDMap.put(stringID, dlc.getID());
        DLCs.add(dlc);
        return dlc;
    }

    public static DLC getDLC(int id) {
        if (id < 0 || id >= DLCs.size()) {
            return null;
        }
        return DLCs.get(id);
    }

    public static DLC getDLC(String stringID) {
        Integer index = stringIDToIDMap.get(stringID);
        if (index == null) {
            return null;
        }
        return DLCs.get(index);
    }

    protected void onDLCInstalledClient() {
        GameLog.out.println("DLC " + Localization.translate("dlc", this.getStringID()) + " installed.");
    }

    static {
        DLCs = new ArrayList();
        stringIDToIDMap = new HashMap();
        SUPPORTER_PACK = DLC.addDLC(new DLC(3947840), "supporterpack");
    }
}

