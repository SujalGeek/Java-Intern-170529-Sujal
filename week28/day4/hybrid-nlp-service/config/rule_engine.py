# class RuleEngine:

#     @staticmethod
#     def keyword_coverage(answer,keywords):
#         if not keywords:
#             return 0.0
#         answer = answer.lower()
#         matched = sum (1 for k in keywords if k.lower() in answer)
#         return round(matched/len(keywords), 3)


#     @staticmethod
#     def grammar_score(answer):
#         length = len(answer.split())
#         if length < 5:
#             return 0.3
#         elif length > 150:
#             return 0.6
#         return 0.9
    

# config/rule_engine.py

def keyword_coverage(student_answer, keywords):
    if not keywords:
        return 0.0

    student_text = student_answer.lower()
    matched = sum(1 for k in keywords if k.lower() in student_text)

    return round(matched / len(keywords), 3)


def grammar_score(student_answer):
    word_count = len(student_answer.split())

    if word_count < 5:
        return 0.3
    elif word_count > 150:
        return 0.6
    else:
        return 0.9


def bloom_cap(score, bloom_level):
    bloom_limits = {
        "Remember": 0.6,
        "Understand": 0.75,
        "Apply": 0.85,
        "Analyze": 1.0
    }
    return min(score, bloom_limits.get(bloom_level, 1.0))
