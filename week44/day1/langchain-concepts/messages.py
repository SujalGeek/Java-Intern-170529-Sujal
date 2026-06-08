from langchain_core.messages import SystemMessage, HumanMessage , AIMessage
from langchain_groq import ChatGroq
from dotenv import load_dotenv

load_dotenv()

model = ChatGroq(
    model="llama-3.3-70b-versatile",
    temperature=0
)
messages = [
    SystemMessage(content="You are helpful assistant"),
    HumanMessage(content="Tell me about LangChain")
]
result = model.invoke(messages)

