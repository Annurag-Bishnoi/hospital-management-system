import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/admin/reports")({
  component: () => <PlaceholderPage title="Reports" module="Reports" />,
});
