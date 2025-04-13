//package com.bishe.chat;
//
//import okhttp3.*;
//import org.apache.commons.lang3.StringEscapeUtils;
//
//import java.io.IOException;
//
//public class IntentClient_bart {
//    private static final String API_URL = "http://127.0.0.1:5000/classify";
//    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//    private final OkHttpClient client = new OkHttpClient();
//
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
//        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new IOException("Unexpected code " + response);
//            }
//            return response.body().string();
//        }
//    }
//
//    // 测试方法
//    public static void main(String[] args) throws IOException {
//        IntentClient_bart intentClient = new IntentClient_bart();
//        String text = "今天天气怎么样";
//        String[] candidateLabels = {"query_income", "query_expense", "general_chat"};
//        String result = intentClient.classify(text, candidateLabels);
//        String decoded = StringEscapeUtils.unescapeJava(result);
//        System.out.println("解码后的结果: " + decoded);
////        System.out.println("分类结果: " + result);
//    }
//}
