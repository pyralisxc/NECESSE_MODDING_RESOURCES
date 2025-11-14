/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.passers;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Passers1
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/p6p/8/8/8/8/2P3P1/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validatePassers(0, 0x20000000000000L, 1);
        this.validatePassers(1, 32768L, 1);
        this.validateUnstoppablePassers(0, 0L);
        this.validateUnstoppablePassers(1, 32768L);
    }
}

