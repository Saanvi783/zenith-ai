from openai import OpenAI
from dotenv import load_dotenv
import os

from utils.session import conversation_state
from services.interview_service import generate_interview
from services.company_service import company_insights
from services.eligibility_service import check_eligibility
from services.roadmap_service import generate_roadmap

load_dotenv()

client = OpenAI(
    base_url="https://openrouter.ai/api/v1",
    api_key=os.getenv("OPENROUTER_API_KEY")
)

MODEL = "openrouter/free"


# -----------------------------
# Intent Classification
# -----------------------------

def classify_intent(user_query):

    query = user_query.lower()

    # Resume
    if any(word in query for word in [
        "resume",
        "cv",
        "ats",
        "review my resume",
        "analyze my resume"
    ]):
        return "resume"

    # Eligibility
    if any(word in query for word in [
        "eligible",
        "eligibility",
        "can i apply",
        "can i get",
        "am i eligible"
    ]):
        return "eligibility"

    # Company
    if any(word in query for word in [
        "amazon",
        "google",
        "microsoft",
        "nvidia",
        "adobe",
        "oracle",
        "uber",
        "flipkart",
        "goldman",
        "jpmorgan",
        "tcs",
        "infosys",
        "wipro",
        "interview process",
        "oa",
        "online assessment",
        "hiring",
        "salary",
        "company"
    ]):
        return "company"

    # Interview
    if any(word in query for word in [
        "interview questions",
        "mock interview",
        "generate interview",
        "ask interview",
        "behavioral",
        "technical interview"
    ]):
        return "interview"

    # Roadmap
    if any(word in query for word in [
        "roadmap",
        "study plan",
        "learning path",
        "how should i prepare",
        "prepare for",
        "placement preparation",
        "plan",
        "road map"
    ]):
        return "roadmap"

    # DSA
    if any(word in query for word in [
        "dsa",
        "leetcode",
        "array",
        "linked list",
        "graph",
        "tree",
        "dynamic programming",
        "dp"
    ]):
        return "dsa"

    # CS Subjects
    if any(word in query for word in [
        "dbms",
        "os",
        "operating system",
        "cn",
        "computer networks",
        "oops",
        "sql"
    ]):
        return "cs"

    return "general"

# -----------------------------
# Main AI Orchestrator
# -----------------------------

def process_query(query):

    intent = classify_intent(query)

    # Resume Module
    if intent == "resume":

        conversation_state["current_task"] = "resume"

        return {
            "intent": "resume",
            "message": "Please upload your resume PDF for analysis."
        }

    # Interview Module
    elif intent == "interview":

        conversation_state["current_task"] = "interview"

        questions = generate_interview(
            company="General",
            role="Software Engineer",
            difficulty="Medium",
            topic=query
        )

        return {
            "intent": "interview",
            "questions": questions
        }

    # Company Module
    elif intent == "company":

        return {
            "intent": "company",
            "response": company_insights(query)
        }

    # Eligibility Module
    elif intent == "eligibility":

        return {
            "intent": "eligibility",
            "response": check_eligibility(query)
        }

    # Roadmap Module
    elif intent == "roadmap":

        return {
            "intent": "roadmap",
            "response": generate_roadmap(query)
        }

    # DSA Module
    elif intent == "dsa":

        return {
            "intent": "dsa",
            "message": "DSA Tutor is under development."
        }

    # CS Module
    elif intent == "cs":

        return {
            "intent": "cs",
            "message": "CS Concept Tutor is under development."
        }

    # General Chat
    else:

        messages = [
            {
                "role": "system",
                "content": "You are Zenith AI, an AI Placement Assistant."
            }
        ]

        for chat in conversation_state["history"]:
            messages.append({
                "role": "user",
                "content": chat["user"]
            })
            messages.append({
                "role": "assistant",
                "content": chat["assistant"]
            })

        messages.append({
            "role": "user",
            "content": query
        })

        completion = client.chat.completions.create(
            model=MODEL,
            messages=messages
        )

        response = completion.choices[0].message.content

        conversation_state["history"].append({
            "user": query,
            "assistant": response
        })

        conversation_state["history"] = conversation_state["history"][-5:]

        return {
            "intent": "general",
            "response": response
        }