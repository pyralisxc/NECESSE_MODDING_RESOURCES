/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.weakfields;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Weak1
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "4k3/pppppppp/8/8/8/8/PPPPPPPP/4K3 b";
    }

    @Override
    @Test
    public void validate() {
        this.validateWeakFields(0, 0L);
        this.validateWeakFields(1, 0L);
    }
}

