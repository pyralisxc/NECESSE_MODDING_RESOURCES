/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.storms;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Storms1
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "6k1/7p/1pP2p1/6p1/8/8/5PKP/8 w";
    }

    @Override
    @Test
    public void validate() {
        this.validateStorms(0, 0x5000000000000L, 12);
        this.validateStorms(1, 33816832L, 12);
        this.validateKingVerticals(0, 0x707070707070707L);
        this.validateKingVerticals(1, 0x707070707070707L);
    }
}

