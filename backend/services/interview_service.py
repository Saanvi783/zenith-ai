import os
import json
from dotenv import load_dotenv
from openai import OpenAI

load_dotenv()

client = OpenAI(
    base_url="https://openrouter.ai/api/v1",
    api_key=os.getenv("OPENROUTER_API_KEY")
)

MODEL = "openrouter/free"


# --------------------------------------------------
# Generate Interview Questions
# --------------------------------------------------

def generate_interview(company, role, difficulty, topic):

    prompt = f"""
You are a Senior Software Engineer at {company}.

Conduct a realistic interview for the role of {role}.

Difficulty: {difficulty}

Focus Area: {topic}

Generate exactly 5 interview questions.

Rules:

- Questions must sound like a real interviewer.
- Avoid giving LeetCode problem statements.
- Ask conceptual questions first.
- Then ask implementation questions.
- Then ask one behavioral question if appropriate.
- Keep every question under 40 words.

Return ONLY a JSON array.

Example:

[
"What is the difference between BFS and DFS?",
"How would you implement an LRU Cache?",
"Explain HashMap collisions.",
"How would you optimize this algorithm?",
"Tell me about a difficult bug you solved."
]
"""

    completion = client.chat.completions.create(
        model=MODEL,
        messages=[
            {
                "role": "user",
                "content": prompt
            }
        ]
    )

    response = completion.choices[0].message.content

    if not response:
        raise Exception("AI returned an empty response.")

    response = response.replace("```json", "")
    response = response.replace("```", "")
    response = response.strip()

    try:
        questions = json.loads(response)

    except Exception:

        questions = []

        for line in response.split("\n"):

            line = line.strip()

            if line:

                line = line.lstrip("1234567890.- ")

                questions.append(line)

    return questions


# --------------------------------------------------
# Analyze Interview Answer
# --------------------------------------------------

def analyze_answer(question, answer):

    filler_words = [
        "um",
        "uh",
        "like",
        "you know",
        "basically",
        "actually",
        "so"
    ]

    answer_lower = answer.lower()

    filler_count = {}

    total_fillers = 0

    for word in filler_words:

        count = answer_lower.count(word)

        filler_count[word] = count

        total_fillers += count

    prompt = f"""
You are an experienced technical interviewer.

Interview Question:
{question}

Candidate Answer:
{answer}

Evaluate the answer.

Give:

Strengths

Weaknesses

Technical Accuracy (/10)

Communication (/10)

Confidence (/10)

Improvement Tips

Keep the response concise.
"""

    completion = client.chat.completions.create(
        model=MODEL,
        messages=[
            {
                "role": "user",
                "content": prompt
            }
        ]
    )

    feedback = completion.choices[0].message.content

    if not feedback:
        feedback = "No feedback generated."

    overall_score = max(
        1,
        round(10 - total_fillers * 0.5, 1)
    )

    return {

        "feedback": feedback,

        "filler_analysis": {

            "total_fillers": total_fillers,

            "counts": filler_count
        },

        "overall_score": overall_score
    }