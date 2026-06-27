import { useState } from "react";
import { Search } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { DataTable, type Column } from "@/components/tables/DataTable";
import { StatusBadge } from "@/components/common/StatusBadge";
import { EmptyState } from "@/components/common/EmptyState";
import { searchPatients } from "@/services/patientService";
import type { Patient } from "@/types";

function DetailRow({ label, value }: { label: string; value?: string }) {
  return (
    <div className="flex justify-between gap-4 border-b py-2 text-sm last:border-0">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium text-right">{value || "—"}</span>
    </div>
  );
}

export function PatientSearch({
  onSelect,
  selectLabel = "Select",
}: {
  onSelect?: (patient: Patient) => void;
  selectLabel?: string;
}) {
  const [keyword, setKeyword] = useState("");
  const [results, setResults] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searched, setSearched] = useState(false);
  const [viewing, setViewing] = useState<Patient | null>(null);

  const doSearch = async (e?: React.FormEvent) => {
    e?.preventDefault();
    if (!keyword.trim()) {
      toast.error("Enter a keyword to search.");
      return;
    }
    setLoading(true);
    setError(null);
    setSearched(true);
    try {
      setResults(await searchPatients(keyword.trim()));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Search failed.");
    } finally {
      setLoading(false);
    }
  };

  const columns: Column<Patient>[] = [
    {
      key: "id",
      header: "ID",
      className: "w-16",
      render: (p) => <span className="text-muted-foreground">#{String(p.id ?? "—")}</span>,
    },
    { key: "fullName", header: "Name" },
    { key: "phone", header: "Phone" },
    { key: "bloodGroup", header: "Blood" },
    {
      key: "active",
      header: "Status",
      render: (p) => <StatusBadge active={p.active !== false} />,
    },
    {
      key: "actions",
      header: "Actions",
      className: "text-right",
      render: (p) => (
        <div className="flex justify-end gap-2">
          <Button variant="outline" size="sm" onClick={() => setViewing(p)}>
            View
          </Button>
          {onSelect && (
            <Button size="sm" onClick={() => onSelect(p)}>
              {selectLabel}
            </Button>
          )}
        </div>
      ),
    },
  ];

  return (
    <div className="space-y-4">
      <form onSubmit={doSearch} className="flex gap-2">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder="Search patients by name, phone or email..."
            className="pl-9"
          />
        </div>
        <Button type="submit" disabled={loading}>
          {loading ? "Searching..." : "Search"}
        </Button>
      </form>

      {searched ? (
        <DataTable
          columns={columns}
          data={results}
          loading={loading}
          error={error}
          searchable={false}
          emptyTitle="No matching patients"
          emptyDescription="Try a different keyword."
          rowKey={(p) => String(p.id)}
        />
      ) : (
        <EmptyState
          icon={<Search className="h-6 w-6" />}
          title="Search for patients"
          description="Enter a keyword above to find registered patients."
        />
      )}

      <Dialog open={!!viewing} onOpenChange={(o) => !o && setViewing(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Patient Details</DialogTitle>
          </DialogHeader>
          {viewing && (
            <div className="space-y-1">
              <DetailRow label="Full Name" value={viewing.fullName} />
              <DetailRow label="Date of Birth" value={viewing.dateOfBirth} />
              <DetailRow label="Gender" value={viewing.gender} />
              <DetailRow label="Blood Group" value={viewing.bloodGroup} />
              <DetailRow label="Phone" value={viewing.phone} />
              <DetailRow label="Email" value={viewing.email} />
              <DetailRow label="Address" value={viewing.address} />
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}

export default PatientSearch;
