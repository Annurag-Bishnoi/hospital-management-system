import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/patient/prescriptions")({
  component: () => <PlaceholderPage title="My Prescriptions" module="Prescription" />,
});
