package com.campusnova.knowledge;

import com.campusnova.model.OfficialKnowledge;
import com.campusnova.repository.*;
import com.campusnova.service.SemanticTextService;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseKnowledgeSourceTest {
    @Test void principalQuestionUsesOnlyCurrentSpecificPrincipalPage() {
        OfficialKnowledge current=record("https://lbscek.ac.in/principal/","Prof. (Dr.) Mohammad Sekoor T. is Principal of LBS College of Engineering.",100);
        OfficialKnowledge stale=record("https://lbscek.ac.in/admission-keam/","Dr. K. V. Anil Kumar was principal.",80);
        OfficialKnowledgeRepository official=mock(OfficialKnowledgeRepository.class);when(official.findByActiveTrueAndOfficialSourceTrueOrderByImportedAtDesc()).thenReturn(Arrays.asList(stale,current));
        DatabaseKnowledgeSource source=new DatabaseKnowledgeSource(mock(FaqRepository.class),mock(AnnouncementRepository.class),official,new SemanticTextService());
        List<KnowledgeDocument> result=source.findRelevant("Who is the current principal of LBSCEK?",Collections.emptyList());
        assertEquals(1,result.size());assertTrue(result.get(0).getContent().contains("Mohammad Sekoor"));assertEquals("https://lbscek.ac.in/principal/",result.get(0).getSourceUrl());
    }
    private OfficialKnowledge record(String page,String text,int priority){OfficialKnowledge r=new OfficialKnowledge(page+"#section","LBSCEK Principal","WEB_PAGE","Administration",text);r.setSourcePageUrl(page);r.setOfficialSource(true);r.setVerified(true);r.setActive(true);r.setSourcePriority(priority);return r;}
}
