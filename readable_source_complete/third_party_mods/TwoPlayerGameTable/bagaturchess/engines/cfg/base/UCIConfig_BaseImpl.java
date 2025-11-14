/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.base;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.uci.api.IUCIConfig;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.api.IUCIOptionsRegistry;
import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptionCombo;

public class UCIConfig_BaseImpl
implements IUCIConfig {
    private String searchAdaptorImpl_ClassName;
    private Object searchAdaptorImpl_ConfigObj;
    private static final String DEFAULT_loggingPolicy = "single file";
    private UCIOption[] options = new UCIOption[]{new UCIOptionCombo("Logging Policy", "single file", "type combo default single file var single file var multiple files var none")};

    public UCIConfig_BaseImpl(String[] args) {
        this.searchAdaptorImpl_ClassName = args[0];
        this.searchAdaptorImpl_ConfigObj = ReflectionUtils.createObjectByClassName_StringsConstructor(args[1], Utils.copyOfRange(args, 2));
    }

    @Override
    public String getUCIAdaptor_ClassName() {
        return this.searchAdaptorImpl_ClassName;
    }

    @Override
    public Object getUCIAdaptor_ConfigObj() {
        return this.searchAdaptorImpl_ConfigObj;
    }

    @Override
    public void registerProviders(IUCIOptionsRegistry registry) {
        registry.registerProvider(this);
        if (this.searchAdaptorImpl_ConfigObj instanceof IUCIOptionsProvider) {
            ((IUCIOptionsProvider)this.searchAdaptorImpl_ConfigObj).registerProviders(registry);
        }
    }

    @Override
    public UCIOption[] getSupportedOptions() {
        return this.options;
    }

    @Override
    public boolean applyOption(UCIOption option) {
        return "Logging Policy".equals(option.getName());
    }

    @Override
    public String getUCIAdaptor_LoggingPolicy() {
        return (String)this.options[0].getValue();
    }
}

