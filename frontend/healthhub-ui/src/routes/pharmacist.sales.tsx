import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/pharmacist/sales")({
  component: () => <PlaceholderPage title="Sales / Billing" module="Pharmacy Sales" />,
});
