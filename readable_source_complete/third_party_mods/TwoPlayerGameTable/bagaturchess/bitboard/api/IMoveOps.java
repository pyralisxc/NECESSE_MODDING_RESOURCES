/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

public interface IMoveOps {
    public boolean isCapture(int var1);

    public boolean isPromotion(int var1);

    public boolean isCaptureOrPromotion(int var1);

    public boolean isEnpassant(int var1);

    public boolean isCastling(int var1);

    public boolean isCastlingKingSide(int var1);

    public boolean isCastlingQueenSide(int var1);

    public int getFigurePID(int var1);

    public int getToFieldID(int var1);

    public int getToField_File(int var1);

    public int getToField_Rank(int var1);

    public int getFromFieldID(int var1);

    public int getFromField_File(int var1);

    public int getFromField_Rank(int var1);

    public int getFigureType(int var1);

    public int getCapturedFigureType(int var1);

    public int getPromotionFigureType(int var1);

    public String moveToString(int var1);

    public void moveToString(int var1, StringBuilder var2);

    public int stringToMove(String var1);
}

