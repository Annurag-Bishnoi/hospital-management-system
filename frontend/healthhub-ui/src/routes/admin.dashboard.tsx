import { useEffect, useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { Users, UserCheck, UserX, Shield, Activity, BedDouble } from "lucide-react";
import { PageHeader } from "@/components/common/PageHeader";
import { StatCard } from "@/components/common/StatCard";
import { LoadingSpinner } from "@/components/common/LoadingSpinner";
import { Card, CardContent } from "@/components/ui/card";
import { getDashboardSummary } from "@/services/userService";

export const Route = createFileRoute("/admin/dashboard")({
  component: AdminDashboard,
});

const ICONS = [Users, UserCheck, UserX, Shield, Activity, BedDouble];
const TONES = ["primary", "emerald", "rose", "violet", "amber"] as const;

function humanize(key: string) {
  return key
    .replace(/([A-Z])/g, " $1")
    .replace(/[_-]/g, " ")
    .replace(/\s+/g, " ")
    .trim()
    .replace(/^\w/, (c) => c.toUpperCase());
}

function AdminDashboard() {
  const [summary, setSummary] = useState<Record<string, unknown> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const data = await getDashboardSummary();
        setSummary((data ?? {}) as Record<string, unknown>);
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to load summary.");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const entries = summary
    ? Object.entries(summary).filter(
        ([, v]) => typeof v === "number" || typeof v === "string",
      )
    : [];

  return (
    <div>
      <PageHeader
        title="Admin Dashboard"
        description="Overview of your hospital management system."
      />
      {loading ? (
        <LoadingSpinner label="Loading summary..." />
      ) : error ? (
        <Card>
          <CardContent className="p-6 text-center text-sm text-destructive">
            {error}
          </CardContent>
        </Card>
      ) : entries.length === 0 ? (
        <Card>
          <CardContent className="p-6 text-center text-sm text-muted-foreground">
            No summary data available.
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {entries.map(([key, value], i) => (
            <StatCard
              key={key}
              label={humanize(key)}
              value={String(value)}
              icon={ICONS[i % ICONS.length]}
              tone={TONES[i % TONES.length]}
            />
          ))}
        </div>
      )}
    </div>
  );
}
