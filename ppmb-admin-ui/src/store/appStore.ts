import { create } from "zustand";
import { persist } from "zustand/middleware";

type ThemeMode = "light" | "dark";
type Locale = "zh-CN" | "en-US";

interface AppState {
  theme: ThemeMode;
  locale: Locale;
  primaryColor: string;
  setTheme: (theme: ThemeMode) => void;
  setLocale: (locale: Locale) => void;
  setPrimaryColor: (color: string) => void;
  toggleTheme: () => void;
}

export const useAppStore = create<AppState>()(
  persist(
    (set) => ({
      theme: "light",
      locale: "zh-CN", // Default to Simplified Chinese
      primaryColor: "#1677ff", // Default primary color
      setTheme: (theme) => set({ theme }),
      setLocale: (locale) => set({ locale }),
      setPrimaryColor: (color) => set({ primaryColor: color }),
      toggleTheme: () =>
        set((state) => ({ theme: state.theme === "light" ? "dark" : "light" })),
    }),
    {
      name: "app-storage", // name of the item in the storage (must be unique)
    },
  ),
);
