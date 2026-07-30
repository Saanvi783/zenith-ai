function Navbar({ activeTab, setActiveTab }) {
  return (
    <nav className="bg-gray-950 border-b border-gray-800">
      <div className="max-w-7xl mx-auto flex items-center justify-between px-8 py-4">
        <h1 
          className="text-2xl font-bold text-cyan-400 cursor-pointer"
          onClick={() => setActiveTab("assistant")}
        >
          Zenith AI
        </h1>

        <ul className="flex gap-8 text-gray-300">
          <li 
            className={`hover:text-cyan-400 cursor-pointer transition ${activeTab === "assistant" ? "text-cyan-400 font-semibold" : ""}`}
            onClick={() => setActiveTab("assistant")}
          >
            Assistant
          </li>
          <li 
            className={`hover:text-cyan-400 cursor-pointer transition ${activeTab === "interview" ? "text-cyan-400 font-semibold" : ""}`}
            onClick={() => setActiveTab("interview")}
          >
            Interview
          </li>
          <li 
            className={`hover:text-cyan-400 cursor-pointer transition ${activeTab === "resume" ? "text-cyan-400 font-semibold" : ""}`}
            onClick={() => setActiveTab("resume")}
          >
            Resume
          </li>
          <li 
            className={`hover:text-cyan-400 cursor-pointer transition ${activeTab === "dashboard" ? "text-cyan-400 font-semibold" : ""}`}
            onClick={() => setActiveTab("dashboard")}
          >
            Dashboard
          </li>
        </ul>
      </div>
    </nav>
  );
}

export default Navbar;