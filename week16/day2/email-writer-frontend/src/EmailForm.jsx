import { useState } from "react";
import axios from "axios";

export default function EmailForm() {
  const [emailContent, setEmailContent] = useState("");
  const [tone, setTone] = useState("formal");
  const [result, setResult] = useState("");
  const [loading, setLoading] = useState(false);

  const handleGenerate = async () => {
    if (!emailContent.trim()) {
      alert("Please enter the email content.");
      return;
    }

    setLoading(true);
    setResult("");

    try {
      const res = await axios.post("http://localhost:8080/api/email/generate", {
        emailContent,
        tone,
      });

      setResult(res.data);
    } catch (err) {
      console.error(err);
      setResult("Error generating reply.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <label className="block text-lg font-semibold mb-2">Email Content</label>
      <textarea
        className="w-full h-40 p-4 bg-gray-700 border border-gray-600 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none text-white"
        placeholder="Paste the email message here..."
        value={emailContent}
        onChange={(e) => setEmailContent(e.target.value)}
      ></textarea>

      <label className="block text-lg font-semibold mt-6 mb-2">Tone</label>
      <select
        className="w-full p-3 bg-gray-700 border border-gray-600 rounded-xl focus:ring-2 focus:ring-blue-500 outline-none text-white"
        value={tone}
        onChange={(e) => setTone(e.target.value)}
      >
        <option value="formal">Formal</option>
        <option value="friendly">Friendly</option>
        <option value="polite">Polite</option>
        <option value="apologetic">Apologetic</option>
        <option value="assertive">Assertive</option>
      </select>

      <button
        onClick={handleGenerate}
        disabled={loading}
        className="mt-6 w-full py-3 bg-blue-600 hover:bg-blue-700 transition-all rounded-xl font-bold text-white shadow-md"
      >
        {loading ? "Generating..." : "Generate Reply"}
      </button>

      {result && (
        <div className="mt-8 bg-gray-700 p-5 rounded-xl border border-gray-600">
          <h2 className="text-xl font-bold mb-2 text-blue-400">
            Generated Reply:
          </h2>
          <p className="whitespace-pre-line leading-relaxed">{result}</p>
        </div>
      )}
    </div>
  );
}
