import { createFileRoute } from "@tanstack/react-router";
import { PageHeader } from "@/components/common/PageHeader";
import { PatientSearch } from "@/components/patients/PatientSearch";

export const Route = createFileRoute("/receptionist/search")({
  component: () => (
    <div>
      <PageHeader
        title="Search Patients"
        description="Find patients by name, phone or email."
      />
      <PatientSearch />
    </div>
  ),
});
