package com.campusnova.config;

import com.campusnova.repository.OfficialKnowledgeRepository;
import com.campusnova.service.OfficialKnowledgeImportService;
import org.slf4j.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;

/** Loads verified official knowledge once for a fresh production database. */
@Configuration public class OfficialKnowledgeBootstrap {
    private static final Logger log=LoggerFactory.getLogger(OfficialKnowledgeBootstrap.class);
    @Bean CommandLineRunner importOfficialKnowledgeOnEmptyDatabase(OfficialKnowledgeRepository repository,OfficialKnowledgeImportService importer){return args->{
        if(!repository.existsByActiveTrueAndOfficialSourceTrueAndPageType("WEB_PAGE")){int imported=importer.importCorePages().size();log.info("Imported {} official LBSCEK knowledge sections for a fresh database",imported);}
    };}
}
