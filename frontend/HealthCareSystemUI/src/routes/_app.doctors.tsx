import { createFileRoute } from "@tanstack/react-router";
import { DataTable, PageHeader } from "@/components/DataTable";
import { doctors, type Doctor } from "@/lib/mockData";

export const Route = createFileRoute("/_app/doctors")({ component: DoctorsPage });

function DoctorsPage() {
  return (
    <div>
      <PageHeader title="Doctors" description="Hospital medical staff" />
      <DataTable<Doctor>
        rows={doctors}
        columns={[
          { key: "id", label: "ID" },
          { key: "name", label: "Name" },
          { key: "department", label: "Department" },
          { key: "phone", label: "Phone" },
          { key: "experience", label: "Experience (yrs)" },
        ]}
      />
    </div>
  );
}
