/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.base;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.engines.cfg.base.TimeConfigImpl;
import bagaturchess.uci.api.ISearchAdaptorConfig;
import bagaturchess.uci.api.ITimeConfig;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.api.IUCIOptionsRegistry;
import bagaturchess.uci.impl.commands.options.UCIOption;

public class UCISearchAdaptorConfig_BaseImpl
implements ISearchAdaptorConfig {
    private static final boolean DEFAULT_OwnBook = true;
    private static final boolean DEFAULT_Ponder = false;
    private static final boolean DEFAULT_AnalyseMode = false;
    private static final boolean DEFAULT_Chess960 = false;
    private final ITimeConfig timeCfg = new TimeConfigImpl();
    private UCIOption[] options = new UCIOption[]{new UCIOption<Boolean>("OwnBook", true, "type check default true"), new UCIOption<Boolean>("Ponder", false, "type check default false"), new UCIOption<Boolean>("UCI_AnalyseMode", false, "type check default false"), new UCIOption<Boolean>("UCI_Chess960", false, "type check default false")};
    private String rootSearchImpl_ClassName;
    private Object rootSearchImpl_ConfigObj;

    public UCISearchAdaptorConfig_BaseImpl(String[] args) {
        this.rootSearchImpl_ClassName = args[0];
        this.rootSearchImpl_ConfigObj = ReflectionUtils.createObjectByClassName_StringsConstructor(args[1], Utils.copyOfRange(args, 2));
    }

    @Override
    public ITimeConfig getTimeConfig() {
        return this.timeCfg;
    }

    @Override
    public String getRootSearchClassName() {
        return this.rootSearchImpl_ClassName;
    }

    @Override
    public Object getRootSearchConfig() {
        return this.rootSearchImpl_ConfigObj;
    }

    @Override
    public boolean isOwnBookEnabled() {
        return (Boolean)this.options[0].getValue();
    }

    @Override
    public boolean isPonderingEnabled() {
        return (Boolean)this.options[1].getValue();
    }

    @Override
    public boolean isAnalyzeMode() {
        return (Boolean)this.options[2].getValue();
    }

    @Override
    public boolean isChess960() {
        return (Boolean)this.options[3].getValue();
    }

    @Override
    public void registerProviders(IUCIOptionsRegistry registry) {
        registry.registerProvider(this);
        if (this.rootSearchImpl_ConfigObj instanceof IUCIOptionsProvider) {
            ((IUCIOptionsProvider)this.rootSearchImpl_ConfigObj).registerProviders(registry);
        }
    }

    @Override
    public UCIOption[] getSupportedOptions() {
        return this.options;
    }

    @Override
    public boolean applyOption(UCIOption option) {
        if ("Ponder".equals(option.getName())) {
            return true;
        }
        if ("OwnBook".equals(option.getName())) {
            return true;
        }
        if ("UCI_AnalyseMode".equals(option.getName())) {
            return true;
        }
        if ("UCI_Chess960".equals(option.getName())) {
            BoardUtils.isFRC = this.isChess960();
            return true;
        }
        return false;
    }
}

