/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.islands;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Islands3
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/1p2p1p1/8/8/1PP5/1PPP1PP1/8/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validateIslandsCount(0, 2);
        this.validateIslandsCount(1, 3);
        this.validateKingOpenedAndSemiOpened(0, 1, 0, 0);
        this.validateKingOpenedAndSemiOpened(1, 1, 0, 0);
    }
}

