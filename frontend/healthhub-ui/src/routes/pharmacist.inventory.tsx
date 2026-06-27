import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/pharmacist/inventory")({
  component: () => <PlaceholderPage title="Medicine Inventory" module="Pharmacy Inventory" />,
});
