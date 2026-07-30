from fastapi import APIRouter, UploadFile, File

from utils.session import conversation_state
from utils.pdf_reader import extract_text_from_pdf
from services.resume_service import analyze_resume

router = APIRouter()


@router.post("/upload")
async def upload_file(file: UploadFile = File(...)):

    try:

        current_task = conversation_state.get("current_task")

        if current_task != "resume":

            return {
                "message": "No resume analysis is currently active."
            }

        resume_text = extract_text_from_pdf(file.file)

        analysis = analyze_resume(resume_text)

        conversation_state.clear()

        return {
            "intent": "resume",
            "analysis": analysis
        }

    except Exception as e:

        return {
            "error": str(e)
        }