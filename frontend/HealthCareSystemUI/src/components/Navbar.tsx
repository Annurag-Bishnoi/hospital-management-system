import { useNavigate } from "@tanstack/react-router";
import { LogOut, UserCircle2 } from "lucide-react";
import { clearUser } from "@/lib/auth";
import { roleLabel, type Role } from "@/lib/mockData";

export function Navbar({ name, role }: { name: string; role: Role }) {
  const navigate = useNavigate();
  return (
    <header className="h-16 bg-card border-b border-border flex items-center justify-between px-4 md:px-8">
      <div>
        <h1 className="text-lg font-semibold text-foreground">MediCare Hospital</h1>
        <p className="text-xs text-muted-foreground">Management System</p>
      </div>
      <div className="flex items-center gap-3">
        <div className="hidden sm:flex flex-col items-end">
          <span className="text-sm font-medium text-foreground">{name}</span>
          <span className="text-xs text-muted-foreground">{roleLabel(role)}</span>
        </div>
        <div className="size-9 rounded-full bg-accent text-accent-foreground grid place-items-center">
          <UserCircle2 className="size-5" />
        </div>
        <button
          onClick={() => { clearUser(); navigate({ to: "/login" }); }}
          className="md:hidden p-2 rounded-lg hover:bg-muted"
          aria-label="Logout"
        >
          <LogOut className="size-4" />
        </button>
      </div>
    </header>
  );
}
