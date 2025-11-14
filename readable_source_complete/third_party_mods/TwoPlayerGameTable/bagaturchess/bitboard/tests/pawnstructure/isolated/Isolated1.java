/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.junit.Test
 */
package bagaturchess.bitboard.tests.pawnstructure.isolated;

import bagaturchess.bitboard.tests.pawnstructure.PawnStructureTest;
import org.junit.Test;

public class Isolated1
extends PawnStructureTest {
    @Override
    public String getFEN() {
        return "7k/p6p/7p/1p2p3/1P1P4/7P/4P2P/7K b";
    }

    @Override
    @Test
    public void validate() {
        this.validateIsolated(0, 0x1014000000000L);
        this.validateIsolated(1, 0x8010100L);
    }
}

