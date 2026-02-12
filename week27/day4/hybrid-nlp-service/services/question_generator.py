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
# from transformers import AutoTokenizer, AutoModelForSeq2SeqLM
# import torch


# class QuestionGenerator:
#     """
#     AI-assisted Question Generator
#     - Context grounded
#     - Bloom taxonomy enforced
#     - Exam-oriented (descriptive questions only)
#     - Hybrid: LLM + Rule-based validation
#     """

#     def __init__(self):
#         self.model_name = "google/flan-t5-large"
#         self.tokenizer = AutoTokenizer.from_pretrained(self.model_name)
#         self.model = AutoModelForSeq2SeqLM.from_pretrained(self.model_name)
#         self.model.eval()

#     # -------------------------------------------------
#     # Prompt Builder (STRICT & EXAM-SAFE)
#     # -------------------------------------------------
#     def _build_prompt(self, concept, description, keywords, bloom_level):
#         keyword_str = ", ".join(keywords)

#         base_instruction = (
#             "You are a senior university examiner setting a written exam.\n"
#             "Generate ONLY ONE descriptive exam question.\n"
#             "Do NOT generate multiple-choice questions.\n"
#             "Do NOT ask definition-only or recall questions.\n"
#             "The question must be suitable for a 5–10 mark answer.\n"
#         )

#         bloom_instruction = ""

#         if bloom_level == "Remember":
#             bloom_instruction = (
#                 "Bloom Level: REMEMBER\n"
#                 "Generate a definition-based question.\n"
#             )

#         elif bloom_level == "Understand":
#             bloom_instruction = (
#                 "Bloom Level: UNDERSTAND\n"
#                 "Generate a conceptual explanation question.\n"
#                 "Do NOT ask for a definition.\n"
#             )

#         elif bloom_level == "Apply":
#             bloom_instruction = (
#                 "Bloom Level: APPLY\n"
#                 "Generate an application-level question.\n"
#                 "The question MUST involve a real Java programming or design scenario.\n"
#                 "The student must APPLY the concept to solve a problem.\n"
#                 "The question MUST start with words like:\n"
#                 "- Design\n"
#                 "- Implement\n"
#                 "- Demonstrate\n"
#                 "- Given a scenario\n"
#             )

#         elif bloom_level == "Analyze":
#             bloom_instruction = (
#                 "Bloom Level: ANALYZE\n"
#                 "Generate a question that requires comparison, reasoning, or justification.\n"
#             )

#         prompt = (
#             base_instruction
#             + bloom_instruction
#             + "\nSubject: Java Object-Oriented Programming\n"
#             + f"Concept: {concept}\n"
#             + f"Description: {description}\n"
#             + f"Keywords: {keyword_str}\n"
#             + "\nExam Question:"
#         )

#         return prompt

#     # -------------------------------------------------
#     # Bloom-level Validators (RULE ENGINE)
#     # -------------------------------------------------
#     # def _is_valid_apply_question(self, text):
#     #     required_verbs = ["design", "implement", "demonstrate", "given"]
#     #     return (
#     #         any(v in text.lower() for v in required_verbs)
#     #         and "?" in text
#     #         and len(text) > 35
#     #         and "which of the following" not in text.lower()
#     #     )
#     def _is_valid_apply_question(self, text):
#         apply_indicators = [
#         "design",
#         "implement",
#         "demonstrate",
#         "consider",
#         "given",
#         "write",
#         "how would you",
#         "how can you",
#         "explain how",
#         "develop"
#         ]

#         return (
#         len(text) > 30
#         and any(word in text.lower() for word in apply_indicators)
#         and "which of the following" not in text.lower()
#         )


#     def _is_valid_understand_question(self, text):
#         banned_words = ["define", "what is"]
#         return (
#             "?" in text
#             and len(text) > 25
#             and not any(b in text.lower() for b in banned_words)
#         )

#     def _is_valid_remember_question(self, text):
#         return "?" in text and len(text) > 20

#     def _is_valid_analyze_question(self, text):
#         required_words = ["compare", "analyze", "justify", "evaluate"]
#         return (
#             any(w in text.lower() for w in required_words)
#             and "?" in text
#             and len(text) > 35
#         )

#     # -------------------------------------------------
#     # Main Generator (HYBRID LOGIC)
#     # -------------------------------------------------
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
#             max_length=140,
#             do_sample=True,
#             temperature=0.7,
#             top_p=0.9,
#             num_return_sequences=count * 3  # generate extra → filter later
#         )

#         questions = []

#         for out in outputs:
#             text = self.tokenizer.decode(
#                 out,
#                 skip_special_tokens=True
#             ).strip()
#             print("RAW generated ",text)

#             # ---------------- RULE FILTER ----------------
#             is_valid = False

#             if bloom_level == "Apply":
#                 is_valid = self._is_valid_apply_question(text)
#             elif bloom_level == "Analyze":
#                 is_valid = self._is_valid_analyze_question(text)
#             elif bloom_level == "Remember":
#                 is_valid = self._is_valid_remember_question(text)
#             else:  # Understand
#                 is_valid = self._is_valid_understand_question(text)

#             if is_valid and text not in questions:
#                 questions.append(text)

#             if len(questions) >= count:
#                 break

#         return questions


# this is the below is the main logic that can be used using the GROQ LLM not able to made the RAG then this logic need to use below will the correct one if anything fails bro
# import os
# import json
# from langchain_groq import ChatGroq
# from langchain_core.prompts import ChatPromptTemplate

# class QuestionGenerator:
#     def __init__(self):
#         print("⏳ Loading Groq AI Agent (Llama-3.1-8b)...")
        
#         # ⚠️ Put your actual Groq API Key here, or load it from a .env file
#         os.environ["GROQ_API_KEY"] = "" 
        
#         try:
#             self.llm = ChatGroq(
#                 model="llama-3.1-8b-instant",
#                 temperature=0.7,
#                 max_tokens=500
#             )
#             print("✅ Groq AI Agent Loaded Successfully!")
#         except Exception as e:
#             print(f"❌ Error loading Groq: {e}")
#             raise e

#     def _clean_json_string(self, raw_string: str) -> str:
#         """Removes Markdown backticks from LLM output so JSON can parse it."""
#         cleaned = raw_string.strip()
#         if cleaned.startswith("```json"):
#             cleaned = cleaned[7:]
#         if cleaned.startswith("```"):
#             cleaned = cleaned[3:]
#         if cleaned.endswith("```"):
#             cleaned = cleaned[:-3]
#         return cleaned.strip()

#     def generate(self, context_text, bloom_level="Remember"):
#         """
#         Generates a single question using LangChain and Groq.
#         """
#         # 1. Define the specific task based on Bloom's Level
#         task_instruction = ""
#         if bloom_level == "Remember":
#             task_instruction = "Write a direct 'Define' or 'What is' question about the main concept."
#         elif bloom_level == "Understand":
#             task_instruction = "Write a 'Why' or 'How' question that tests explanation."
#         elif bloom_level == "Apply":
#             task_instruction = "Create a practical coding scenario or real-world problem where the student must use this concept."
#         elif bloom_level == "Analyze":
#             task_instruction = "Write a question that asks to distinguish or compare concepts."
#         elif bloom_level == "Evaluate":
#             task_instruction = "Write a question evaluating the pros/cons or limitations of this concept."
#         else:
#             task_instruction = "Generate a question about this text."

#         # 2. Build the Prompt
#         prompt = ChatPromptTemplate.from_messages([
#             ("system", f"""You are an expert Computer Science Professor. 
#             Task: {task_instruction}
#             Constraint 1: The question MUST match the requested difficulty.
#             Constraint 2: Do NOT write a multiple choice question.
            
#             Return ONLY a valid JSON object with this exact structure:
#             {{{{ "question": "your generated question here" }}}}
#             """),
#             ("human", f"Text Context: {context_text}")
#         ])

#         # 3. Create the LangChain pipeline
#         chain = prompt | self.llm

#         # 4. Generate and Parse
#         try:
#             response = chain.invoke({})
#             clean_json_str = self._clean_json_string(response.content)
#             question_data = json.loads(clean_json_str)
#             return question_data.get("question", "Failed to extract question from JSON.")
#         except Exception as e:
#             print(f"Generation Error: {e}")
#             return f"Explain the main concepts discussed in this text: {context_text[:50]}..."

#     def generate_quiz(self, context, count=1, bloom_level="Remember"):
#         """
#         Generates a list of questions.
#         """
#         questions = []
#         unique_q = set()
        
#         # Loop to generate the requested number of questions
#         for _ in range(count):
#             q = self.generate(context, bloom_level)
#             if q not in unique_q and "Explain the main concepts" not in q:
#                 unique_q.add(q)
#                 questions.append(q)
                
#         return list(unique_q)

# import os
# import json
# from langchain_groq import ChatGroq
# from langchain_core.prompts import ChatPromptTemplate


# class QuestionGenerator:
#     """
#     RAG-Grounded Question Generator
#     - Strict Bloom Taxonomy control
#     - Uses retrieved context ONLY
#     - Prevents hallucination
#     - Hybrid: LLM + Rule Validation
#     """

#     def __init__(self):
#         print("⏳ Loading Groq LLM (Llama-3.1-8b)...")

#         # IMPORTANT: Set your real key here
#         os.environ["GROQ_API_KEY"] = "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"

#         self.llm = ChatGroq(
#             model="llama-3.1-8b-instant",
#             temperature=0.4,  # lower = less hallucination
#             max_tokens=400
#         )

#         print("✅ Question Generator Ready")

#     # ------------------------------------------------
#     # STRICT PROMPT (RAG-GROUNDED)
#     # ------------------------------------------------
#     def _build_prompt(self, retrieved_context, concept, bloom_level):

#         bloom_instruction = ""

#         if bloom_level == "Remember":
#             bloom_instruction = "Ask a definition-based question."
#         elif bloom_level == "Understand":
#             bloom_instruction = "Ask a conceptual explanation question."
#         elif bloom_level == "Apply":
#             bloom_instruction = (
#                 "Create a practical programming scenario question where the student must APPLY this concept."
#             )
#         elif bloom_level == "Analyze":
#             bloom_instruction = (
#                 "Ask a comparison or reasoning-based analytical question."
#             )
#         else:
#             bloom_instruction = "Ask a conceptual question."

#         system_message = f"""
# You are a university-level Computer Science examiner.

# STRICT RULES:
# 1. Use ONLY the provided context.
# 2. Do NOT invent new facts.
# 3. Do NOT create multiple choice questions.
# 4. The question must be descriptive (5–10 marks).
# 5. Bloom Level: {bloom_level}.
# 6. {bloom_instruction}

# Return ONLY valid JSON:
# {{
#   "question": "Your generated question here"
# }}
# """

#         human_message = f"""
# Concept: {concept}

# Retrieved Context:
# {retrieved_context}
# """

#         return ChatPromptTemplate.from_messages([
#             ("system", system_message),
#             ("human", human_message)
#         ])

#     # ------------------------------------------------
#     # RULE VALIDATION (ANTI-GENERIC FILTER)
#     # ------------------------------------------------
#     def _is_valid_question(self, text, bloom_level):

#         if len(text) < 25:
#             return False

#         if "which of the following" in text.lower():
#             return False

#         if bloom_level == "Apply":
#             apply_words = ["design", "implement", "develop", "create", "demonstrate"]
#             return any(word in text.lower() for word in apply_words)

#         if bloom_level == "Analyze":
#             analyze_words = ["compare", "analyze", "justify", "evaluate"]
#             return any(word in text.lower() for word in analyze_words)

#         return True

#     # ------------------------------------------------
#     # MAIN GENERATION
#     # ------------------------------------------------
#     def generate_quiz(self, context, concept, bloom_level="Understand", count=1):

#         questions = []
#         unique_set = set()

#         for _ in range(count * 2):  # retry attempts
#             prompt = self._build_prompt(context, concept, bloom_level)
#             chain = prompt | self.llm

#             try:
#                 response = chain.invoke({})
#                 raw = response.content.strip()

#                 # Clean markdown if present
#                 if raw.startswith("```"):
#                     raw = raw.replace("```json", "").replace("```", "").strip()

#                 parsed = json.loads(raw)
#                 question = parsed.get("question", "").strip()

#                 if (
#                     question
#                     and question not in unique_set
#                     and self._is_valid_question(question, bloom_level)
#                 ):
#                     unique_set.add(question)
#                     questions.append(question)

#                 if len(questions) >= count:
#                     break

#             except Exception:
#                 continue

#         return questions

# services/question_generator.py

# import os
# import json
# from langchain_groq import ChatGroq
# from langchain_core.prompts import ChatPromptTemplate

# class QuestionGenerator:

#     def __init__(self):
#         print("Loading Groq LLM...")
#         os.environ["GROQ_API_KEY"] = "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"

#         self.llm = ChatGroq(
#             model="llama-3.1-8b-instant",
#             temperature=0.6,
#             max_tokens=600
#         )

#     def _build_prompt(self, context, bloom_level, count):

#         context = context.replace("{", "{{").replace("}", "}}")

#         bloom_instruction = {
#             "Remember": "Generate recall-based descriptive questions.",
#             "Understand": "Generate explanation-based conceptual questions.",
#             "Apply": "Generate real coding scenario questions requiring application.",
#             "Analyze": "Generate analytical reasoning questions requiring comparison or justification."
#         }

#         return ChatPromptTemplate.from_messages([
#             ("system", f"""
#     You are a university-level Java programming examiner.

#     Generate {count} descriptive exam questions.
#     Do NOT create multiple-choice questions.
#     Match Bloom level strictly: {bloom_level}
#     Instruction: {bloom_instruction.get(bloom_level, bloom_instruction["Understand"])}

#     Return ONLY valid JSON:
# {{ "questions": ["Q1", "Q2"] }}
# """),
#             ("human", f"Reference Context:\n{context}")
#         ])

#     def generate_questions(self, context, bloom_level="Understand", count=3):

#         prompt = self._build_prompt(context, bloom_level, count)
#         chain = prompt | self.llm

#         try:
#             response = chain.invoke({})
#             content = response.content.strip()

#             if content.startswith("```"):
#                 content = content.replace("```json", "").replace("```", "").strip()

#             parsed = json.loads(content)
#             return parsed.get("questions", [])

#         except Exception:
#             return []

# import os
# import json
# from langchain_groq import ChatGroq
# from langchain_core.prompts import ChatPromptTemplate
# from dotenv import load_dotenv


# class QuestionGenerator:

#     def __init__(self):
#         print("🔹 Initializing Groq Question Generator...")

#         # Load environment variables
#         load_dotenv()

#         api_key = os.getenv("GROQ_API_KEY")
#         if not api_key:
#             raise ValueError("❌ GROQ_API_KEY not found in environment variables.")

#         print("🔹 Loading Groq LLM (Llama-3.1-8b-instant)...")

#         self.llm = ChatGroq(
#             model="llama-3.1-8b-instant",
#             temperature=0.6,
#             max_tokens=700
#         )

#         print("✅ Groq Question Generator Ready.\n")

#     # ---------------------------------------------------
#     # Prompt Builder
#     # ---------------------------------------------------
#     def _build_prompt(self, context, bloom_level, count):

#         bloom_instruction = {
#             "Remember": "Generate recall-based descriptive questions.",
#             "Understand": "Generate explanation-based conceptual questions.",
#             "Apply": "Generate real coding scenario questions requiring application.",
#             "Analyze": "Generate analytical reasoning questions requiring comparison or justification."
#         }

#         print("🔹 Building Prompt...")
#         print("   → Bloom Level:", bloom_level)
#         print("   → Question Count:", count)

#         return ChatPromptTemplate.from_messages([
#             ("system", f"""
# You are a university-level Java programming examiner.

# Generate exactly {count} descriptive exam questions.
# Do NOT create multiple-choice questions.
# Match Bloom level strictly: {bloom_level}
# Instruction: {bloom_instruction.get(bloom_level, bloom_instruction["Understand"])}

# Return ONLY valid JSON in this format:
# {{ "questions": ["Q1", "Q2", "Q3"] }}
# """),
#             ("human", f"Reference Context:\n{context}")
#         ])

#     # ---------------------------------------------------
#     # Main Generation Method
#     # ---------------------------------------------------
#     def generate_questions(self, context, bloom_level="Understand", count=3):

#         print("\n🚀 Generating Questions...")
#         print("--------------------------------------------------")

#         if not context or len(context.strip()) < 20:
#             print("❌ Context too short.")
#             return []

#         prompt = self._build_prompt(context, bloom_level, count)
#         chain = prompt | self.llm

#         try:
#             print("🔹 Sending request to Groq LLM...")
#             response = chain.invoke({})

#             content = response.content.strip()

#             print("\n🔹 RAW LLM OUTPUT:")
#             print(content)
#             print("--------------------------------------------------")

#             # Remove markdown wrapping if exists
#             if content.startswith("```"):
#                 content = content.replace("```json", "").replace("```", "").strip()

#             parsed = json.loads(content)

#             questions = parsed.get("questions", [])

#             print("🔹 Parsed Questions:", questions)
#             print("✅ Generation Successful.\n")

#             return questions

#         except json.JSONDecodeError:
#             print("❌ JSON Parsing Failed.")
#             print("Returning empty list.\n")
#             return []

#         except Exception as e:
#             print("❌ Unexpected Error:", str(e))
#             return []


# import os
# import json
# from langchain_groq import ChatGroq
# from langchain_core.prompts import ChatPromptTemplate


# class QuestionGenerator:

#     def __init__(self):
#         print("🔹 Loading Groq LLM...")

#         # Use environment variable (DO NOT hardcode key)
#         self.llm = ChatGroq(
#             model="llama-3.1-8b-instant",
#             temperature=0.6,
#             max_tokens=700
#         )

#         print("✅ Groq LLM Ready")

#     # ---------------------------------------------------------
#     # PROMPT BUILDER (FIXED JSON ESCAPING)
#     # ---------------------------------------------------------
#     def _build_prompt(self, context, bloom_level, count):

#         bloom_instruction = {
#             "Remember": "Generate recall-based descriptive questions.",
#             "Understand": "Generate explanation-based conceptual questions.",
#             "Apply": "Generate real coding scenario questions requiring application.",
#             "Analyze": "Generate analytical reasoning questions requiring comparison or justification."
#         }

#         return ChatPromptTemplate.from_messages([
#             (
#                 "system",
#                 f"""
# You are a university-level Java programming examiner.

# Generate {count} descriptive exam questions.
# Do NOT create multiple-choice questions.
# Match Bloom level strictly: {bloom_level}
# Instruction: {bloom_instruction.get(bloom_level, bloom_instruction["Understand"])}

# Return ONLY valid JSON in this exact structure:

# {{{{ "questions": ["Question 1?", "Question 2?"] }}}}
# """
#             ),
#             (
#                 "human",
#                 f"Reference Context:\n{context}"
#             )
#         ])

#     # ---------------------------------------------------------
#     # GENERATION FUNCTION (WITH FULL DEBUGGING)
#     # ---------------------------------------------------------
#     def generate_questions(self, context, bloom_level="Understand", count=3):

#         print("\n🔹 Building prompt for Groq...")
#         prompt = self._build_prompt(context, bloom_level, count)

#         chain = prompt | self.llm

#         try:
#             print("🔹 Sending request to Groq LLM...")
#             response = chain.invoke({})
#             content = response.content.strip()

#             print("\n==============================")
#             print("🧠 RAW LLM OUTPUT:")
#             print(content)
#             print("==============================\n")

#             # Remove markdown if present
#             if content.startswith("```"):
#                 content = content.replace("```json", "").replace("```", "").strip()

#             parsed = json.loads(content)

#             print("✅ Parsed JSON:", parsed)

#             questions = parsed.get("questions", [])

#             print(f"🎯 Final Extracted Questions: {len(questions)}")

#             return questions

#         except Exception as e:
#             print("❌ JSON Parsing Error:", e)
#             print("⚠️ Raw content was:", content)
#             return []

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

        print("\n📤 Sending request to Groq...")
        print("🔹 Bloom Level:", bloom_level)
        print("🔹 Context Preview:", context[:300], "\n")

        try:
            response = self.llm.invoke(full_prompt)

            content = response.content.strip()

            print("🧠 RAW LLM Output:\n", content, "\n")

            if content.startswith("```"):
                content = content.replace("```json", "").replace("```", "").strip()

            parsed = json.loads(content)

            return parsed.get("questions", [])

        except Exception as e:
            print("❌ Groq Error:", str(e))
            return []
        

# def generate_reference_answer(self, context, question, bloom_level):

#     context = context.replace("{", "{{").replace("}", "}}")

#     print("\n📤 Sending Reference Generation Prompt to Groq...")

#     prompt = ChatPromptTemplate.from_messages([
#         ("system", f"""
# You are a university-level Java programming professor.

# Generate a detailed model reference answer suitable for a 10-mark exam question.

# STRICT RULES:
# - Use ONLY the provided reference context.
# - Do NOT add external knowledge.
# - Do NOT hallucinate.
# - Align answer difficulty with Bloom Level: {bloom_level}
# - Write in clear academic style.
# - Minimum 150 words.

# Return ONLY valid JSON:
# {{{{ "reference_answer": "Your detailed model answer here" }}}}
# """),
#         ("human", f"""
# Question:
# {question}

# Reference Context:
# {context}
# """)
#     ])

#     chain = prompt | self.llm

#     try:
#         response = chain.invoke({})
#         content = response.content.strip()

#         print("\n📥 RAW LLM RESPONSE:\n", content)

#         if content.startswith("```"):
#             content = content.replace("```json", "").replace("```", "").strip()

#         parsed = json.loads(content)

#         return parsed.get("reference_answer", "")

#     except Exception as e:
#         print("❌ Reference Generation Error:", e)
#         return "Failed to generate reference answer."

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
            print("❌ Reference Generation Error:", str(e))
            return "Failed to generate reference answer."

#     def generate_reference_answer(self, context, question, bloom_level):
        

#         context = context.replace("{", "{{").replace("}", "}}")

#         prompt = ChatPromptTemplate.from_messages([
#             ("system", f"""
# You are a university-level Java programming professor.

# Generate a detailed model reference answer suitable for a 10-mark exam question.

# STRICT RULES:
# - Use ONLY the provided reference context.
# - Do NOT add external knowledge.
# - Do NOT hallucinate.
# - Align answer difficulty with Bloom Level: {bloom_level}
# - Write in academic style.
# - Minimum 150 words.

# Return ONLY valid JSON:
# {{{{ "reference_answer": "Your detailed answer here" }}}}
# """),
#             ("human", f"""
# Question:
# {question}

# Reference Context:
# {context}
# """)
#         ])

#         chain = prompt | self.llm

#         try:
#             print("\n🔹 Sending request to Groq LLM for Reference Answer...")

#             response = chain.invoke({})
#             content = response.content.strip()

#             print("\n📥 RAW Reference Response:\n", content)

#             if content.startswith("```"):
#                 content = content.replace("```json", "").replace("```", "").strip()

#             parsed = json.loads(content)
#             return parsed.get("reference_answer", "")

        # except Exception as e:
            # return "fau"

#             print("❌ Reference Generation Error:", e)
#             return "Failed to generate reference answer."