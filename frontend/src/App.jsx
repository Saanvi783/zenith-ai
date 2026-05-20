import { useState, useRef } from "react"
import axios from "axios"

function App() {

  const [company, setCompany] = useState("")
  const [role, setRole] = useState("")
  const [difficulty, setDifficulty] = useState("")
  const [topic, setTopic] = useState("")

  const [questions, setQuestions] = useState([])
  const [currentQuestion, setCurrentQuestion] = useState(0)

  const [answer, setAnswer] = useState("")
  const [feedback, setFeedback] = useState("")

  const [fillerAnalysis, setFillerAnalysis] = useState(null)

  const [loading, setLoading] = useState(false)

  const recognitionRef = useRef(null)

  // ===================================
  // Speech Recognition
  // ===================================

  const startListening = () => {

    const SpeechRecognition =
      window.SpeechRecognition ||
      window.webkitSpeechRecognition

    if (!SpeechRecognition) {

      alert("Speech Recognition not supported")

      return
    }

    const recognition = new SpeechRecognition()

    recognition.continuous = false
    recognition.interimResults = false
    recognition.lang = "en-US"

    recognition.onresult = (event) => {

      const transcript =
        event.results[0][0].transcript

      setAnswer(transcript)
    }

    recognition.start()

    recognitionRef.current = recognition
  }

  // ===================================
  // Generate Interview
  // ===================================

  const generateInterview = async () => {

    setLoading(true)

    try {

      const response = await axios.post(
        "http://127.0.0.1:8000/generate-interview",
        {
          company,
          role,
          difficulty,
          topic
        }
      )

      const text = response.data.questions

      const splitQuestions = text
        .split("\n")
        .filter(q => q.trim() !== "")

      setQuestions(splitQuestions)

    } catch (error) {

      console.log(error)

    }

    setLoading(false)
  }

  // ===================================
  // Analyze Answer
  // ===================================

  const analyzeAnswer = async () => {

    setLoading(true)

    try {

      const response = await axios.post(
        "http://127.0.0.1:8000/analyze-answer",
        {
          question: questions[currentQuestion],
          answer: answer
        }
      )

      setFeedback(response.data.feedback)

      setFillerAnalysis(
        response.data.filler_analysis
      )

    } catch (error) {

      console.log(error)

    }

    setLoading(false)
  }

  // ===================================
  // Next Question
  // ===================================

  const nextQuestion = () => {

    setAnswer("")
    setFeedback("")
    setFillerAnalysis(null)

    setCurrentQuestion(currentQuestion + 1)
  }

  return (

    <div className="min-h-screen bg-black text-white p-10">

      {/* Hero */}

      <div className="text-center">

        <h1 className="text-6xl font-bold">
          Zenith AI Interviewer
        </h1>

        <p className="text-gray-400 mt-4 text-xl">
          Real-time AI mock interview platform
        </p>

      </div>

      {/* Form */}

      {questions.length === 0 && (

        <div className="max-w-4xl mx-auto mt-16 bg-gray-900 border border-gray-800 p-8 rounded-3xl">

          <div className="grid grid-cols-2 gap-6">

            <input
              type="text"
              placeholder="Company"
              className="bg-black border border-gray-700 p-4 rounded-xl"
              onChange={(e) => setCompany(e.target.value)}
            />

            <input
              type="text"
              placeholder="Role"
              className="bg-black border border-gray-700 p-4 rounded-xl"
              onChange={(e) => setRole(e.target.value)}
            />

            <input
              type="text"
              placeholder="Difficulty"
              className="bg-black border border-gray-700 p-4 rounded-xl"
              onChange={(e) => setDifficulty(e.target.value)}
            />

            <input
              type="text"
              placeholder="Topic"
              className="bg-black border border-gray-700 p-4 rounded-xl"
              onChange={(e) => setTopic(e.target.value)}
            />

          </div>

          <button
            onClick={generateInterview}
            className="w-full bg-cyan-500 text-black font-bold py-4 rounded-xl mt-8 hover:bg-cyan-400 transition"
          >

            {loading ? "Generating..." : "Start Interview"}

          </button>

        </div>
      )}

      {/* Interview UI */}

      {questions.length > 0 && currentQuestion < questions.length && (

        <div className="max-w-5xl mx-auto mt-16">

          {/* Question */}

          <div className="bg-gray-900 border border-gray-800 p-8 rounded-3xl">

            <h2 className="text-3xl font-bold mb-6">
              Interview Question
            </h2>

            <p className="text-cyan-400 mb-4 text-lg">
              Question {currentQuestion + 1} / {questions.length}
            </p>

            <div className="w-full bg-gray-800 rounded-full h-3 mb-6">

              <div
                className="bg-cyan-400 h-3 rounded-full transition-all duration-500"
                style={{
                  width: `${((currentQuestion + 1) / questions.length) * 100}%`
                }}
              ></div>

            </div>

            <p className="text-xl text-gray-300">
              {questions[currentQuestion]}
            </p>

          </div>

          {/* Answer Box */}

          <div className="bg-gray-900 border border-gray-800 p-8 rounded-3xl mt-8">

            <textarea
              rows="8"
              placeholder="Type your answer..."
              className="w-full bg-black border border-gray-700 rounded-xl p-4 text-white"
              value={answer}
              onChange={(e) => setAnswer(e.target.value)}
            />

            <button
              onClick={startListening}
              className="w-full bg-purple-500 text-white font-bold py-4 rounded-xl mt-6 hover:bg-purple-400 transition"
            >
              🎤 Start Voice Answer
            </button>

            <button
              onClick={analyzeAnswer}
              className="w-full bg-cyan-500 text-black font-bold py-4 rounded-xl mt-6 hover:bg-cyan-400 transition"
            >

              {loading ? "Analyzing..." : "Submit Answer"}

            </button>

          </div>

          {/* Feedback */}

          {feedback && (

            <div className="bg-gray-900 border border-gray-800 p-8 rounded-3xl mt-8 whitespace-pre-wrap">

              <h2 className="text-3xl font-bold mb-6">
                AI Feedback
              </h2>

              <p className="text-gray-300">
                {feedback}
              </p>

              {/* Filler Word Analytics */}

              {fillerAnalysis && (

                <div className="mt-8 bg-black border border-gray-700 p-6 rounded-2xl">

                  <h3 className="text-2xl font-bold text-cyan-400 mb-4">
                    Communication Analytics
                  </h3>

                  <p className="text-lg mb-4">
                    Total Filler Words:
                    <span className="text-red-400 font-bold">
                      {" "}
                      {fillerAnalysis.total_fillers}
                    </span>
                  </p>

                  <div className="space-y-2">

                    {
                      Object.entries(
                        fillerAnalysis.counts
                      ).map(([word, count]) => (

                        <div
                          key={word}
                          className="flex justify-between bg-gray-900 p-3 rounded-lg"
                        >

                          <span>{word}</span>

                          <span className="text-red-400 font-bold">
                            {count}
                          </span>

                        </div>
                      ))
                    }

                  </div>

                </div>
              )}

              <button
                onClick={nextQuestion}
                className="mt-8 bg-green-500 text-black font-bold px-8 py-4 rounded-xl hover:bg-green-400 transition"
              >
                Next Question
              </button>

            </div>

          )}

        </div>
      )}

      {/* Interview Completed */}

      {questions.length > 0 && currentQuestion >= questions.length && (

        <div className="text-center mt-20">

          <h1 className="text-5xl font-bold text-green-400">
            Interview Completed 🎉
          </h1>

        </div>

      )}

    </div>
  )
}

export default App