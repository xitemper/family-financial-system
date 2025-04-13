//package com.bishe.service.serviceImpl;
//
//import com.alibaba.dashscope.aigc.generation.Generation;
//import com.alibaba.dashscope.aigc.generation.GenerationParam;
//import com.alibaba.dashscope.aigc.generation.GenerationResult;
//import com.alibaba.dashscope.common.Message;
//import com.alibaba.dashscope.common.Role;
//import com.alibaba.dashscope.exception.ApiException;
//import com.alibaba.dashscope.exception.InputRequiredException;
//import com.alibaba.dashscope.exception.NoApiKeyException;
//import com.bishe.entity.ChatMessages;
//import com.bishe.entity.ChatSessions;
//import com.bishe.entity.Result;
//import com.bishe.mapper.ChatMessagesMapper;
//import com.bishe.mapper.ChatSessionsMapper;
//import com.bishe.service.ChatService;
//import com.bishe.util.MessageUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//
//@Service
//public class ChatServiceImpl_chushiban implements ChatService {
//
//    @Autowired
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    @Autowired
//    private ChatSessionsMapper chatSessionsMapper;
//
//    @Autowired
//    private ChatMessagesMapper chatMessagesMapper;
//
//    private static final long SESSION_TTL = 24 * 60 * 60;
//
//    public ChatServiceImpl_chushiban(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    private GenerationParam createGenerationParam(List<Message> messages) {
//        return GenerationParam.builder()
//                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
//                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
//                // 模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
//                .model("qwen-plus")
//                .messages(messages)
//                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
//                .build();
//    }
//    private GenerationResult callGenerationWithMessages(GenerationParam param) throws ApiException, NoApiKeyException, InputRequiredException {
//        Generation gen = new Generation();
//        return gen.call(param);
//    }
//    @Override
//    public Result processChat(String userInput,String sessionId) {
//        try {
//            List<Message> messages = (List<Message>) redisTemplate.opsForValue().get("chat:session:"+sessionId);
//            // 初始化系统消息（仅首次需要）
//            if(messages==null){
//                messages=new ArrayList<>();
//            }
//            if(messages.isEmpty()) {
//                messages.add(createMessage(Role.SYSTEM, "You are a helpful assistant."));
//            }
//            messages.add(createMessage(Role.USER, userInput));
//            GenerationParam param = createGenerationParam(messages);
//            GenerationResult result = callGenerationWithMessages(param);
//            System.out.println("模型输出："+result.getOutput().getChoices().get(0).getMessage().getContent());
//            //保存assistant回答
//            messages.add(MessageUtils.convertResponse(result));
//            redisTemplate.opsForValue().set("chat:session:"+sessionId,messages);
//            return Result.succeed("回答成功！",result.getOutput().getChoices().get(0).getMessage().getContent());
//        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
//            e.printStackTrace();
//        }
//       return Result.fail("系统出现问题，请稍后再试！");
//    }
//
//    //查询是否新对话
//    public boolean isNewSession(String sessionId){
//        List<Message> messageList = (List<Message>) redisTemplate.opsForValue().get("chat:session:" + sessionId);
//        if (messageList.isEmpty()) {
//            return true;
//        }
//        return false;
//    }
//
//    private static Message createMessage(Role role, String content) {
//        return Message.builder().role(role.getValue()).content(content).build();
//    }
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
