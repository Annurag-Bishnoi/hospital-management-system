import { createFileRoute } from "@tanstack/react-router";
import { PageHeader } from "@/components/common/PageHeader";
import { PatientsManager } from "@/components/patients/PatientsManager";

export const Route = createFileRoute("/admin/patients")({
  component: () => (
    <div>
      <PageHeader
        title="Patients"
        description="View and manage all registered patients."
      />
      <PatientsManager />
    </div>
  ),
});
