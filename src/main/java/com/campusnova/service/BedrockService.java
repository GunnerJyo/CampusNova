package com.campusnova.service; import com.campusnova.knowledge.RetrievalResult; import java.util.*;
/** External AI boundary, intentionally easy to mock in tests. */
public interface BedrockService { Optional<String> generate(String question,RetrievalResult context); }
