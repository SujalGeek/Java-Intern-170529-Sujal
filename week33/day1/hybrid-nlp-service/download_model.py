import os
# Force CPU mode for Transformers/Torch
os.environ["CUDA_VISIBLE_DEVICES"] = ""

from sentence_transformers import SentenceTransformer

model_name = "all-MiniLM-L6-v2"
save_path = "./model_cache"

try:
    print(f"📦 Pre-downloading {model_name}...")
    model = SentenceTransformer(model_name, device="cpu")
    model.save(save_path)
    print(f"✅ Model baked successfully at {save_path}")
except Exception as e:
    print(f"❌ Error baking model: {e}")
    exit(1)