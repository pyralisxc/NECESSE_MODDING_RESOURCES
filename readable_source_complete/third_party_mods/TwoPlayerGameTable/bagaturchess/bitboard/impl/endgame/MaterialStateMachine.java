/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.endgame;

import bagaturchess.bitboard.impl.endgame.Gaviota;
import java.util.HashSet;
import java.util.Set;

public class MaterialStateMachine {
    private static final char[] SIMBOLS = new char[]{'q', 'r', 'b', 'n', 'p'};

    private static Set<String> generatorOfOneSide(int length) {
        HashSet<String> result = new HashSet<String>();
        MaterialStateMachine.generator_helper("k", 0, result, length);
        return result;
    }

    public static void generator_helper(String currentSignature, int simbolsStartIndex, Set<String> result, int length) {
        if (currentSignature.length() >= length) {
            result.add(currentSignature);
            return;
        }
        if (simbolsStartIndex == SIMBOLS.length) {
            result.add(currentSignature);
            return;
        }
        MaterialStateMachine.generator_helper(currentSignature + SIMBOLS[simbolsStartIndex], simbolsStartIndex, result, length);
        MaterialStateMachine.generator_helper(currentSignature + SIMBOLS[simbolsStartIndex], simbolsStartIndex + 1, result, length);
        MaterialStateMachine.generator_helper(currentSignature, simbolsStartIndex + 1, result, length);
    }

    private static Set<Pair> generatePairs(int maxlength) {
        Set<String> oneSide = MaterialStateMachine.generatorOfOneSide(maxlength - 1);
        String[] oneSideStrs = new String[oneSide.size()];
        int k = 0;
        for (String cur : oneSide) {
            oneSideStrs[k++] = cur;
        }
        HashSet<Pair> result = new HashSet<Pair>();
        for (int i = 0; i < oneSideStrs.length; ++i) {
            for (int j = 0; j < oneSideStrs.length; ++j) {
                String all;
                String first = oneSideStrs[i];
                String second = oneSideStrs[j];
                long firstScores = MaterialStateMachine.getScores(first);
                long secondScores = MaterialStateMachine.getScores(second);
                if (secondScores > firstScores) {
                    String tmp = second;
                    second = first;
                    first = tmp;
                }
                if ((all = first + second).length() <= 2 || all.length() > maxlength) continue;
                result.add(new Pair(first, second));
                System.out.println("first=" + first + ", second=" + second);
            }
        }
        return result;
    }

    private static long getScores(String source) {
        long result = (long)Math.pow(10.0, source.length());
        for (int i = 0; i < SIMBOLS.length; ++i) {
            char cur = SIMBOLS[i];
            int count = MaterialStateMachine.count(source, cur);
            result = (long)((double)result + Math.pow(2 * count, SIMBOLS.length - i));
        }
        return result;
    }

    private static int count(String source, char what) {
        int count = 0;
        for (int i = 0; i < source.length(); ++i) {
            if (source.charAt(i) != what) continue;
            ++count;
        }
        return count;
    }

    public static void main(String[] args) {
        Set<Pair> result = MaterialStateMachine.generatePairs(5);
        HashSet<String> result_str = new HashSet<String>();
        for (Pair pair : result) {
            result_str.add(pair.toString());
        }
        for (String string : Gaviota.names_man345_set) {
            if (result_str.contains(string)) continue;
            System.out.println("NOTFOUND> " + string);
        }
        System.out.println(result_str.size() + " " + Gaviota.names_man345_set.size());
    }

    private static class Pair {
        private String first;
        private String second;

        public Pair(String _first, String _second) {
            this.first = _first;
            this.second = _second;
        }

        public int hashCode() {
            return this.first.hashCode() + this.second.hashCode();
        }

        public boolean equals(Object obj) {
            Pair other = (Pair)obj;
            return this.first.equals(other.first) && this.second.equals(other.second);
        }

        public String toString() {
            return this.first + this.second;
        }
    }
}

