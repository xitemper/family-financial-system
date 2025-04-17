package com.bishe.chat;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IntentClient {
    // 指向本地 Flask 服务的接口
    private static final String API_URL = "http://127.0.0.1:5000/classify";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

//    public String classify(String text, String[] candidateLabels) throws IOException {
//        // 构造 JSON 请求体
//        StringBuilder labelsJson = new StringBuilder("[");
//        for (int i = 0; i < candidateLabels.length; i++) {
//            labelsJson.append("\"").append(candidateLabels[i]).append("\"");
//            if (i != candidateLabels.length - 1) {
//                labelsJson.append(",");
//            }
//        }
//        labelsJson.append("]");
//
//        String json = "{\"text\": \"" + text + "\", \"candidate_labels\": " + labelsJson.toString() + "}";
//        RequestBody body = RequestBody.create(json, JSON);
//        Request request = new Request.Builder()
//                .url(API_URL)
//                .post(body)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new IOException("Unexpected code " + response);
//            }
//            String result = response.body().string();
//
//            // 解析 JSON，提取 labels 和 scores
//            JSONObject jsonObject = new JSONObject(result);
//            JSONArray labels = jsonObject.getJSONArray("labels");
//            JSONArray scores = jsonObject.getJSONArray("scores");
//
//            // 找出分数最高的 index
//            int maxIndex = 0;
//            double maxScore = scores.getDouble(0);
//            for (int i = 1; i < scores.length(); i++) {
//                double score = scores.getDouble(i);
//                if (score > maxScore) {
//                    maxScore = score;
//                    maxIndex = i;
//                }
//            }
//
//            // 返回分数最高的 label
//            return labels.getString(maxIndex);
//        }
//    }

    public String classify(String text, String[] candidateLabels) throws IOException {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("text", text);
        requestMap.put("candidate_labels", candidateLabels);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(requestMap);  // 👈 安全构造 JSON

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String result = response.body().string();
            JSONObject jsonObject = new JSONObject(result);
            JSONArray labels = jsonObject.getJSONArray("labels");
            JSONArray scores = jsonObject.getJSONArray("scores");

            int maxIndex = 0;
            double maxScore = scores.getDouble(0);
            for (int i = 1; i < scores.length(); i++) {
                double score = scores.getDouble(i);
                if (score > maxScore) {
                    maxScore = score;
                    maxIndex = i;
                }
            }

            double sum = 0;
            for (int i = 0; i < scores.length(); i++) {
                sum += scores.getDouble(i);
            }
            double avg = sum / scores.length();
            double confidence = maxScore - avg;

            System.out.println("当前JsonObject的scores:"+scores);
            System.out.println("当前JsonObject的labels:"+labels);

            for (String candidateLabel : candidateLabels) {
                if(text.contains(candidateLabel)){
                    return candidateLabel;
                }
            }

            if (confidence < 0.10
                    || maxScore < 0.3
                    || labels.getString(maxIndex).equals("为什么")
                    || labels.getString(maxIndex).equals("我想知道")
                    || labels.getString(maxIndex).contains("告诉我")
                    || labels.getString(maxIndex).equals("你觉得")) {
                return "通用查询";
            }
            return labels.getString(maxIndex);
//            if (maxScore < 0.65) {
//                return "通用查询";
//            }
//            return labels.getString(maxIndex);
        }
    }

    // 测试方法
    public static void main(String[] args) throws IOException {
        IntentClient intentClient = new IntentClient();
        // 示例输入：中文问题
        String text = "今天天气怎么样";
        // 候选标签使用与模型训练时相同的语言（这里示例为中文）
        String[] candidateLabels = {"查询收入", "查询支出", "通用查询"};
        String result = intentClient.classify(text, candidateLabels);
        System.out.println("分类结果: " + result);
    }
}
