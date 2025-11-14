/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.egtb.syzygy;

import bagaturchess.bitboard.api.BoardUtils;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.utils.VarStatistic;
import bagaturchess.egtb.syzygy.JSONUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class OnlineSyzygy {
    private static final String CHARSET_ENCODING = "UTF-8";
    private static long last_server_response_timestamp = 0L;
    private static int MAX_powerof2_for_waiting_time = 7;
    private static int current_powerof2_for_waiting_time = 0;
    private static final VarStatistic stat_response_times = new VarStatistic();
    private static final VarStatistic stat_waiting_times = new VarStatistic();

    private static final int getWaitingTimeBetweenRequests() {
        return (int)(Math.pow(2.0, current_powerof2_for_waiting_time) * OnlineSyzygy.minimalPossibleWaitingTime());
    }

    private static double minimalPossibleWaitingTime() {
        return Math.max(15.0, stat_response_times.getEntropy() + stat_response_times.getDisperse());
    }

    public static final String getDTZandDTM_BlockingOnSocketConnection(String fen, int colour_to_move, long timeToThinkInMiliseconds, int[] dtz_and_dtm, Logger logger) {
        if ((double)timeToThinkInMiliseconds < OnlineSyzygy.minimalPossibleWaitingTime()) {
            return null;
        }
        if (System.currentTimeMillis() <= last_server_response_timestamp + (long)OnlineSyzygy.getWaitingTimeBetweenRequests()) {
            return null;
        }
        dtz_and_dtm[0] = -1;
        dtz_and_dtm[1] = -1;
        last_server_response_timestamp = System.currentTimeMillis();
        String bestmove_string = null;
        String url_for_the_request = "http://tablebase.lichess.ovh/standard?fen=" + fen;
        try {
            String game_category_string;
            String dtm_string;
            String server_response_json_text = OnlineSyzygy.getHTMLFromURL(url_for_the_request, logger);
            logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: json_response_text=" + server_response_json_text);
            if (--current_powerof2_for_waiting_time < 0) {
                current_powerof2_for_waiting_time = 0;
            }
            stat_waiting_times.addValue(OnlineSyzygy.getWaitingTimeBetweenRequests());
            logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: current_powerof2_for_waiting_time set to " + current_powerof2_for_waiting_time);
            stat_response_times.addValue(System.currentTimeMillis() - last_server_response_timestamp);
            logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: stat_waiting_times: AVG=" + stat_waiting_times.getEntropy() + " ms, STDEV=" + stat_waiting_times.getDisperse() + " ms, MAX=" + stat_waiting_times.getMaxVal() + " ms");
            logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: stat_response_time: AVG=" + stat_response_times.getEntropy() + " ms, STDEV=" + stat_response_times.getDisperse() + " ms, MAX=" + stat_response_times.getMaxVal() + " ms");
            String dtz_string = JSONUtils.extractJSONAttribute(logger, server_response_json_text, "\"dtz\":");
            if (dtz_string != null && !dtz_string.equals("null")) {
                try {
                    dtz_and_dtm[0] = Integer.parseInt(dtz_string);
                }
                catch (NumberFormatException nfe) {
                    logger.addException(nfe);
                }
            }
            if ((dtm_string = JSONUtils.extractJSONAttribute(logger, server_response_json_text, "\"dtm\":")) != null && !dtm_string.equals("null")) {
                try {
                    dtz_and_dtm[1] = Integer.parseInt(dtm_string);
                }
                catch (NumberFormatException nfe) {
                    logger.addException(nfe);
                }
            }
            if ((game_category_string = JSONUtils.extractJSONAttribute(logger, server_response_json_text, "\"category\":")) != null && !game_category_string.equals("\"unknown\"")) {
                if (game_category_string.equals("\"win\"") || game_category_string.equals("\"blessed-loss\"") || game_category_string.equals("\"draw\"")) {
                    String array_element;
                    String first_array_string = JSONUtils.extractFirstJSONArray(logger, server_response_json_text);
                    String[] array_elements = JSONUtils.extractJSONArrayElements(logger, first_array_string);
                    if (array_elements.length > 0 && (bestmove_string = JSONUtils.extractJSONAttribute(logger, array_element = array_elements[0], "\"uci\":")) != null) {
                        bestmove_string = bestmove_string.replace("\"", "");
                        logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: bestmove_string=" + bestmove_string);
                    }
                } else {
                    logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: skiped category: " + game_category_string);
                }
            }
        }
        catch (Exception e) {
            logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: " + e.getMessage());
            if (++current_powerof2_for_waiting_time > MAX_powerof2_for_waiting_time) {
                current_powerof2_for_waiting_time = MAX_powerof2_for_waiting_time;
            }
            stat_waiting_times.addValue(OnlineSyzygy.getWaitingTimeBetweenRequests());
            logger.addText("OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection: current_powerof2_for_waiting_time set to " + current_powerof2_for_waiting_time);
        }
        return bestmove_string;
    }

    public static final String getWDL_BlockingOnSocketConnection(String fen, int colour_to_move, long timeToThinkInMiliseconds, int[] result, Logger logger) {
        String bestmove_string;
        block13: {
            if ((double)timeToThinkInMiliseconds < OnlineSyzygy.minimalPossibleWaitingTime()) {
                return null;
            }
            if (System.currentTimeMillis() <= last_server_response_timestamp + (long)OnlineSyzygy.getWaitingTimeBetweenRequests()) {
                return null;
            }
            result[0] = -1;
            result[1] = -1;
            last_server_response_timestamp = System.currentTimeMillis();
            bestmove_string = null;
            String url_for_the_request_mainline = "http://tablebase.lichess.ovh/standard/mainline?fen=" + fen;
            try {
                String first_array_string;
                String[] array_elements;
                String server_response_json_text = OnlineSyzygy.getHTMLFromURL(url_for_the_request_mainline, logger);
                logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: server_response_json_text=" + server_response_json_text);
                if (--current_powerof2_for_waiting_time < 0) {
                    current_powerof2_for_waiting_time = 0;
                }
                stat_waiting_times.addValue(OnlineSyzygy.getWaitingTimeBetweenRequests());
                logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: current_powerof2_for_waiting_time set to " + current_powerof2_for_waiting_time);
                stat_response_times.addValue(System.currentTimeMillis() - last_server_response_timestamp);
                logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: stat_waiting_times: AVG=" + stat_waiting_times.getEntropy() + " ms, STDEV=" + stat_waiting_times.getDisperse() + " ms, MAX=" + stat_waiting_times.getMaxVal() + " ms");
                logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: stat_response_times: AVG=" + stat_response_times.getEntropy() + " ms, STDEV=" + stat_response_times.getDisperse() + " ms, MAX=" + stat_response_times.getMaxVal() + " ms");
                String winner_string = JSONUtils.extractJSONAttribute(logger, server_response_json_text, "\"winner\":");
                if (winner_string == null) break block13;
                int winner_color = -1;
                if (winner_string.equals("\"w\"")) {
                    winner_color = 0;
                } else if (winner_string.equals("\"b\"")) {
                    winner_color = 1;
                }
                result[0] = winner_color;
                if (result[0] != colour_to_move || (array_elements = JSONUtils.extractJSONArrayElements(logger, first_array_string = JSONUtils.extractFirstJSONArray(logger, server_response_json_text))).length <= 0) break block13;
                String array_element = array_elements[0];
                String dtz_string = JSONUtils.extractJSONAttribute(logger, array_element, "\"dtz\":");
                if (dtz_string != null) {
                    try {
                        int dtz = Integer.parseInt(dtz_string);
                        dtz += dtz > 0 ? 1 : -1;
                        result[1] = dtz = -dtz;
                    }
                    catch (NumberFormatException nfe) {
                        logger.addException(nfe);
                    }
                }
                if ((bestmove_string = JSONUtils.extractJSONAttribute(logger, array_element, "\"uci\":")) != null) {
                    bestmove_string = bestmove_string.replace("\"", "");
                    logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: bestmove_string=" + bestmove_string);
                }
            }
            catch (Exception e) {
                logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: " + e.getMessage());
                if (++current_powerof2_for_waiting_time > MAX_powerof2_for_waiting_time) {
                    current_powerof2_for_waiting_time = MAX_powerof2_for_waiting_time;
                }
                stat_waiting_times.addValue(OnlineSyzygy.getWaitingTimeBetweenRequests());
                logger.addText("OnlineSyzygy.getWDL_BlockingOnSocketConnection: current_powerof2_for_waiting_time set to " + current_powerof2_for_waiting_time);
            }
        }
        return bestmove_string;
    }

    private static String getHTMLFromURL(String urlToRead, Logger logger) throws Exception {
        logger.addText("OnlineSyzygy.getHTMLFromURL: open and read stream of connection " + urlToRead);
        URL current_request_url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection)current_request_url.openConnection();
        conn.setConnectTimeout(300000);
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestMethod("GET");
        byte[] bytes = OnlineSyzygy.readAllBytes(conn);
        current_request_url = null;
        String html = new String(bytes, Charset.forName(CHARSET_ENCODING));
        return html;
    }

    private static byte[] readAllBytes(HttpURLConnection conn) throws IOException {
        InputStream inputStream = conn.getInputStream();
        int bufLen = 4096;
        byte[] buf = new byte[4096];
        try {
            int readLen;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while ((readLen = inputStream.read(buf, 0, 4096)) != -1) {
                outputStream.write(buf, 0, readLen);
            }
            byte[] byArray = outputStream.toByteArray();
            return byArray;
        }
        catch (IOException e) {
            throw e;
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
            conn.disconnect();
        }
    }

    public static void main(String[] args) {
        IBitBoard board = BoardUtils.createBoard_WithPawnsCache("3k4/8/8/8/8/8/3P4/3K4 w - -");
        for (int counter = 0; counter < 100; ++counter) {
            System.out.println("Try " + (counter + 1));
            String fen = board.toEPD().replace(' ', '_');
            int[] dtz_and_dtm = new int[2];
            String best_move = OnlineSyzygy.getDTZandDTM_BlockingOnSocketConnection(fen, board.getColourToMove(), 500L, dtz_and_dtm, new Logger(){

                @Override
                public void addText(String message) {
                    System.out.println(message);
                }

                @Override
                public void addException(Exception exception) {
                    exception.printStackTrace();
                }
            });
            System.out.println("dtz=" + dtz_and_dtm[0] + ", best_move=" + best_move + ", dtm=" + dtz_and_dtm[1]);
            try {
                System.out.println("Waiting " + OnlineSyzygy.getWaitingTimeBetweenRequests() + " ms");
                Thread.sleep(OnlineSyzygy.getWaitingTimeBetweenRequests());
                continue;
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
    }

    static {
        for (int i = 0; i < 10; ++i) {
            stat_response_times.addValue(111.0);
        }
    }

    public static interface Logger {
        public void addText(String var1);

        public void addException(Exception var1);
    }
}

