from services.general_service import ask_zenith


def check_eligibility(query):

    prompt = f"""
You are Zenith AI's Eligibility Checker.

Return clean GitHub Markdown.

Rules:
- Use ## headings.
- Keep spacing compact.
- Use bullet points.
- No unnecessary blank lines.

Analyze the following query:

{query}

Return:

## Eligibility Status

## Reason

## Missing Requirements

## Preparation Advice
"""

    return ask_zenith(
        "You are an expert placement mentor.",
        prompt
    )