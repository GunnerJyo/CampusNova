package com.campusnova.knowledge; import java.util.*; public interface KnowledgeSource { List<KnowledgeDocument> findRelevant(String question,Collection<String> conversationTerms); }
