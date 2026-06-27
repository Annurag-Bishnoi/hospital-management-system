import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/patient/bills")({
  component: () => <PlaceholderPage title="My Bills" module="Billing" />,
});
