import { useState } from "react"
import axios from "axios"

function App() {

  const [company, setCompany] = useState("")
  const [role, setRole] = useState("")
  const [difficulty, setDifficulty] = useState("")
  const [topic, setTopic] = useState("")
  const [questions, setQuestions] = useState("")
  const [loading, setLoading] = useState(false)

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

      setQuestions(response.data.questions)

    } catch (error) {

      console.log(error)

      setQuestions("Something went wrong.")

    }

    setLoading(false)
  }

  return (
    <div className="min-h-screen bg-black text-white">

      {/* Navbar */}
      <nav className="flex justify-between items-center px-10 py-6 border-b border-gray-800">

        <h1 className="text-3xl font-bold text-cyan-400">
          Zenith AI
        </h1>

      </nav>

      {/* Hero */}
      <section className="flex flex-col items-center text-center px-6 pt-20">

        <h1 className="text-6xl font-extrabold max-w-4xl leading-tight">
          AI Powered Interview Preparation
        </h1>

        <p className="text-gray-400 text-xl mt-6 max-w-2xl">
          Generate mock interviews, company-specific questions,
          and personalized preparation strategies instantly.
        </p>

      </section>

      {/* Form */}
      <section className="max-w-4xl mx-auto mt-16 bg-gray-900 p-10 rounded-2xl border border-gray-800">

        <div className="grid md:grid-cols-2 gap-6">

          <input
            type="text"
            placeholder="Company"
            className="bg-black border border-gray-700 p-4 rounded-xl outline-none"
            onChange={(e) => setCompany(e.target.value)}
          />

          <input
            type="text"
            placeholder="Role"
            className="bg-black border border-gray-700 p-4 rounded-xl outline-none"
            onChange={(e) => setRole(e.target.value)}
          />

          <input
            type="text"
            placeholder="Difficulty"
            className="bg-black border border-gray-700 p-4 rounded-xl outline-none"
            onChange={(e) => setDifficulty(e.target.value)}
          />

          <input
            type="text"
            placeholder="Topic"
            className="bg-black border border-gray-700 p-4 rounded-xl outline-none"
            onChange={(e) => setTopic(e.target.value)}
          />

        </div>

        <button
          onClick={generateInterview}
          className="mt-8 w-full bg-cyan-500 hover:bg-cyan-400 text-black py-4 rounded-xl font-bold text-lg transition"
        >

          {
            loading
            ?
            "Generating..."
            :
            "Generate Interview Questions"
          }

        </button>

      </section>

      {/* Output */}
      <section className="max-w-4xl mx-auto mt-10 pb-20">

        <div className="bg-gray-900 border border-gray-800 p-8 rounded-2xl whitespace-pre-wrap text-gray-300">

          {questions || "AI generated interview questions will appear here..."}

        </div>

      </section>

    </div>
  )
}

export default App