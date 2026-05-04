import axios, {
  InternalAxiosRequestConfig,
  AxiosResponse,
  AxiosError,
} from "axios";
import { notification } from "antd";
import { useAuthStore } from "../store/useAuthStore";
import { Result } from "../api/types/common";

// Create an axios instance
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  timeout: 10000,
});

// Request interceptor
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Get token from zustand store
    const token = useAuthStore.getState().token;

    // Add token to headers
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  },
);

// Response interceptor
request.interceptors.response.use(
  (response: AxiosResponse<Result>) => {
    const res = response.data;

    // Check if the business logic code is successful
    if (res.code === "00000") {
      return res.data;
    } else {
      // Business error
      notification.error({
        message: "请求错误",
        description: res.message || "业务处理异常",
        placement: "topRight",
      });
      return Promise.reject(new Error(res.message || "Error"));
    }
  },
  (error: AxiosError) => {
    const { response } = error;

    if (response) {
      // Try to get message from ProblemDetail (RFC 7807)
      const data = response.data as any;
      const errorMessage =
        data.detail || data.title || `请求错误: ${response.status}`;

      // Handle HTTP errors
      switch (response.status) {
        case 400:
          notification.error({
            message: data.title || "请求参数错误",
            description: data.detail || "请检查输入信息",
            placement: "topRight",
          });
          break;
        case 401:
          // If on login page, show the error message (e.g. invalid credentials)
          if (window.location.pathname === "/login") {
            notification.error({
              message: "登录失败",
              description: errorMessage,
              placement: "topRight",
            });
          } else {
            notification.warning({
              message: "登录过期",
              description: "您的登录已失效，请重新登录",
              placement: "topRight",
            });
            // Clear auth state
            useAuthStore.getState().logout();
            // Redirect to login page
            window.location.href = "/login";
          }
          break;
        case 403:
          notification.error({
            message: "拒绝访问",
            description: "您没有权限执行此操作",
            placement: "topRight",
          });
          break;
        case 404:
          notification.error({
            message: "资源未找到",
            description: "请求的资源不存在",
            placement: "topRight",
          });
          break;
        case 500:
          notification.error({
            message: "服务器错误",
            description: errorMessage || "系统内部异常，请稍后再试",
            placement: "topRight",
          });
          break;
        default:
          notification.error({
            message: `错误 ${response.status}`,
            description: errorMessage,
            placement: "topRight",
          });
      }
    } else {
      // Network errors, timeout, etc.
      notification.error({
        message: "网络异常",
        description: "请检查您的网络连接或稍后再试",
        placement: "topRight",
      });
    }

    return Promise.reject(error);
  },
);

export default request;
