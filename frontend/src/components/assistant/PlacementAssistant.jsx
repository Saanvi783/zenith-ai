import { useState } from "react";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import API from "../../services/api";
import rehypeRaw from "rehype-raw";

export default function PlacementAssistant() {
  const [query, setQuery] = useState("");
  const [response, setResponse] = useState("");
  const [loading, setLoading] = useState(false);

  const askZenith = async () => {
    if (!query.trim()) return;

    setLoading(true);
    setResponse("");

    try {
      const res = await API.post("/assistant", {
        query,
      });

      const data = res.data;

      console.log("Backend Response:", data);

      if (data.questions) {
        setResponse(data.questions.join("\n\n"));
      } else if (data.response) {
        setResponse(data.response);
      } else if (data.message) {
        setResponse(data.message);
      } else {
        setResponse("No response received.");
      }
    } catch (error) {
      console.error(error);
      setResponse("Failed to contact Zenith AI.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ marginTop: "60px" }}>
      <h1>AI Placement Assistant</h1>

      <textarea
        rows={6}
        placeholder="Ask anything about placements..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        style={{
          width: "100%",
          padding: "18px",
          fontSize: "16px",
          borderRadius: "12px",
          background: "#131d2f",
          color: "white",
          border: "1px solid #2a3446",
          resize: "vertical",
        }}
      />

      <button
        onClick={askZenith}
        disabled={loading}
        style={{
          marginTop: "20px",
          padding: "12px 28px",
          borderRadius: "10px",
          border: "none",
          background: "#00c8ff",
          color: "#000",
          fontWeight: "bold",
          cursor: "pointer",
        }}
      >
        {loading ? "Thinking..." : "Ask Zenith"}
      </button>

      {loading && (
        <h3
          style={{
            marginTop: "30px",
            color: "#00c8ff",
          }}
        >
          Zenith is thinking...
        </h3>
      )}

      {response && (
        <div
          style={{
            marginTop: "35px",
            background: "#101828",
            padding: "25px",
            borderRadius: "12px",
            color: "white",
            whiteSpace: "pre-wrap",
            lineHeight: "1.8",
            fontSize: "16px",
            overflowWrap: "break-word",
            border: "1px solid #1f2937",
          }}
        >
          <ReactMarkdown
  remarkPlugins={[remarkGfm]}
  rehypePlugins={[rehypeRaw]}
  skipHtml={true}
  components={{
    p: ({ children }) => (
      <p
        style={{
          margin: "8px 0",
          lineHeight: "1.6",
        }}
      >
        {children}
      </p>
    ),

    h1: ({ children }) => (
      <h1 style={{ margin: "18px 0 10px" }}>
        {children}
      </h1>
    ),

    h2: ({ children }) => (
      <h2
        style={{
          marginTop: "20px",
          marginBottom: "8px",
          color: "#22d3ee",
        }}
      >
        {children}
      </h2>
    ),

    h3: ({ children }) => (
      <h3
        style={{
          marginTop: "16px",
          marginBottom: "6px",
          color: "#ffffff",
        }}
      >
        {children}
      </h3>
    ),

    ul: ({ children }) => (
      <ul style={{ paddingLeft: "22px", margin: "8px 0" }}>
        {children}
      </ul>
    ),

    ol: ({ children }) => (
      <ol style={{ paddingLeft: "22px", margin: "8px 0" }}>
        {children}
      </ol>
    ),

    li: ({ children }) => (
      <li style={{ marginBottom: "6px" }}>
        {children}
      </li>
    ),

    hr: () => (
      <hr
        style={{
          margin: "16px 0",
          border: "1px solid #334155",
        }}
      />
    ),

    table: ({ children }) => (
      <div
        style={{
          overflowX: "auto",
          margin: "10px 0",
        }}
      >
        <table
          style={{
            width: "100%",
            borderCollapse: "collapse",
          }}
        >
          {children}
        </table>
      </div>
    ),

    th: ({ children }) => (
      <th
        style={{
          border: "1px solid #444",
          padding: "10px",
          background: "#1d293d",
        }}
      >
        {children}
      </th>
    ),

    td: ({ children }) => (
      <td
        style={{
          border: "1px solid #444",
          padding: "10px",
        }}
      >
        {children}
      </td>
    ),
  }}
>
  {response.replace(/\n{3,}/g, "\n\n")}
</ReactMarkdown>
        </div>
      )}
    </div>
  );
}