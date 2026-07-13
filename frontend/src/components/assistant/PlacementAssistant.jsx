import { useState } from "react";

function PlacementAssistant() {
  const [question, setQuestion] = useState("");

  return (
    <section className="max-w-5xl mx-auto px-6 py-10">
      <h2 className="text-3xl font-bold mb-6">
        AI Placement Assistant
      </h2>

      <textarea
        rows="4"
        value={question}
        onChange={(e) => setQuestion(e.target.value)}
        placeholder="Ask anything about placements..."
        className="w-full bg-gray-900 border border-gray-700 rounded-xl p-4 text-white"
      />

      <button className="mt-4 bg-cyan-500 hover:bg-cyan-400 text-black font-bold px-6 py-3 rounded-xl">
        Ask Zenith
      </button>
    </section>
  );
}

export default PlacementAssistant;