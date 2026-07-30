from pydantic import BaseModel


class ResumeAnalysisRequest(BaseModel):
    resume_text: str