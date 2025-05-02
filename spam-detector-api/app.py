from fastapi import FastAPI, Request
from pydantic import BaseModel
import joblib

app = FastAPI()

# Load model and vectorizer
model = joblib.load("spam_model.pkl")
vectorizer = joblib.load("vectorizer.pkl")

class MessageInput(BaseModel):
    message: str

@app.post("/predict")
def predict(data: MessageInput):
    vectorized = vectorizer.transform([data.message])
    prediction = model.predict(vectorized)
    return {"prediction": "spam" if prediction[0] == 1 else "ham"}
