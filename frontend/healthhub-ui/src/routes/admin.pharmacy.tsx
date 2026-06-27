import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/admin/pharmacy")({
  component: () => <PlaceholderPage title="Pharmacy" module="Pharmacy" />,
});
