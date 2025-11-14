/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.doubled;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Doubled1
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/7p/7p/8/8/7P/7P/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validateDoubled(0, 0x1000000000000L);
        this.validateDoubled(1, 256L);
        this.validateKingOpenedAndSemiOpened(0, 1, 0, 0);
        this.validateKingOpenedAndSemiOpened(1, 1, 0, 0);
    }
}

