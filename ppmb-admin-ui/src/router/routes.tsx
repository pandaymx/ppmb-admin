import React from "react";
import { createBrowserRouter, RouteObject } from "react-router-dom";
import {
  FileOutlined,
  PieChartOutlined,
  TeamOutlined,
  UserOutlined,
} from "@ant-design/icons";

import BasicLayout from "../layouts/BasicLayout";
import UserLayout from "../layouts/UserLayout";
import AuthPage from "../pages/login/AuthPage";
import DashboardPage from "../pages/dashboard/index";
import ChatPage from "../pages/ai/Chat";
import { RobotOutlined } from "@ant-design/icons";

import ProtectedRoute from "../components/ProtectedRoute";

// Extended RouteObject type for custom metadata
export type AppRouteObject = RouteObject & {
  meta?: {
    title?: string;
    icon?: React.ReactNode;
    hideInMenu?: boolean;
    permissions?: string[];
  };
  children?: AppRouteObject[];
};

export const routes: AppRouteObject[] = [
  {
    path: "/login",
    element: <UserLayout />,
    meta: { hideInMenu: true },
    children: [
      {
        index: true,
        element: <AuthPage />,
      },
    ],
  },
  {
    path: "/",
    element: (
      <ProtectedRoute>
        <BasicLayout />
      </ProtectedRoute>
    ),
    children: [
      {
        index: true,
        element: <DashboardPage />,
        meta: {
          title: "Dashboard",
          icon: <PieChartOutlined />,
        },
      },
      {
        path: "ai",
        element: <ChatPage />,
        meta: {
          title: "AI Assistant",
          icon: <RobotOutlined />,
        },
      },
      {
        path: "system",
        meta: {
          title: "System",
          icon: <UserOutlined />,
        },
        children: [
          {
            path: "users",
            element: (
              <ProtectedRoute permissions={["sys:user:list"]}>
                <div>Users Page (Placeholder)</div>
              </ProtectedRoute>
            ),
            meta: {
              title: "Users",
              permissions: ["sys:user:list"],
            },
          },
          {
            path: "roles",
            element: (
              <ProtectedRoute permissions={["sys:role:list"]}>
                <div>Roles Page (Placeholder)</div>
              </ProtectedRoute>
            ),
            meta: {
              title: "Roles",
              permissions: ["sys:role:list"],
            },
          },
          {
            path: "departments",
            element: (
              <ProtectedRoute permissions={["sys:dept:list"]}>
                <div>Departments Page (Placeholder)</div>
              </ProtectedRoute>
            ),
            meta: {
              title: "Departments",
              permissions: ["sys:dept:list"],
            },
          },
        ],
      },
      {
        path: "monitoring",
        meta: {
          title: "Monitoring",
          icon: <TeamOutlined />,
        },
        children: [
          {
            path: "logs",
            element: <div>Logs Page (Placeholder)</div>,
            meta: { title: "Logs" },
          },
          {
            path: "performance",
            element: <div>Performance Page (Placeholder)</div>,
            meta: { title: "Performance" },
          },
        ],
      },
      {
        path: "files",
        element: <div>Files Page (Placeholder)</div>,
        meta: {
          title: "Files",
          icon: <FileOutlined />,
        },
      },
    ],
  },
  {
    path: "*",
    element: <div>404 Not Found</div>,
    meta: { hideInMenu: true },
  },
];

const router = createBrowserRouter(routes);

export default router;
