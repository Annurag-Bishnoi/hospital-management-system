import { useEffect, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { Shield } from "lucide-react";
import { PageHeader } from "@/components/common/PageHeader";
import { LoadingSpinner } from "@/components/common/LoadingSpinner";
import { EmptyState } from "@/components/common/EmptyState";
import { Card, CardContent } from "@/components/ui/card";
import { getRoles } from "@/services/userService";
import type { RoleItem } from "@/types";

export const Route = createFileRoute("/admin/roles")({
  component: RolesPage,
});

function RolesPage() {
  const [roles, setRoles] = useState<RoleItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        setRoles(await getRoles());
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to load roles.");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  return (
    <div>
      <PageHeader
        title="Roles"
        description="System roles available for user assignment."
      />
      {loading ? (
        <LoadingSpinner label="Loading roles..." />
      ) : error ? (
        <Card>
          <CardContent className="p-6 text-center text-sm text-destructive">
            {error}
          </CardContent>
        </Card>
      ) : roles.length === 0 ? (
        <EmptyState title="No roles found" />
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {roles.map((r, i) => (
            <Card key={String(r.id ?? r.roleCode ?? r.code ?? i)}>
              <CardContent className="flex items-center gap-3 p-5">
                <div className="rounded-lg bg-primary/10 p-3 text-primary">
                  <Shield className="h-5 w-5" />
                </div>
                <div>
                  <p className="font-semibold text-foreground">
                    {String(r.roleName ?? r.name ?? r.roleCode ?? r.code ?? "—")}
                  </p>
                  <p className="text-xs text-muted-foreground">
                    {String(r.roleCode ?? r.code ?? "")}
                  </p>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
