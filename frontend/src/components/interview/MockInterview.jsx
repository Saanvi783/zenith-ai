import { useState } from "react";
import API from "../../services/api";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import rehypeRaw from "rehype-raw";

export default function MockInterview() {
  const [config, setConfig] = useState({
    company: "Google",
    role: "Software Engineer",
    difficulty: "Medium",
    topic: "Data Structures & Algorithms"
  });

  const [session, setSession] = useState(null);
  const [loading, setLoading] = useState(false);
  const [currentQuestion, setCurrentQuestion] = useState("");
  const [answer, setAnswer] = useState("");
  const [feedbackList, setFeedbackList] = useState([]);
  const [currentFeedback, setCurrentFeedback] = useState(null);
  const [questionOrder, setQuestionOrder] = useState(1);
  const [isFinished, setIsFinished] = useState(false);
  const [finalReport, setFinalReport] = useState(null);
  const [error, setError] = useState("");

  const handleConfigChange = (e) => {
    setConfig({ ...config, [e.target.name]: e.target.value });
  };

  const startInterview = async () => {
    setLoading(true);
    setError("");
    setFeedbackList([]);
    setCurrentFeedback(null);
    setIsFinished(false);
    setFinalReport(null);
    setQuestionOrder(1);

    try {
      const res = await API.post("/api/interview/start", config);
      setSession(res.data.sessionId);
      setCurrentQuestion(res.data.question);
    } catch (err) {
      console.error(err);
      setError("Failed to start mock interview session. Verify the backend connection.");
    } finally {
      setLoading(false);
    }
  };

  const submitAnswer = async () => {
    if (!answer.trim()) {
      setError("Please write an answer before submitting.");
      return;
    }

    setLoading(true);
    setError("");
    setCurrentFeedback(null);

    try {
      const res = await API.post(`/api/interview/submit-answer?sessionId=${session}`, {
        answer: answer
      });

      const data = res.data;
      setCurrentFeedback(data);
      setFeedbackList([...feedbackList, {
        question: currentQuestion,
        answer: answer,
        feedback: data.questionFeedback,
        score: data.questionScore,
        fillers: data.fillerAnalysis
      }]);

      if (data.isFinished) {
        setIsFinished(true);
        setFinalReport(data);
      } else {
        setCurrentQuestion(data.nextQuestion);
        setQuestionOrder(prev => prev + 1);
        setAnswer("");
      }
    } catch (err) {
      console.error(err);
      setError("Failed to submit answer.");
    } finally {
      setLoading(false);
    }
  };

  if (isFinished && finalReport) {
    return (
      <div className="max-w-4xl mx-auto p-6 bg-gray-950 rounded-2xl border border-gray-800 shadow-2xl mt-10 text-left">
        <h2 className="text-3xl font-extrabold text-cyan-400 mb-2">Interview Session Complete!</h2>
        <p className="text-gray-400 mb-6">Here is your customized feedback, filler word analysis, and career mentor recommendations.</p>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="p-5 bg-gray-900 border border-gray-800 rounded-xl text-center">
            <span className="text-gray-500 text-sm font-semibold uppercase tracking-wider block mb-1">Overall Score</span>
            <span className="text-5xl font-black text-cyan-400">{finalReport.overallScore}/10</span>
          </div>
          <div className="p-5 bg-gray-900 border border-gray-800 rounded-xl text-center">
            <span className="text-gray-500 text-sm font-semibold uppercase tracking-wider block mb-1">Filler Words Detected</span>
            <span className="text-5xl font-black text-red-400">{finalReport.grandTotalFillers}</span>
          </div>
          <div className="p-5 bg-gray-900 border border-gray-800 rounded-xl text-center flex flex-col justify-center">
            <span className="text-gray-500 text-sm font-semibold uppercase tracking-wider block mb-1">Target Company</span>
            <span className="text-2xl font-bold text-white">{config.company}</span>
          </div>
        </div>

        <div className="bg-gray-900/60 p-6 rounded-xl border border-gray-800 text-gray-300 mb-8">
          <h3 className="text-2xl font-bold text-cyan-400 mb-4 border-b border-gray-800 pb-2">Career Mentor Synthesis</h3>
          <ReactMarkdown
            remarkPlugins={[remarkGfm]}
            rehypePlugins={[rehypeRaw]}
            components={{
              h2: ({ children }) => <h2 className="text-lg font-bold text-white mt-4 mb-2">{children}</h2>,
              p: ({ children }) => <p className="mb-3 text-sm leading-relaxed">{children}</p>,
              ul: ({ children }) => <ul className="list-disc pl-5 mb-3 text-sm space-y-1">{children}</ul>,
              li: ({ children }) => <li className="mb-1">{children}</li>,
            }}
          >
            {finalReport.summaryFeedback}
          </ReactMarkdown>
        </div>

        <button onClick={() => setFinalReport(null)} className="px-8 py-3 bg-cyan-400 hover:bg-cyan-300 text-black font-bold rounded-xl transition">
          Start New Interview
        </button>
      </div>
    );
  }

  if (session) {
    return (
      <div className="max-w-4xl mx-auto p-6 bg-gray-950 rounded-2xl border border-gray-800 shadow-2xl mt-10 text-left">
        <div className="flex justify-between items-center mb-6">
          <span className="text-cyan-400 font-bold uppercase tracking-wider text-sm">Active Session: {config.company}</span>
          <span className="text-gray-500 font-semibold text-sm">Question {questionOrder} of 5</span>
        </div>

        <div className="w-full bg-gray-900 border border-gray-800 rounded-xl p-5 mb-6">
          <h3 className="text-gray-400 font-bold text-xs uppercase tracking-wider mb-2">Interviewer Question</h3>
          <p className="text-xl text-white font-medium leading-relaxed">{currentQuestion}</p>
        </div>

        <div className="mb-6">
          <label className="text-gray-400 font-bold text-xs uppercase tracking-wider block mb-2">Your Answer</label>
          <textarea
            rows={6}
            placeholder="Type your structured answer here (explain concepts, write pseudo-code, or outline your design)..."
            value={answer}
            onChange={(e) => setAnswer(e.target.value)}
            disabled={loading}
            className="w-full p-4 text-white bg-gray-900 border border-gray-800 rounded-xl focus:border-cyan-400 focus:outline-none resize-vertical text-sm leading-relaxed"
          />
        </div>

        {error && <div className="p-4 bg-red-950/50 border border-red-800 text-red-400 rounded-xl mb-6">{error}</div>}

        <div className="flex justify-between items-center">
          <button onClick={() => setSession(null)} className="px-6 py-2 bg-gray-900 border border-gray-800 hover:bg-gray-800 text-white rounded-lg transition">
            Cancel Session
          </button>
          <button onClick={submitAnswer} disabled={loading} className="px-8 py-3 bg-cyan-400 hover:bg-cyan-300 text-black font-bold rounded-xl transition">
            {loading ? "Evaluating..." : "Submit Answer"}
          </button>
        </div>

        {currentFeedback && (
          <div className="mt-8 bg-gray-900 p-6 rounded-xl border border-gray-800">
            <h4 className="text-cyan-400 font-bold text-sm uppercase tracking-wider mb-3">Live Response Grade</h4>
            <div className="flex items-center gap-4 mb-4">
              <span className="text-3xl font-black text-cyan-400">{currentFeedback.questionScore}/10</span>
              <span className="text-xs text-gray-500">Includes penalty for {currentFeedback.fillerAnalysis?.totalFillers || 0} filler words.</span>
            </div>
            <ReactMarkdown
              remarkPlugins={[remarkGfm]}
              rehypePlugins={[rehypeRaw]}
              components={{
                h2: ({ children }) => <h2 className="text-md font-bold text-white mt-4 mb-2">{children}</h2>,
                p: ({ children }) => <p className="mb-3 text-xs leading-relaxed text-gray-300">{children}</p>,
                ul: ({ children }) => <ul className="list-disc pl-5 mb-3 text-xs text-gray-300">{children}</ul>,
              }}
            >
              {currentFeedback.questionFeedback}
            </ReactMarkdown>
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto p-6 bg-gray-950 rounded-2xl border border-gray-800 shadow-2xl mt-10 text-left">
      <h2 className="text-3xl font-extrabold text-cyan-400 mb-2">Adaptive Mock Interview</h2>
      <p className="text-gray-400 mb-8">Launch a context-aware mock interview round. The subsequent AI questions adapt to your technical depth and explanations.</p>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div>
          <label className="text-gray-400 font-bold text-xs uppercase tracking-wider block mb-2">Target Company</label>
          <select name="company" value={config.company} onChange={handleConfigChange} className="w-full p-3 bg-gray-900 border border-gray-800 rounded-xl text-white focus:outline-none">
            <option value="Google">Google</option>
            <option value="Amazon">Amazon</option>
            <option value="Microsoft">Microsoft</option>
            <option value="NVIDIA">NVIDIA</option>
            <option value="Goldman Sachs">Goldman Sachs</option>
            <option value="TCS">TCS</option>
            <option value="General">General/Unspecified</option>
          </select>
        </div>

        <div>
          <label className="text-gray-400 font-bold text-xs uppercase tracking-wider block mb-2">Target Role</label>
          <input type="text" name="role" value={config.role} onChange={handleConfigChange} className="w-full p-3 bg-gray-900 border border-gray-800 rounded-xl text-white focus:outline-none" />
        </div>

        <div>
          <label className="text-gray-400 font-bold text-xs uppercase tracking-wider block mb-2">Difficulty</label>
          <select name="difficulty" value={config.difficulty} onChange={handleConfigChange} className="w-full p-3 bg-gray-900 border border-gray-800 rounded-xl text-white focus:outline-none">
            <option value="Easy">Easy (Conceptual)</option>
            <option value="Medium">Medium (Practical)</option>
            <option value="Hard">Hard (Deep Dive & Design)</option>
          </select>
        </div>

        <div>
          <label className="text-gray-400 font-bold text-xs uppercase tracking-wider block mb-2">Focus Topic / Core CS</label>
          <input type="text" name="topic" value={config.topic} onChange={handleConfigChange} placeholder="e.g. DFS/BFS, DBMS transactions, Caching" className="w-full p-3 bg-gray-900 border border-gray-800 rounded-xl text-white focus:outline-none" />
        </div>
      </div>

      {error && <div className="p-4 bg-red-950/50 border border-red-800 text-red-400 rounded-xl mb-6">{error}</div>}

      <button onClick={startInterview} disabled={loading} className="w-full py-4 bg-cyan-400 hover:bg-cyan-300 text-black font-extrabold rounded-xl transition text-lg shadow-lg">
        {loading ? "Configuring Interview..." : "Start Mock Interview"}
      </button>
    </div>
  );
}
