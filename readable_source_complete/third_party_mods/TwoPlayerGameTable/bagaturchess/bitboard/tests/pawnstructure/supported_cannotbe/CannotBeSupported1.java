/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.supported_cannotbe;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class CannotBeSupported1
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/3p1p2/1P4P1/1Pp1p1P1/1Pp1p1P1/2p1p3/P6P/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validateCannotBeSupported(0, 0x4242000000L);
        this.validateCannotBeSupported(1, 0x2828000000L);
        this.validateKingOpenedAndSemiOpened(0, 0, 0, 2);
        this.validateKingOpenedAndSemiOpened(1, 0, 2, 0);
    }
}

