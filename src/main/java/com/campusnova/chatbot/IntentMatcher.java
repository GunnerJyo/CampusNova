package com.campusnova.chatbot; import com.campusnova.model.Faq; public interface IntentMatcher { double score(String normalizedQuestion,Faq faq); }
