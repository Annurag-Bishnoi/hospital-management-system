import { createFileRoute } from "@tanstack/react-router";
import { DataTable, PageHeader } from "@/components/DataTable";
import { Badge } from "@/components/Badge";
import { bills, billTotal, type Bill } from "@/lib/mockData";

export const Route = createFileRoute("/_app/my-bills")({ component: MyBillsPage });

function MyBillsPage() {
  return (
    <div>
      <PageHeader title="My Bills" description="Your billing history" />
      <DataTable<Bill>
        rows={bills}
        columns={[
          { key: "id", label: "Bill ID" },
          { key: "consultation", label: "Consult", render: (r) => `₹${r.consultation}` },
          { key: "medicine", label: "Medicine", render: (r) => `₹${r.medicine}` },
          { key: "room", label: "Room", render: (r) => `₹${r.room}` },
          { key: "other", label: "Other", render: (r) => `₹${r.other}` },
          { key: "id", label: "Total", render: (r) => <span className="font-semibold">₹{billTotal(r)}</span> },
          { key: "status", label: "Status", render: (r) => <Badge>{r.status}</Badge> },
        ]}
      />
    </div>
  );
}
