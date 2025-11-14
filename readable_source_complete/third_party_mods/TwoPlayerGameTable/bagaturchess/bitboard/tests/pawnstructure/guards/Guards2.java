/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.guards;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Guards2
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "6k1/7p/1pP2p1/8/8/8/5PKP/8 b";
    }

    @Override
    @Test
    public void validate() {
        this.validateGuards(0, 0L, 0);
        this.validateGuards(1, 262400L, 3);
        this.validateKingOpenedAndSemiOpened(0, 1, 0, 0);
        this.validateKingOpenedAndSemiOpened(1, 1, 0, 0);
    }
}

