import { createFileRoute } from "@tanstack/react-router";
import { PageHeader } from "@/components/common/PageHeader";
import { RegisterPatient } from "@/components/patients/RegisterPatient";

export const Route = createFileRoute("/receptionist/register-patient")({
  component: () => (
    <div>
      <PageHeader
        title="Register Patient"
        description="Username and password are optional — leave blank to auto-generate credentials."
      />
      <RegisterPatient />
    </div>
  ),
});
