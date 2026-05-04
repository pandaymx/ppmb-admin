import axios, { InternalAxiosRequestConfig, AxiosResponse, AxiosError } from "axios";
import { message } from "antd";
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
  }
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
      message.error(res.message || "请求失败");
      return Promise.reject(new Error(res.message || "Error"));
    }
  },
  (error: AxiosError) => {
    const { response } = error;

    if (response) {
      // Handle HTTP errors
      switch (response.status) {
        case 401:
          message.error("登录已过期，请重新登录");
          // Clear auth state
          useAuthStore.getState().logout();
          // Force redirect to login page
          window.location.href = "/login";
          break;
        case 403:
          message.error("没有权限访问");
          break;
        case 404:
          message.error("请求的资源不存在");
          break;
        case 500:
          message.error("服务器内部错误");
          break;
        default:
          message.error(`请求错误: ${response.status}`);
      }
    } else {
      // Network errors, timeout, etc.
      message.error("网络异常，请检查网络连接");
    }

    return Promise.reject(error);
  }
);

export default request;
