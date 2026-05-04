import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import App from "./App";
import "./index.css";
import "./locales/i18n"; // Import i18n initialization
import { ThemeConfigProvider } from "./components/ThemeConfigProvider";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <BrowserRouter>
      <ThemeConfigProvider>
        <App />
      </ThemeConfigProvider>
    </BrowserRouter>
  </React.StrictMode>,
);
