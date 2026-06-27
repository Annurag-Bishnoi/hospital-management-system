import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/patient/appointments")({
  component: () => <PlaceholderPage title="My Appointments" module="Appointment" />,
});
