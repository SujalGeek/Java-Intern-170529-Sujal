FROM python:3.10-slim

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Install System Dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    python3-dev \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Upgrade pip
RUN pip install --no-cache-dir --upgrade pip

# Install requirements (Fixed versions)
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Bake the model
COPY download_model.py .
RUN python3 download_model.py

# Copy source code
COPY . .

RUN mkdir -p /app/chroma_db
EXPOSE 5001

CMD ["python", "app.py"]