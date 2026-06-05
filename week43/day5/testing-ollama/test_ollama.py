import ollama

response = ollama.chat(model='gemma2:2b', messages=[
  {
    'role': 'user',
    'content': 'Why is the sky blue? Keep it short.',
  },
])
print(response['message']['content'])
