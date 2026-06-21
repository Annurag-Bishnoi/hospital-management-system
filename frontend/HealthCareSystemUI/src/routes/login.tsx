import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { useState } from "react";
import { HeartPulse } from "lucide-react";
import { Button, Field, Input, Select } from "@/components/UI";
import { ROLES, roleLabel, type Role } from "@/lib/mockData";
import { setUser } from "@/lib/auth";

export const Route = createFileRoute("/login")({
  component: LoginPage,
  head: () => ({ meta: [{ title: "Login — MediCare Hospital" }] }),
});

function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("demo@medicare.com");
  const [password, setPassword] = useState("demo");
  const [role, setRole] = useState<Role>("admin");

  const onSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) return;
    const name = email.split("@")[0].replace(/[._]/g, " ").replace(/\b\w/g, (c) => c.toUpperCase());
    setUser({ email, role, name });
    navigate({ to: "/dashboard" });
  };

  return (
    <div className="min-h-screen grid place-items-center p-4 bg-gradient-to-br from-secondary/40 via-background to-accent/40">
      <div className="w-full max-w-md">
        <div className="bg-card border border-border rounded-2xl shadow-sm p-8">
          <div className="flex flex-col items-center text-center mb-6">
            <div className="size-12 rounded-xl bg-primary text-primary-foreground grid place-items-center mb-3">
              <HeartPulse className="size-6" />
            </div>
            <h1 className="text-xl font-semibold">MediCare Hospital</h1>
            <p className="text-sm text-muted-foreground mt-1">Sign in to your dashboard</p>
          </div>
          <form onSubmit={onSubmit} className="space-y-4">
            <Field label="Email">
              <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
            </Field>
            <Field label="Password">
              <Input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
            </Field>
            <Field label="Role">
              <Select value={role} onChange={(e) => setRole(e.target.value as Role)}>
                {ROLES.map((r) => <option key={r} value={r}>{roleLabel(r)}</option>)}
              </Select>
            </Field>
            <Button type="submit" className="w-full">Login</Button>
          </form>
          <p className="text-xs text-muted-foreground text-center mt-4">
            Demo login — any credentials work. Pick a role to explore.
          </p>
        </div>
      </div>
    </div>
  );
}
