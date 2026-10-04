package com.campusnova.service;

import com.campusnova.knowledge.KnowledgeDocument;
import org.springframework.stereotype.Service;
import java.util.*;

/** Converts one verified section into a short student-facing answer; it never returns a raw document. */
@Service
public class OfficialAnswerFormatter {
    public String format(String question, KnowledgeDocument document) {
        String content=document.getContent().replaceFirst("^[^\\n]*\\n?", "").replaceAll("\\s+", " ").trim();
        if(content.isEmpty()) return "The relevant official LBSCEK information is available in the cited source.";
        Set<String> terms=new HashSet<String>(Arrays.asList(question.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")));
        List<String> selected=new ArrayList<String>();
        for(String sentence:content.split("(?<=[.!?])\\s+")){String clean=sentence.trim();if(clean.length()<18)continue;String lower=clean.toLowerCase(Locale.ROOT);boolean matches=false;for(String term:terms)if(term.length()>2&&lower.contains(term)){matches=true;break;}if(matches||selected.isEmpty())selected.add(clean);if(selected.size()==2)break;}
        String answer=String.join(" ",selected);
        if(answer.length()>650) answer=answer.substring(0,650).replaceFirst("\\s+\\S*$","")+"…";
        return answer;
    }
}
