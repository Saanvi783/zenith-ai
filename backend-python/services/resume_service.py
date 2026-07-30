from services.general_service import ask_zenith


def analyze_resume(resume_text):

    prompt = f"""
You are Zenith AI's Resume Reviewer.

Return clean GitHub Markdown.

Rules:
- Use ## headings.
- Keep spacing compact.
- Use bullet points.
- No unnecessary blank lines.

Analyze this resume.

Resume:

{resume_text}

Return:

## ATS Score

## Skills Found

## Missing Skills

## Strengths

## Weaknesses

## Projects Feedback

## Resume Improvements
"""

    return ask_zenith(
        "You are an expert ATS Resume Reviewer and Technical Recruiter.",
        prompt
    )