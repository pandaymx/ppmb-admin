import { create } from "zustand";
import { persist } from "zustand/middleware";

export interface User {
  id?: number | string;
  username?: string;
  [key: string]: any;
}

interface AuthState {
  token: string | null;
  user: User | null;
  permissions: string[];
  setToken: (token: string | null) => void;
  setUser: (user: User | null) => void;
  setPermissions: (permissions: string[]) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      permissions: [],
      setToken: (token) => set({ token }),
      setUser: (user) => set({ user }),
      setPermissions: (permissions) => set({ permissions }),
      logout: () => set({ token: null, user: null, permissions: [] }),
    }),
    {
      name: "ppmb-auth-storage",
    },
  ),
);
