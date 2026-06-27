import { createFileRoute } from "@tanstack/react-router";
import { PageHeader } from "@/components/common/PageHeader";
import { PatientsManager } from "@/components/patients/PatientsManager";

export const Route = createFileRoute("/receptionist/patients")({
  component: () => (
    <div>
      <PageHeader
        title="All Patients"
        description="View and manage all registered patients."
      />
      <PatientsManager />
    </div>
  ),
});
