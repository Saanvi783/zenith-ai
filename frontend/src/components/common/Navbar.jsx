function Navbar() {
  return (
    <nav className="bg-gray-950 border-b border-gray-800">
      <div className="max-w-7xl mx-auto flex items-center justify-between px-8 py-4">
        <h1 className="text-2xl font-bold text-cyan-400">
          Zenith AI
        </h1>

        <ul className="flex gap-8 text-gray-300">
          <li className="hover:text-cyan-400 cursor-pointer">Home</li>
          <li className="hover:text-cyan-400 cursor-pointer">Interview</li>
          <li className="hover:text-cyan-400 cursor-pointer">Resume</li>
          <li className="hover:text-cyan-400 cursor-pointer">Assistant</li>
          <li className="hover:text-cyan-400 cursor-pointer">Dashboard</li>
        </ul>
      </div>
    </nav>
  );
}

export default Navbar;