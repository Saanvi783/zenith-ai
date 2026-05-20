from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from dotenv import load_dotenv
from openai import OpenAI
import os

# Load environment variables
load_dotenv()

# FastAPI app
app = FastAPI()

# CORS setup
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# OpenRouter Client
client = OpenAI(
    base_url="https://openrouter.ai/api/v1",
    api_key=os.getenv("OPENROUTER_API_KEY")
)

# =========================================
# Request Models
# =========================================

class InterviewRequest(BaseModel):
    company: str
    role: str
    difficulty: str
    topic: str


class AnswerRequest(BaseModel):
    question: str
    answer: str


# =========================================
# Home Route
# =========================================

@app.get("/")
def home():

    return {
        "message": "Zenith AI Backend Running"
    }


# =========================================
# Generate Interview Questions
# =========================================

@app.post("/generate-interview")
def generate_interview(data: InterviewRequest):

    prompt = f"""
    Generate 5 realistic interview questions for:

    Company: {data.company}
    Role: {data.role}
    Difficulty: {data.difficulty}
    Topic: {data.topic}

    Ask high-quality interview questions.
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


# =========================================
# Analyze Candidate Answers
# =========================================

@app.post("/analyze-answer")
def analyze_answer(data: AnswerRequest):

    prompt = f"""
    You are an expert technical interviewer.

    Interview Question:
    {data.question}

    Candidate Answer:
    {data.answer}

    Analyze the answer carefully.

    Give:
    1. Score out of 10
    2. Strengths
    3. Weaknesses
    4. Better answer suggestion
    5. Communication feedback
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
            "feedback":
            completion.choices[0].message.content
        }

    except Exception as e:

        return {
            "error": str(e)
        }