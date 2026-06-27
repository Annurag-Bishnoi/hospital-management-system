import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/admin/settings")({
  component: () => <PlaceholderPage title="Settings" module="Settings" />,
});
