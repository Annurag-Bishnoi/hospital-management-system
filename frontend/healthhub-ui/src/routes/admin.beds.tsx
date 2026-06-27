import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/admin/beds")({
  component: () => <PlaceholderPage title="Bed Management" module="Bed Management" />,
});
