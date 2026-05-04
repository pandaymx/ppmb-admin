import React, { useEffect, useMemo } from "react";
import { ConfigProvider, theme as antdTheme } from "antd";
import { useAppStore } from "../store/appStore";

import zhCN from "antd/locale/zh_CN";
import enUS from "antd/locale/en_US";

interface ThemeConfigProviderProps {
  children: React.ReactNode;
}

export const ThemeConfigProvider: React.FC<ThemeConfigProviderProps> = ({
  children,
}) => {
  const { theme, locale } = useAppStore();

  // Handle Tailwind dark mode class on HTML root
  useEffect(() => {
    const root = window.document.documentElement;
    if (theme === "dark") {
      root.classList.add("dark");
    } else {
      root.classList.remove("dark");
    }
  }, [theme]);

  const antdLocale = useMemo(() => {
    return locale === "en-US" ? enUS : zhCN;
  }, [locale]);

  return (
    <ConfigProvider
      locale={antdLocale}
      theme={{
        algorithm:
          theme === "dark"
            ? antdTheme.darkAlgorithm
            : antdTheme.defaultAlgorithm,
        token: {
          colorPrimary: "#1677ff",
          borderRadius: 6,
        },
      }}
    >
      {children}
    </ConfigProvider>
  );
};
