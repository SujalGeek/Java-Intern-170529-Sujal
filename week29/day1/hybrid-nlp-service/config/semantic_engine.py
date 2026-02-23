# from sentence_transformers import SentenceTransformer , util

# class SemanticEngine:
#     def __init__(self):
#         self.model = SentenceTransformer("finetuned_student_sbert")

#     def similarity(self,student_answer,reference_answers):
#         student_emb = self.model.encode(student_answer)
#         best_score = 0.0

#         for ref in reference_answers:
#             ref_emb = self.model.encode(ref)
#             score = float(util.cos_sim(student_emb,ref_emb)[0][0])
#             best_score = max(best_score,score)

#         return round(best_score,3)



# config/semantic_engine.py

from sentence_transformers import SentenceTransformer, util

class SemanticEngine:
    def __init__(self):
        # Load fine-tuned SBERT model
        self.model = SentenceTransformer("config/finetuned_student_sbert")

    def semantic_similarity(self, student_answer, reference_answers):
        """
        Calculates max semantic similarity between student answer
        and multiple reference answers
        """
        student_embedding = self.model.encode(student_answer)
        max_score = 0.0

        for ref in reference_answers:
            ref_embedding = self.model.encode(ref)
            score = float(util.cos_sim(student_embedding, ref_embedding)[0][0])
            max_score = max(max_score, score)

        return round(max_score, 3)
