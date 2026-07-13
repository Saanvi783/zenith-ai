from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from dotenv import load_dotenv

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

# Home
@app.get("/")
def home():
    return {
        "message": "Zenith AI Backend Running"
    }