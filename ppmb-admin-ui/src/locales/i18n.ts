import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";

import enUS from "./en-US.json";
import zhCN from "./zh-CN.json";

import { useAppStore } from "../store/appStore";

// Get the initial locale from Zustand state
const initialLocale = useAppStore.getState().locale;

const resources = {
  "en-US": enUS,
  "zh-CN": zhCN,
};

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources,
    lng: initialLocale, // use state from zustand
    fallbackLng: "zh-CN",
    interpolation: {
      escapeValue: false, // react already safes from xss
    },
  });

// Subscribe to Zustand store changes to update i18n language
useAppStore.subscribe((state, prevState) => {
  if (state.locale !== prevState.locale) {
    i18n.changeLanguage(state.locale);
  }
});

export default i18n;
