import { createFileRoute } from "@tanstack/react-router";
import { DataTable, PageHeader } from "@/components/DataTable";
import { prescriptions, type Prescription } from "@/lib/mockData";

export const Route = createFileRoute("/_app/my-prescriptions")({ component: MyPrescriptionsPage });

function MyPrescriptionsPage() {
  return (
    <div>
      <PageHeader title="My Prescriptions" description="View your prescriptions" />
      <DataTable<Prescription>
        rows={prescriptions}
        columns={[
          { key: "id", label: "ID" },
          { key: "diagnosis", label: "Diagnosis" },
          { key: "medicines", label: "Medicines" },
          { key: "dosage", label: "Dosage" },
          { key: "instructions", label: "Instructions" },
          { key: "followUp", label: "Follow-up" },
        ]}
      />
    </div>
  );
}
