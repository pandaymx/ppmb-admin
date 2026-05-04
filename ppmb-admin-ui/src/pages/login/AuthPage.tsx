import React, { useState } from "react";
import { Form, Input, Button, Checkbox, Tabs, message, Divider } from "antd";
import {
  UserOutlined,
  LockOutlined,
  MailOutlined,
  GithubOutlined,
  WechatOutlined,
  GoogleOutlined,
} from "@ant-design/icons";
import { useNavigate } from "react-router-dom";
import bgImage from "../../assets/login-bg.png";

import { useAuthStore } from "../../store/useAuthStore";

import { login } from "../../api/auth";

const AuthPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<"login" | "register">("login");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const setToken = useAuthStore((state) => state.setToken);
  const setUser = useAuthStore((state) => state.setUser);
  const setPermissions = useAuthStore((state) => state.setPermissions);

  const onLogin = async (values: any) => {
    setLoading(true);
    try {
      const response = await login({
        username: values.username,
        password: values.password,
      });

      // login 接口返回的是 Result<TokenDto>，request 拦截器已处理过 Result 包装
      // 这里的 response 就是 TokenDto (accessToken, expiresIn)
      const { accessToken } = response as any;

      setToken(accessToken);
      setUser({ username: values.username });

      // 注意：目前后端可能还没实现获取用户详情/权限的接口
      // 我们暂时保留这里的模拟权限，或者从 Token 中解析（如果有的话）
      setPermissions(["sys:user:list", "sys:role:list"]);

      message.success("登录成功");
      navigate("/");
    } catch (error: any) {
      console.error("Login failed:", error);
    } finally {
      setLoading(false);
    }
  };

  const onRegister = (values: any) => {
    setLoading(true);
    console.log("Register values:", values);
    setTimeout(() => {
      setLoading(false);
      message.success("注册成功，请登录");
      setActiveTab("login");
    }, 1500);
  };

  return (
    <div
      className="flex items-center justify-center min-h-screen w-full bg-cover bg-center bg-no-repeat"
      style={{ backgroundImage: `url(${bgImage})` }}
    >
      <div className="absolute inset-0 bg-black/20 backdrop-blur-[2px]"></div>

      <div className="relative z-10 w-full max-w-[420px] p-8 mx-4">
        <div className="bg-white/80 backdrop-blur-xl rounded-2xl shadow-2xl overflow-hidden border border-white/20 p-8">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-extrabold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
              PPMB Admin
            </h1>
            <p className="text-gray-500 mt-2">企业级中后台管理系统</p>
          </div>

          <Tabs
            activeKey={activeTab}
            onChange={(key) => setActiveTab(key as any)}
            centered
            items={[
              {
                key: "login",
                label: <span className="px-4 text-lg">登录</span>,
                children: (
                  <Form
                    name="login"
                    initialValues={{ remember: true }}
                    onFinish={onLogin}
                    layout="vertical"
                    size="large"
                    className="mt-4"
                  >
                    <Form.Item
                      name="username"
                      rules={[{ required: true, message: "请输入用户名!" }]}
                    >
                      <Input
                        prefix={<UserOutlined className="text-gray-400" />}
                        placeholder="用户名"
                      />
                    </Form.Item>
                    <Form.Item
                      name="password"
                      rules={[{ required: true, message: "请输入密码!" }]}
                    >
                      <Input.Password
                        prefix={<LockOutlined className="text-gray-400" />}
                        placeholder="密码"
                      />
                    </Form.Item>
                    <div className="flex justify-between items-center mb-6">
                      <Form.Item
                        name="remember"
                        valuePropName="checked"
                        noStyle
                      >
                        <Checkbox className="text-gray-500 text-sm">
                          记住我
                        </Checkbox>
                      </Form.Item>
                      <a
                        className="text-blue-600 text-sm hover:underline"
                        href=""
                      >
                        忘记密码?
                      </a>
                    </div>
                    <Form.Item>
                      <Button
                        type="primary"
                        htmlType="submit"
                        block
                        loading={loading}
                        className="h-12 text-lg font-medium shadow-lg shadow-blue-500/30"
                      >
                        登 录
                      </Button>
                    </Form.Item>
                  </Form>
                ),
              },
              {
                key: "register",
                label: <span className="px-4 text-lg">注册</span>,
                children: (
                  <Form
                    name="register"
                    onFinish={onRegister}
                    layout="vertical"
                    size="large"
                    className="mt-4"
                  >
                    <Form.Item
                      name="username"
                      rules={[{ required: true, message: "请输入用户名!" }]}
                    >
                      <Input
                        prefix={<UserOutlined className="text-gray-400" />}
                        placeholder="用户名"
                      />
                    </Form.Item>
                    <Form.Item
                      name="email"
                      rules={[
                        {
                          required: true,
                          type: "email",
                          message: "请输入有效的邮箱地址!",
                        },
                      ]}
                    >
                      <Input
                        prefix={<MailOutlined className="text-gray-400" />}
                        placeholder="邮箱"
                      />
                    </Form.Item>
                    <Form.Item
                      name="password"
                      rules={[{ required: true, message: "请输入密码!" }]}
                    >
                      <Input.Password
                        prefix={<LockOutlined className="text-gray-400" />}
                        placeholder="密码"
                      />
                    </Form.Item>
                    <Form.Item
                      name="confirm"
                      dependencies={["password"]}
                      rules={[
                        { required: true, message: "请确认您的密码!" },
                        ({ getFieldValue }) => ({
                          validator(_, value) {
                            if (!value || getFieldValue("password") === value) {
                              return Promise.resolve();
                            }
                            return Promise.reject(
                              new Error("两次输入的密码不一致!"),
                            );
                          },
                        }),
                      ]}
                    >
                      <Input.Password
                        prefix={<LockOutlined className="text-gray-400" />}
                        placeholder="确认密码"
                      />
                    </Form.Item>
                    <Form.Item>
                      <Button
                        type="primary"
                        htmlType="submit"
                        block
                        loading={loading}
                        className="h-12 text-lg font-medium shadow-lg shadow-blue-500/30"
                      >
                        注 册
                      </Button>
                    </Form.Item>
                  </Form>
                ),
              },
            ]}
          />

          <Divider plain className="text-gray-400 text-xs">
            其他登录方式
          </Divider>

          <div className="flex justify-center space-x-6">
            <Button
              shape="circle"
              icon={<GithubOutlined />}
              className="hover:text-black border-gray-200"
            />
            <Button
              shape="circle"
              icon={<WechatOutlined />}
              className="hover:text-green-500 border-gray-200"
            />
            <Button
              shape="circle"
              icon={<GoogleOutlined />}
              className="hover:text-red-500 border-gray-200"
            />
          </div>
        </div>

        <div className="mt-8 text-center text-white/60 text-sm">
          © {new Date().getFullYear()} PPMB Admin. Power by Antigravity
        </div>
      </div>
    </div>
  );
};

export default AuthPage;
