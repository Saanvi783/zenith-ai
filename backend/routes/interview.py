from fastapi import APIRouter
from schemas.interview import InterviewRequest, AnswerRequest
from services.interview_service import (
    generate_interview,
    analyze_answer as analyze_answer_service
)

router = APIRouter()


@router.post("/generate-interview")
def interview(data: InterviewRequest):

    try:

        questions = generate_interview(
            data.company,
            data.role,
            data.difficulty,
            data.topic
        )

        return {
            "questions": questions
        }

    except Exception as e:

        return {
            "error": str(e)
        }


@router.post("/analyze-answer")
def analyze_answer(data: AnswerRequest):

    try:

        result = analyze_answer_service(
            data.question,
            data.answer
        )

        return result

    except Exception as e:

        return {
            "error": str(e)
        }