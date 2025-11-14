/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.backward;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Backward1
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/7p/7p/PP2P4/2P5/7P/5P1P/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validateBackward(0, 1408611834134528L);
        this.validateBackward(1, 65792L);
        this.validateHalfOpenedFiles(0, 0x1212121212121212L);
        this.validateHalfOpenedFiles(1, -72340172838076674L);
        this.validateOpenedFiles(0x1212121212121212L);
        this.validateKingVerticals(0, 0x303030303030303L);
        this.validateKingVerticals(1, 0x303030303030303L);
        this.validateKingOpenedAndSemiOpened(0, 1, 0, 0);
        this.validateKingOpenedAndSemiOpened(1, 1, 0, 0);
    }
}

