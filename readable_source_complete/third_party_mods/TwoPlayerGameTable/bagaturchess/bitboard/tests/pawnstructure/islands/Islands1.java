/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.islands;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Islands1
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/8/8/8/8/8/8/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validateIslandsCount(0, 0);
        this.validateIslandsCount(1, 0);
        this.validateOpenedFiles(-1L);
        this.validateKingOpenedAndSemiOpened(0, 2, 0, 0);
        this.validateKingOpenedAndSemiOpened(1, 2, 0, 0);
    }
}

