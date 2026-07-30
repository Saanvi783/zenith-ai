from fastapi import APIRouter
from pydantic import BaseModel

from services.assistant_service import process_query

router = APIRouter()


class AssistantRequest(BaseModel):
    query: str


@router.post("/assistant")
def assistant(data: AssistantRequest):

    try:

        return process_query(data.query)

    except Exception as e:

        return {
            "error": str(e)
        }