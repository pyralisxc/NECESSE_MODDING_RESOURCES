/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class VersionMigration {
    public static String[][] oldTileStringIDs = new String[][]{{"stonefloor", "rockfloor"}, {"deepstonefloor", "deeprockfloor"}};
    public static String[][] oldObjectStringIDs = new String[][]{{"sprucedinnertable", "wooddinnertable"}, {"sprucedinnertable2", "wooddinnertable2"}, {"sprucedesk", "wooddesk"}, {"sprucemodulartable", "woodmodulartable"}, {"sprucechair", "woodchair"}, {"sprucebookshelf", "woodbookshelf"}, {"sprucebathtub", "woodbathtub"}, {"sprucebathtub2", "woodbathtubr"}, {"sprucetoilet", "woodtoilet"}, {"sprucebed", "woodbed"}, {"sprucebed2", "woodbed2"}, {"sprucedisplay", "itemstand"}, {"woolcarpet", "woolcarpetr", "woolcarpetd", "woolcarpetdr"}, {"leathercarpet", "leathercarpetr", "leathercarpetd", "leathercarpetdr"}, {"stonepressureplate", "rockpressureplate"}, {"deepstonepressureplate", "deeprockpressureplate"}, {"tungstenworkstation", "advancedworkstation"}, {"tungstenworkstation2", "advancedworkstation2"}, {"teleportationstone", "travelstone"}};
    public static String[][] oldLogicGateStringIDs = new String[0][];
    public static String[][] oldItemStringIDs = new String[][]{{"brainonastick", "babyzombie"}, {"inefficientfeather", "tameostrich"}, {"weticicle", "petpenguin"}, {"exoticseeds", "petparrot"}, {"magicstilts", "petwalkingtorch"}, {"sprucelog", "log"}, {"healthregenpotion", "combatregenpotion"}, {"waterfaevinyl", "surfacevinyl"}, {"musesvinyl", "surfacenightvinyl"}, {"runningvinyl", "cavevinyl"}, {"grindthealarmsvinyl", "deepcavevinyl"}, {"bythefieldvinyl", "desertsurfacevinyl"}, {"sunstonesvinyl", "desertnightvinyl"}, {"caravantusksvinyl", "desertcavevinyl"}, {"homeatlastvinyl", "snowsurfacevinyl"}, {"telltalevinyl", "snownightvinyl"}, {"icyrusevinyl", "snowcavevinyl"}, {"icestarvinyl", "deepsnowcavevinyl"}, {"eyesofthedesertvinyl", "swampsurfacevinyl"}, {"rialtovinyl", "swampnightvinyl"}, {"silverlakevinyl", "swampcavevinyl"}, {"awayvinyl", "piratesurfacevinyl"}, {"kronosvinyl", "dungeonvinyl"}, {"lostgripvinyl", "raidvinyl"}, {"elektrakvinyl", "boss1vinyl"}, {"thecontrolroomvinyl", "boss2vinyl"}, {"airlockfailurevinyl", "boss3vinyl"}, {"konsoleglitchvinyl", "boss4vinyl"}, {"beatdownvinyl", "boss5vinyl"}, {"siegevinyl", "boss6vinyl"}, {"halodromevinyl", "boss7vinyl"}, {"milleniumvinyl", "boss8vinyl"}, {"kandiruvinyl", "boss9vinyl"}, {"spiritboard", "ouijaboard"}, {"teleportationscroll", "travelscroll"}, {"teleportationstone", "travelstone"}};
    public static String[][] oldMobStringIDs = new String[][]{{"exoticmerchanthuman", "travelingmerchant"}};
    public static String[][] oldBiomeStringIDs = new String[][]{{"dungeon", "dungeonisland", "DungeonIsland"}, {"piratevillage", "pirateisland", "PirateIsland"}, {"forest", "forestvillage", "villageisland", "VillageIsland"}, {"plains", "plainsvillage"}, {"desert", "desertvillage"}, {"snow", "snowvillage"}};

    public static String tryFixStringID(String stringID, String[][] oldStringIDs) {
        int index = VersionMigration.getFixedConversionIndex(stringID, oldStringIDs);
        if (index != -1) {
            return oldStringIDs[index][0];
        }
        return stringID;
    }

    private static int getFixedConversionIndex(String stringID, String[][] oldStringIDs) {
        for (int i = 0; i < oldStringIDs.length; ++i) {
            if (oldStringIDs[i].length < 2) continue;
            for (int j = 0; j < oldStringIDs[i].length; ++j) {
                if (!stringID.equals(oldStringIDs[i][j])) continue;
                return i;
            }
        }
        return -1;
    }

    private static int getNewStringIDIndex(String fromStringID, String[] toStringIDs, String[][] oldStringIDs, int unknownID) {
        int newIndex;
        for (int j = 0; j < toStringIDs.length; ++j) {
            if (toStringIDs[j] == null || !toStringIDs[j].equals(fromStringID)) continue;
            return j;
        }
        if (oldStringIDs != null && (newIndex = VersionMigration.getFixedConversionIndex(fromStringID, oldStringIDs)) != -1) {
            return VersionMigration.getNewStringIDIndex(oldStringIDs[newIndex][0], toStringIDs, null, unknownID);
        }
        return unknownID;
    }

    public static int[] getConversionArray(String[] fromStringIDs, String[] toStringIDs, String[][] oldStringIDs, int unknownID) {
        if (fromStringIDs == null || toStringIDs == null) {
            return null;
        }
        int[] out = new int[fromStringIDs.length];
        boolean updated = false;
        for (int i = 0; i < fromStringIDs.length; ++i) {
            int newIndex;
            String fromStringID = fromStringIDs[i];
            if (fromStringID == null) continue;
            out[i] = newIndex = VersionMigration.getNewStringIDIndex(fromStringID, toStringIDs, oldStringIDs, unknownID);
            if (newIndex == i) continue;
            updated = true;
        }
        if (!updated) {
            return null;
        }
        return out;
    }

    public static boolean convertArray(int[] array, int[] conversionArray) {
        if (conversionArray == null) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            int old = array[i];
            if (old < 0 || old >= conversionArray.length) continue;
            array[i] = conversionArray[old];
        }
        return true;
    }

    public static int convertIndex(int index, int[] conversionArray) {
        if (index >= 0 && index < conversionArray.length) {
            return conversionArray[index];
        }
        return index;
    }

    public static String[][] concatOldStringIDs(String[][] ... arrays) {
        int totalLength = 0;
        for (String[][] array : arrays) {
            totalLength += array.length;
        }
        String[][] result = new String[totalLength][];
        int lengthCounter = 0;
        for (String[][] array : arrays) {
            System.arraycopy(array, 0, result, lengthCounter, array.length);
            lengthCounter += array.length;
        }
        return result;
    }

    public static boolean convertArray(int[] array, String[] fromArray, String[] toArray, int unknownID, String[][] oldStringIDs) {
        return VersionMigration.convertArray(array, VersionMigration.getConversionArray(fromArray, toArray, oldStringIDs, unknownID));
    }

    public static boolean convertArray(int[] array, String[] fromArray, String[] toArray, int unknownID) {
        return VersionMigration.convertArray(array, fromArray, toArray, unknownID, null);
    }

    public static String[] generateStringIDsArray(Collection<Integer> ids, Function<Integer, String> toStringID) {
        String[] array = new String[ids.size() * 2];
        int i = 0;
        for (int id : ids) {
            array[i++] = Integer.toString(id);
            array[i++] = toStringID.apply(id);
        }
        return array;
    }

    public static int[] generateStringIDsArrayConversionArray(String[] stringIDsArray, String[] toStringIDs, int unknownID, String[][] oldStringIDs) {
        String[] fromStringIDs = new String[toStringIDs.length];
        for (int i = 0; i < stringIDsArray.length; i += 2) {
            try {
                int id = Integer.parseInt(stringIDsArray[i]);
                if (id < 0) continue;
                if (fromStringIDs.length <= id) {
                    fromStringIDs = Arrays.copyOf(fromStringIDs, id + 1);
                }
                fromStringIDs[id] = stringIDsArray[i + 1];
                continue;
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return VersionMigration.getConversionArray(fromStringIDs, toStringIDs, oldStringIDs, unknownID);
    }

    static {
        oldItemStringIDs = VersionMigration.concatOldStringIDs(oldTileStringIDs, oldObjectStringIDs, oldItemStringIDs);
    }
}

