package com.campusnova.knowledge;

import com.campusnova.model.*;
import com.campusnova.repository.*;
import com.campusnova.service.SemanticTextService;
import org.springframework.stereotype.Component;
import java.util.*;

/** Retrieval applies source freshness and specificity before any AI boundary. */
@Component public class DatabaseKnowledgeSource implements KnowledgeSource {
    private final FaqRepository faqs; private final AnnouncementRepository announcements; private final OfficialKnowledgeRepository official; private final SemanticTextService semantic;
    public DatabaseKnowledgeSource(FaqRepository f,AnnouncementRepository a,OfficialKnowledgeRepository o,SemanticTextService s){faqs=f;announcements=a;official=o;semantic=s;}
    public List<KnowledgeDocument> findRelevant(String q,Collection<String> memory){
        List<OfficialKnowledge> records=official.findByActiveTrueAndOfficialSourceTrueOrderByImportedAtDesc(); boolean principal=isPrincipalQuestion(q);
        if(principal){List<OfficialKnowledge> exact=new ArrayList<OfficialKnowledge>();for(OfficialKnowledge d:records)if(d.getSourcePageUrl()!=null&&d.getSourcePageUrl().contains("/principal/"))exact.add(d);records=exact;}
        List<Scored> scored=new ArrayList<Scored>();
        for(OfficialKnowledge d:records){KnowledgeDocument doc=new KnowledgeDocument(d.getTitle(),d.getCategory(),d.getTitle()+"\n"+d.getExtractedText(),d.isVerified(),true,d.getSourcePageUrl()==null?d.getSourceUrl():d.getSourcePageUrl());double score=semantic.score(q,memory,doc.getContent());double currentLeadership=principal&&"Current Principal".equals(d.getSectionHeading())?2:0;if(principal||relevant(q,doc,score,semantic.overlap(q,memory,doc.getContent())))scored.add(new Scored(doc,score+.20+d.getSourcePriority()/1000.0+currentLeadership,d.getImportedAt()));}
        if(!principal){for(Faq f:faqs.findByActiveTrue()){KnowledgeDocument d=new KnowledgeDocument("FAQ: "+f.getQuestion(),f.getCategory().getName(),f.getQuestion()+"\n"+f.getAnswer()+"\n"+f.getKeywords(),false,false,null);double score=semantic.score(q,memory,d.getContent());if(relevant(q,d,score,semantic.overlap(q,memory,d.getContent())))scored.add(new Scored(d,score,null));}for(Announcement a:announcements.findAll()){KnowledgeDocument d=new KnowledgeDocument("Announcement: "+a.getTitle(),"Announcements",a.getTitle()+"\n"+a.getBody(),false,false,null);double score=semantic.score(q,memory,d.getContent());if(relevant(q,d,score,semantic.overlap(q,memory,d.getContent())))scored.add(new Scored(d,score,null));}}
        Collections.sort(scored,(a,b)->{int order=Double.compare(b.score,a.score);return order!=0?order:Comparator.nullsLast(Comparator.<java.time.LocalDateTime>naturalOrder()).compare(b.importedAt,a.importedAt);});List<KnowledgeDocument> result=new ArrayList<KnowledgeDocument>();for(Scored s:scored)if(result.size()<(principal?1:4))result.add(s.document);return result;
    }
    private boolean isPrincipalQuestion(String q){String n=q.toLowerCase(Locale.ROOT);return n.contains("principal")||n.contains("heads lbscek")||n.contains("head of the college");}
    private boolean relevant(String question,KnowledgeDocument d,double score,int overlap){return semantic.hasStrongTopicMatch(question,d.getContent())||(overlap>=2&&score>=.35);}
    private static class Scored{KnowledgeDocument document;double score;java.time.LocalDateTime importedAt;Scored(KnowledgeDocument d,double s,java.time.LocalDateTime i){document=d;score=s;importedAt=i;}}
}
