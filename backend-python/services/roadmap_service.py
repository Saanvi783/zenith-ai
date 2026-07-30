from services.general_service import ask_zenith


def generate_roadmap(query):

    prompt = f"""
You are Zenith AI's Placement Roadmap Generator.

Return the response in clean GitHub Markdown.

Rules:
- Use ## headings.
- Use bullet points.
- Use tables only when useful.
- No unnecessary blank lines.
- Keep the response concise.
- Make it look like ChatGPT.

Create a personalized roadmap for:

{query}

Include:
- Timeline
- Skills to Learn
- Resources
- Practice Plan
- Projects
- Tips
"""

    return ask_zenith(
        "You are an expert placement mentor.",
        prompt
    )