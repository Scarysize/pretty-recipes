import joblib

from extract import extract_ingredients
from bottle import run, get, post, request

model = joblib.load("model.joblib")
print("Loaded model from file")

@post("/ingredients")
def ingredients():
  phrases = request.json["phrases"]
  labels = extract_ingredients(model, phrases)
  return {"labels": labels}

@get("/health")
def health():
  return "ok"


run(host='localhost', port=8081)
