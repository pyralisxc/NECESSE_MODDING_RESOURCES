/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.common;

import bagaturchess.bitboard.impl.Constants;

public class Fen
extends Constants {
    private int colourToMove;
    private boolean whiteKingSide = false;
    private boolean whiteQueenSide = false;
    private boolean blackKingSide = false;
    private boolean blackQueenSide = false;
    private String enpassantTargetSquare;
    private String halfmoveClock;
    private String fullmoveNumber;
    private int[] board = new int[64];

    public static final Fen parse(String fenStr) {
        return new Fen(fenStr);
    }

    public Fen(String fen) {
        StringBuilder buffer = new StringBuilder(fen);
        this.parse(buffer);
    }

    private void parse(StringBuilder buffer) {
        int endIndex = this.parsePiecePlacement(buffer);
        if (++endIndex >= buffer.length()) {
            throw new IllegalStateException("Invalid fen: no active colour field");
        }
        char cur = buffer.charAt(endIndex);
        if (cur == 'w') {
            this.colourToMove = 0;
        } else if (cur == 'b') {
            this.colourToMove = 1;
        } else {
            throw new IllegalStateException("Invalid fen: char='" + cur + "' in active colour field");
        }
        ++endIndex;
        if (++endIndex < buffer.length()) {
            cur = buffer.charAt(endIndex);
            if (cur != '-') {
                while (endIndex < buffer.length() && (cur = buffer.charAt(endIndex)) > ' ') {
                    switch (cur) {
                        case 'K': {
                            this.whiteKingSide = true;
                            break;
                        }
                        case 'Q': {
                            this.whiteQueenSide = true;
                            break;
                        }
                        case 'k': {
                            this.blackKingSide = true;
                            break;
                        }
                        case 'q': {
                            this.blackQueenSide = true;
                            break;
                        }
                        default: {
                            throw new IllegalStateException("Invalid fen: char='" + cur + "' in castling availability field");
                        }
                    }
                    ++endIndex;
                }
            } else {
                ++endIndex;
            }
            if (++endIndex < buffer.length()) {
                int startIndex = endIndex;
                cur = buffer.charAt(endIndex);
                if (cur != '-') {
                    while (endIndex < buffer.length() && (cur = buffer.charAt(endIndex)) > ' ') {
                        ++endIndex;
                    }
                    this.enpassantTargetSquare = buffer.substring(startIndex, endIndex);
                } else {
                    ++endIndex;
                }
                if (++endIndex < buffer.length()) {
                    startIndex = endIndex;
                    cur = buffer.charAt(endIndex);
                    if (cur != '-') {
                        while (endIndex < buffer.length() && (cur = buffer.charAt(endIndex)) > ' ') {
                            ++endIndex;
                        }
                        this.halfmoveClock = buffer.substring(startIndex, endIndex);
                    }
                    if (++endIndex < buffer.length()) {
                        startIndex = endIndex;
                        cur = buffer.charAt(endIndex);
                        if (cur != '-') {
                            while (endIndex < buffer.length() && (cur = buffer.charAt(endIndex)) > ' ') {
                                ++endIndex;
                            }
                            this.fullmoveNumber = buffer.substring(startIndex, buffer.length()).trim();
                        }
                    }
                }
            }
        }
    }

    private int parsePiecePlacement(StringBuilder buffer) {
        char cur;
        int letter = 0;
        int digit = 7;
        int endIndex = 0;
        while ((cur = buffer.charAt(endIndex)) > ' ') {
            block30: {
                block29: {
                    if (cur == '/') {
                        --digit;
                        letter = 0;
                        if (++endIndex < buffer.length()) continue;
                        throw new IllegalStateException("Invalid fen: no space char after data field");
                    }
                    if (!Character.isDigit(cur)) break block29;
                    switch (cur) {
                        case '0': {
                            throw new IllegalStateException("Invalid fen: char='" + cur + "' in data field");
                        }
                        case '1': {
                            ++letter;
                            break block30;
                        }
                        case '2': {
                            letter += 2;
                            break block30;
                        }
                        case '3': {
                            letter += 3;
                            break block30;
                        }
                        case '4': {
                            letter += 4;
                            break block30;
                        }
                        case '5': {
                            letter += 5;
                            break block30;
                        }
                        case '6': {
                            letter += 6;
                            break block30;
                        }
                        case '7': {
                            letter += 7;
                            break block30;
                        }
                        case '8': {
                            letter += 8;
                            break block30;
                        }
                        default: {
                            throw new IllegalStateException("Invalid fen: char='" + cur + "' in data field");
                        }
                    }
                }
                if (Character.isLetter(cur)) {
                    int l = letter++;
                    int d = digit;
                    int square = 8 * d + l;
                    switch (cur) {
                        case 'p': {
                            this.board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][1];
                            break;
                        }
                        case 'n': {
                            this.board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][2];
                            ++letter;
                            break;
                        }
                        case 'b': {
                            this.board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][3];
                            ++letter;
                            break;
                        }
                        case 'r': {
                            this.board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][4];
                            ++letter;
                            break;
                        }
                        case 'q': {
                            this.board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][5];
                            ++letter;
                            break;
                        }
                        case 'k': {
                            this.board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[1][6];
                            ++letter;
                            break;
                        }
                        case 'P': {
                            this.board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][1];
                            ++letter;
                            break;
                        }
                        case 'N': {
                            this.board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][2];
                            ++letter;
                            break;
                        }
                        case 'B': {
                            this.board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][3];
                            ++letter;
                            break;
                        }
                        case 'R': {
                            this.board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][4];
                            ++letter;
                            break;
                        }
                        case 'Q': {
                            this.board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][5];
                            ++letter;
                            break;
                        }
                        case 'K': {
                            this.board[square] = COLOUR_AND_TYPE_2_PIECE_IDENTITY[0][6];
                            ++letter;
                            break;
                        }
                        default: {
                            throw new IllegalStateException("Invalid fen: char='" + cur + "' in data field");
                        }
                    }
                } else {
                    throw new IllegalStateException("Invalid fen: char='" + cur + "' in data field");
                }
            }
            if (++endIndex < buffer.length()) continue;
            throw new IllegalStateException("Invalid fen: no space char after data field");
        }
        return endIndex;
    }

    public boolean hasBlackKingSide() {
        return this.blackKingSide;
    }

    public boolean hasBlackQueenSide() {
        return this.blackQueenSide;
    }

    public int[] getBoardArray() {
        return this.board;
    }

    public int getColourToMove() {
        return this.colourToMove;
    }

    public boolean hasWhiteKingSide() {
        return this.whiteKingSide;
    }

    public boolean hasWhiteQueenSide() {
        return this.whiteQueenSide;
    }

    public String getEnpassantTargetSquare() {
        return this.enpassantTargetSquare;
    }

    public String getFullmoveNumber() {
        return this.fullmoveNumber;
    }

    public String getHalfmoveClock() {
        return this.halfmoveClock;
    }

    public String toString() {
        Object result = "";
        result = (String)result + "active colour   = " + this.getColourToMove() + "\r\n";
        result = (String)result + "castling        = " + this.hasWhiteKingSide() + " " + this.hasWhiteQueenSide() + " " + this.hasBlackKingSide() + " " + this.hasBlackQueenSide() + "\r\n";
        result = (String)result + "enpassantSquare = '" + this.getEnpassantTargetSquare() + "'\r\n";
        result = (String)result + "halfmoveClock   = '" + this.getHalfmoveClock() + "'\r\n";
        result = (String)result + "fullmoveNumber  = '" + this.getFullmoveNumber() + "'\r\n";
        return result;
    }
}

