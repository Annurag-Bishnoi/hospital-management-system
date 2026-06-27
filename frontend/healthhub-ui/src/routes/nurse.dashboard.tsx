import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/nurse/dashboard")({
  component: () => <PlaceholderPage title="Nurse Dashboard" module="Nurse" />,
});
