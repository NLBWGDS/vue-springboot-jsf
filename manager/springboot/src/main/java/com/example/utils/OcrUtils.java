package com.example.utils;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;

@Component
public class OcrUtils {

    private static AipOcr client;

    public OcrUtils(
            @Value("${baidu.ocr.app-id}") String appId,
            @Value("${baidu.ocr.api-key}") String apiKey,
            @Value("${baidu.ocr.secret-key}") String secretKey) {

        client = new AipOcr(appId, apiKey, secretKey);
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
    }
    /**
     * 通用文字识别
     * @param imagePath 图片路径
     * @return 识别结果
     */
    public static String generalBasic(String imagePath) {
        // 可选参数
        HashMap<String, String> options = new HashMap<>();
        options.put("language_type", "CHN_ENG");
        options.put("detect_direction", "true");
        options.put("detect_language", "true");
        options.put("probability", "true");

        // 调用通用文字识别接口
        JSONObject res = client.basicGeneral(imagePath, options);
        return res.toString();
    }

    /**
     * 通用文字识别（高精度版）
     * @param imagePath 图片路径
     * @return 识别结果
     */
    public static String generalAccurate(String imagePath) {
        // 可选参数
        HashMap<String, String> options = new HashMap<>();
        options.put("detect_direction", "true");
        options.put("probability", "true");

        // 调用通用文字识别（高精度版）接口
        JSONObject res = client.basicGeneral(imagePath, options);
        return res.toString();
    }

    /**
     * 从识别结果中提取文字
     * @param jsonResult 识别结果JSON字符串
     * @return 提取的文字
     */
    public static String extractText(String jsonResult) {
        JSONObject jsonObject = new JSONObject(jsonResult);
        StringBuilder text = new StringBuilder();
        if (jsonObject.has("words_result")) {
            org.json.JSONArray wordsResult = jsonObject.getJSONArray("words_result");
            for (int i = 0; i < wordsResult.length(); i++) {
                JSONObject item = wordsResult.getJSONObject(i);
                if (item.has("words")) {
                    text.append(item.getString("words"));
                    text.append(" ");
                }
            }
        }
        return text.toString().trim();
    }
}
