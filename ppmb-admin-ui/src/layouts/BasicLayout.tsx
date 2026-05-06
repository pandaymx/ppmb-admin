import React from "react";
import { Layout, Menu, theme, Dropdown, Space, Avatar } from "antd";
import { UserOutlined, LogoutOutlined } from "@ant-design/icons";
import { Outlet, useNavigate, useLocation } from "react-router-dom";
import { useAuthStore } from "../store/useAuthStore";
import { routes } from "../router/routes";
import { AppRouteObject } from "../router/routes";
import type { MenuProps } from "antd";
import { getRouters } from "../api/menu";
import { RouterVo } from "../api/types/menu";
import * as Icons from "@ant-design/icons";

const { Header, Content, Footer, Sider } = Layout;

type MenuItem = Required<MenuProps>["items"][number];

const BasicLayout: React.FC = () => {
  const [collapsed, setCollapsed] = React.useState(false);
  const logout = useAuthStore((state) => state.logout);
  const user = useAuthStore((state) => state.user);
  const permissions = useAuthStore((state) => state.permissions);
  const menus = useAuthStore((state) => state.menus);
  const setMenus = useAuthStore((state) => state.setMenus);
  const token = useAuthStore((state) => state.token);
  const navigate = useNavigate();
  const location = useLocation();

  React.useEffect(() => {
    if (token && (!menus || menus.length === 0)) {
      getRouters()
        .then((res) => {
          setMenus(res as any);
        })
        .catch((err) => {
          console.error("Failed to fetch menus in layout:", err);
        });
    }
  }, [token, menus, setMenus]);

  const {
    token: { colorBgContainer },
  } = theme.useToken();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const userMenuItems: MenuItem[] = [
    {
      key: "logout",
      label: "退出登录",
      icon: <LogoutOutlined />,
      onClick: handleLogout,
    },
  ];

  // Helper to check if route should be visible
  const hasPermission = (route: AppRouteObject) => {
    if (!route.meta?.permissions || route.meta.permissions.length === 0) {
      return true;
    }
    return route.meta.permissions.some((code) => permissions.includes(code));
  };

  // Generate menu items from route config recursively
  const generateMenuItems = (
    routeList: AppRouteObject[],
    basePath = "",
  ): MenuItem[] => {
    const items: MenuItem[] = [];

    routeList.forEach((route) => {
      // Skip routes marked as hidden in menu
      if (route.meta?.hideInMenu) return;

      // Skip routes without permissions
      if (!hasPermission(route)) return;

      const currentPath = route.path?.startsWith("/")
        ? route.path
        : `${basePath}/${route.path}`.replace(/\/+/g, "/");

      // Handle index route explicitly if needed, but typically index routes don't appear in menu separately
      // Or they might represent the parent's clickable entry
      if (route.index) return;

      // Filter children that should be in the menu
      const validChildren = route.children?.filter(
        (child) =>
          !(child as AppRouteObject).meta?.hideInMenu &&
          !child.index &&
          hasPermission(child as AppRouteObject),
      ) as AppRouteObject[] | undefined;

      const hasChildren = validChildren && validChildren.length > 0;

      if (route.meta?.title) {
        items.push({
          key: currentPath,
          icon: route.meta.icon,
          label: route.meta.title,
          children: hasChildren
            ? generateMenuItems(validChildren, currentPath)
            : undefined,
          onClick: !hasChildren ? () => navigate(currentPath) : undefined,
        });
      }
    });

    return items;
  };

  // Map RouterVo from backend to Ant Design MenuItems
  const mapRoutersToMenuItems = (routerList: RouterVo[]): MenuItem[] => {
    return routerList
      .filter((item) => !item.hidden)
      .map((item) => {
        const IconComponent =
          item.meta?.icon && (Icons as any)[item.meta.icon]
            ? (Icons as any)[item.meta.icon]
            : undefined;

        return {
          key: item.path,
          icon: IconComponent ? React.createElement(IconComponent) : undefined,
          label: item.meta?.title || item.name,
          children:
            item.children && item.children.length > 0
              ? mapRoutersToMenuItems(item.children)
              : undefined,
          onClick:
            !item.children || item.children.length === 0
              ? () => navigate(item.path)
              : undefined,
        };
      });
  };

  // We find the main routes section (usually children of the '/' route)
  const rootRoute = routes.find((r) => r.path === "/") as
    | AppRouteObject
    | undefined;

  // Priority: 1. Dynamic menus from backend, 2. Static routes (fallback)
  const menuItems =
    menus && menus.length > 0
      ? mapRoutersToMenuItems(menus)
      : rootRoute?.children
        ? generateMenuItems(rootRoute.children, "/")
        : [];

  // Default to selecting the current path
  const selectedKeys = [location.pathname];
  // Simple logic to find the open keys based on the path
  const openKeys = ["/" + location.pathname.split("/")[1]];

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
          selectedKeys={selectedKeys}
          defaultOpenKeys={openKeys}
          mode="inline"
          items={menuItems}
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
        <Content style={{ margin: "16px" }}>
          {/* Outlet renders the child route components */}
          <Outlet />
        </Content>
        <Footer style={{ textAlign: "center" }}>
          PPMB Admin ©{new Date().getFullYear()} Created by Antigravity
        </Footer>
      </Layout>
    </Layout>
  );
};

export default BasicLayout;
