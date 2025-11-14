/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.base;

import bagaturchess.search.api.IExtensionMode;
import bagaturchess.search.api.ISearchConfig_AB;
import bagaturchess.uci.api.IUCIOptionsProvider;
import bagaturchess.uci.api.IUCIOptionsRegistry;
import bagaturchess.uci.impl.commands.options.UCIOption;
import bagaturchess.uci.impl.commands.options.UCIOptionCombo;

public class SearchConfigImpl_AB
implements ISearchConfig_AB,
IUCIOptionsProvider {
    private UCIOption[] options = new UCIOption[]{new UCIOptionCombo("Opening Mode", "most played first", "type combo default most played first var most played first var random intermediate var random full")};
    private static final int MAX_INDEX = 200;
    private IExtensionMode mode = IExtensionMode.DYNAMIC;
    private int dynamicExt_UpdateInterval = 1000;
    public int extension_CheckInPV = 0;
    public int extension_SingleReplyInPV = 0;
    public int extension_WinCapNonPawnInPV = 0;
    public int extension_WinCapPawnInPV = 0;
    public int extension_RecapturePV = 0;
    public int extension_PasserPushPV = 0;
    public int extension_PromotionPV = 0;
    public int extension_MateThreatPV = 0;
    public int extension_MoveEvalPV = 0;
    public boolean extension_MateLeafPV = false;
    public int extension_CheckInNonPV = 0;
    public int extension_SingleReplyInNonPV = 0;
    public int extension_WinCapNonPawnInNonPV = 0;
    public int extension_WinCapPawnInNonPV = 0;
    public int extension_RecaptureNonPV = 0;
    public int extension_PasserPushNonPV = 0;
    public int extension_PromotionNonPV = 0;
    public int extension_MateThreatNonPV = 0;
    public int extension_MoveEvalNonPV = 0;
    public boolean extension_MateLeafNonPV = false;
    public int reduction_LMRRootIndex1 = 1;
    public int reduction_LMRRootIndex2 = 1;
    public int reduction_LMRPVIndex1 = 1;
    public int reduction_LMRPVIndex2 = 1;
    public int reduction_LMRNonPVIndex1 = 0;
    public int reduction_LMRNonPVIndex2 = 1;
    public boolean reduction_ReduceCapturesInLMR = false;
    public boolean reduction_ReduceHistoryMovesInLMR = true;
    public boolean reduction_ReduceHighEvalMovesInLMR = true;
    public int pruning_StaticPVIndex = 1;
    public int pruning_StaticNonPVIndex = 1;
    public boolean pruning_NullMove = true;
    public boolean pruning_Razoring = false;
    public boolean prunning_MateDistance = true;
    public boolean IID_PV = true;
    public boolean IID_NonPV = true;
    public boolean other_SingleBestmove = false;
    public boolean other_StoreTPTInQsearch = true;
    public boolean other_UseCheckInQSearch = true;
    public boolean other_UsePVHistory = true;
    public boolean other_UseSeeInQSearch = true;
    public boolean other_UseTPTInRoot = true;
    public boolean other_UseTPTScoresNonPV = true;
    public boolean other_UseTPTScoresPV = true;
    public boolean other_UseTPTScoresQsearchPV = true;
    public int orderingWeight_TPT_MOVE = 1;
    public int orderingWeight_MATE_MOVE = 1;
    public int orderingWeight_COUNTER = 1;
    public int orderingWeight_WIN_CAP = 1;
    public int orderingWeight_PREV_BEST_MOVE = 1;
    public int orderingWeight_EQ_CAP = 1;
    public int orderingWeight_MATE_KILLER = 1;
    public int orderingWeight_PREVPV_MOVE = 1;
    public int orderingWeight_CASTLING = 1;
    public int orderingWeight_PASSER_PUSH = 1;
    public int orderingWeight_KILLER = 1;
    public int orderingWeight_LOSE_CAP = 1;

    public SearchConfigImpl_AB() {
    }

    public SearchConfigImpl_AB(String[] args) {
    }

    @Override
    public int getExtension_MoveEvalPV() {
        return this.extension_MoveEvalPV;
    }

    @Override
    public int getExtension_MoveEvalNonPV() {
        return this.extension_MoveEvalNonPV;
    }

    public static int getMAX_INDEX() {
        return 200;
    }

    public static int getPLY() {
        return 16;
    }

    public int getPly() {
        return 16;
    }

    @Override
    public IExtensionMode getExtensionMode() {
        return this.mode;
    }

    @Override
    public int getDynamicExtUpdateInterval() {
        return this.dynamicExt_UpdateInterval;
    }

    @Override
    public int getExtension_CheckInNonPV() {
        return this.extension_CheckInNonPV;
    }

    @Override
    public int getExtension_CheckInPV() {
        return this.extension_CheckInPV;
    }

    @Override
    public int getExtension_MateThreatPV() {
        return this.extension_MateThreatPV;
    }

    @Override
    public int getExtension_MateThreatNonPV() {
        return this.extension_MateThreatNonPV;
    }

    @Override
    public int getExtension_SingleReplyInNonPV() {
        return this.extension_SingleReplyInNonPV;
    }

    @Override
    public int getExtension_SingleReplyInPV() {
        return this.extension_SingleReplyInPV;
    }

    @Override
    public int getExtension_WinCapNonPawnInNonPV() {
        return this.extension_WinCapNonPawnInNonPV;
    }

    @Override
    public int getExtension_WinCapNonPawnInPV() {
        return this.extension_WinCapNonPawnInPV;
    }

    @Override
    public int getExtension_WinCapPawnInNonPV() {
        return this.extension_WinCapPawnInNonPV;
    }

    @Override
    public int getExtension_WinCapPawnInPV() {
        return this.extension_WinCapPawnInPV;
    }

    @Override
    public boolean isIID_NonPV() {
        return this.IID_NonPV;
    }

    @Override
    public boolean isIID_PV() {
        return this.IID_PV;
    }

    @Override
    public boolean isOther_SingleBestmove() {
        return this.other_SingleBestmove;
    }

    @Override
    public boolean isOther_StoreTPTInQsearch() {
        return this.other_StoreTPTInQsearch;
    }

    @Override
    public boolean isPruning_NullMove() {
        return this.pruning_NullMove;
    }

    @Override
    public boolean isPruning_Razoring() {
        return this.pruning_Razoring;
    }

    @Override
    public int getPruning_StaticNonPVIndex() {
        return this.pruning_StaticNonPVIndex;
    }

    @Override
    public int getPruning_StaticPVIndex() {
        return this.pruning_StaticPVIndex;
    }

    @Override
    public int getReduction_LMRNonPVIndex1() {
        return this.reduction_LMRNonPVIndex1;
    }

    @Override
    public int getReduction_LMRNonPVIndex2() {
        return this.reduction_LMRNonPVIndex2;
    }

    @Override
    public int getReduction_LMRPVIndex1() {
        return this.reduction_LMRPVIndex1;
    }

    @Override
    public int getReduction_LMRPVIndex2() {
        return this.reduction_LMRPVIndex2;
    }

    @Override
    public int getReduction_LMRRootIndex1() {
        return this.reduction_LMRRootIndex1;
    }

    @Override
    public int getReduction_LMRRootIndex2() {
        return this.reduction_LMRRootIndex2;
    }

    @Override
    public int getExtension_PasserPushPV() {
        return this.extension_PasserPushPV;
    }

    @Override
    public int getExtension_PromotionPV() {
        return this.extension_PromotionPV;
    }

    @Override
    public int getExtension_PasserPushNonPV() {
        return this.extension_PasserPushNonPV;
    }

    @Override
    public int getExtension_PromotionNonPV() {
        return this.extension_PromotionNonPV;
    }

    @Override
    public int getExtension_RecaptureNonPV() {
        return this.extension_RecaptureNonPV;
    }

    @Override
    public int getExtension_RecapturePV() {
        return this.extension_RecapturePV;
    }

    @Override
    public boolean isOther_UseCheckInQSearch() {
        return this.other_UseCheckInQSearch;
    }

    @Override
    public boolean isOther_UsePVHistory() {
        return this.other_UsePVHistory;
    }

    @Override
    public boolean isOther_UseSeeInQSearch() {
        return this.other_UseSeeInQSearch;
    }

    @Override
    public boolean isOther_UseTPTInRoot() {
        return this.other_UseTPTInRoot;
    }

    @Override
    public boolean isOther_UseTPTScoresNonPV() {
        return this.other_UseTPTScoresNonPV;
    }

    @Override
    public boolean isOther_UseTPTScoresPV() {
        return this.other_UseTPTScoresPV;
    }

    @Override
    public boolean isOther_UseTPTScoresQsearchPV() {
        return this.other_UseTPTScoresQsearchPV;
    }

    @Override
    public boolean isPrunning_MateDistance() {
        return this.prunning_MateDistance;
    }

    @Override
    public boolean isReduction_ReduceCapturesInLMR() {
        return this.reduction_ReduceCapturesInLMR;
    }

    @Override
    public boolean isReduction_ReduceHistoryMovesInLMR() {
        return this.reduction_ReduceHistoryMovesInLMR;
    }

    @Override
    public boolean isExtension_MateLeafNonPV() {
        return this.extension_MateLeafNonPV;
    }

    @Override
    public boolean isExtension_MateLeafPV() {
        return this.extension_MateLeafPV;
    }

    @Override
    public int getOrderingWeight_CASTLING() {
        return this.orderingWeight_CASTLING;
    }

    @Override
    public int getOrderingWeight_COUNTER() {
        return this.orderingWeight_COUNTER;
    }

    @Override
    public int getOrderingWeight_EQ_CAP() {
        return this.orderingWeight_EQ_CAP;
    }

    @Override
    public int getOrderingWeight_KILLER() {
        return this.orderingWeight_KILLER;
    }

    @Override
    public int getOrderingWeight_LOSE_CAP() {
        return this.orderingWeight_LOSE_CAP;
    }

    @Override
    public int getOrderingWeight_MATE_KILLER() {
        return this.orderingWeight_MATE_KILLER;
    }

    @Override
    public int getOrderingWeight_MATE_MOVE() {
        return this.orderingWeight_MATE_MOVE;
    }

    @Override
    public int getOrderingWeight_PASSER_PUSH() {
        return this.orderingWeight_PASSER_PUSH;
    }

    @Override
    public int getOrderingWeight_PREV_BEST_MOVE() {
        return this.orderingWeight_PREV_BEST_MOVE;
    }

    @Override
    public int getOrderingWeight_PREVPV_MOVE() {
        return this.orderingWeight_PREVPV_MOVE;
    }

    @Override
    public int getOrderingWeight_TPT_MOVE() {
        return this.orderingWeight_TPT_MOVE;
    }

    @Override
    public int getOrderingWeight_WIN_CAP() {
        return this.orderingWeight_WIN_CAP;
    }

    @Override
    public boolean isReduction_ReduceHighEvalMovesInLMR() {
        return this.reduction_ReduceHighEvalMovesInLMR;
    }

    @Override
    public boolean randomizeMoveLists() {
        return true;
    }

    @Override
    public boolean sortMoveLists() {
        return true;
    }

    @Override
    public UCIOption[] getSupportedOptions() {
        return this.options;
    }

    @Override
    public int getOpeningBook_Mode() {
        int openingBook_Mode = 3;
        if (((String)this.options[0].getValue()).equals("most played first")) {
            openingBook_Mode = 3;
        } else if (((String)this.options[0].getValue()).equals("random intermediate")) {
            openingBook_Mode = 2;
        } else if (((String)this.options[0].getValue()).equals("random full")) {
            openingBook_Mode = 1;
        } else {
            throw new IllegalStateException("Opening Modeset to illegal value = " + String.valueOf(this.options[0].getValue()));
        }
        return openingBook_Mode;
    }

    @Override
    public boolean applyOption(UCIOption option) {
        return !"Search [Use TPT scores in PV Nodes]".equals(option.getName()) && "Opening Mode".equals(option.getName());
    }

    @Override
    public void registerProviders(IUCIOptionsRegistry registry) {
    }

    @Override
    public int getTPTUsageDepthCut() {
        return 0;
    }

    @Override
    public boolean isOther_UseTPTScores() {
        return true;
    }

    @Override
    public boolean isOther_UseAlphaOptimizationInQSearch() {
        return true;
    }
}

