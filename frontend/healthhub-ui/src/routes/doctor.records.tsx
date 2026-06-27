import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/doctor/records")({
  component: () => <PlaceholderPage title="Medical Records" module="Medical Records" />,
});
