import joblib

from extract import extract_ingredients
from bottle import run, get, post, request

model = joblib.load("model.joblib")
print("Loaded model from file")

@post("/ingredients")
def ingredients():
  phrase = request.json["phrase"]
  labels = extract_ingredients(model, phrase)
  return {"labels": labels}

@get("/health")
def health():
  return "ok"


run(host='localhost', port=8081)
