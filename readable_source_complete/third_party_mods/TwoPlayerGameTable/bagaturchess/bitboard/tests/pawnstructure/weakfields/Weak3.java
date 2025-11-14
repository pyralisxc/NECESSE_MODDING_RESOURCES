/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.weakfields;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Weak3
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "4k3/p1p3p1/4p3/8/8/3P4/1P3P1P/4K3 b";
    }

    @Override
    @Test
    public void validate() {
        this.validateWeakFields(0, 0x555500000000L);
        this.validateWeakFields(1, 0xAAAA0000L);
    }
}

