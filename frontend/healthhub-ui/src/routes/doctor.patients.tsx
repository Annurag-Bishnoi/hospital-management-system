import { createFileRoute } from "@tanstack/react-router";
import { PageHeader } from "@/components/common/PageHeader";
import { PatientSearch } from "@/components/patients/PatientSearch";

export const Route = createFileRoute("/doctor/patients")({
  component: () => (
    <div>
      <PageHeader
        title="Patients"
        description="Search patients and view their details."
      />
      <PatientSearch />
    </div>
  ),
});
