import { useState, type ReactNode } from "react";
import { Outlet } from "@tanstack/react-router";
import { Sidebar } from "./Sidebar";
import { Navbar } from "./Navbar";
import { RoleBasedRoute } from "@/components/common/RoleBasedRoute";
import { NAV_CONFIG } from "./nav-config";
import type { Role } from "@/types";

export function AppLayout({
  role,
  children,
}: {
  role: Role;
  children?: ReactNode;
}) {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <RoleBasedRoute allow={role}>
      <div className="min-h-screen bg-background">
        <Sidebar
          items={NAV_CONFIG[role]}
          open={sidebarOpen}
          onNavigate={() => setSidebarOpen(false)}
        />
        {sidebarOpen && (
          <div
            className="fixed inset-0 z-30 bg-black/40 lg:hidden"
            onClick={() => setSidebarOpen(false)}
          />
        )}
        <div className="lg:pl-64">
          <Navbar onToggleSidebar={() => setSidebarOpen((o) => !o)} />
          <main className="p-4 lg:p-6">{children ?? <Outlet />}</main>
        </div>
      </div>
    </RoleBasedRoute>
  );
}

export default AppLayout;
