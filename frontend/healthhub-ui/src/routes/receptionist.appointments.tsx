import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/receptionist/appointments")({
  component: () => <PlaceholderPage title="Appointments" module="Appointment" />,
});
