import Navbar from "./components/common/Navbar";
import Hero from "./components/common/Hero";
import PlacementAssistant from "./components/assistant/PlacementAssistant";

function App() {
  return (
    <div className="min-h-screen bg-black text-white">
      <Navbar />
      <Hero />
      <PlacementAssistant />
    </div>
  );
}

export default App;