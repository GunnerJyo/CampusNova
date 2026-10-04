package com.campusnova.service;

import com.campusnova.config.CollegeProfile;
import com.campusnova.dto.ChatRequest;
import com.campusnova.knowledge.*;
import com.campusnova.model.ChatHistory;
import com.campusnova.repository.ChatHistoryRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class NovaChatService {
    private final ConversationService conversations; private final KnowledgeRetrievalService retrieval; private final CollegeRelevanceService relevance;
    private final BedrockService bedrock; private final GroundingValidationService grounding; private final ChatHistoryRepository history;
    private final CollegeProfile profile; private final OfficialAnswerFormatter formatter;
    public NovaChatService(ConversationService c, KnowledgeRetrievalService r, CollegeRelevanceService rel, BedrockService b, GroundingValidationService g, ChatHistoryRepository h, CollegeProfile p, OfficialAnswerFormatter f) { conversations=c;retrieval=r;relevance=rel;bedrock=b;grounding=g;history=h;profile=p;formatter=f; }

    public Map<String,Object> respond(ChatRequest request) {
        long start=System.currentTimeMillis(); String question=request.getQuestion().trim(); String session=request.getSessionId()==null||request.getSessionId().trim().isEmpty()?"guest":request.getSessionId();
        List<String> memory=conversations.recentTerms(session); RetrievalResult found=retrieval.retrieve(question,memory); boolean college=relevance.isCollegeRelated(question,found,memory);
        String answer,type,category="General"; boolean answered=false; List<String> sources=Collections.emptyList();
        if(!college) { answer="I’m NOVA, the AI student information assistant for "+profile.getOfficialName()+". I can help with LBSCEK academics, admissions, services, campus facilities, and official information."; type="OUT_OF_SCOPE"; }
        else if(found.hasContext()&&hasOfficial(found)) {
            RetrievalResult official=officialOnly(found); KnowledgeDocument primary=official.getDocuments().get(0); Optional<String> generated=bedrock.generate(question,official);
            if(generated.isPresent()&&grounding.acceptable(generated.get(),official)) { answer=generated.get(); type="AI_GROUNDED"; }
            else { answer=(formatter==null?new OfficialAnswerFormatter():formatter).format(question,primary); type="VERIFIED_OFFICIAL"; }
            category=primary.getCategory(); answered=true; sources=official.sources();
        } else { answer="I don’t currently have the official LBSCEK-specific information needed to answer that exactly. Please check the relevant college office, department, or official notice at "+profile.getOfficialDomain()+" for the confirmed procedure."; type="GENERAL_GUIDANCE"; }
        long elapsed=System.currentTimeMillis()-start; history.save(new ChatHistory(session,question,answer,category,answered,type,!college,elapsed));
        Map<String,Object> response=new LinkedHashMap<String,Object>(); response.put("answer",answer);response.put("category",category);response.put("answered",answered);response.put("responseType",type);response.put("sources",sources);response.put("suggestions",Arrays.asList("LBSCEK library information","LBSCEK admission procedure","LBSCEK bus service"));return response;
    }
    private boolean hasOfficial(RetrievalResult result){for(KnowledgeDocument d:result.getDocuments())if(d.isOfficial())return true;return false;}
    private RetrievalResult officialOnly(RetrievalResult result){List<KnowledgeDocument>d=new ArrayList<KnowledgeDocument>();for(KnowledgeDocument item:result.getDocuments())if(item.isOfficial())d.add(item);return new RetrievalResult(d,result.getConfidence());}
}
