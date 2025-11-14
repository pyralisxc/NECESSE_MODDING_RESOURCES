/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.passers;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Passers51
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "5k2/8/1P6/8/8/8/8/4K3 b";
    }

    @Override
    @Test
    public void validate() {
        this.validatePassers(0, 0x400000L, 5);
        this.validatePassers(1, 0L, 0);
        this.validateUnstoppablePassers(0, 0x400000L);
        this.validateUnstoppablePassers(1, 0L);
        this.validateKingVerticals(0, 0x1C1C1C1C1C1C1C1CL);
        this.validateKingVerticals(1, 0xE0E0E0E0E0E0E0EL);
        this.validateKingOpenedAndSemiOpened(0, 3, 0, 0);
        this.validateKingOpenedAndSemiOpened(1, 3, 0, 0);
    }
}

