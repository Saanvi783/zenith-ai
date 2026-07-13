from pydantic import BaseModel


class InterviewRequest(BaseModel):
    company: str
    role: str
    difficulty: str
    topic: str


class AnswerRequest(BaseModel):
    question: str
    answer: str