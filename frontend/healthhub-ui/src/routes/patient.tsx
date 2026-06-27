import { createFileRoute } from "@tanstack/react-router";
import { AppLayout } from "@/components/layout/AppLayout";

export const Route = createFileRoute("/patient")({
  component: () => <AppLayout role="PATIENT" />,
});
