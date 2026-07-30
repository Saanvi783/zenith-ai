from services.general_service import ask_zenith
from utils.session import conversation_state
import re



COMPANIES = [
    "Amazon",
    "Google",
    "Microsoft",
    "Adobe",
    "Oracle",
    "Goldman Sachs",
    "JPMorgan",
    "Flipkart",
    "Walmart",
    "Uber",
    "Atlassian",
    "NVIDIA",
    "Qualcomm",
    "Intel",
    "Apple",
    "Meta",
    "Netflix",
    "TCS",
    "Infosys",
    "Wipro",
    "Accenture",
    "Cognizant",
    "Capgemini",
    "Deloitte"
]


def extract_company(query):

    query_lower = query.lower()

    for company in COMPANIES:

        if company.lower() in query_lower:

            return company

    return "General"


def company_insights(query):

    company = extract_company(query)

    if company != "General":
        conversation_state["company"] = company
    else:
        company = conversation_state.get("company", "General")

    prompt = f"""
You are Zenith AI's Company Intelligence Engine.

Return the response in clean GitHub Markdown.

Rules:
- Use ## for section headings.
- Use bullet points wherever possible.
- Use tables only if they improve readability.
- Do NOT add unnecessary blank lines.
- Do NOT use excessive separators (---).
- Keep spacing compact like ChatGPT.
- Be concise but complete.
- Use only the sections relevant to the user's query.

Company:
{company}

User Query:
{query}

Possible sections:
- Hiring Process
- Interview Rounds
- Online Assessment (OA)
- Important Topics
- Skills Required
- Expected Projects
- Salary (Approx.)
- Preparation Tips

If the user asks about only one topic (e.g. salary or interview process), answer only that topic instead of all sections.
"""

    response= ask_zenith(
        "You are an expert placement mentor.",
        prompt
    )

    response = re.sub(r"\n{3,}", "\n\n", response)
    conversation_state["history"].append(
        {"user": query, "assistant": response}
    )
    conversation_state["history"] = conversation_state["history"][-5:]

    return response