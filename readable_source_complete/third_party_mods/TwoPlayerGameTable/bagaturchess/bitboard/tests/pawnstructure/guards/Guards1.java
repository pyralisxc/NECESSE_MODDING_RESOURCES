/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.guards;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Guards1
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/7p/1pP5/8/8/8/5PPP/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validateGuards(0, 0x3000000000000L, 2);
        this.validateGuards(1, 256L, 1);
        this.validateKingOpenedAndSemiOpened(0, 0, 0, 1);
        this.validateKingOpenedAndSemiOpened(1, 0, 1, 0);
    }
}

