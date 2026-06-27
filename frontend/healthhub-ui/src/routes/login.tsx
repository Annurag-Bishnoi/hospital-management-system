import { useEffect, useState } from "react";
import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { HeartPulse, Loader2 } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { FormInput } from "@/components/forms/FormInput";
import { login } from "@/services/authService";
import { getUser, homeForRole, saveUser } from "@/utils/auth";

export const Route = createFileRoute("/login")({
  head: () => ({
    meta: [
      { title: "Login — HMS" },
      { name: "description", content: "Sign in to the Hospital Management System." },
    ],
  }),
  component: LoginPage,
});

function LoginPage() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const user = getUser();
    if (user) navigate({ to: homeForRole(String(user.role)), replace: true });
  }, [navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) {
      toast.error("Please enter username and password.");
      return;
    }
    setLoading(true);
    try {
      const res = await login({ username, password });
      if (res.success === false) {
        toast.error(res.message || "Invalid credentials.");
        return;
      }
      if (!res.role) {
        toast.error("Login response missing role. Cannot redirect.");
        return;
      }
      saveUser(res);
      toast.success(res.message || `Welcome, ${res.fullName ?? username}!`);
      navigate({ to: homeForRole(String(res.role)), replace: true });
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Login failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-secondary via-background to-accent/40 px-4">
      <div className="w-full max-w-md">
        <div className="mb-6 flex flex-col items-center text-center">
          <div className="mb-3 flex h-14 w-14 items-center justify-center rounded-2xl bg-primary text-primary-foreground shadow-lg">
            <HeartPulse className="h-7 w-7" />
          </div>
          <h1 className="text-2xl font-bold text-foreground">HMS</h1>
          <p className="text-sm text-muted-foreground">
            Hospital Management System
          </p>
        </div>

        <Card className="shadow-xl">
          <CardHeader>
            <h2 className="text-lg font-semibold">Sign in to your account</h2>
            <p className="text-sm text-muted-foreground">
              Enter your credentials to continue
            </p>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              <FormInput
                id="username"
                label="Username"
                placeholder="e.g. doctor2"
                autoComplete="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
              <FormInput
                id="password"
                label="Password"
                type="password"
                placeholder="••••••••"
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <Button type="submit" className="w-full" disabled={loading}>
                {loading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                Sign in
              </Button>
            </form>
          </CardContent>
        </Card>

        <p className="mt-6 text-center text-xs text-muted-foreground">
          Connected to {import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080"}
        </p>
      </div>
    </div>
  );
}
