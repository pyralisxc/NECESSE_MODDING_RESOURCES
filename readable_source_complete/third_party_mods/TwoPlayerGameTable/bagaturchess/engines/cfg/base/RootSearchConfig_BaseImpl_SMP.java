/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.base;

import bagaturchess.bitboard.impl.utils.BinarySemaphoreFactory;
import bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl;
import bagaturchess.search.api.IRootSearchConfig_SMP;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptionSpin_Integer;

public abstract class RootSearchConfig_BaseImpl_SMP
extends RootSearchConfig_BaseImpl
implements IRootSearchConfig_SMP,
IUCIOptionsProvider {
    private static final int DEFAULT_SMP_Threads = RootSearchConfig_BaseImpl_SMP.getDefaultThreadsCount();
    private static final int DEFAULT_CountTranspositionTables = 1;
    private static final int MAX_CountTranspositionTables = (int)Math.sqrt(DEFAULT_SMP_Threads);
    private UCIOption[] options = new UCIOption[]{new UCIOptionSpin_Integer("CountTranspositionTables", 1, "type spin default 1 min 1 max " + MAX_CountTranspositionTables), new UCIOptionSpin_Integer("SMP Threads", DEFAULT_SMP_Threads, "type spin default " + DEFAULT_SMP_Threads + " min 1 max " + Math.max(2, 2 * DEFAULT_SMP_Threads))};

    public RootSearchConfig_BaseImpl_SMP(String[] args) {
        super(args);
    }

    @Override
    public String getSemaphoreFactoryClassName() {
        return BinarySemaphoreFactory.class.getName();
    }

    @Override
    public int getThreadsCount() {
        return (Integer)this.options[1].getValue();
    }

    @Override
    public int getTPTsCount() {
        return (Integer)this.options[0].getValue();
    }

    @Override
    public UCIOption[] getSupportedOptions() {
        UCIOption[] parentOptions = super.getSupportedOptions();
        UCIOption[] result = new UCIOption[parentOptions.length + this.options.length];
        System.arraycopy(this.options, 0, result, 0, this.options.length);
        System.arraycopy(parentOptions, 0, result, this.options.length, parentOptions.length);
        return result;
    }

    @Override
    public boolean applyOption(UCIOption option) {
        if ("SMP Threads".equals(option.getName())) {
            return true;
        }
        if ("CountTranspositionTables".equals(option.getName())) {
            return true;
        }
        return super.applyOption(option);
    }

    private static final int getDefaultThreadsCount() {
        int threads = Runtime.getRuntime().availableProcessors();
        if ((threads /= 2) < 1) {
            threads = 1;
        }
        return threads;
    }
}

