import { useState } from "react";
import API from "../../services/api";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import rehypeRaw from "rehype-raw";

export default function Dashboard() {
  const [activeTab, setActiveTab] = useState("dsa");
  const [dsaQuery, setDsaQuery] = useState("");
  const [dsaResponse, setDsaResponse] = useState("");

  const [problemDesc, setProblemDesc] = useState("");
  const [code, setCode] = useState("");
  const [codeEval, setCodeEval] = useState("");

  const [csSubject, setCsSubject] = useState("DBMS");
  const [csQuery, setCsQuery] = useState("");
  const [csResponse, setCsResponse] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleDsaSearch = async () => {
    if (!dsaQuery.trim()) return;
    setLoading(true);
    setError("");
    setDsaResponse("");
    try {
      const res = await API.post("/api/tutor/dsa", { query: dsaQuery });
      setDsaResponse(res.data.response);
    } catch (err) {
      console.error(err);
      setError("Failed to fetch tutor response.");
    } finally {
      setLoading(false);
    }
  };

  const handleCodeEvaluation = async () => {
    if (!code.trim() || !problemDesc.trim()) {
      setError("Please input both the problem description and your code solution.");
      return;
    }
    setLoading(true);
    setError("");
    setCodeEval("");
    try {
      const res = await API.post("/api/tutor/evaluate-code", {
        problemDescription: problemDesc,
        candidateCode: code
      });
      setCodeEval(res.data.evaluation);
    } catch (err) {
      console.error(err);
      setError("Failed to compile evaluation.");
    } finally {
      setLoading(false);
    }
  };

  const handleCsTutor = async () => {
    if (!csQuery.trim()) return;
    setLoading(true);
    setError("");
    setCsResponse("");
    try {
      const res = await API.post("/api/tutor/cs", {
        subject: csSubject,
        query: csQuery
      });
      setCsResponse(res.data.response);
    } catch (err) {
      console.error(err);
      setError("Failed to fetch Core CS feedback.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto p-6 bg-gray-950 rounded-2xl border border-gray-800 shadow-2xl mt-10 text-left">
      <h2 className="text-3xl font-extrabold text-cyan-400 mb-2">Intelligent Tutoring System</h2>
      <p className="text-gray-400 mb-6">Learn algorithms, get code submissions evaluated, or review DBMS, OS, and System Design theory.</p>

      {/* Tabs */}
      <div className="flex gap-4 border-b border-gray-800 pb-3 mb-6">
        <button
          onClick={() => { setActiveTab("dsa"); setError(""); }}
          className={`px-4 py-2 font-bold rounded-lg transition ${activeTab === "dsa" ? "bg-cyan-400 text-black" : "text-gray-400 hover:text-white"}`}
        >
          DSA Tutor
        </button>
        <button
          onClick={() => { setActiveTab("code"); setError(""); }}
          className={`px-4 py-2 font-bold rounded-lg transition ${activeTab === "code" ? "bg-cyan-400 text-black" : "text-gray-400 hover:text-white"}`}
        >
          Java Code Evaluator
        </button>
        <button
          onClick={() => { setActiveTab("cs"); setError(""); }}
          className={`px-4 py-2 font-bold rounded-lg transition ${activeTab === "cs" ? "bg-cyan-400 text-black" : "text-gray-400 hover:text-white"}`}
        >
          Core CS Tutor
        </button>
      </div>

      {error && <div className="p-4 bg-red-950/50 border border-red-800 text-red-400 rounded-xl mb-6">{error}</div>}

      {/* DSA Tutor Tab */}
      {activeTab === "dsa" && (
        <div>
          <div className="flex gap-3 mb-6">
            <input
              type="text"
              placeholder="Ask a DSA question (e.g. explain Dijkstra's algorithm, find cycle in a graph)..."
              value={dsaQuery}
              onChange={(e) => setDsaQuery(e.target.value)}
              className="flex-1 p-4 bg-gray-900 border border-gray-800 rounded-xl text-white focus:outline-none text-sm"
              onKeyDown={(e) => e.key === "Enter" && handleDsaSearch()}
            />
            <button onClick={handleDsaSearch} disabled={loading} className="px-6 bg-cyan-400 hover:bg-cyan-300 text-black font-bold rounded-xl transition">
              {loading ? "Teaching..." : "Search"}
            </button>
          </div>

          {dsaResponse && (
            <div className="bg-gray-900/50 p-6 rounded-xl border border-gray-800 text-gray-300">
              <ReactMarkdown
                remarkPlugins={[remarkGfm]}
                rehypePlugins={[rehypeRaw]}
                components={{
                  h2: ({ children }) => <h2 className="text-lg font-bold text-cyan-400 mt-4 mb-2">{children}</h2>,
                  p: ({ children }) => <p className="mb-3 text-sm leading-relaxed">{children}</p>,
                  code: ({ node, inline, className, children, ...props }) => (
                    <code className="bg-gray-950 text-cyan-300 px-2 py-1 rounded block text-xs overflow-x-auto my-3 font-mono leading-relaxed" {...props}>
                      {children}
                    </code>
                  ),
                }}
              >
                {dsaResponse}
              </ReactMarkdown>
            </div>
          )}
        </div>
      )}

      {/* Java Code Evaluator Tab */}
      {activeTab === "code" && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          <div className="flex flex-col gap-4">
            <div>
              <label className="text-gray-400 font-bold text-xs uppercase tracking-wider block mb-2">Problem Description</label>
              <input
                type="text"
                placeholder="e.g. Reverse a LinkedList, Two Sum, LFU Cache"
                value={problemDesc}
                onChange={(e) => setProblemDesc(e.target.value)}
                className="w-full p-3 bg-gray-900 border border-gray-800 rounded-xl text-white focus:outline-none text-sm"
              />
            </div>
            <div>
              <label className="text-gray-400 font-bold text-xs uppercase tracking-wider block mb-2">Java Code Submission</label>
              <textarea
                rows={12}
                placeholder="public class Solution {\n  public int[] twoSum(int[] nums, int target) {\n    ...\n  }\n}"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                className="w-full p-4 bg-gray-900 border border-gray-800 rounded-xl text-cyan-300 focus:outline-none font-mono text-xs leading-relaxed"
              />
            </div>
            <button onClick={handleCodeEvaluation} disabled={loading} className="py-3 bg-cyan-400 hover:bg-cyan-300 text-black font-extrabold rounded-xl transition">
              {loading ? "Evaluating Compilation & Time Complexity..." : "Evaluate Submission"}
            </button>
          </div>

          <div className="bg-gray-900/50 p-6 rounded-xl border border-gray-800 text-gray-300 max-h-[500px] overflow-y-auto">
            <h3 className="text-xs uppercase tracking-wider font-bold text-gray-500 mb-4">AI Code Evaluation Report</h3>
            {codeEval ? (
              <ReactMarkdown
                remarkPlugins={[remarkGfm]}
                rehypePlugins={[rehypeRaw]}
                components={{
                  h2: ({ children }) => <h2 className="text-md font-bold text-cyan-400 mt-4 mb-2">{children}</h2>,
                  p: ({ children }) => <p className="mb-3 text-xs leading-relaxed">{children}</p>,
                  code: ({ children }) => <code className="bg-gray-950 text-cyan-200 px-2 py-1 rounded block text-[10px] overflow-x-auto font-mono my-2">{children}</code>,
                }}
              >
                {codeEval}
              </ReactMarkdown>
            ) : (
              <p className="text-gray-500 text-sm">Submit your code solution on the left to see complexity profiles and refactored optimizations.</p>
            )}
          </div>
        </div>
      )}

      {/* Core CS Tutor Tab */}
      {activeTab === "cs" && (
        <div>
          <div className="flex gap-4 mb-6">
            <select
              value={csSubject}
              onChange={(e) => setCsSubject(e.target.value)}
              className="p-4 bg-gray-900 border border-gray-800 rounded-xl text-white focus:outline-none text-sm font-bold"
            >
              <option value="DBMS">Database (DBMS/SQL)</option>
              <option value="OS">Operating Systems</option>
              <option value="CN">Computer Networks</option>
              <option value="OOP">Object-Oriented Programming</option>
              <option value="System Design">System Design</option>
            </select>
            <input
              type="text"
              placeholder={`Ask a question in ${csSubject} (e.g. explain ACID properties, how does TCP handshake work)...`}
              value={csQuery}
              onChange={(e) => setCsQuery(e.target.value)}
              className="flex-1 p-4 bg-gray-900 border border-gray-800 rounded-xl text-white focus:outline-none text-sm"
              onKeyDown={(e) => e.key === "Enter" && handleCsTutor()}
            />
            <button onClick={handleCsTutor} disabled={loading} className="px-6 bg-cyan-400 hover:bg-cyan-300 text-black font-bold rounded-xl transition">
              {loading ? "Retrieving..." : "Tutor Me"}
            </button>
          </div>

          {csResponse && (
            <div className="bg-gray-900/50 p-6 rounded-xl border border-gray-800 text-gray-300">
              <ReactMarkdown
                remarkPlugins={[remarkGfm]}
                rehypePlugins={[rehypeRaw]}
                components={{
                  h2: ({ children }) => <h2 className="text-lg font-bold text-cyan-400 mt-4 mb-2">{children}</h2>,
                  p: ({ children }) => <p className="mb-3 text-sm leading-relaxed">{children}</p>,
                  ul: ({ children }) => <ul className="list-disc pl-5 mb-3 text-sm space-y-1">{children}</ul>,
                }}
              >
                {csResponse}
              </ReactMarkdown>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
