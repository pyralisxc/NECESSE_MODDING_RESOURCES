/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.supported_cannotbe;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class CannotBeSupported2
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/8/1P1p1pP1/1Pp1p1P1/1Pp1p1P1/P1p1p2P/8/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validateCannotBeSupported(0, 0x4242000000L);
        this.validateCannotBeSupported(1, 0x2828000000L);
        this.validateHalfOpenedFiles(0, 0x3C3C3C3C3C3C3C3CL);
        this.validateHalfOpenedFiles(1, -4340410370284600381L);
        this.validateOpenedFiles(0L);
    }
}

