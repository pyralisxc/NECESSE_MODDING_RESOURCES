/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.passers;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Passers8
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/8/2pP4/8/8/8/8/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validatePassers(0, 0x100000L, 5);
        this.validatePassers(1, 0x200000L, 2);
        this.validateUnstoppablePassers(0, 0x100000L);
        this.validateUnstoppablePassers(1, 0L);
    }
}

