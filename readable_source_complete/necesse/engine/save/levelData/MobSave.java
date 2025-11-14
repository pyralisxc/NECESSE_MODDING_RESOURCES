/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save.levelData;

import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class MobSave {
    public static Mob loadSave(LoadData save, Level level) {
        try {
            Mob mob;
            String newStringID;
            String stringID = save.getUnsafeString("stringID");
            if (stringID.startsWith("mob.")) {
                stringID = stringID.substring(4);
            }
            if (!MobRegistry.mobExists(stringID) && !stringID.equals(newStringID = VersionMigration.tryFixStringID(stringID, VersionMigration.oldMobStringIDs))) {
                System.out.println("Migrated mob from " + stringID + " to " + newStringID);
                stringID = newStringID;
            }
            if ((mob = MobRegistry.getMob(stringID, level)) == null) {
                System.err.println("Loaded mob of type " + stringID + " on level " + (level == null ? "NULL" : level.getIdentifier()) + " was invalid.");
                return null;
            }
            mob.applyLoadData(save);
            return mob;
        }
        catch (Exception e) {
            String type = save.getUnsafeString("stringID", "N/A");
            System.err.println("Could not load mob with type " + type + ", error:");
            e.printStackTrace();
            return null;
        }
    }

    public static SaveData getSave(String saveName, Mob mob) {
        SaveData save = new SaveData(saveName);
        save.addUnsafeString("stringID", mob.getStringID());
        mob.addSaveData(save);
        return save;
    }
}

