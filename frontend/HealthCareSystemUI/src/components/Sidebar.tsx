import { Link, useRouterState, useNavigate } from "@tanstack/react-router";
import {
  LayoutDashboard, Users, Stethoscope, CalendarDays, BedDouble, Receipt,
  Pill, BarChart3, Settings, FileText, Activity, LogOut, HeartPulse, ClipboardList,
} from "lucide-react";
import type { Role } from "@/lib/mockData";
import { roleLabel } from "@/lib/mockData";
import { clearUser } from "@/lib/auth";

interface MenuItem {
  to: string;
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  roles: Role[];
}

const MENU: MenuItem[] = [
  { to: "/dashboard", label: "Dashboard", icon: LayoutDashboard, roles: ["admin", "doctor", "nurse", "pharmacist", "receptionist", "patient"] },
  { to: "/patients", label: "Patients", icon: Users, roles: ["admin", "doctor", "nurse", "receptionist"] },
  { to: "/doctors", label: "Doctors", icon: Stethoscope, roles: ["admin"] },
  { to: "/appointments", label: "Appointments", icon: CalendarDays, roles: ["admin", "doctor", "receptionist"] },
  { to: "/prescriptions", label: "Prescriptions", icon: FileText, roles: ["doctor"] },
  { to: "/vitals", label: "Patient Vitals", icon: Activity, roles: ["nurse"] },
  { to: "/beds", label: "Bed Availability", icon: BedDouble, roles: ["admin", "nurse"] },
  { to: "/pharmacy", label: "Pharmacy", icon: Pill, roles: ["admin", "pharmacist"] },
  { to: "/billing", label: "Billing", icon: Receipt, roles: ["admin", "receptionist"] },
  { to: "/analytics", label: "Analytics", icon: BarChart3, roles: ["admin"] },
  { to: "/my-appointments", label: "My Appointments", icon: CalendarDays, roles: ["patient"] },
  { to: "/my-prescriptions", label: "My Prescriptions", icon: ClipboardList, roles: ["patient"] },
  { to: "/my-bills", label: "My Bills", icon: Receipt, roles: ["patient"] },
  { to: "/settings", label: "Settings", icon: Settings, roles: ["admin"] },
];

export function Sidebar({ role }: { role: Role }) {
  const navigate = useNavigate();
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const items = MENU.filter((m) => m.roles.includes(role));

  const handleLogout = () => {
    clearUser();
    navigate({ to: "/login" });
  };

  return (
    <aside className="hidden md:flex w-64 flex-col bg-sidebar border-r border-sidebar-border">
      <div className="px-6 py-5 border-b border-sidebar-border flex items-center gap-2">
        <div className="size-9 rounded-lg bg-primary text-primary-foreground grid place-items-center">
          <HeartPulse className="size-5" />
        </div>
        <div>
          <div className="font-semibold text-sidebar-foreground leading-tight">MediCare</div>
          <div className="text-xs text-muted-foreground">{roleLabel(role)} Portal</div>
        </div>
      </div>
      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        {items.map((item) => {
          const active = pathname === item.to;
          const Icon = item.icon;
          return (
            <Link
              key={item.to}
              to={item.to}
              className={[
                "flex items-center gap-3 px-3 py-2 rounded-lg text-sm transition-colors",
                active
                  ? "bg-sidebar-accent text-sidebar-accent-foreground font-medium"
                  : "text-sidebar-foreground hover:bg-sidebar-accent/60",
              ].join(" ")}
            >
              <Icon className="size-4" />
              {item.label}
            </Link>
          );
        })}
      </nav>
      <div className="p-3 border-t border-sidebar-border">
        <button
          onClick={handleLogout}
          className="flex w-full items-center gap-3 px-3 py-2 rounded-lg text-sm text-sidebar-foreground hover:bg-sidebar-accent/60"
        >
          <LogOut className="size-4" />
          Logout
        </button>
      </div>
    </aside>
  );
}
