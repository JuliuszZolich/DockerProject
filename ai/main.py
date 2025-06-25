import json

import uvicorn
from fastapi import FastAPI
from google import genai
from prompts import *
from models import *

API_KEY = "DODAĆ KLUCZ TUTAJ"
client = genai.Client(api_key=API_KEY)

app = FastAPI(
    title="Google GenAI API",
    description="API for Google GenAI",
    version="1.0.0",
    docs_url=None,
    redoc_url=None,
    openapi_url=None,
)

@app.post("/api/ingredient")
async def get_nutritional_values_of_ingredient(data: Ingredient):
    print(f"Received ingredient data: {data}")
    prompt = get_ingredient_nutritional_values_prompt(data)
    data = client.models.generate_content(
        model="gemini-2.0-flash",
        contents=prompt
    ).text
    print(f"Generated nutritional values: {data}")
    return json.loads(data)


if __name__ == "__main__":
   uvicorn.run("main:app", host="0.0.0.0", port=4000, reload=True)