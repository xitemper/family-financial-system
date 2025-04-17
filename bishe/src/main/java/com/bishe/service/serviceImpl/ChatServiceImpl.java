package com.bishe.service.serviceImpl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.bishe.chat.IntentClient;
import com.bishe.entity.*;
import com.bishe.mapper.ChatMessagesMapper;
import com.bishe.mapper.ChatSessionsMapper;
import com.bishe.mapper.TransactionMapper;
import com.bishe.service.ChatService;
import com.bishe.util.MessageUtils;
import com.bishe.util.UserContext;
import io.reactivex.Flowable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ChatSessionsMapper chatSessionsMapper;

    @Autowired
    private ChatMessagesMapper chatMessagesMapper;

    @Autowired
    private TransactionMapper transactionMapper;

    private IntentClient intentClient = new IntentClient();

    private static final long SESSION_TTL = 24 * 60 * 60;

    public ChatServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private GenerationParam createGenerationParam(List<Message> messages) {
        return GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                // 模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-max")
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
    }

    private GenerationParam createGenerationParamStream(List<Message> messages) {
        return GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                // 模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-max")
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .incrementalOutput(true)
                .build();
    }

    private Flowable<GenerationResult> callGenerationWithMessagesStream(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        return gen.streamCall(param);
    }

    private GenerationResult callGenerationWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        return gen.call(param);
    }

    //TODO 扩展 AI 查询 数据库的内容
    @Override
    public Result processChat(Long userId,String userInput, String sessionId) {
        // —— 第一步：意图识别 ——
        List<String> candidateIntents = Arrays.asList("查询收入", "查询支出", "通用查询","查询收入账单信息","查询支出账单信息");
        String intent;
        try {
            intent = intentClient.classify(userInput, (String[]) candidateIntents.toArray());
            System.out.println("进入意图识别~~~~~");
        } catch (IOException e) {
            // 识别失败则退到默认对话
            System.out.println("意图识别结束，没有特殊意图，进入通用接口调用");
            intent = "通用查询";
        }

        // 3. 解析日期范围：优先查找 “YYYY年M月” 格式，否则默认本月
        YearMonth ym = parseYearMonth(userInput);
        LocalDateTime startDate = ym.atDay(1).atStartOfDay();
        LocalDateTime endDate   = ym.atEndOfMonth().atTime(23,59,59);
        String monthLabel = ym.getYear() + "年" + ym.getMonthValue() + "月";
        System.out.println("从用户输入解析出的日期范围："+monthLabel);
        String reply="";
        List<Message> messages = (List<Message>) redisTemplate.opsForValue().get("chat:session:"+sessionId);
        if(messages==null){
            messages = new ArrayList<>();
        }
        if(messages.isEmpty()){
            messages.add(createMessage(Role.SYSTEM, "You are a helpful assistant."));
        }
        // —— 第二步：根据意图路由 ——
        System.out.println("识别到意图：" + intent);
        switch (intent) {
            case "查询收入":
                // 调用本地 Service 查询本月收入
                messages.add(createMessage(Role.USER, userInput));
                Double income = transactionMapper.getTotalIncome(userId,"income",startDate,endDate);
                if (income == null) {income = 0.0;}
                monthLabel = ym.getYear() + "年" + ym.getMonthValue() + "月";
                reply = String.format("您%s的%s总额是：%.2f 元。",
                        monthLabel,
                        intent.contains("收入") ? "收入" : "支出",
                        income);
                messages.add(MessageUtils.convertString(reply));
                redisTemplate.opsForValue().set("chat:session:"+sessionId,messages);
                return Result.succeed("回答成功！",reply);
            case "查询支出":
                // 调用本地 Service 查询本月支出
                messages.add(createMessage(Role.USER, userInput));
                Double expense = transactionMapper.getTotalIncome(userId,"expense",startDate,endDate);
                if (expense == null) {expense = 0.0;}
                monthLabel = ym.getYear() + "年" + ym.getMonthValue() + "月";
                reply = String.format("您%s的%s总额是：%.2f 元。",
                        monthLabel,
                        intent.equals("query_income") ? "收入" : "支出",
                        expense);
                messages.add(MessageUtils.convertString(reply));
                redisTemplate.opsForValue().set("chat:session:"+sessionId,messages);
                return Result.succeed("回答成功！",reply);
            case "查询收入账单信息":
                // 调用本地 Service 查询
                messages.add(createMessage(Role.USER, userInput));
                List<Transaction> incomeList = transactionMapper.getTransactionsByTypeAndDate(userId, "income", startDate, endDate);
                if (incomeList == null||incomeList.isEmpty())
                {return Result.succeed("回答成功！","并未查询到相应账单信息哦！");}
                monthLabel = ym.getYear() + "年" + ym.getMonthValue() + "月";
                StringBuilder sb = new StringBuilder();
                sb.append("您");
                sb.append(monthLabel+"的账单信息是：");
                sb.append(intent.equals("query_income") ? "收入" : "支出");
                sb.append("\n");
                sb.append(incomeList.stream().map(Transaction::toString).collect(Collectors.joining("\n")));
                messages.add(MessageUtils.convertString(reply));
                redisTemplate.opsForValue().set("chat:session:"+sessionId,messages);
                return Result.succeed("回答成功！",sb.toString());
            case "查询支出账单信息":
                // 调用本地 Service 查询
                messages.add(createMessage(Role.USER, userInput));
                List<Transaction> expenseList = transactionMapper.getTransactionsByTypeAndDate(userId, "expense", startDate, endDate);
                if (expenseList == null||expenseList.isEmpty())
                {return Result.succeed("回答成功！","并未查询到相应账单信息哦！");}
                monthLabel = ym.getYear() + "年" + ym.getMonthValue() + "月";
                StringBuilder ssb = new StringBuilder();
                ssb.append("您");
                ssb.append(monthLabel+"的账单信息是：");
                ssb.append(intent.equals("query_income") ? "收入" : "支出");
                ssb.append("\n");
                ssb.append(expenseList.stream().map(Transaction::toString).collect(Collectors.joining("\n")));
                messages.add(MessageUtils.convertString(reply));
                redisTemplate.opsForValue().set("chat:session:"+sessionId,messages);
                return Result.succeed("回答成功！",ssb.toString());
            default:
                // 其他意图走大模型
                return processWithAI(userInput, sessionId);
        }
    }

    private Result processWithAI(String userInput,String sessionId) {
        try {
            @SuppressWarnings("unchecked")
            List<Message> messages = (List<Message>) redisTemplate.opsForValue().get("chat:session:"+sessionId);
            // 初始化系统消息（仅首次需要）
            if(messages==null){
                messages=new ArrayList<>();
            }
            if(messages.isEmpty()) {
                messages.add(createMessage(Role.SYSTEM, "You are a helpful assistant."));
            }
            messages.add(createMessage(Role.USER, userInput));
            // 调用大模型
            GenerationParam param = createGenerationParam(messages);
            GenerationResult result = callGenerationWithMessages(param);
//            Flowable<GenerationResult> result = callGenerationWithMessages(param);
//            result.blockingForEach(message -> handleGenerationResult(message));

            String aiReply = result.getOutput().getChoices().get(0).getMessage().getContent();
            System.out.println("模型输出："+result.getOutput().getChoices().get(0).getMessage().getContent());
            //保存assistant回答
            messages.add(MessageUtils.convertResponse(result));
            redisTemplate.opsForValue().set("chat:session:"+sessionId,messages);
            // 保存并返回
//            saveAssistantMessage(sessionId, aiReply);
//            return Result.succeed("回答成功！", "test流式输出中");
            return Result.succeed("回答成功！", aiReply);
        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            e.printStackTrace();
            return Result.fail("系统出现问题，请稍后再试！");
        }

    }

    private static void handleGenerationResult(GenerationResult message) {
        String content = message.getOutput().getChoices().get(0).getMessage().getContent();
        System.out.println(content);
    }

    @Override
    public Result getOverAllAdvice(List<FinancialHealthDimensionScore> scores) {
        return processWithAI(buildPrompt(scores),"getOverAllAdviceSessionId");
    }

    @Override
    public SseEmitter processChatStream(Long userId, String userInput, String sessionId) {
        SseEmitter emitter = new SseEmitter(0L);

        // —— 第一步：意图识别 ——
//        List<String> candidateIntents = Arrays.asList("查询收入", "查询支出", "通用查询");
        List<String> candidateIntents = Arrays.asList(
                "收入总额", "支出总额", "通用查询","收入账单信息","支出账单信息","为什么","我想知道","告诉我","你觉得");
        new Thread(() -> {
            try {
                // —— 第一步：意图识别 —— (保持你原有逻辑)
                String intent;
                try {
                    intent = intentClient.classify(userInput,  (String[]) candidateIntents.toArray()/* … */);
                } catch (IOException e) {
                    intent = "通用查询";
                }

                // —— 第二步：根据意图路由 ——
                System.out.println("识别到意图：" + intent);

                // 3. 解析日期范围：优先查找 “YYYY年M月” 格式，否则默认本月
                YearMonth ym = parseYearMonth(userInput);
                LocalDateTime startDate = ym.atDay(1).atStartOfDay();
                LocalDateTime endDate   = ym.atEndOfMonth().atTime(23,59,59);
                String monthLabel = ym.getYear() + "年" + ym.getMonthValue() + "月";
                System.out.println("从用户输入解析出的日期范围："+monthLabel);
                String reply="";

                // —— 第二步：本地 DB 逻辑 ——
                if ("收入总额".equals(intent) || "支出总额".equals(intent)) {
                    Double total = transactionMapper.getTotalIncome(userId,
                            intent.contains("收入") ? "income" : "expense",
                            startDate, endDate);
                    reply = String.format("您"+monthLabel+"的%s总额：%.2f 元。",
                            intent.contains("收入")?"收入":"支出",
                            total==null?0.0:total);
                    // 直接推送一条完整消息然后结束
                    emitter.send(SseEmitter.event().data(reply));
                    emitter.complete();
                    return;
                }

                if("收入账单信息".equals(intent)){
//                    messages.add(createMessage(Role.USER, userInput));
                    List<Transaction> incomeList = transactionMapper.getTransactionsByTypeAndDate(userId, "income", startDate, endDate);
                    if (incomeList == null||incomeList.isEmpty())
                    {        // 如果查询结果为空，返回友好的提示消息
                        String errorMessage = "并未查询到相应的支出账单信息哦！";
                        emitter.send(SseEmitter.event().data(errorMessage));
                        emitter.complete();}
                    monthLabel = ym.getYear() + "年" + ym.getMonthValue() + "月";
                    StringBuilder sb = new StringBuilder();
                    sb.append("您");
                    sb.append(monthLabel+"的");
                    sb.append(intent.contains("收入") ? "收入" : "支出");
                    sb.append("账单信息是：");
//                    sb.append("<br>");
                    for (Transaction transaction : incomeList) {
                        sb.append(transaction.toString());
                    }
//                    sb.append(incomeList.stream().map(Transaction::toString));
//                    messages.add(MessageUtils.convertString(sb.toString()));
//                    redisTemplate.opsForValue().set("chat:session:"+sessionId,messages);
                    // 直接推送一条完整消息然后结束
                    emitter.send(SseEmitter.event().data(sb.toString()));
                    emitter.complete();
                    return;
                }

                if("支出账单信息".equals(intent)){
                    // 调用本地 Service 查询
//                    messages.add(createMessage(Role.USER, userInput));
                    List<Transaction> expenseList = transactionMapper.getTransactionsByTypeAndDate(userId, "expense", startDate, endDate);
                    if (expenseList == null||expenseList.isEmpty())
                    {        // 如果查询结果为空，返回友好的提示消息
                        String errorMessage = "并未查询到相应的支出账单信息哦！";
                        emitter.send(SseEmitter.event().data(errorMessage));
                        emitter.complete();}
                    monthLabel = ym.getYear() + "年" + ym.getMonthValue() + "月";
                    StringBuilder ssb = new StringBuilder();
                    ssb.append("您");
                    ssb.append(monthLabel+"的");
                    ssb.append(intent.contains("收入") ? "收入" : "支出");
                    ssb.append("账单信息是：");
                    for (Transaction transaction : expenseList) {
                        ssb.append(transaction.toString());
                    }
//                    ssb.append(intent.contains("收入") ? "收入" : "支出");
//                    ssb.append("\n");
//                    ssb.append(expenseList.stream().map(Transaction::toString).collect(Collectors.joining("\n")));
//                    messages.add(MessageUtils.convertString(reply));
//                    redisTemplate.opsForValue().set("chat:session:"+sessionId,messages);
                    emitter.send(SseEmitter.event().data(ssb.toString()));
                    emitter.complete();
                    return ;
                }


                // —— 第三步：大模型流式调用 ——
                // 取出历史 messages
                @SuppressWarnings("unchecked")
                List<Message> messages = (List<Message>) redisTemplate.opsForValue()
                        .get("chat:session:" + sessionId);
                if (messages == null) {messages = new ArrayList<>();}
                if (messages.isEmpty()) {
                    messages.add(createMessage(Role.SYSTEM, "You are a helpful assistant."));
                }
                messages.add(createMessage(Role.USER, userInput));

                // 构造 param 并且 incrementalOutput(true)
                GenerationParam param = createGenerationParamStream(messages);

                Flowable<GenerationResult> flow = callGenerationWithMessagesStream(param);

                // 每来一条增量，立即发送给前端
                StringBuilder full = new StringBuilder();
                flow.blockingForEach(gr -> {
                    String chunk = gr.getOutput()
                            .getChoices()
                            .get(0)
                            .getMessage()
                            .getContent();
                    full.append(chunk);
                    emitter.send(SseEmitter.event().data(chunk));
                });

                // 保存完整回复到 Redis/DB
                String aiReply = full.toString();
                messages.add(MessageUtils.convertString(aiReply));
                redisTemplate.opsForValue().set("chat:session:" + sessionId, messages);
                emitter.send(SseEmitter.event().name("end").data(""));
                emitter.complete();

            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().data("[Error] " + e.getMessage()));
                } catch (IOException io) { /* ignore */ }
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }

    private String buildPrompt(List<FinancialHealthDimensionScore> scores) {
        StringBuilder sb = new StringBuilder();
        sb.append("根据这个数据：[");
        for (int i = 0; i < scores.size(); i++) {
            FinancialHealthDimensionScore score = scores.get(i);
            sb.append("{ ");
            sb.append("\"dimension\": \"").append(score.getDimension()).append("\", ");
            sb.append("\"score\": ").append(score.getScore()).append(", ");
            sb.append("\"advice\": \"").append(score.getAdvice()).append("\" ");
            sb.append("}");
            if (i < scores.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("] 给出针对低分维度给出具体财务建议。");

        return sb.toString();
    }

    private YearMonth parseYearMonth(String text) {
        // 优先中文格式
//        Matcher m = Pattern.compile("(\\d{4})年(\\d{1,2})月").matcher(text);
//        if (m.find()) {
//            int y = Integer.parseInt(m.group(1));
//            int mo = Integer.parseInt(m.group(2));
//            return YearMonth.of(y, mo);
//        }
//        // 再试 YYYY-MM
//        m = Pattern.compile("(\\d{4})-(\\d{1,2})").matcher(text);
//        if (m.find()) {
//            int y = Integer.parseInt(m.group(1));
//            int mo = Integer.parseInt(m.group(2));
//            return YearMonth.of(y, mo);
//        }
//        // 默认本月
//        return YearMonth.now();
        YearMonth now = YearMonth.now();
        String t = text.replaceAll("\\s+", "");

        // 1. 先处理相对表达
        // 上月
        if (t.contains("上月") || t.contains("上个月")) {
            return now.minusMonths(1);
        }
        // 下月
        if (t.contains("下月") || t.contains("下个月")) {
            return now.plusMonths(1);
        }
        // 前N月
        Matcher m = Pattern.compile("前(\\d{1,2})月").matcher(t);
        if (m.find()) {
            int n = Integer.parseInt(m.group(1));
            return now.minusMonths(n);
        }
        // 后N月
//        m = Pattern.compile("后(\\d{1,2})月").matcher(t);
//        if (m.find()) {
//            int n = Integer.parseInt(m.group(1));
//            return now.plusMonths(n);
//        }
        // 去年N月 / 上一年N月
        m = Pattern.compile("(去年|上一年)(\\d{1,2})月").matcher(t);
        if (m.find()) {
            int month = Integer.parseInt(m.group(2));
            return YearMonth.of(now.getYear() - 1, month);
        }
        // 今年N月
        m = Pattern.compile("今年(\\d{1,2})月").matcher(t);
        if (m.find()) {
            int month = Integer.parseInt(m.group(1));
            return YearMonth.of(now.getYear(), month);
        }

        // 2. 再处理绝对表达
        // “YYYY年M月”
        m = Pattern.compile("(\\d{4})年(\\d{1,2})月").matcher(t);
        if (m.find()) {
            int y = Integer.parseInt(m.group(1));
            int mo = Integer.parseInt(m.group(2));
            return YearMonth.of(y, mo);
        }
        // “YYYY-MM”
        m = Pattern.compile("(\\d{4})-(\\d{1,2})").matcher(t);
        if (m.find()) {
            int y = Integer.parseInt(m.group(1));
            int mo = Integer.parseInt(m.group(2));
            return YearMonth.of(y, mo);
        }

        // 3. 最后兜底：本月
        return now;
    }



    //查询是否新对话
    public boolean isNewSession(String sessionId){
        List<Message> messageList = (List<Message>) redisTemplate.opsForValue().get("chat:session:" + sessionId);
        if (messageList.isEmpty()) {
            return true;
        }
        return false;
    }

    private static Message createMessage(Role role, String content) {
        return Message.builder().role(role.getValue()).content(content).build();
    }

    @Override
    public void saveSession(String sessionId, List<Message> messages) {
        String key = "chat:session:" + sessionId;
        redisTemplate.opsForValue().set(key, messages);
        redisTemplate.expire(key, SESSION_TTL, TimeUnit.SECONDS);
    }

    @Override
    public Result createNewSession(Long userId) {
        // 检查当前是否有活跃会话
        UserContext.setUserId(userId);
        String currentSessionKey = "user:cursession:" + userId;
        String existingSessionId = (String) redisTemplate.opsForValue().get(currentSessionKey);

        if (existingSessionId != null) {
            // 存在旧会话 → 触发持久化
            persistSession(userId,existingSessionId);
        }

        // 创建新会话
        String newSessionId = generateSessionId();
        redisTemplate.opsForValue().set(currentSessionKey, newSessionId);
        return Result.succeed("新对话创建成功",newSessionId);
    }

    @Override
    public List<Message> getSessionMessage(String sessionId) {
        String key = "chat:session:" + sessionId;
        return (List<Message>) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteSession(String sessionId) {
        String key = "chat:session:" + sessionId;
        redisTemplate.delete(key);
    }

    private String generateSessionId(){
        return UUID.randomUUID().toString();
    }


    // 场景1：用户主动结束会话
    @Override
    @Transactional
    public void persistSession(Long userId,String sessionId) {

        List<Message> messages = getSessionMessage(sessionId);
        //用来间隔开信息的插入时间 让后续可以通过时间asc排序
        int count = 0;
        if (messages != null) {
            List<Message> messageList= (List<Message>) redisTemplate.opsForValue().get("chat:session:"+sessionId);
            ChatSessions chatSession = new ChatSessions();
            chatSession.setSessionId(sessionId);
            chatSession.setLastActive(LocalDateTime.now());
            chatSession.setTitle(messages.get(1).getContent());
            chatSession.setUserId(userId);
            chatSessionsMapper.insertRecord(chatSession);
            for (Message message : messageList) {
                ChatMessages chatMessage = new ChatMessages();
                chatMessage.setSessionId(sessionId);
                chatMessage.setRole(message.getRole());
                chatMessage.setContent(message.getContent());
                chatMessage.setCreatedAt(LocalDateTime.now().plusSeconds(count++));
                chatMessagesMapper.insertRecord(chatMessage);
            }
            // 清理Redis数据
            String key = "user:cursession:" + userId;
            redisTemplate.delete(key);
            deleteSession(sessionId);
        }
    }

    @Override
    public Result getHistorySession(Long userId) {
        List<ChatSessions> historySessions = chatSessionsMapper.getSessionsByUserId(userId);
        if(historySessions == null||historySessions.isEmpty()){
            //TODO 前端接收resp.data.code == 500 判定为没有历史对话
            return Result.fail("当前用户没有");
        }
        return Result.succeed("成功获取当前用户历史对话",historySessions);
    }

    @Override
    public Result loadSession(String sessionId) {
        List<ChatMessages>  messages=chatMessagesMapper.getMessagesBySessionId(sessionId);
        if(messages==null||messages.isEmpty()){
            return Result.fail("该对话暂时读取不到相关数据！");
        }
        return Result.succeed("对话数据读取成功！",messages);
    }



    // 场景2：定时任务扫描过期会话
//    @Override
//    @Scheduled(fixedRate = 5 * 60 * 1000) // 每5分钟执行
//    @Async
//    public void autoPersistExpiringSessions() {
//        Set<String> keys = redisTemplate.keys("chat:session:*");
//        for (String key : keys) {
//            Long ttl = redisTemplate.getExpire(key);
//            if (ttl != null && ttl < 60) { // 剩余时间<1分钟时触发
//                String sessionId = key.replace("chat:session:", "");
//                persistSession(sessionId);
//            }
//        }
//    }


}
