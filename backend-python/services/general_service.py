from openai import OpenAI
from dotenv import load_dotenv
import os

load_dotenv()

client = OpenAI(
    base_url="https://openrouter.ai/api/v1",
    api_key=os.getenv("OPENROUTER_API_KEY")
)

MODEL = "openrouter/free"


def ask_zenith(system_prompt, user_prompt):

    system_prompt = f"""
You are Zenith AI, an AI Placement Assistant.

{system_prompt}

Formatting Rules:
- Return clean GitHub Markdown.
- Use ## for headings.
- Use bullet points where appropriate.
- Keep spacing compact.
- Do NOT add unnecessary blank lines.
- Use tables only when they improve readability.
- Be concise but complete.
"""

    completion = client.chat.completions.create(
        model=MODEL,
        messages=[
            {
                "role": "system",
                "content": system_prompt
            },
            {
                "role": "user",
                "content": user_prompt
            }
        ]
    )

    return completion.choices[0].message.content