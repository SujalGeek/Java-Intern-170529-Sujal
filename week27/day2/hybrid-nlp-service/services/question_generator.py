# from transformers import AutoTokenizer, AutoModelForSeq2SeqLM


# class QuestionGenerator:
#     """
#     AI-assisted Question Generator
#     - Context grounded
#     - Bloom taxonomy aware
#     - Exam-oriented (NOT generic QA)
#     """

#     def __init__(self):
#         self.model_name = "google/flan-t5-base"
#         self.tokenizer = AutoTokenizer.from_pretrained(self.model_name)
#         self.model = AutoModelForSeq2SeqLM.from_pretrained(self.model_name)

#     def _build_prompt(self, concept, description, keywords, bloom_level):
#         """
#         Strong, exam-safe prompt engineering
#         """

#         keyword_str = ", ".join(keywords)

#         base_instruction = (
#             "You are a university-level computer science examiner.\n"
#             "Generate ONLY ONE exam question.\n"
#             "The question must be technically correct and suitable for a written exam.\n"
#             "Do NOT generate multiple-choice questions.\n"
#             "Do NOT ask reading comprehension or grammar-based questions.\n"
#         )

#         bloom_instructions = {
#             "Remember": (
#                 "Generate a definition-based question.\n"
#                 "Focus on recalling the concept.\n"
#             ),
#             "Understand": (
#                 "Generate a conceptual explanation question.\n"
#                 "Focus on understanding, not definition.\n"
#             ),
#             "Apply": (
#                 "Generate an application-level question.\n"
#                 "The question MUST involve a real Java programming or design scenario.\n"
#                 "The student should APPLY the concept to solve a problem.\n"
#             ),
#             "Analyze": (
#                 "Generate an analysis-level question.\n"
#                 "The question MUST require reasoning, comparison, or justification.\n"
#             )
#         }

#         prompt = (
#             base_instruction
#             + bloom_instructions.get(bloom_level, bloom_instructions["Understand"])
#             + f"\nSubject: Java Object-Oriented Programming\n"
#             + f"Concept: {concept}\n"
#             + f"Description: {description}\n"
#             + f"Keywords: {keyword_str}\n"
#             + "\nExam Question:"
#         )

#         return prompt

#     def generate_questions(
#         self,
#         concept,
#         description,
#         keywords,
#         bloom_level="Understand",
#         count=3
#     ):
#         """
#         Generate Bloom-level aligned exam questions
#         """

#         prompt = self._build_prompt(
#             concept=concept,
#             description=description,
#             keywords=keywords,
#             bloom_level=bloom_level
#         )

#         inputs = self.tokenizer(
#             prompt,
#             return_tensors="pt",
#             truncation=True,
#             max_length=512
#         )

#         outputs = self.model.generate(
#             **inputs,
#             max_length=120,
#             num_return_sequences=count,
#             do_sample=True,
#             temperature=0.7,
#             top_p=0.9
#         )

#         questions = []
#         for out in outputs:
#             text = self.tokenizer.decode(out, skip_special_tokens=True).strip()

#             # Hard safety filter
#             if (
#                 len(text) > 20
#                 and "?" in text
#                 and text not in questions
#             ):
#                 questions.append(text)

#         return questions

# import os
# from transformers import AutoModelForSeq2SeqLM, AutoTokenizer
# import torch

# class QuestionGenerator:
#     def __init__(self):
#         print("⏳ Loading Exam Generation Model (Flan-T5-Base)...")
#         # We use 'google/flan-t5-base' because it is smarter at following instructions than standard T5
#         self.model_name = "google/flan-t5-base"
        
#         try:
#             self.tokenizer = AutoTokenizer.from_pretrained(self.model_name)
#             self.model = AutoModelForSeq2SeqLM.from_pretrained(self.model_name)
#             print("✅ Exam Gen AI Loaded Successfully!")
#         except Exception as e:
#             print(f"❌ Error loading model: {e}")
#             raise e

#     def generate(self, context_text, bloom_level="Remember"):
#         """
#         Generates a single question based on the text and Bloom's level.
#         """
#         # 1. Prompt Engineering: Mapping Bloom's Taxonomy to AI Instructions
#         instruction = ""
#         if bloom_level == "Remember":
#             instruction = "Generate a question asking to explain how the concept works in: "
#             # instruction = "Generate a question that asks for a definition or fact based on this text: "
#         elif bloom_level == "Understand":
#             instruction = "Generate a question that asks for an explanation or summary of this text: "
#         elif bloom_level == "Apply":
#             instruction = "Generate a coding scenario or a 'give an example' question based on: "
#             # instruction = "Generate a coding scenario or real-world example question based on this text: "
#         elif bloom_level == "Analyze":
#             instruction = "Generate a question that asks to compare or analyze the difference between concepts in: "
#             # instruction = "Generate a question that compares concepts or analyzes the logic in this text: "
#         elif bloom_level == "Evaluate":
#             instruction = "Generate a critical thinking question about the advantages or disadvantages of: "
#             # instruction = "Generate a critical thinking question based on this text: "
#         else:
#             instruction = "Generate a question based on: "
#             # instruction = "Generate a question based on this text: "

#         input_text = instruction + context_text

#         # 2. Tokenize Input
#         input_ids = self.tokenizer(input_text, return_tensors="pt", max_length=512, truncation=True).input_ids

#         # 3. Generate Output (Adjust 'num_beams' for better quality)
#         outputs = self.model.generate(
#             input_ids, 
#             max_length=64, 
#             num_beams=5,             # Search the top 5 possibilities
#             do_sample=False,         # Deterministic for exams (we want the best one)
#             early_stopping=True
#         )

#         # 4. Decode Output
#         question = self.tokenizer.decode(outputs[0], skip_special_tokens=True)
#         return question

#     def generate_quiz(self, context, count=3, bloom_level="Remember"):
#         """
#         Generates a list of unique questions.
#         """
#         questions = []
#         attempts = 0
        
#         # Simple loop to try and get 'count' questions
#         # Note: Since T5 is deterministic, getting *different* questions from *identical* input 
#         # is hard without changing parameters. 
#         # Trick: We vary parameters slightly (temperature) or chunks if needed.
#         # For this Final Year Project scope, generating 1 high-quality question per chunk is often enough.
#         # Here we will generate one main question. 
        
#         # If you need MULTIPLE questions from ONE paragraph, we use 'do_sample=True'
        
#         unique_q = set()
        
#         for i in range(count):
#             # We add a little noise to the prompt to get variation if count > 1
#             prompt_variation = input_text = f"Generate question {i+1} based on: {context}"
#             if i == 0: 
#                 q = self.generate(context, bloom_level)
#             else:
#                 # Slightly different prompt for variety
#                 q = self.generate(context + f" [Variation {i}]", bloom_level)
            
#             if q not in unique_q:
#                 unique_q.add(q)
#                 questions.append(q)
        
#         return list(unique_q)
    
# import os
# from transformers import AutoModelForSeq2SeqLM, AutoTokenizer
# import torch

# class QuestionGenerator:
#     def __init__(self):
#         print("⏳ Loading Exam Generation Model (Flan-T5-Base)...")
#         self.model_name = "google/flan-t5-large"
        
#         try:
#             self.tokenizer = AutoTokenizer.from_pretrained(self.model_name)
#             self.model = AutoModelForSeq2SeqLM.from_pretrained(self.model_name)
#             print("✅ Exam Gen AI Loaded Successfully!")
#         except Exception as e:
#             print(f"❌ Error loading model: {e}")
#             raise e
#     def generate(self, context_text, bloom_level="Remember"):
        
#         # --- 1. ROLE-PLAYING PROMPTS (The "Nuclear Option") ---
#         # We force the model into a specific "Persona" to stop it from being lazy.
        
#         prompt = ""
        
#         if bloom_level == "Remember":
#             prompt = f"""
#             Act as a teacher. Write a direct "Define" or "What is" question based on this text.
#             Text: {context_text}
#             Question:
#             """
            
#         elif bloom_level == "Understand":
#             prompt = f"""
#             Act as a teacher. Write a "Why" or "How" question that tests explanation.
#             Do NOT ask a Yes/No question.
#             Text: {context_text}
#             Question:
#             """
            
#         elif bloom_level == "Apply":
#             # 🔥 FORCE CODE / SCENARIO
#             prompt = f"""
#             Act as a coding interviewer. Create a short "Write a code snippet" or "Predict the output" problem based on this text.
#             Do NOT use the phrase "Which of the following".
#             Text: {context_text}
#             Problem:
#             """
            
#         elif bloom_level == "Analyze":
#             prompt = f"""
#             Act as a Professor. Write a question that asks to distinguish or compare two concepts found in this text.
#             Text: {context_text}
#             Question:
#             """
            
#         elif bloom_level == "Evaluate":
#             prompt = f"""
#             Act as a critic. Write a question about the limitations, pros, or cons of the concept in this text.
#             Text: {context_text}
#             Question:
#             """
            
#         else:
#             prompt = f"Generate a question about: {context_text}"

#         # --- 2. GENERATION PARAMETERS ---
#         input_ids = self.tokenizer(prompt, return_tensors="pt", max_length=512, truncation=True).input_ids

#         outputs = self.model.generate(
#             input_ids, 
#             max_length=128, 
#             num_beams=5,
#             do_sample=True,      # Creativity ON
#             temperature=0.9,     # Higher temperature = More risky/creative questions
#             top_k=50,
#             repetition_penalty=2.0, # Strong penalty for repeating "Which of the following"
#             early_stopping=True
#         )

#         question = self.tokenizer.decode(outputs[0], skip_special_tokens=True)
        
#         # --- 3. FINAL SAFETY NET (Python Logic) ---
#         # If the model ignores us and STILL generates an MCQ, we catch it and force a generic "Explain" question.
        
#         banned_phrases = ["which of the following", "choose the correct", "select the best"]
#         if any(phrase in question.lower() for phrase in banned_phrases):
#             # Fallback strategy: Force an "Explain" question instead
#             fallback_prompt = f"Explain the concept of {context_text[:20]}... in your own words."
#             return f"Explain the main concept discussed in the text: '{context_text[:30]}...'"
            
#         return question
    
    
    # def generate(self, context_text, bloom_level="Remember"):
        
    #     # --- PROMPT ENGINEERING WITH CONSTRAINTS ---
        
    #     instruction = ""
        
    #     if bloom_level == "Remember":
    #         # Simple direct question
    #         instruction = f"""
    #         Read the text below.
    #         Task: Write a simple 'What is' or 'Define' question about the main concept.
    #         Constraint: Do not ask "Which of the following".
    #         Text: {context_text}
    #         Question:
    #         """
            
    #     elif bloom_level == "Understand":
    #         # Explanation
    #         instruction = f"""
    #         Read the text below.
    #         Task: Write a question asking to explain 'Why' or 'How' the concept works.
    #         Constraint: Do not use the phrase "Which of the following".
    #         Text: {context_text}
    #         Question:
    #         """
            
    #     elif bloom_level == "Apply":
    #         # 🔥 THE FIX FOR YOUR ISSUE 🔥
    #         # We explicitly ask for a "Problem Statement" or "Code Snippet request"
    #         instruction = f"""
    #         Read the text below.
    #         Task: Create a practical coding problem or a "Suppose you have..." scenario based on this text.
    #         Constraint: Do NOT write a multiple choice question. Do NOT say "Which of the following".
    #         Example: "Write a Java class that overrides the method..."
    #         Text: {context_text}
    #         Question:
    #         """
            
    #     elif bloom_level == "Analyze":
    #         instruction = f"""
    #         Read the text below.
    #         Task: Write a question that asks to compare two concepts or find the difference.
    #         Text: {context_text}
    #         Question:
    #         """
            
    #     else:
    #         instruction = f"Generate a question about: {context_text}"

    #     # --- GENERATION ---
    #     input_ids = self.tokenizer(instruction, return_tensors="pt", max_length=512, truncation=True).input_ids

    #     outputs = self.model.generate(
    #         input_ids, 
    #         max_length=128, 
    #         num_beams=10,        # Increased beams to find better sentence structures
    #         do_sample=True,      # Keep creativity ON
    #         temperature=0.8,     # Creativity level
    #         repetition_penalty=1.5, # Stops it from repeating phrases
    #         early_stopping=True
    #     )

    #     question = self.tokenizer.decode(outputs[0], skip_special_tokens=True)
        
    #     # Final Safety Check: If it STILL generates "Which of the following", we force a retry logic (optional)
    #     # But usually, the negative constraint fixes it.
        
    #     return question
    
    # def generate(self, context_text, bloom_level="Remember"):
        
    #     # --- 1. AGGRESSIVE PROMPT ENGINEERING ---
    #     # We give it a specific 'pattern' to follow so it doesn't default to "What is..."
        
    #     instruction = ""
        
    #     if bloom_level == "Remember":
    #         # Simple definition
    #         instruction = f"Write a short question asking for the definition of the main term in this text: "
            
    #     elif bloom_level == "Understand":
    #         # Explanation
    #         instruction = f"Write a question asking 'Why' or 'How' the concept works in this text: "
            
    #     elif bloom_level == "Apply":
    #         # 🔥 FORCE A SCENARIO: We explicitly say "coding problem" or "example"
    #         instruction = f"Write a practical coding problem or scenario-based question where a student must use the concepts from this text: "
            
    #     elif bloom_level == "Analyze":
    #         # Comparison
    #         instruction = f"Write a question asking to compare or find the difference between two concepts mentioned in: "
            
    #     elif bloom_level == "Evaluate":
    #         # Critical thinking
    #         instruction = f"Write a tricky question about the limitations or advantages of the concept in: "
            
    #     else:
    #         instruction = "Generate a question based on: "

    #     # Combine: Instruction + Context
    #     input_text = instruction + context_text

    #     # --- 2. ENCODING ---
    #     input_ids = self.tokenizer(input_text, return_tensors="pt", max_length=512, truncation=True).input_ids

    #     # --- 3. GENERATION WITH 'CREATIVITY' ---
    #     # do_sample=True allows it to pick less common words (like "Create", "Suppose", "Write")
    #     # temperature=0.7 makes it creative but not crazy
        
    #     outputs = self.model.generate(
    #         input_ids, 
    #         max_length=128,          # Give it space to write a longer scenario
    #         num_beams=5,             # Look for best quality
    #         do_sample=True,          # <--- ENABLE CREATIVITY (Crucial for 'Apply')
    #         temperature=0.7,         # <--- Control randomness
    #         top_k=50,
    #         top_p=0.95,
    #         early_stopping=True
    #     )

    #     question = self.tokenizer.decode(outputs[0], skip_special_tokens=True)
    #     return question

    # def generate_quiz(self, context, count=1, bloom_level="Remember"):
    #     """
    #     Generates a list of unique questions.
    #     """
    #     questions = []
    #     unique_q = set()
        
    #     # Retry loop: If it generates a duplicate or empty question, try again
    #     max_retries = count * 3 
    #     attempts = 0

    #     while len(questions) < count and attempts < max_retries:
    #         attempts += 1
            
    #         # Add slight noise to prompt for variety if requesting multiple
    #         if len(questions) > 0:
    #              q = self.generate(context + " ", bloom_level) # subtle change to force new path
    #         else:
    #              q = self.generate(context, bloom_level)

    #         # Cleanup: Remove "Question:" if the model added it
    #         q = q.replace("Question:", "").strip()

    #         # Filter: If it's too short (garbage), ignore it
    #         if len(q) > 10 and q not in unique_q:
    #             unique_q.add(q)
    #             questions.append(q)
        
    #     return list(unique_q)


# from transformers import AutoTokenizer, AutoModelForSeq2SeqLM


# class QuestionGenerator:
#     """
#     AI-assisted Question Generator
#     - Context grounded
#     - Bloom taxonomy aware
#     - Exam-oriented (NOT generic QA)
#     """

#     def __init__(self):
#         self.model_name = "google/flan-t5-small"
#         self.tokenizer = AutoTokenizer.from_pretrained(self.model_name)
#         self.model = AutoModelForSeq2SeqLM.from_pretrained(self.model_name)

#     def _build_prompt(self, concept, description, keywords, bloom_level):
#         keyword_str = ", ".join(keywords)

#         base_instruction = (
#         "You are a senior university examiner setting a written Java exam.\n"
#         "Generate ONE descriptive question only.\n"
#         "The question MUST be suitable for a 5–10 mark exam.\n"
#         "Do NOT generate MCQs.\n"
#         "Do NOT ask definition or recall questions.\n"
#         )

#         bloom_instruction = ""

#         if bloom_level == "Apply":
#             bloom_instruction = (
#             "Bloom Level: APPLY\n"
#             "The question MUST present a real programming or design scenario.\n"
#             "The student must APPLY the concept to solve a problem.\n"
#             "The question MUST start with phrases like:\n"
#             "- \"Design\"\n"
#             "- \"Implement\"\n"
#             "- \"Demonstrate\"\n"
#             "- \"Given a scenario\"\n"
#         )
#         elif bloom_level == "Analyze":
#             bloom_instruction = (
#             "Bloom Level: ANALYZE\n"
#             "The question MUST require comparison, justification, or reasoning.\n"
#         )
#         else:
#             bloom_instruction = (
#             "Bloom Level: UNDERSTAND\n"
#             "The question must test conceptual understanding.\n"
#         )

#         prompt = (

#             base_instruction
#         +   bloom_instruction
#         + "\nSubject: Java Object-Oriented Programming\n"
#         + f"Concept: {concept}\n"
#         + f"Description: {description}\n"
#         + f"Keywords: {keyword_str}\n"
#         + "\nExam Question:"
#     )

#         return prompt


#     # def _build_prompt(self, concept, description, keywords, bloom_level):
#     #     keyword_str = ", ".join(keywords)

#     #     base_instruction = (
#     #         "You are a university-level computer science examiner.\n"
#     #         "Generate ONLY ONE descriptive exam question.\n"
#     #         "The question must be technically correct.\n"
#     #         "Do NOT generate MCQs.\n"
#     #         "Do NOT ask vague or generic questions.\n"
#     #     )

#     #     bloom_instructions = {
#     #         "Remember": (
#     #             "Generate a definition-based question that tests recall.\n"
#     #         ),
#     #         "Understand": (
#     #             "Generate a conceptual explanation question.\n"
#     #             "Do NOT ask for a definition.\n"
#     #         ),
#     #         "Apply": (
#     #             "Generate an application-level question.\n"
#     #             "The question MUST involve a real Java programming or design scenario.\n"
#     #         ),
#     #         "Analyze": (
#     #             "Generate an analysis-level question.\n"
#     #             "The question MUST require reasoning or comparison.\n"
#     #         )
#     #     }

#     #     prompt = (
#     #         base_instruction
#     #         + bloom_instructions.get(bloom_level, bloom_instructions["Understand"])
#     #         + "\nSubject: Java Object-Oriented Programming\n"
#     #         + f"Concept: {concept}\n"
#     #         + f"Description: {description}\n"
#     #         + f"Keywords: {keyword_str}\n"
#     #         + "\nExam Question:"
#     #     )

#     #     return   prompt

#     def generate_questions(
#         self,
#         concept,
#         description,
#         keywords,
#         bloom_level="Understand",
#         count=3
#     ):
#         prompt = self._build_prompt(
#             concept=concept,
#             description=description,
#             keywords=keywords,
#             bloom_level=bloom_level
#         )

#         inputs = self.tokenizer(
#             prompt,
#             return_tensors="pt",
#             truncation=True,
#             max_length=512
#         )

#         outputs = self.model.generate(
#             **inputs,
#             max_length=120,
#             num_return_sequences=count,
#             do_sample=True,
#             temperature=0.7,
#             top_p=0.9
#         )

#         questions = []
#         for out in outputs:
#             text = self.tokenizer.decode(out, skip_special_tokens=True).strip()

#             # Safety filters
#             if (
#                 len(text) > 25
#                 and "?" in text
#                 and text not in questions
#             ):
#                 questions.append(text)

#         return questions
from transformers import AutoTokenizer, AutoModelForSeq2SeqLM
import torch


class QuestionGenerator:
    """
    AI-assisted Question Generator
    - Context grounded
    - Bloom taxonomy enforced
    - Exam-oriented (descriptive questions only)
    - Hybrid: LLM + Rule-based validation
    """

    def __init__(self):
        self.model_name = "google/flan-t5-base"
        self.tokenizer = AutoTokenizer.from_pretrained(self.model_name)
        self.model = AutoModelForSeq2SeqLM.from_pretrained(self.model_name)
        self.model.eval()

    # -------------------------------------------------
    # Prompt Builder (STRICT & EXAM-SAFE)
    # -------------------------------------------------
    def _build_prompt(self, concept, description, keywords, bloom_level):
        keyword_str = ", ".join(keywords)

        base_instruction = (
            "You are a senior university examiner setting a written exam.\n"
            "Generate ONLY ONE descriptive exam question.\n"
            "Do NOT generate multiple-choice questions.\n"
            "Do NOT ask definition-only or recall questions.\n"
            "The question must be suitable for a 5–10 mark answer.\n"
        )

        bloom_instruction = ""

        if bloom_level == "Remember":
            bloom_instruction = (
                "Bloom Level: REMEMBER\n"
                "Generate a definition-based question.\n"
            )

        elif bloom_level == "Understand":
            bloom_instruction = (
                "Bloom Level: UNDERSTAND\n"
                "Generate a conceptual explanation question.\n"
                "Do NOT ask for a definition.\n"
            )

        elif bloom_level == "Apply":
            bloom_instruction = (
                "Bloom Level: APPLY\n"
                "Generate an application-level question.\n"
                "The question MUST involve a real Java programming or design scenario.\n"
                "The student must APPLY the concept to solve a problem.\n"
                "The question MUST start with words like:\n"
                "- Design\n"
                "- Implement\n"
                "- Demonstrate\n"
                "- Given a scenario\n"
            )

        elif bloom_level == "Analyze":
            bloom_instruction = (
                "Bloom Level: ANALYZE\n"
                "Generate a question that requires comparison, reasoning, or justification.\n"
            )

        prompt = (
            base_instruction
            + bloom_instruction
            + "\nSubject: Java Object-Oriented Programming\n"
            + f"Concept: {concept}\n"
            + f"Description: {description}\n"
            + f"Keywords: {keyword_str}\n"
            + "\nExam Question:"
        )

        return prompt

    # -------------------------------------------------
    # Bloom-level Validators (RULE ENGINE)
    # -------------------------------------------------
    def _is_valid_apply_question(self, text):
        required_verbs = ["design", "implement", "demonstrate", "given"]
        return (
            any(v in text.lower() for v in required_verbs)
            and "?" in text
            and len(text) > 35
            and "which of the following" not in text.lower()
        )

    def _is_valid_understand_question(self, text):
        banned_words = ["define", "what is"]
        return (
            "?" in text
            and len(text) > 25
            and not any(b in text.lower() for b in banned_words)
        )

    def _is_valid_remember_question(self, text):
        return "?" in text and len(text) > 20

    def _is_valid_analyze_question(self, text):
        required_words = ["compare", "analyze", "justify", "evaluate"]
        return (
            any(w in text.lower() for w in required_words)
            and "?" in text
            and len(text) > 35
        )

    # -------------------------------------------------
    # Main Generator (HYBRID LOGIC)
    # -------------------------------------------------
    def generate_questions(
        self,
        concept,
        description,
        keywords,
        bloom_level="Understand",
        count=3
    ):
        prompt = self._build_prompt(
            concept=concept,
            description=description,
            keywords=keywords,
            bloom_level=bloom_level
        )

        inputs = self.tokenizer(
            prompt,
            return_tensors="pt",
            truncation=True,
            max_length=512
        )

        outputs = self.model.generate(
            **inputs,
            max_length=140,
            do_sample=True,
            temperature=0.7,
            top_p=0.9,
            num_return_sequences=count * 3  # generate extra → filter later
        )

        questions = []

        for out in outputs:
            text = self.tokenizer.decode(
                out,
                skip_special_tokens=True
            ).strip()

            # ---------------- RULE FILTER ----------------
            is_valid = False

            if bloom_level == "Apply":
                is_valid = self._is_valid_apply_question(text)
            elif bloom_level == "Analyze":
                is_valid = self._is_valid_analyze_question(text)
            elif bloom_level == "Remember":
                is_valid = self._is_valid_remember_question(text)
            else:  # Understand
                is_valid = self._is_valid_understand_question(text)

            if is_valid and text not in questions:
                questions.append(text)

            if len(questions) >= count:
                break

        return questions
