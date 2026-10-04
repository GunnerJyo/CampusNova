package com.campusnova.repository;

import com.campusnova.model.OfficialKnowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface OfficialKnowledgeRepository extends JpaRepository<OfficialKnowledge, Long> {
    List<OfficialKnowledge> findByActiveTrueAndOfficialSourceTrueOrderByImportedAtDesc();
    Optional<OfficialKnowledge> findBySourceUrl(String url);
    List<OfficialKnowledge> findBySourcePageUrl(String sourcePageUrl);
    List<OfficialKnowledge> findBySourceUrlAndPageType(String sourceUrl, String pageType);
}
