import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/patient/dashboard")({
  component: () => (
    <PlaceholderPage title="Patient Dashboard" module="Patient self-service" />
  ),
});
