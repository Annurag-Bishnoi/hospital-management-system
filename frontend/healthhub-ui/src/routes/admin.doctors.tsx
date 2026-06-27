import { createFileRoute } from "@tanstack/react-router";
import { PageHeader } from "@/components/common/PageHeader";
import { DoctorsManager } from "@/components/doctors/DoctorsManager";

export const Route = createFileRoute("/admin/doctors")({
  component: () => (
    <div>
      <PageHeader
        title="Doctors"
        description="View and manage all registered doctors."
      />
      <DoctorsManager />
    </div>
  ),
});
