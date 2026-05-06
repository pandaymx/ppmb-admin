import { create } from "zustand";
import { persist } from "zustand/middleware";
import { RouterVo } from "@/api/types/menu";

export interface User {
  id?: number | string;
  username?: string;
  [key: string]: any;
}

interface AuthState {
  token: string | null;
  user: User | null;
  permissions: string[];
  menus: RouterVo[];
  setToken: (token: string | null) => void;
  setUser: (user: User | null) => void;
  setPermissions: (permissions: string[]) => void;
  setMenus: (menus: RouterVo[]) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      permissions: [],
      menus: [],
      setToken: (token) => set({ token }),
      setUser: (user) => set({ user }),
      setPermissions: (permissions) => set({ permissions }),
      setMenus: (menus) => set({ menus }),
      logout: () =>
        set({ token: null, user: null, permissions: [], menus: [] }),
    }),
    {
      name: "ppmb-auth-storage",
    },
  ),
);
