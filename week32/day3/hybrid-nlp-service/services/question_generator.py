import os
import json
from dotenv import load_dotenv
from langchain_groq import ChatGroq
from langchain_core.prompts import ChatPromptTemplate

# Load environment variables from .env
load_dotenv()


class QuestionGenerator:

    def __init__(self):
        print("🔹 Loading Groq LLM...")

        api_key = os.getenv("GROQ_API_KEY")

        if not api_key:
            raise ValueError("❌ GROQ_API_KEY not found in .env file.")

        self.llm = ChatGroq(
            model="llama-3.1-8b-instant",
            temperature=0.6,
            max_tokens=700
        )

        print("✅ Groq LLM Ready")

    def generate_questions(self, context, bloom_level="Understand", count=3):

        bloom_instruction = {
            "Remember": "Generate recall-based descriptive questions.",
            "Understand": "Generate explanation-based conceptual questions.",
            "Apply": "Generate real coding scenario questions requiring application.",
            "Analyze": "Generate analytical reasoning questions requiring comparison or justification."
        }

        system_prompt = f"""
You are a university-level Java programming examiner.

Generate {count} descriptive exam questions.
Do NOT create multiple-choice questions.
Match Bloom level strictly: {bloom_level}
Instruction: {bloom_instruction.get(bloom_level, bloom_instruction["Understand"])}

Return ONLY valid JSON:
{{{{ "questions": ["Q1", "Q2"] }}}}
"""

        full_prompt = system_prompt + "\nReference Context:\n" + context

        print("\n Sending request to Groq...")
        print("Bloom Level:", bloom_level)
        print("Context Preview:", context[:300], "\n")

        try:
            response = self.llm.invoke(full_prompt)

            content = response.content.strip()

            print("RAW LLM Output:\n", content, "\n")

            if content.startswith("```"):
                content = content.replace("```json", "").replace("```", "").strip()

            parsed = json.loads(content)

            return parsed.get("questions", [])

        except Exception as e:
            print("Groq Error:", str(e))
            return []
        

    def generate_reference_answer(self, context, question, bloom_level):

        print("\n🔹 Sending request to Groq LLM for Reference Answer...")

        prompt = f"""
You are a university-level Java programming professor.

Generate a detailed model reference answer suitable for a 10-mark exam question.

STRICT RULES:
- Use ONLY the provided reference context.
- Do NOT add external knowledge.
- Do NOT hallucinate.
- Align answer difficulty with Bloom Level: {bloom_level}
- Write in academic style.
- Minimum 150 words.

Question:
{question}

Reference Context:
{context}

Return ONLY the reference answer text.
Do NOT return JSON.
"""

        try:
            response = self.llm.invoke(prompt)
            answer = response.content.strip()

            print("\n📥 RAW Reference Answer:\n")
            print(answer[:1000])

            return answer

        except Exception as e:
            print("Reference Generation Error:", str(e))
            return "Failed to generate reference answer."
        
