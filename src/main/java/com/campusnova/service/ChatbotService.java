package com.campusnova.service; import com.campusnova.dto.ChatRequest; import org.springframework.stereotype.Service; import java.util.*;
/** Backward-compatible facade for the existing public chat endpoint. */
@Service public class ChatbotService { private final NovaChatService nova; public ChatbotService(NovaChatService n){nova=n;} public Map<String,Object> respond(ChatRequest request){return nova.respond(request);} }
