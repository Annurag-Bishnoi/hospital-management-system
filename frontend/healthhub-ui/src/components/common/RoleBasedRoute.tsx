import { useEffect, useState, type ReactNode } from "react";
import { useNavigate } from "@tanstack/react-router";
import { getUser, homeForRole, normalizeRole } from "@/utils/auth";
import type { Role } from "@/types";
import { LoadingSpinner } from "@/components/common/LoadingSpinner";

/**
 * Client-side guard. The HMS backend is external, so auth state lives in
 * localStorage. Redirects unauthenticated users to /login and users with the
 * wrong role to their own dashboard.
 */
export function RoleBasedRoute({
  allow,
  children,
}: {
  allow: Role;
  children: ReactNode;
}) {
  const navigate = useNavigate();
  const [status, setStatus] = useState<"checking" | "ok">("checking");

  useEffect(() => {
    const user = getUser();
    if (!user) {
      navigate({ to: "/login", replace: true });
      return;
    }
    const role = normalizeRole(String(user.role));
    if (role !== allow) {
      navigate({ to: homeForRole(role), replace: true });
      return;
    }
    setStatus("ok");
  }, [allow, navigate]);

  if (status === "checking") {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <LoadingSpinner label="Loading..." />
      </div>
    );
  }

  return <>{children}</>;
}

export default RoleBasedRoute;
