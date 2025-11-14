/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.base;

import bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl_SMP;
import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptionSpin_Integer;

public class RootSearchConfig_BaseImpl_SMP_Processes
extends RootSearchConfig_BaseImpl_SMP {
    private int currentThreadMemory = 1024;
    private UCIOption[] options = new UCIOption[]{new UCIOptionSpin_Integer("Thread Memory (MB)", this.currentThreadMemory, "type spin default " + this.currentThreadMemory + " min 256 max 1024")};

    public RootSearchConfig_BaseImpl_SMP_Processes(String[] args) {
        super(args);
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
        if ("Thread Memory (MB)".equals(option.getName())) {
            this.currentThreadMemory = (Integer)option.getValue();
            return true;
        }
        return super.applyOption(option);
    }

    @Override
    public int getThreadMemory_InMegabytes() {
        return this.currentThreadMemory;
    }

    @Override
    public boolean initCaches() {
        return false;
    }
}

