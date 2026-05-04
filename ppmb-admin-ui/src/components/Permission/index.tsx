import React from "react";
import { useAuthStore } from "../../store/useAuthStore";

interface PermissionProps {
  code: string | string[];
  children: React.ReactNode;
}

const Permission: React.FC<PermissionProps> = ({ code, children }) => {
  const permissions = useAuthStore((state) => state.permissions);

  const codes = Array.isArray(code) ? code : [code];

  const hasPermission = codes.some((c) => permissions.includes(c));

  if (!hasPermission) {
    return null;
  }

  return <>{children}</>;
};

export default Permission;
