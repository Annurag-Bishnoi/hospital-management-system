import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/nurse/patients")({
  component: () => <PlaceholderPage title="Assigned Patients" module="Nursing" />,
});
