/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.passers;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Passers7
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/8/2p5/1P6/8/8/8/7K w";
    }

    @Override
    @Test
    public void validate() {
        this.validatePassers(0, 0L, 0);
        this.validatePassers(1, 0L, 0);
        this.validateUnstoppablePassers(0, 0L);
        this.validateUnstoppablePassers(1, 0L);
    }
}

