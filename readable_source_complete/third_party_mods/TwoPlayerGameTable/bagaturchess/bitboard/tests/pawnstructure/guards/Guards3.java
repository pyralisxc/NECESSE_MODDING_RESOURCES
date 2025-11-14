/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.guards;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Guards3
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "6k1/7p/1pP2p1/6p1/8/8/5PKP/8 w";
    }

    @Override
    @Test
    public void validate() {
        System.out.println(this.bitboard);
        this.validateGuards(0, 0L, 0);
        this.validateGuards(1, 33816832L, 6);
        this.validateKingOpenedAndSemiOpened(0, 0, 1, 0);
        this.validateKingOpenedAndSemiOpened(1, 0, 0, 1);
    }
}

