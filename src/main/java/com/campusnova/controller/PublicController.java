package com.campusnova.controller;

import com.campusnova.dto.ChatRequest;
import com.campusnova.model.*;
import com.campusnova.repository.*;
import com.campusnova.service.*;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.*;

@RestController @RequestMapping("/api") public class PublicController {
    private final ChatbotService chatbot; private final FaqService faq; private final AnnouncementRepository announcements; private final ChatHistoryRepository history; private final OfficialKnowledgeRepository official;
    public PublicController(ChatbotService c,FaqService f,AnnouncementRepository a,ChatHistoryRepository h,OfficialKnowledgeRepository o){chatbot=c;faq=f;announcements=a;history=h;official=o;}
    @PostMapping("/chat") public Map<String,Object> chat(@Valid @RequestBody ChatRequest request){return chatbot.respond(request);}
    @GetMapping("/faqs") public List<Faq> faqs(@RequestParam(required=false)String q){return faq.all(q);}
    @GetMapping("/announcements") public List<Announcement> announcements(){return announcements.findAll();}
    @GetMapping("/history/{session}") public List<ChatHistory> history(@PathVariable String session){return history.findTop20BySessionIdOrderByCreatedAtDesc(session);}
    @GetMapping("/knowledge/status") public Map<String,Object> knowledge(){List<OfficialKnowledge> records=official.findByActiveTrueAndOfficialSourceTrueOrderByImportedAtDesc();List<Map<String,String>> published=new ArrayList<Map<String,String>>();for(OfficialKnowledge item:records){Map<String,String> row=new LinkedHashMap<String,String>();row.put("title",item.getTitle());row.put("category",item.getCategory());row.put("sourceUrl",item.getSourcePageUrl()==null?item.getSourceUrl():item.getSourcePageUrl());published.add(row);if(published.size()==9)break;}Map<String,Object> result=new LinkedHashMap<String,Object>();result.put("count",records.size());result.put("items",published);return result;}
}
