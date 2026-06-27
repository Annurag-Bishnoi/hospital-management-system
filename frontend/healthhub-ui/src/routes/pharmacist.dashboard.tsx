import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/pharmacist/dashboard")({
  component: () => (
    <PlaceholderPage title="Pharmacist Dashboard" module="Pharmacist" />
  ),
});
