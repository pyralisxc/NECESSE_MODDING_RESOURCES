/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.doubled;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Doubled2
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/7p/7p/8/1P6/1P5P/1P5P/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validateDoubled(0, 0x41400000000000L);
        this.validateDoubled(1, 256L);
    }
}

