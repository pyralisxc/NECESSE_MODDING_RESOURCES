/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.egtb.syzygy;

import bagaturchess.egtb.syzygy.OnlineSyzygy;
import java.util.ArrayList;

public class JSONUtils {
    public static String extractFirstJSONArray(OnlineSyzygy.Logger logger, String json_text) {
        int start_index = json_text.indexOf("[");
        if (start_index == -1) {
            return null;
        }
        int end_index = json_text.indexOf("]", start_index);
        if (end_index == -1) {
            return null;
        }
        String attribute_value = json_text.substring(start_index, end_index + 1);
        return attribute_value;
    }

    public static String[] extractJSONArrayElements(OnlineSyzygy.Logger logger, String json_array) {
        ArrayList<String> array_elements_list = new ArrayList<String>();
        char[] chars = json_array.toCharArray();
        block0: for (int i = 0; i < chars.length; ++i) {
            char cur_char1 = chars[i];
            if (cur_char1 != '{') continue;
            for (int j = i; j < chars.length; ++j) {
                char cur_char2 = chars[j];
                if (cur_char2 != '}') continue;
                array_elements_list.add(json_array.substring(i, j + 1));
                i = j;
                continue block0;
            }
        }
        return array_elements_list.toArray(new String[0]);
    }

    public static String extractJSONAttribute(OnlineSyzygy.Logger logger, String json_object, String attribute_name) {
        int start_index = json_object.indexOf(attribute_name);
        if (start_index == -1) {
            return null;
        }
        logger.addText("OnlineSyzygy.extractJSONAttribute: attribute_name=" + attribute_name + " found");
        int possible_end_index1 = json_object.indexOf(",", start_index);
        int possible_end_index2 = json_object.indexOf("}", start_index);
        int end_index = 0;
        if (possible_end_index1 != -1 && possible_end_index2 != -1) {
            end_index = Math.min(possible_end_index1, possible_end_index2);
        } else if (possible_end_index1 != -1) {
            end_index = possible_end_index1;
        } else if (possible_end_index2 != -1) {
            end_index = possible_end_index2;
        } else {
            return null;
        }
        String attribute_value = json_object.substring(start_index + attribute_name.length(), end_index);
        logger.addText("OnlineSyzygy.extractJSONAttribute: attribute_value=" + attribute_value);
        return attribute_value;
    }
}

