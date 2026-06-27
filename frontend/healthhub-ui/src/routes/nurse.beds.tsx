import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/nurse/beds")({
  component: () => <PlaceholderPage title="Bed Status" module="Bed Management" />,
});
