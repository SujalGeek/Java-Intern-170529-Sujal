import os
from langchain_groq import ChatGroq
from langchain_core.messages import SystemMessage, HumanMessage, AIMessage
from dotenv import load_dotenv

# 1. LOAD THE KEY FIRST!
# This pulls the GROQ_API_KEY from your .env file into the system memory
load_dotenv()

# 2. INITIALIZE THE MODEL SECOND!
# Now when ChatGroq wakes up, it will successfully find the key
model = ChatGroq(
    model="llama-3.3-70b-versatile",
    temperature=0
)

chat_history = [
    SystemMessage(content="You are a helpful AI Assistant")
]

while True:
    user_input = input('You: ')

    if user_input.lower() == 'exit':
        break

    chat_history.append(HumanMessage(content=user_input))

    result = model.invoke(chat_history)

    chat_history.append(AIMessage(content=result.content))
    print("AI: ", result.content)

print("\n--Final Chat History")
for msg in chat_history:
    print(msg)