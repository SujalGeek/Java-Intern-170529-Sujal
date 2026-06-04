import os
from crewai import Agent,LLM
from langchain_groq import ChatGroq


# llm = LLM(
#     model="groq/llama-3.3-70b-versatile",
#     temperature=0
# )

groq_llm = LLM(
    temperature=0, 
    model="llama-3.3-70b-versatile",
    base_url="https://api.groq.com/openai/v1",
    api_key=os.environ.get("GROQ_API_KEY") 
)


trader_agent = Agent(
    role="Strategic Stock Trader",
    goal=(
        "Decide whether to Buy, Sell or Hold a given stock based on live market data,"
        "price movements, and financial analysis with available data."
    ),
    backstory=(
        "You are a strategic trader with years of experience in timing market entry and exit points."
        "You rely on real-time stock data, daily price movements, and volume trends to make trending decisions"
        "that optimize returns and reduce risk."
    ),
    llm=groq_llm,
    tools=[],
    verbose=True,
)