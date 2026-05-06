import request from "@/utils/request";
import { RouterVo } from "./types/menu";

/**
 * 获取路由菜单树
 */
export function getRouters() {
  return request.get<RouterVo[]>("/menus/routers");
}

/**
 * 获取所有菜单列表（管理用）
 */
export function getMenuList() {
  return request.get("/menus/tree");
}
