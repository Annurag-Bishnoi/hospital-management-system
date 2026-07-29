import { createFileRoute, Outlet, useNavigate } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import { Sidebar } from "@/components/Sidebar";
import { Navbar } from "@/components/Navbar";
import { getUser, type SessionUser } from "@/lib/auth";

export const Route = createFileRoute("/_app")({
  component: AppLayout,
});

function AppLayout() {
  const navigate = useNavigate();
  const [user, setUser] = useState<SessionUser | null | undefined>(undefined);

  useEffect(() => {
    const sessionUser = getUser();
    setUser(sessionUser);

    if (!sessionUser) {
      navigate({ to: "/login", replace: true });
    }
  }, [navigate]);

  if (user === undefined) {
    return (
      <div className="min-h-screen grid place-items-center bg-background text-sm text-muted-foreground">
        Loading dashboard...
      </div>
    );
  }

  if (!user) return null;

  return (
    <div className="min-h-screen flex bg-background">
      <Sidebar role={user.role} />
      <div className="flex-1 flex flex-col min-w-0">
        <Navbar name={user.name} role={user.role} />
        <main className="flex-1 p-4 md:p-8 overflow-x-hidden">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
