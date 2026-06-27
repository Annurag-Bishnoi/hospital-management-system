import { createFileRoute } from "@tanstack/react-router";
import { AppointmentsManager } from "@/components/appointments/AppointmentsManager";

export const Route = createFileRoute("/admin/appointments")({
  component: AppointmentsManager,
});
