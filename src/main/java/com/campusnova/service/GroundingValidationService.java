package com.campusnova.service; import com.campusnova.knowledge.RetrievalResult; import org.springframework.stereotype.Service;
@Service public class GroundingValidationService { public boolean acceptable(String answer,RetrievalResult context){return answer!=null&&answer.trim().length()>2&&answer.length()<5000&&context.hasContext();} }
