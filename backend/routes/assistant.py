from fastapi import APIRouter
from pydantic import BaseModel
from openai import OpenAI
import os
from dotenv import load_dotenv

load_dotenv()

router = APIRouter()

client = OpenAI(
    base_url="https://openrouter.ai/api/v1",
    api_key=os.getenv("OPENROUTER_API_KEY")
)

class AssistantRequest(BaseModel):
    question: str

@router.post("/assistant")
def assistant(data: AssistantRequest):

    prompt = f"""
    You are Zenith AI, an AI Placement Preparation Assistant.

    Answer the user's placement-related question professionally.

    User Question:
    {data.question}
    """

    response = client.chat.completions.create(
        model="mistralai/mistral-7b-instruct",
        messages=[
            {
                "role": "user",
                "content": prompt
            }
        ]
    )

    return {
        "answer": response.choices[0].message.content
    }