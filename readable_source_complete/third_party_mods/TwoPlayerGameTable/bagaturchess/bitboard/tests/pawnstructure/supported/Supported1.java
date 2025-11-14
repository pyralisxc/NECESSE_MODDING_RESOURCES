/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.supported;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Supported1
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/6pp/1Pp5/2PpPp2/8/5P2/6PP/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validateSupported(0, 0x3040000400000L);
        this.validateSupported(1, 0x10000300L);
        this.validateKingOpenedAndSemiOpened(0, 0, 0, 0);
        this.validateKingOpenedAndSemiOpened(1, 0, 0, 0);
    }
}

