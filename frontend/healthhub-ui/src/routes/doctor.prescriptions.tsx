import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/doctor/prescriptions")({
  component: () => <PlaceholderPage title="Prescriptions" module="Prescription" />,
});
