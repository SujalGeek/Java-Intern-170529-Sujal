import fitz  # PyMuPDF
# import numpy as np
# from sentence_transformers import SentenceTransformer, util

# class RAGEngine:
#     def __init__(self):
#         print("Loading SBERT model...")
#         self.model = SentenceTransformer("all-MiniLM-L6-v2")
#         self.chunks = []
#         self.embeddings = None

#     # -------------------------------
#     # 1. Extract Text from PDF
#     # -------------------------------
#     def extract_text_from_pdf(self, file_path):
#         doc = fitz.open(file_path)
#         text = ""
#         for page in doc:
#             text += page.get_text()
#         return text

#     # -------------------------------
#     # 2. Chunking (clean and structured)
#     # -------------------------------
#     def chunk_text(self, text, chunk_size=400):
#         words = text.split()
#         chunks = []

#         for i in range(0, len(words), chunk_size):
#             chunk = " ".join(words[i:i+chunk_size])
#             if len(chunk) > 100:
#                 chunks.append(chunk)

#         return chunks

#     # -------------------------------
#     # 3. Build Vector Store
#     # -------------------------------
#     def build_vector_store(self, text):
#         self.chunks = self.chunk_text(text)
#         self.embeddings = self.model.encode(self.chunks, convert_to_tensor=True)
#         print(f"Vector store created with {len(self.chunks)} chunks")

#     # -------------------------------
#     # 4. Multi-Query Retrieval
#     # -------------------------------
#     def retrieve(self, query, threshold=0.55):
#         if self.embeddings is None:
#             return None, 0.0

#         query_embedding = self.model.encode(query, convert_to_tensor=True)

#         scores = util.cos_sim(query_embedding, self.embeddings)[0]
#         best_score = float(scores.max())
#         best_index = int(scores.argmax())

#         if best_score < threshold:
#             return None, best_score

#         return self.chunks[best_index], best_score


# services/rag_engine.py

# import os
# import numpy as np
# from sentence_transformers import SentenceTransformer, util
# from PyPDF2 import PdfReader

# class RAGEngine:

#     def __init__(self):
#         print("Loading SBERT model...")
#         self.model = SentenceTransformer("all-MiniLM-L6-v2")
#         self.chunks = []
#         self.embeddings = None

#     # ------------------------------------------
#     # PDF INDEXING
#     # ------------------------------------------
#     def index_pdf(self, pdf_path):
#         reader = PdfReader(pdf_path)
#         full_text = ""

#         for page in reader.pages:
#             full_text += page.extract_text() + "\n"

#         self.chunks = self._chunk_text(full_text)
#         self.embeddings = self.model.encode(self.chunks, convert_to_tensor=True)

#         return True

#     # ------------------------------------------
#     # TEXT CHUNKING (Overlapping)
#     # ------------------------------------------
#     def _chunk_text(self, text, chunk_size=400, overlap=100):
#         words = text.split()
#         chunks = []

#         for i in range(0, len(words), chunk_size - overlap):
#             chunk = words[i:i + chunk_size]
#             chunks.append(" ".join(chunk))

#         return chunks

#     # ------------------------------------------
#     # RETRIEVAL
#     # ------------------------------------------
#     def retrieve(self, query, top_k=5):
#         if self.embeddings is None or self.embeddings.size(0) == 0:

#         # if not self.embeddings or len(self.chunks) == 0:
#             return [], 0.0, "not_indexed"

#         query_embedding = self.model.encode(query, convert_to_tensor=True)
#         scores = util.cos_sim(query_embedding, self.embeddings)[0]

#         top_results = np.argsort(-scores.cpu().numpy())[:top_k]

#         retrieved_chunks = [self.chunks[i] for i in top_results]
#         retrieved_scores = [float(scores[i]) for i in top_results]

#         avg_confidence = sum(retrieved_scores[:3]) / min(3, len(retrieved_scores))

#         if avg_confidence >= 0.75:
#             mode = "strong"
#         elif avg_confidence >= 0.60:
#             mode = "medium"
#         else:
#             mode = "weak"

#         return retrieved_chunks, round(avg_confidence, 3), mode

import torch
from sentence_transformers import SentenceTransformer, util
import PyPDF2


class RAGEngine:

    def __init__(self):
        print("🔹 Loading SBERT model...")
        self.model = SentenceTransformer("all-MiniLM-L6-v2")
        self.chunks = []
        self.embeddings = None
        print("✅ SBERT Ready\n")

    # ---------------------------------------------------
    # INDEX PDF
    # ---------------------------------------------------
    def index_pdf(self, pdf_path):

        print("📚 Indexing PDF:", pdf_path)

        text = ""

        with open(pdf_path, "rb") as file:
            reader = PyPDF2.PdfReader(file)
            for page in reader.pages:
                text += page.extract_text() or ""

        # Simple chunking
        self.chunks = [
            chunk.strip()
            for chunk in text.split("\n")
            if len(chunk.strip()) > 100
        ]

        if len(self.chunks) == 0:
            print("❌ No valid chunks extracted.")
            return False

        print("🔹 Total Chunks:", len(self.chunks))

        self.embeddings = self.model.encode(
            self.chunks,
            convert_to_tensor=True
        )

        print("✅ PDF Indexed Successfully\n")
        return True
    


    # ---------------------------------------------------
    # RETRIEVE TOP CHUNKS
    # ---------------------------------------------------
    def retrieve(self, query, top_k=3):

        if self.embeddings is None or len(self.chunks) == 0:
            print("❌ No indexed data found.")
            return [], 0.0, "none"

        print("🔍 Running Retrieval for:", query)

        query_embedding = self.model.encode(
            query,
            convert_to_tensor=True
        )

        scores = util.cos_sim(query_embedding, self.embeddings)[0]

        top_results = torch.topk(scores, k=min(top_k, len(scores)))

        retrieved_chunks = []
        score_values = []

        for idx, score in zip(top_results.indices, top_results.values):
            retrieved_chunks.append(self.chunks[int(idx)])
            score_values.append(float(score))

        confidence = sum(score_values) / len(score_values)

        # Confidence mode
        if confidence > 0.75:
            mode = "high"
        elif confidence > 0.55:
            mode = "medium"
        else:
            mode = "low"

        print("🔹 Retrieval Confidence:", round(confidence, 3))
        print("🔹 Retrieval Mode:", mode, "\n")

        return retrieved_chunks, round(confidence, 3), mode
    
    # def generate(self, prompt):

    #     print("🧠 Generating content using LLM...")

    #     try:
    #         from transformers import pipeline

    #         if not hasattr(self, "generator"):
    #             print("🔹 Loading Text Generation Model...")
    #             self.generator = pipeline(
    #                 "text-generation",
    #                 # model = "GPT2"
    #                 model="google/flan-t5-large"
    #             )

    #         # LIMIT INPUT SIZE (VERY IMPORTANT)
    #         max_input_length = 700  # safe for GPT2
    #         prompt = prompt[:max_input_length]

    #         response = self.generator(
    #             prompt,
    #             max_new_tokens=400,   # reduce from 800
    #             temperature=0.7,
    #             do_sample=True,
    #             # pad_token_id=50256
    #         )

    #         # generated_text = response[0]["generated_text"]
    #         generated_text = response[0]["generated_text"] if "generated_text" in response[0] else response[0]["generated_text"]


    #         print("✅ Generation Complete\n")


    #         # return generated_text
    #         # Try to extract JSON block safely
    #         start = generated_text.find("{")
    #         end = generated_text.rfind("}") + 1

    #         if start != -1 and end != -1:
    #             return generated_text[start:end]
    #         else:
    #             return "{}"


    #     except Exception as e:
    #         print("❌ Generation Error:", str(e))
    #         return "{}"
    

        # ---------------------------------------------------
    # GENERATE USING CONTEXT (LLM CALL)
    # ---------------------------------------------------
    # def generate(self, prompt):

    #     print("🧠 Generating content using LLM...")

    #     try:
    #         from transformers import pipeline

    #         # Lazy load generator (only first time)
    #         if not hasattr(self, "generator"):
    #             print("🔹 Loading Text Generation Model...")
    #             self.generator = pipeline(
    #                 "text-generation",
    #                 model="gpt2",  # You can replace later
    #                 max_new_tokens=800
    #             )

    #         response = self.generator(
    #             prompt,
    #             temperature=0.7,
    #             do_sample=True
    #         )

    #         generated_text = response[0]["generated_text"]

    #         print("✅ Generation Complete\n")

    #         return generated_text

    #     except Exception as e:
    #         print("❌ Generation Error:", str(e))
    #         return "{}"


    

    # def generate(self, prompt):
    #     print("🧠 Generating content using FLAN-T5...")

    #     try:
    #         from transformers import AutoTokenizer, AutoModelForSeq2SeqLM
    #         if not hasattr(self, "tokenizer"):
    #             print("🔹 Loading FLAN-T5 Model...")
    #             self.tokenizer = AutoTokenizer.from_pretrained("google/flan-t5-base")
    #             self.llm = AutoModelForSeq2SeqLM.from_pretrained("google/flan-t5-base")

    #     # Limit prompt size
    #         prompt = prompt[:1500]

    #         inputs = self.tokenizer(
    #             prompt,
    #             return_tensors="pt",
    #             truncation=True,
    #             max_length=512
    #         )

    #         outputs = self.llm.generate(
    #             **inputs,
    #             max_new_tokens=300,
    #             temperature=0.3,
    #             do_sample=False
    #         )

    #         generated_text = self.tokenizer.decode(
    #             outputs[0],
    #             skip_special_tokens=True
    #         )

    #         print("✅ Generation Complete\n")

    #     # Extract JSON safely
    #         start = generated_text.find("{")
    #         end = generated_text.rfind("}") + 1

    #         if start != -1 and end != -1:
    #             return generated_text[start:end]
    #         else:
    #             return "{}"

    #     except Exception as e:
    #         print("❌ Generation Error:", str(e))
    #         return "{}"
    def generate(self, prompt):
        print("🧠 Generating content using FLAN-T5...")
        try:
            from transformers import AutoTokenizer, AutoModelForSeq2SeqLM
            import torch

        # Load model only once
            if not hasattr(self, "tokenizer"):
                print("🔹 Loading FLAN-T5 Model...")
                self.tokenizer = AutoTokenizer.from_pretrained("google/flan-t5-base")
                self.llm = AutoModelForSeq2SeqLM.from_pretrained("google/flan-t5-base")

        # -----------------------------
        # LIMIT PROMPT SIZE (CRITICAL)
        # -----------------------------
            prompt = prompt[:1200]   # safer length

            inputs = self.tokenizer(
                prompt,
                return_tensors="pt",
                truncation=True,
                max_length=512
            )

        # -----------------------------
        # GENERATE OUTPUT
        # -----------------------------
            with torch.no_grad():
                outputs = self.llm.generate(
                    **inputs,
                    max_new_tokens=250,
                    temperature=0.2,     # lower = more structured
                    do_sample=False,     # deterministic output
                    repetition_penalty=1.2
                )

            generated_text = self.tokenizer.decode(
                outputs[0],
                skip_special_tokens=True
            )

            print("✅ Generation Complete\n")

        # -----------------------------
        # CLEAN + EXTRACT JSON
        # -----------------------------
            start = generated_text.find("{")
            end = generated_text.rfind("}")

            if start != -1 and end != -1:
                json_text = generated_text[start:end + 1]
                return json_text.strip()
            else:
                print("⚠️ JSON not detected in model output")
                return "{}"

        except Exception as e:
            print("❌ Generation Error:", str(e))
            return "{}"
