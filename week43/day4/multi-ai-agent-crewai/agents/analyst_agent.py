import os
from crewai import Agent,LLM
from langchain_groq import ChatGroq

from tools.stock_research_tool import get_stock_price


groq_llm = LLM(
    temperature=0, 
    model="llama-3.3-70b-versatile",
    base_url="https://api.groq.com/openai/v1",
    api_key=os.environ.get("GROQ_API_KEY") 
)

analyst_agent= Agent(
    role="Financial Market Analyst",
    goal = (
        "Perform in depth evaluations of publicly traded stocks using real time data, "
        "identifying trends, performance insights, and key financial signals to support decision-making."
    ),
    backstory=(
        "You are a veterian financial analyst with deep expertise in interpreting stock market data, "
        "technical trends and fundamentals. You specialize in producing well structured reports that evaluate"
        "stock performance using live market indicators."
    ),
    llm =groq_llm,
    tools=[get_stock_price],
    verbose=True,
)