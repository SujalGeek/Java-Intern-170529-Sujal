from langchain_core.prompts import PromptTemplate
from langchain_groq import ChatGroq

from dotenv import load_dotenv

load_dotenv()

model = ChatGroq(
    model="llama-3.3-70b-versatile",
    temperature=0
)

template2 = PromptTemplate(
    template = "Great this person in 5 languages. The name of the person is {name}",
    input_variables=['name']
)


prompt = template2.invoke({
    'name':'nitish'
})

result = model.invoke(prompt)

print(result.content)
