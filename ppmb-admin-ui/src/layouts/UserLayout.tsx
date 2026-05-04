import React from "react";
import { Outlet } from "react-router-dom";
import { theme } from "antd";
import bgImage from "../assets/login-bg.png";

const UserLayout: React.FC = () => {
  const {
    token: { colorBgLayout },
  } = theme.useToken();

  return (
    <div
      className="flex flex-col min-h-screen"
      style={{
        background: `url(${bgImage}) no-repeat center center`,
        backgroundColor: colorBgLayout, // Fallback background color
        backgroundSize: "cover",
      }}
    >
      <div className="flex-grow flex items-center justify-center p-4">
        {/* Child routes (like AuthPage) render here */}
        <Outlet />
      </div>
      <footer className="w-full text-center py-4 text-gray-500 text-sm bg-white/10 backdrop-blur-sm">
        PPMB Admin ©{new Date().getFullYear()} Created by Antigravity
      </footer>
    </div>
  );
};

export default UserLayout;
