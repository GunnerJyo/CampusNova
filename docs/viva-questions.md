# Likely viva questions
1. **Why no AI API?** The course goal is OOP; a local rule/data-driven matcher is transparent, free, testable and suitable for controlled college information.
2. **How does NOVA learn a new answer?** An admin creates an FAQ; it is persisted and the matcher reads active FAQs on every query.
3. **Where is polymorphism?** `ChatbotService` depends on `IntentMatcher`, so `KeywordMatcher` can be replaced by another implementation.
4. **How is SQL injection prevented?** JPA parameterizes database operations; raw SQL concatenation is not used.
5. **What is the confidence score?** It is the proportion of question tokens found in an FAQ's searchable terms.
6. **What would you improve for production?** BCrypt user accounts, durable token/session storage, audit logs, official data approval workflow, and richer matching strategies.
