import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/admin/receptionists")({
  component: () => <PlaceholderPage title="Receptionists" module="Receptionist" />,
});
