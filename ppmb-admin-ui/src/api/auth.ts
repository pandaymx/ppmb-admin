import request from "@/utils/request";
import { LoginParams, LoginResult } from "@/api/types/auth";

/**
 * 登录接口
 * @param data 登录参数
 */
export function login(data: LoginParams) {
  return request.post<LoginResult>("/auth/login", data);
}

/**
 * 获取当前用户信息（包含权限）
 * 提示：后续需要后端提供该接口，目前先返回 mock
 */
export function getUserInfo() {
  return request.get("/auth/me");
}

/**
 * 退出登录
 */
export function logout() {
  return request.post("/auth/logout");
}
