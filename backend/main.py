from fastapi.middleware.cors import CORSMiddleware
from fastapi import FastAPI
from pydantic import BaseModel
from dotenv import load_dotenv
from openai import OpenAI
import os

load_dotenv()

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

client = OpenAI(
    base_url="https://openrouter.ai/api/v1",
    api_key=os.getenv("OPENROUTER_API_KEY")
)


# Request Body
class InterviewRequest(BaseModel):
    company: str
    role: str
    difficulty: str
    topic: str


@app.get("/")
def home():
    return {
        "message": "Zenith AI Backend Running"
    }


@app.post("/generate-interview")
def generate_interview(data: InterviewRequest):

    prompt = f"""
    Generate 5 interview questions for:

    Company: {data.company}
    Role: {data.role}
    Difficulty: {data.difficulty}
    Topic: {data.topic}

    Ask realistic interview questions.
    """

    try:

        completion = client.chat.completions.create(

            model="openai/gpt-3.5-turbo",

            messages=[
                {
                    "role": "user",
                    "content": prompt
                }
            ]
        )

        return {
            "questions":
            completion.choices[0].message.content
        }

    except Exception as e:

        return {
            "error": str(e)
        }