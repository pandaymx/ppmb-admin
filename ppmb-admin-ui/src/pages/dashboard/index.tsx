import React from "react";
import { Breadcrumb, theme } from "antd";

const Dashboard: React.FC = () => {
  const {
    token: { colorBgContainer, borderRadiusLG },
  } = theme.useToken();

  return (
    <>
      <Breadcrumb style={{ margin: "0 0 16px 0" }}>
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
    </>
  );
};

export default Dashboard;
