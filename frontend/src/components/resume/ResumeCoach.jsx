import { useState } from "react";
import API from "../../services/api";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import rehypeRaw from "rehype-raw";

export default function ResumeCoach() {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [analysis, setAnalysis] = useState("");
  const [error, setError] = useState("");

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
    setAnalysis("");
    setError("");
  };

  const handleUpload = async () => {
    if (!file) {
      setError("Please select a PDF file first.");
      return;
    }

    setLoading(true);
    setAnalysis("");
    setError("");

    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await API.post("/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      if (res.data.analysis) {
        setAnalysis(res.data.analysis);
      } else {
        setError("Failed to generate resume analysis.");
      }
    } catch (err) {
      console.error(err);
      setError("Failed to upload and analyze resume.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6 bg-gray-950 rounded-2xl border border-gray-800 shadow-2xl mt-10">
      <h2 className="text-3xl font-extrabold text-cyan-400 mb-2">AI Resume Coach</h2>
      <p className="text-gray-400 mb-6">Upload your PDF resume to receive qualitative ATS scores, missing skill detection, and bullet-point rewrites.</p>

      <div className="flex flex-col items-center justify-center border-2 border-dashed border-gray-800 rounded-xl p-8 bg-gray-900/50 hover:bg-gray-900/80 transition cursor-pointer mb-6">
        <input type="file" accept="application/pdf" onChange={handleFileChange} className="hidden" id="resume-upload" />
        <label htmlFor="resume-upload" className="w-full text-center cursor-pointer">
          <svg className="w-12 h-12 text-cyan-400 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
          </svg>
          <span className="text-cyan-400 font-bold block mb-1">Click to select PDF resume</span>
          <span className="text-xs text-gray-500">Supports PDF format (Max 10MB)</span>
        </label>
      </div>

      {file && (
        <div className="flex items-center justify-between p-4 bg-gray-900 border border-gray-800 rounded-xl mb-6">
          <div className="flex items-center gap-3">
            <svg className="w-6 h-6 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            <div className="text-left">
              <span className="font-semibold block text-white text-sm">{file.name}</span>
              <span className="text-xs text-gray-400">{(file.size / 1024 / 1024).toFixed(2)} MB</span>
            </div>
          </div>
          <button onClick={handleUpload} disabled={loading} className="px-6 py-2 bg-cyan-400 hover:bg-cyan-300 text-black font-bold rounded-lg transition disabled:bg-gray-700">
            {loading ? "Analyzing..." : "Coach Me"}
          </button>
        </div>
      )}

      {error && <div className="p-4 bg-red-950/50 border border-red-800 text-red-400 rounded-xl mb-6">{error}</div>}

      {loading && (
        <div className="flex flex-col items-center justify-center p-12">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-cyan-400 mb-4"></div>
          <p className="text-cyan-400 font-medium">Zenith AI is reviewing your resume...</p>
        </div>
      )}

      {analysis && (
        <div className="mt-8 bg-gray-900/60 p-6 rounded-xl border border-gray-800 text-gray-300 text-left">
          <ReactMarkdown
            remarkPlugins={[remarkGfm]}
            rehypePlugins={[rehypeRaw]}
            components={{
              h2: ({ children }) => <h2 className="text-xl font-bold text-cyan-400 mt-6 mb-3 border-b border-gray-800 pb-2">{children}</h2>,
              p: ({ children }) => <p className="mb-4 text-sm leading-relaxed">{children}</p>,
              ul: ({ children }) => <ul className="list-disc pl-5 mb-4 text-sm space-y-1">{children}</ul>,
              ol: ({ children }) => <ol className="list-decimal pl-5 mb-4 text-sm space-y-1">{children}</ol>,
              li: ({ children }) => <li className="mb-1">{children}</li>,
              table: ({ children }) => <table className="w-full text-left border-collapse border border-gray-800 my-4 text-sm">{children}</table>,
              th: ({ children }) => <th className="border border-gray-800 p-2 bg-gray-900 font-bold">{children}</th>,
              td: ({ children }) => <td className="border border-gray-800 p-2">{children}</td>,
            }}
          >
            {analysis}
          </ReactMarkdown>
        </div>
      )}
    </div>
  );
}
