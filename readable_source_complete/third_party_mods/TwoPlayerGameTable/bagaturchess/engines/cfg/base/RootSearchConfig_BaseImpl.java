/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.base;

import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.impl.utils.BinarySemaphoreFactory_Dummy;
import bagaturchess.bitboard.impl.utils.ReflectionUtils;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.ISearchConfig_AB;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.api.IUCIOptionsRegistry;
import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptionSpin_Integer;
import bagaturchess.uci.impl.commands.options.UCIOptionString;
import java.io.File;
import java.util.Arrays;

public abstract class RootSearchConfig_BaseImpl
implements IRootSearchConfig,
IUCIOptionsProvider {
    protected static final double MEM_USAGE_TPT = 0.5;
    protected static final double MEM_USAGE_EVALCACHE = 0.35;
    protected static final double MEM_USAGE_PAWNCACHE = 0.15;
    private static final String DEFAULT_TbPath = RootSearchConfig_BaseImpl.getDefaultTBPath();
    private static final boolean DEFAULT_SyzygyOnline = false;
    private static final int DEFAULT_MEM_USAGE_percent = 73;
    private static final boolean DEFAULT_UseTranspositionTable = true;
    private static final boolean DEFAULT_UseEvalCache = true;
    private static final boolean DEFAULT_UseSyzygyDTZCache = true;
    private UCIOption[] options = new UCIOption[]{new UCIOptionSpin_Integer("MemoryUsagePercent", 73, "type spin default 73 min 50 max 90"), new UCIOption<Boolean>("TranspositionTable", true, "type check default true"), new UCIOption<Boolean>("EvalCache", true, "type check default true"), new UCIOptionString("SyzygyPath", DEFAULT_TbPath, "type string default " + DEFAULT_TbPath), new UCIOption<Boolean>("SyzygyOnline", false, "type check default false"), new UCIOption<Boolean>("SyzygyDTZCache", true, "type check default true"), new UCIOptionSpin_Integer("MultiPV", new Integer(1), "type spin default 1 min 1 max 100")};
    private String searchImpl_ClassName;
    private ISearchConfig_AB searchImpl_ConfigObj;
    private IBoardConfig boardCfg;
    private IEvalConfig evalCfg;

    public RootSearchConfig_BaseImpl(String[] args) {
        this.searchImpl_ClassName = args[0];
        String[] searchParams = this.extractParams(args, "-s");
        this.searchImpl_ConfigObj = searchParams == null ? (ISearchConfig_AB)ReflectionUtils.createObjectByClassName_NoArgsConstructor(args[1]) : (ISearchConfig_AB)ReflectionUtils.createObjectByClassName_StringsConstructor(args[1], searchParams);
        if (args.length > 2) {
            String[] boardParams = this.extractParams(args, "-b");
            this.boardCfg = boardParams == null ? (IBoardConfig)ReflectionUtils.createObjectByClassName_NoArgsConstructor(args[2]) : (IBoardConfig)ReflectionUtils.createObjectByClassName_StringsConstructor(args[2], boardParams);
            if (args.length > 3) {
                String[] evalParams = this.extractParams(args, "-e");
                this.evalCfg = evalParams == null ? (IEvalConfig)ReflectionUtils.createObjectByClassName_NoArgsConstructor(args[3]) : (IEvalConfig)ReflectionUtils.createObjectByClassName_StringsConstructor(args[3], evalParams);
            }
        }
    }

    private String[] extractParams(String[] args, String markingPrefix) {
        int i;
        if (args == null) {
            return null;
        }
        int param_start_index = -1;
        int param_end_index = -1;
        for (i = 0; i < args.length; ++i) {
            String curr_arg = args[i];
            if (markingPrefix.equals(curr_arg)) {
                param_start_index = i + 1;
                continue;
            }
            if (param_start_index == -1) continue;
            if (curr_arg == null) {
                param_end_index = i - 1;
                break;
            }
            if (curr_arg.startsWith("-")) {
                param_end_index = i - 1;
                break;
            }
            if (i != args.length - 1) continue;
            param_end_index = args.length - 1;
        }
        if (param_start_index > -1) {
            for (i = param_start_index; i <= param_end_index; ++i) {
                if (args[i].contains("=")) continue;
                throw new IllegalStateException("Index=" + i + ", args[i]=" + args[i]);
            }
        }
        if (param_start_index == -1) {
            return null;
        }
        return Arrays.copyOfRange(args, param_start_index, param_end_index + 1);
    }

    @Override
    public boolean initCaches() {
        return true;
    }

    @Override
    public int getThreadsCount() {
        return 1;
    }

    @Override
    public int getThreadMemory_InMegabytes() {
        throw new IllegalStateException();
    }

    @Override
    public String getSearchClassName() {
        return this.searchImpl_ClassName;
    }

    @Override
    public ISearchConfig_AB getSearchConfig() {
        return this.searchImpl_ConfigObj;
    }

    @Override
    public IBoardConfig getBoardConfig() {
        return this.boardCfg;
    }

    @Override
    public IEvalConfig getEvalConfig() {
        return this.evalCfg;
    }

    @Override
    public String getTbPath() {
        return (String)this.options[3].getValue();
    }

    @Override
    public boolean useOnlineSyzygy() {
        return (Boolean)this.options[4].getValue();
    }

    @Override
    public double getTPTUsagePercent() {
        return 0.5;
    }

    @Override
    public double getEvalCacheUsagePercent() {
        return 0.35;
    }

    @Override
    public double getPawnsCacheUsagePercent() {
        return 0.15;
    }

    @Override
    public int getMultiPVsCount() {
        return (Integer)this.options[6].getValue();
    }

    @Override
    public double get_MEMORY_USAGE_PERCENT() {
        return (float)((Integer)this.options[0].getValue()).intValue() / 100.0f;
    }

    @Override
    public boolean useTPT() {
        return (Boolean)this.options[1].getValue();
    }

    @Override
    public boolean useEvalCache() {
        return (Boolean)this.options[2].getValue();
    }

    @Override
    public boolean useSyzygyDTZCache() {
        return (Boolean)this.options[5].getValue();
    }

    @Override
    public int getHiddenDepth() {
        return 0;
    }

    @Override
    public void registerProviders(IUCIOptionsRegistry registry) {
        registry.registerProvider(this);
        if (this.searchImpl_ConfigObj instanceof IUCIOptionsProvider) {
            registry.registerProvider((IUCIOptionsProvider)((Object)this.searchImpl_ConfigObj));
        }
        if (this.evalCfg instanceof IUCIOptionsProvider) {
            registry.registerProvider((IUCIOptionsProvider)((Object)this.evalCfg));
        }
    }

    @Override
    public UCIOption[] getSupportedOptions() {
        return this.options;
    }

    @Override
    public boolean applyOption(UCIOption option) {
        if ("MultiPV".equals(option.getName())) {
            return true;
        }
        if ("MemoryUsagePercent".equals(option.getName())) {
            return true;
        }
        if ("TranspositionTable".equals(option.getName())) {
            return true;
        }
        if ("EvalCache".equals(option.getName())) {
            return true;
        }
        if ("SyzygyDTZCache".equals(option.getName())) {
            return true;
        }
        if ("SyzygyPath".equals(option.getName())) {
            return true;
        }
        return "SyzygyOnline".equals(option.getName());
    }

    @Override
    public String getBoardFactoryClassName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSemaphoreFactoryClassName() {
        return BinarySemaphoreFactory_Dummy.class.getName();
    }

    private static final String getDefaultTBPath() {
        File work_dir = new File(".");
        if (work_dir.getName().equals("bin")) {
            work_dir = work_dir.getParentFile();
        }
        return work_dir.getAbsolutePath() + File.separatorChar + "data" + File.separatorChar + "egtb";
    }
}

