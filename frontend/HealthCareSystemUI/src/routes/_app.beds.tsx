import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { BedDouble } from "lucide-react";
import { PageHeader } from "@/components/DataTable";
import { Badge } from "@/components/Badge";
import { Button, Select } from "@/components/UI";
import { beds as seed, type Bed } from "@/lib/mockData";

export const Route = createFileRoute("/_app/beds")({ component: BedsPage });

const WARDS = ["All", "General", "ICU", "Emergency", "Private"] as const;

function BedsPage() {
  const [rows, setRows] = useState<Bed[]>(seed);
  const [filter, setFilter] = useState<(typeof WARDS)[number]>("All");

  const toggle = (id: string) => {
    setRows((r) =>
      r.map((b) =>
        b.id === id
          ? { ...b, status: b.status === "Available" ? "Occupied" : "Available", patient: b.status === "Occupied" ? undefined : "New Patient" }
          : b
      )
    );
  };

  const filtered = filter === "All" ? rows : rows.filter((b) => b.ward === filter);

  return (
    <div>
      <PageHeader
        title="Bed Availability"
        description="Track ward and bed status in real time"
        action={
          <Select value={filter} onChange={(e) => setFilter(e.target.value as typeof filter)} className="w-40">
            {WARDS.map((w) => <option key={w}>{w}</option>)}
          </Select>
        }
      />
      <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-4">
        {filtered.map((b) => (
          <div key={b.id} className="bg-card border border-border rounded-xl p-4">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <div className="size-9 rounded-lg bg-accent text-accent-foreground grid place-items-center">
                  <BedDouble className="size-4" />
                </div>
                <div>
                  <div className="font-semibold">{b.number}</div>
                  <div className="text-xs text-muted-foreground">{b.ward}</div>
                </div>
              </div>
              <Badge>{b.status}</Badge>
            </div>
            <div className="text-sm text-muted-foreground min-h-[20px]">
              {b.patient ? `Patient: ${b.patient}` : "—"}
            </div>
            {b.status !== "Maintenance" && (
              <Button size="sm" variant="outline" className="w-full mt-3" onClick={() => toggle(b.id)}>
                {b.status === "Available" ? "Assign" : "Discharge"}
              </Button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
