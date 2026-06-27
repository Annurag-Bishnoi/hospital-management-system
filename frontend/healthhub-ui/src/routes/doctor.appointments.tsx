import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/doctor/appointments")({
  component: () => <PlaceholderPage title="Today Appointments" module="Appointment" />,
});
