# Chatbot algorithm
1. Lowercase and remove punctuation from the question.
2. Convert it to a token set.
3. For every active FAQ, create a token set from the question, keywords, and category.
4. Score the overlap as matching query tokens / query tokens.
5. Reply with the best FAQ at 20% confidence or higher; otherwise present a safe fallback and suggestions.

`IntentMatcher` makes future strategies—synonyms, weighted terms or edit distance—pluggable without rewriting the service.
