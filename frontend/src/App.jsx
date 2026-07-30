import { useState } from "react";
import Navbar from "./components/common/Navbar";
import Hero from "./components/common/Hero";
import PlacementAssistant from "./components/assistant/PlacementAssistant";
import MockInterview from "./components/interview/MockInterview";
import ResumeCoach from "./components/resume/ResumeCoach";
import Dashboard from "./components/dashboard/Dashboard";

function App() {
  const [activeTab, setActiveTab] = useState("assistant");

  return (
    <div className="min-h-screen bg-black text-white pb-20">
      <Navbar activeTab={activeTab} setActiveTab={setActiveTab} />
      
      {activeTab === "assistant" && <Hero />}
      
      <main className="max-w-7xl mx-auto px-8">
        {activeTab === "assistant" && <PlacementAssistant />}
        {activeTab === "interview" && <MockInterview />}
        {activeTab === "resume" && <ResumeCoach />}
        {activeTab === "dashboard" && <Dashboard />}
      </main>
    </div>
  );
}

export default App;