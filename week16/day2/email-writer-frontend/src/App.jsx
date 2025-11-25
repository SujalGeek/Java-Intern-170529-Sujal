import { useState } from "react";

export default function App() {
  const [emailContent, setEmailContent] = useState("");
  const [tone, setTone] = useState("formal");
  const [result, setResult] = useState("");

  const generateReply = async () => {
    const res = await fetch("http://localhost:8080/api/email/generate", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ emailContent, tone }),
    });
    setResult(await res.text());
  };

  return (
    <div className="bg-gray-800 w-full max-w-2xl p-10 rounded-2xl shadow-xl text-white">
      <h1 className="text-4xl md:text-5xl font-bold text-center mb-8">
        AI Email Reply <span className="text-blue-400">Generator</span>
      </h1>

      <label className="block mb-2 text-lg">Email Content</label>
      <textarea
        rows="6"
        className="w-full p-4 bg-gray-700 border border-gray-600 rounded-lg mb-6"
        placeholder="Paste email here..."
        value={emailContent}
        onChange={(e) => setEmailContent(e.target.value)}
      />

      <label className="block mb-2 text-lg">Tone</label>
      <select
        className="w-full p-3 bg-gray-700 border border-gray-600 rounded-lg mb-6"
        value={tone}
        onChange={(e) => setTone(e.target.value)}
      >
        <option value="formal">Formal</option>
        <option value="polite">Polite</option>
        <option value="friendly">Friendly</option>
        <option value="professional">Professional</option>
        <option value="casual">Casual</option>
      </select>

      <button
        onClick={generateReply}
        className="w-full py-3 bg-blue-600 rounded-lg font-semibold hover:bg-blue-700"
      >
        Generate Reply
      </button>

      {result && (
        <div className="mt-6 bg-gray-700 p-4 rounded-lg border border-gray-600">
          <h2 className="text-xl font-bold mb-2 text-blue-400">
            Generated Reply:
          </h2>
          <p className="whitespace-pre-wrap">{result}</p>
        </div>
      )}
    </div>
  );
}
