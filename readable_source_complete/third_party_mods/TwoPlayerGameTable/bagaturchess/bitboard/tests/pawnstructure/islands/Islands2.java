/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.islands;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Islands2
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/p3p2p/8/8/8/PPPPPPP1/7P/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validateIslandsCount(0, 1);
        this.validateIslandsCount(1, 3);
    }
}

