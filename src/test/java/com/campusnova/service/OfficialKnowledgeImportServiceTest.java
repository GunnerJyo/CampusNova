package com.campusnova.service;

import com.campusnova.config.CollegeProfile;
import com.campusnova.repository.OfficialKnowledgeRepository;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class OfficialKnowledgeImportServiceTest {
    @Test void extractsOnlyMeaningfulMainSectionsAndExcludesNavigation() {
        CollegeProfile profile=new CollegeProfile(); profile.setOfficialDomain("https://lbscek.ac.in/");
        OfficialKnowledgeImportService importer=new OfficialKnowledgeImportService(mock(OfficialKnowledgeRepository.class),profile);
        String html="<html><head><title>Central Library | LBSCEK</title></head><body><header>Home About Departments Admissions Login</header><nav>Home Academics Facilities Contact</nav><main><h1>Central Library</h1><p>The central library supports students and faculty with print and digital learning resources.</p><h2>Library Services</h2><p>Reference, circulation, and reading facilities are available for the LBSCEK academic community.</p><h2>Working Hours</h2><p>The library publishes current working hours through official college notices.</p></main><footer>Copyright Follow us Home Contact</footer></body></html>";
        List<OfficialKnowledgeImportService.ExtractedSection> sections=importer.extractSections("https://lbscek.ac.in/central-library/",html);
        assertEquals(3,sections.size());
        assertTrue(sections.stream().allMatch(s->!s.text.contains("Home About")&&!s.text.contains("Copyright")));
        assertEquals("Library Services",sections.get(1).heading);
    }
    @Test void rejectsNonOfficialSource() throws Exception {
        CollegeProfile profile=new CollegeProfile(); profile.setOfficialDomain("https://lbscek.ac.in/");
        OfficialKnowledgeImportService importer=new OfficialKnowledgeImportService(mock(OfficialKnowledgeRepository.class),profile);
        assertThrows(IllegalArgumentException.class,()->importer.canonicalOfficialUrl("https://example.com/library"));
    }
    @Test void principalProfileIsStoredAsDedicatedCurrentLeadershipSection() {
        CollegeProfile profile=new CollegeProfile(); profile.setOfficialDomain("https://lbscek.ac.in/");
        OfficialKnowledgeImportService importer=new OfficialKnowledgeImportService(mock(OfficialKnowledgeRepository.class),profile);
        String html="<html><title>Principal – LBSCEK</title><main><h1>Principal</h1><div>Prof. (Dr.) Mohammad Sekoor T. LBS College of Engineering Kasaragod, Kerala.</div><h4>Educational Qualifications</h4><p>Mechanical Engineering qualifications and experience.</p></main></html>";
        List<OfficialKnowledgeImportService.ExtractedSection> sections=importer.extractSections("https://lbscek.ac.in/principal/",html);
        assertEquals("Current Principal",sections.get(0).heading);assertTrue(sections.get(0).text.contains("Mohammad Sekoor"));assertFalse(sections.get(0).text.contains("Educational Qualifications"));
    }
}
