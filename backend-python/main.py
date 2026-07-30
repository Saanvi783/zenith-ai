from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv
from routes.resume import router as resume_router
from routes.assistant import router as assistant_router
from routes.upload import router as upload_router

from routes.interview import router as interview_router

load_dotenv()

app = FastAPI(
    title="Zenith AI Backend"
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Routes
app.include_router(interview_router)
app.include_router(resume_router)
app.include_router(assistant_router)
app.include_router(upload_router)

# Home
@app.get("/")
def home():
    return {
        "message": "Zenith AI Backend Running"
    }