/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.passers;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Passers2
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/p1p1p1p1/3p4/8/8/8/1P3P1P/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validatePassers(0, 0L, 0);
        this.validatePassers(1, 0x100000L, 2);
        this.validateUnstoppablePassers(0, 0L);
        this.validateUnstoppablePassers(1, 0L);
        this.validateHalfOpenedFiles(0, -4991471925827290438L);
        this.validateHalfOpenedFiles(1, 0x4545454545454545L);
        this.validateOpenedFiles(0L);
    }
}

