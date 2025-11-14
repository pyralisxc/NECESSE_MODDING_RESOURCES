/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.tests.pawnstructure;

import bagaturchess.bitboard.tests.pawnstructure.passers.Passers1;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

public class RunAll {
    private static Collection<String> testcases = new ArrayList<String>();

    public static void main(String[] args) {
        for (String clazzName : testcases) {
            try {
                Class<?> clazz = RunAll.class.getClassLoader().loadClass(clazzName);
                Object obj = clazz.newInstance();
                System.out.print("Executing " + clazz.getName() + " ... ");
                Method m = clazz.getMethod("validate", null);
                m.invoke(obj, null);
                System.out.println("OK");
            }
            catch (Exception e) {
                System.out.println("FAILED");
                e.printStackTrace();
            }
        }
    }

    static {
        testcases.add(Passers1.class.getName());
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.passers.Passers2");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.passers.Passers3");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.passers.Passers4");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.passers.Passers5");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.passers.Passers51");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.passers.Passers6");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.passers.Passers7");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.passers.Passers8");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.guards.Guards1");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.guards.Guards2");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.guards.Guards3");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.storms.Storms1");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.doubled.Doubled1");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.doubled.Doubled2");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.backward.Backward1");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.isolated.Isolated1");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.supported.Supported1");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.supported_cannotbe.CannotBeSupported1");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.supported_cannotbe.CannotBeSupported2");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.islands.Islands1");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.islands.Islands2");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.islands.Islands3");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.weakfields.Weak1");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.weakfields.Weak2");
        testcases.add("bagaturchess.bitboard.tests.pawnstructure.weakfields.Weak3");
    }
}

