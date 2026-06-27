import { Link, useRouterState } from "@tanstack/react-router";
import { HeartPulse } from "lucide-react";
import type { NavItem } from "./nav-config";
import { cn } from "@/lib/utils";

export function Sidebar({
  items,
  open,
  onNavigate,
}: {
  items: NavItem[];
  open: boolean;
  onNavigate?: () => void;
}) {
  const pathname = useRouterState({
    select: (s) => s.location.pathname,
  });

  return (
    <aside
      className={cn(
        "fixed inset-y-0 left-0 z-40 flex w-64 flex-col border-r bg-sidebar transition-transform duration-200 lg:translate-x-0",
        open ? "translate-x-0" : "-translate-x-full",
      )}
    >
      <div className="flex h-16 items-center gap-2 border-b px-5">
        <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary text-primary-foreground">
          <HeartPulse className="h-5 w-5" />
        </div>
        <div className="leading-tight">
          <p className="text-sm font-semibold text-sidebar-foreground">HMS</p>
          <p className="text-xs text-muted-foreground">Hospital System</p>
        </div>
      </div>

      <nav className="flex-1 space-y-1 overflow-y-auto px-3 py-4">
        {items.map((item) => {
          const active = pathname === item.to;
          const Icon = item.icon;
          return (
            <Link
              key={item.to}
              to={item.to}
              onClick={onNavigate}
              className={cn(
                "group flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                active
                  ? "bg-primary text-primary-foreground"
                  : "text-sidebar-foreground/80 hover:bg-sidebar-accent hover:text-sidebar-accent-foreground",
              )}
            >
              <Icon className="h-4 w-4 shrink-0" />
              <span className="truncate">{item.label}</span>
              {item.placeholder && (
                <span
                  className={cn(
                    "ml-auto rounded px-1.5 py-0.5 text-[10px] font-semibold uppercase",
                    active
                      ? "bg-primary-foreground/20 text-primary-foreground"
                      : "bg-muted text-muted-foreground",
                  )}
                >
                  Soon
                </span>
              )}
            </Link>
          );
        })}
      </nav>

      <div className="border-t px-5 py-3 text-xs text-muted-foreground">
        HMS Frontend v1.0
      </div>
    </aside>
  );
}

export default Sidebar;
