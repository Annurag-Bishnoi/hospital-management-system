import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/receptionist/billing")({
  component: () => <PlaceholderPage title="Billing" module="Billing" />,
});
