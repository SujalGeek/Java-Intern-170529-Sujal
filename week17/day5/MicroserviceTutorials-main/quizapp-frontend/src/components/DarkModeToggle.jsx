import { useEffect, useState } from "react";

export default function DarkModeToggle() {
  const [dark, setDark] = useState(
    localStorage.getItem("theme") === "dark"
  );

  useEffect(() => {
    if (dark) {
      document.body.classList.add("dark");
      localStorage.setItem("theme", "dark");
    } else {
      document.body.classList.remove("dark");
      localStorage.setItem("theme", "light");
    }
  }, [dark]);

  return (
    <button
      className="btn"
      onClick={() => setDark(!dark)}
      style={{ position: "absolute", top: 20, right: 20 }}
    >
      {dark ? "☀" : "🌙"}
    </button>
  );
}
