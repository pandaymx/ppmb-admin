import React from "react";
import { Routes, Route, Navigate, useNavigate } from "react-router-dom";
import { Layout, Menu, Breadcrumb, theme, Dropdown, Space, Avatar } from "antd";
import {
  FileOutlined,
  PieChartOutlined,
  TeamOutlined,
  UserOutlined,
  LogoutOutlined,
} from "@ant-design/icons";
import AuthPage from "./pages/login/AuthPage";
import ProtectedRoute from "./components/ProtectedRoute";
import { useAuthStore } from "./store/useAuthStore";

const { Header, Content, Footer, Sider } = Layout;

type MenuItem = {
  key: string;
  label: React.ReactNode;
  icon?: React.ReactNode;
  children?: MenuItem[];
};

function getItem(
  label: React.ReactNode,
  key: string,
  icon?: React.ReactNode,
  children?: MenuItem[],
): MenuItem {
  return {
    key,
    icon,
    children,
    label,
  };
}

const items: MenuItem[] = [
  getItem("Dashboard", "1", <PieChartOutlined />),
  getItem("System", "sub1", <UserOutlined />, [
    getItem("Users", "3"),
    getItem("Roles", "4"),
    getItem("Departments", "5"),
  ]),
  getItem("Monitoring", "sub2", <TeamOutlined />, [
    getItem("Logs", "6"),
    getItem("Performance", "8"),
  ]),
  getItem("Files", "9", <FileOutlined />),
];

const Dashboard: React.FC = () => {
  const [collapsed, setCollapsed] = React.useState(false);
  const logout = useAuthStore((state) => state.logout);
  const user = useAuthStore((state) => state.user);
  const navigate = useNavigate();

  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const userMenuItems = [
    {
      key: "logout",
      label: "退出登录",
      icon: <LogoutOutlined />,
      onClick: handleLogout,
    },
  ];

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={(value) => setCollapsed(value)}
      >
        <div
          className="demo-logo-vertical"
          style={{
            height: 32,
            margin: 16,
            background: "rgba(255,255,255,.2)",
            borderRadius: 6,
          }}
        />
        <Menu
          theme="dark"
          defaultSelectedKeys={["1"]}
          mode="inline"
          items={items}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            padding: "0 24px",
            background: colorBgContainer,
            display: "flex",
            justifyContent: "flex-end",
            alignItems: "center",
          }}
        >
          <Dropdown menu={{ items: userMenuItems }}>
            <Space className="cursor-pointer">
              <Avatar icon={<UserOutlined />} />
              <span className="font-medium text-gray-700">
                {user?.username || "Admin"}
              </span>
            </Space>
          </Dropdown>
        </Header>
        <Content style={{ margin: "0 16px" }}>
          <Breadcrumb style={{ margin: "16px 0" }}>
            <Breadcrumb.Item>User</Breadcrumb.Item>
            <Breadcrumb.Item>Admin</Breadcrumb.Item>
          </Breadcrumb>
          <div
            style={{
              padding: 24,
              minHeight: 360,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
            }}
          >
            <h1 className="text-2xl font-bold text-blue-600">
              Welcome to PPMB Admin
            </h1>
            <p className="mt-4">This is the v0.1.0 React Frontend Dashboard.</p>
          </div>
        </Content>
        <Footer style={{ textAlign: "center" }}>
          PPMB Admin ©{new Date().getFullYear()} Created by Antigravity
        </Footer>
      </Layout>
    </Layout>
  );
};

const App: React.FC = () => {
  return (
    <Routes>
      <Route path="/login" element={<AuthPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default App;
