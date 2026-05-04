import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuthStore } from "../store/useAuthStore";

interface ProtectedRouteProps {
  children: React.ReactNode;
  permissions?: string[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, permissions }) => {
  const token = useAuthStore((state) => state.token);
  const userPermissions = useAuthStore((state) => state.permissions);
  const location = useLocation();

  if (!token) {
    // Redirect to login page but save the current location to redirect back after login
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (permissions && permissions.length > 0) {
    const hasPermission = permissions.some((code) => userPermissions.includes(code));
    if (!hasPermission) {
      // You can redirect to an unauthorized page or dashboard if desired
      return <Navigate to="/" replace />;
    }
  }

  return <>{children}</>;
};

export default ProtectedRoute;
