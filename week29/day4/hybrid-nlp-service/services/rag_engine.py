import fitz 
import os
from groq import Groq
import torch
from sentence_transformers import SentenceTransformer, util
import PyPDF2


class RAGEngine:

    def __init__(self):
        print("Loading SBERT model...")
        self.model = SentenceTransformer("all-MiniLM-L6-v2")
        self.chunks = []
        self.embeddings = None
        print("SBERT Ready\n")


        print("Loading Groq LLM...")
        self.groq_client = Groq(api_key=os.getenv("GROQ_API_KEY"))
        print(" Groq LLM Ready\n")

    def chunk_text(self, text, chunk_size=400, overlap=80):
        words = text.split()
        chunks = []

        for i in range(0, len(words), chunk_size - overlap):
            chunk = " ".join(words[i:i + chunk_size])
            if len(chunk.strip()) > 150:
                chunks.append(chunk.strip())

        return chunks
    # ---------------------------------------------------
    # INDEX PDF
    # ---------------------------------------------------
    def index_pdf(self, pdf_path):

        print("Indexing PDF:", pdf_path)

        text = ""

        with open(pdf_path, "rb") as file:
            reader = PyPDF2.PdfReader(file)
            for page in reader.pages:
                text += page.extract_text() or ""

        text = text.replace("Download free eBooks", "")
        text = text.replace("©", "")
        text = text.replace("\n", " ")
        self.chunks = self.chunk_text(text, chunk_size=350, overlap=75)

        if len(self.chunks) == 0:
            print("No valid chunks extracted.")
            return False

        print("Total Chunks:", len(self.chunks))

        self.embeddings = self.model.encode(
            self.chunks,
            convert_to_tensor=True
        )

        print(" PDF Indexed Successfully\n")
        return True
    


    # ---------------------------------------------------
    # RETRIEVE TOP CHUNKS
    # ---------------------------------------------------
    def retrieve(self, query, top_k=5):

        if self.embeddings is None or len(self.chunks) == 0:
            print("No indexed data found.")
            return [], 0.0, "none"

        print("Running Retrieval for:", query)

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

        top_scores = score_values[:3]
        weights = [0.6, 0.3, 0.1][:len(top_scores)]

        confidence = sum(s * w for s, w in zip(top_scores, weights))
        confidence = max(0,min(confidence,1))
        

        print("\n==FULL RETRIEVED CHUNKS===")
        for chunk in retrieved_chunks:
            print(chunk)
            print("-----")

        # Confidence mode
        if confidence > 0.70:
            mode = "high"
        elif confidence > 0.50:
            mode = "medium"
        else:
            mode = "low"

        print("🔹 Retrieval Confidence:", round(confidence, 3))
        print("🔹 Retrieval Mode:", mode, "\n")

        print("🔹 Top Retrieved Context Preview:")
        for i, chunk in enumerate(retrieved_chunks):
            print(f"[Chunk {i+1}] {chunk[:200]}...\n")

        return retrieved_chunks, round(confidence, 3), mode

    def generate(self, prompt):
        print("Generating content using Groq LLM...")
        try:
            response = self.groq_client.chat.completions.create(
                model="llama-3.1-8b-instant", 
                messages=[
                    {
                        "role": "system",
                        "content": (
                            "You are an academic exam generator.\n"
                            "STRICT RULES:\n"
                            "1. Use ONLY the provided context.\n"
                            "2. Do NOT use external knowledge.\n"
                            "3. If context is insufficient, say 'INSUFFICIENT CONTEXT'.\n"
                            "4. Follow formatting exactly as instructed.\n"
                        )
                    },
                    {
                        "role": "user",
                        "content": prompt
                    }
                ],
                temperature=0.2
            )

            generated_text = response.choices[0].message.content
            print("Groq Generation Complete\n")

            return generated_text
        except Exception as e:
            print("Groq Generation Error:", str(e))
            return ""


