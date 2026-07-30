from fastapi import APIRouter
from schemas.resume import ResumeAnalysisRequest
from services.resume_service import analyze_resume

router = APIRouter()


@router.post("/analyze-resume")
def resume_analysis(data: ResumeAnalysisRequest):

    try:

        result = analyze_resume(data.resume_text)

        return {
            "analysis": result
        }

    except Exception as e:

        return {
            "error": str(e)
        }