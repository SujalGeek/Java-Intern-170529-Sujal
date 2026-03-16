# Use Python 3.10 (Standard for NLP)
FROM python:3.10-slim

# 1. Install System Dependencies for ChromaDB/pysqlite
RUN apt-get update && apt-get install -y \
    build-essential \
    python3-dev \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 2. Install Python dependencies
# Use the requirements.txt we discussed (flask, chromadb, langchain, etc.)
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 3. Copy the ENTIRE NLP structure
# This includes services/, config/, and your .env file
COPY . .

# 4. Create a folder for the Vector DB so data persists
RUN mkdir -p /app/chroma_db

EXPOSE 5001

# Run the Flask app
CMD ["python", "app.py"]