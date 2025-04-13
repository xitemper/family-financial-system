//package com.bishe.service.serviceImpl;
//
//import com.alibaba.dashscope.aigc.generation.Generation;
//import com.alibaba.dashscope.aigc.generation.GenerationParam;
//import com.alibaba.dashscope.aigc.generation.GenerationResult;
//import com.alibaba.dashscope.common.*;
//import com.alibaba.dashscope.exception.*;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.TypeReference;
//import com.bishe.chat.FunctionDef;
//import com.bishe.entity.*;
//import com.bishe.entity.Result;
//import com.bishe.mapper.*;
//import com.bishe.service.ChatService;
//import com.bishe.util.MessageUtils;
//import jdk.nashorn.internal.ir.FunctionCall;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class ChatServiceImpl_tongyi implements ChatService {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//    private final ChatSessionsMapper chatSessionsMapper;
//    private final ChatMessagesMapper chatMessagesMapper;
//    private final TransactionMapper transactionMapper;
//
//    private static final long SESSION_TTL = 24 * 60 * 60;
//
//    // Java 8兼容的工具定义
//    private static final List<Tool> TOOLS = Arrays.asList(
//            new Tool().setType("function")
//                    .setFunction(new FunctionDef()
//                            .setName("get_month_expense")
//                            .setDescription("获取指定月份的支出总额")
//                            .setParameters(Collections.singletonMap(
//                                    "properties", new HashMap<String, Object>() {{
//                                        put("year", Collections.singletonMap("type", "integer"));
//                                        put("month", Collections.singletonMap("type", "integer"));
//                                    }})
//                            )),
//            new Tool().setType("function")
//                    .setFunction(new FunctionDef()
//                            .setName("get_category_expense")
//                            .setDescription("获取指定类别的年度支出")
//                            .setParameters(Collections.singletonMap(
//                                    "properties", new HashMap<String, Object>() {{
//                                        put("category", Collections.singletonMap("type", "string"));
//                                        put("year", Collections.singletonMap("type", "integer"));
//                                    }})
//                            ))
//    );
//
//    @Autowired
//    public ChatServiceImpl_tongyi(RedisTemplate<String, Object> redisTemplate,
//                                  ChatSessionsMapper chatSessionsMapper,
//                                  ChatMessagesMapper chatMessagesMapper,
//                                  TransactionMapper transactionMapper) {
//        this.redisTemplate = redisTemplate;
//        this.chatSessionsMapper = chatSessionsMapper;
//        this.chatMessagesMapper = chatMessagesMapper;
//        this.transactionMapper = transactionMapper;
//    }
//
//    @Override
//    public Result processChat(String userInput, String sessionId) {
//        try {
//            List<Message> messages = getSessionMessages(sessionId);
//            initSystemMessage(messages);
//
//            // 构建临时消息列表
//            List<Message> tempMessages = new ArrayList<>(messages);
//            tempMessages.add(MessageUtils.createMessage(Role.USER, userInput));
//
//            // 首次调用检查工具调用
//            GenerationParam param = buildParamWithTools(tempMessages);
//            GenerationResult result = new Generation().call(param);
//            Message response = result.getOutput().getChoices().get(0).getMessage();
//
//            if (!response.getToolCalls().isEmpty()) {
//                return handleToolCall(sessionId, messages, response, userInput);
//            }
//
//            return handleNormalResponse(sessionId, messages, response, userInput);
//        } catch (Exception e) {
//            return Result.fail("处理失败: " + e.getMessage());
//        }
//    }
//
//    private Result handleToolCall(String sessionId,
//                                  List<Message> originalMessages,
//                                  Message response,
//                                  String userInput) throws Exception {
//        // 获取工具调用信息
//        ToolCall toolCall = response.getToolCalls().get(0);
//        FunctionCall functionCall = toolCall.getFunction();
//        String funcName = functionCall.getName();
//        String arguments = functionCall.getArguments();
//
//        // 执行函数调用
//        Map<String, Object> params = parseParams(arguments);
//        Object funcResult = executeFunction(funcName, params);
//
//        // 构建工具响应消息
//        Message toolMessage = new Message()
//                .setRole(Role.TOOL.getValue())
//                .setContent(JSON.toJSONString(funcResult))
//                .setToolCallId(toolCall.getId());
//
//        // 构建完整消息链
//        List<Message> newMessages = new ArrayList<>(originalMessages);
//        newMessages.add(MessageUtils.createMessage(Role.USER, userInput));
//        newMessages.add(response);
//        newMessages.add(toolMessage);
//
//        // 二次调用生成最终回答
//        GenerationResult finalResult = new Generation().call(buildParam(newMessages));
//        Message finalResponse = finalResult.getOutput().getChoices().get(0).getMessage();
//
//        // 保存完整对话记录
//        newMessages.add(finalResponse);
//        saveSession(sessionId, newMessages);
//
//        return Result.succeed("操作成功", finalResponse.getContent());
//    }
//
//    private Object executeFunction(String funcName, Map<String, Object> params) {
//        switch (funcName) {
//            case "get_month_expense":
//                int year = params.containsKey("year") ?
//                        Integer.parseInt(params.get("year").toString()) :
//                        LocalDateTime.now().getYear();
//                int month = params.containsKey("month") ?
//                        Integer.parseInt(params.get("month").toString()) :
//                        LocalDateTime.now().getMonthValue();
//                return expenseMapper.getMonthExpense(year, month);
//
//            case "get_category_expense":
//                String category = params.get("category").toString();
//                int queryYear = params.containsKey("year") ?
//                        Integer.parseInt(params.get("year").toString()) :
//                        LocalDateTime.now().getYear();
//                return expenseMapper.getCategoryExpense(category, queryYear);
//
//            default:
//                throw new IllegalArgumentException("未支持的函数调用: " + funcName);
//        }
//    }
//
//    private GenerationParam buildParamWithTools(List<Message> messages) {
//        return GenerationParam.builder()
//                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
//                .model("qwen-max")
//                .messages(messages)
//                .tools(TOOLS)
//                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
//                .build();
//    }
//
//    private GenerationParam buildParam(List<Message> messages) {
//        return GenerationParam.builder()
//                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
//                .model("qwen-max")
//                .messages(messages)
//                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
//                .build();
//    }
//
//    // 辅助方法
//    private List<Message> getSessionMessages(String sessionId) {
//        Object messages = redisTemplate.opsForValue().get("chat:session:" + sessionId);
//        return messages != null ? (List<Message>) messages : new ArrayList<>();
//    }
//
//    private void initSystemMessage(List<Message> messages) {
//        if (messages.isEmpty()) {
//            messages.add(MessageUtils.createMessage(Role.SYSTEM, "你是智能助手，可以查询系统数据"));
//        }
//    }
//
//    private Map<String, Object> parseParams(String arguments) {
//        return JSON.parseObject(arguments, new TypeReference<HashMap<String, Object>>() {});
//    }
//
//    private Result handleNormalResponse(String sessionId,
//                                        List<Message> originalMessages,
//                                        Message response,
//                                        String userInput) {
//        List<Message> newMessages = new ArrayList<>(originalMessages);
//        newMessages.add(MessageUtils.createMessage(Role.USER, userInput));
//        newMessages.add(response);
//        saveSession(sessionId, newMessages);
//        return Result.succeed("回答成功", response.getContent());
//    }
//
//
//
//
//    @Override
//    public void saveSession(String sessionId, List<Message> messages) {
//        String key = "chat:session:" + sessionId;
//        redisTemplate.opsForValue().set(key, messages);
//        redisTemplate.expire(key, SESSION_TTL, TimeUnit.SECONDS);
//    }
//
//    @Override
//    public Result createNewSession(Long userId) {
//        // 检查当前是否有活跃会话
//        String currentSessionKey = "user:cursession:" + userId;
//        String existingSessionId = (String) redisTemplate.opsForValue().get(currentSessionKey);
//
//        if (existingSessionId != null) {
//            // 存在旧会话 → 触发持久化
//            persistSession(userId,existingSessionId);
//        }
//
//        // 创建新会话
//        String newSessionId = generateSessionId();
//        redisTemplate.opsForValue().set(currentSessionKey, newSessionId);
//        return Result.succeed("新对话创建成功",newSessionId);
//    }
//
//    @Override
//    public List<Message> getSessionMessage(String sessionId) {
//        String key = "chat:session:" + sessionId;
//        return (List<Message>) redisTemplate.opsForValue().get(key);
//    }
//
//    @Override
//    public void deleteSession(String sessionId) {
//        String key = "chat:session:" + sessionId;
//        redisTemplate.delete(key);
//    }
//
//    private String generateSessionId(){
//        return UUID.randomUUID().toString();
//    }
//
//
//    // 场景1：用户主动结束会话
//    @Override
//    @Transactional
//    public void persistSession(Long userId,String sessionId) {
//
//        List<Message> messages = getSessionMessage(sessionId);
//        //用来间隔开信息的插入时间 让后续可以通过时间asc排序
//        int count = 0;
//        if (messages != null) {
//            List<Message> messageList= (List<Message>) redisTemplate.opsForValue().get("chat:session:"+sessionId);
//            ChatSessions chatSession = new ChatSessions();
//            chatSession.setSessionId(sessionId);
//            chatSession.setLastActive(LocalDateTime.now());
//            chatSession.setTitle(messages.get(1).getContent());
//            chatSession.setUserId(userId);
//            chatSessionsMapper.insertRecord(chatSession);
//            for (Message message : messageList) {
//                ChatMessages chatMessage = new ChatMessages();
//                chatMessage.setSessionId(sessionId);
//                chatMessage.setRole(message.getRole());
//                chatMessage.setContent(message.getContent());
//                chatMessage.setCreatedAt(LocalDateTime.now().plusSeconds(count++));
//                chatMessagesMapper.insertRecord(chatMessage);
//            }
//            // 清理Redis数据
//            String key = "user:cursession:" + userId;
//            redisTemplate.delete(key);
//            deleteSession(sessionId);
//        }
//    }
//
//    @Override
//    public Result getHistorySession(Long userId) {
//        List<ChatSessions> historySessions = chatSessionsMapper.getSessionsByUserId(userId);
//        if(historySessions == null||historySessions.isEmpty()){
//            //TODO 前端接收resp.data.code == 500 判定为没有历史对话
//            return Result.fail("当前用户没有");
//        }
//        return Result.succeed("成功获取当前用户历史对话",historySessions);
//    }
//
//    @Override
//    public Result loadSession(String sessionId) {
//        List<ChatMessages>  messages=chatMessagesMapper.getMessagesBySessionId(sessionId);
//        if(messages==null||messages.isEmpty()){
//            return Result.fail("该对话暂时读取不到相关数据！");
//        }
//        return Result.succeed("对话数据读取成功！",messages);
//    }
//
//    // 场景2：定时任务扫描过期会话
////    @Override
////    @Scheduled(fixedRate = 5 * 60 * 1000) // 每5分钟执行
////    @Async
////    public void autoPersistExpiringSessions() {
////        Set<String> keys = redisTemplate.keys("chat:session:*");
////        for (String key : keys) {
////            Long ttl = redisTemplate.getExpire(key);
////            if (ttl != null && ttl < 60) { // 剩余时间<1分钟时触发
////                String sessionId = key.replace("chat:session:", "");
////                persistSession(sessionId);
////            }
////        }
////    }
//
//
//}
