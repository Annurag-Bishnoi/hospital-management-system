import { createFileRoute } from "@tanstack/react-router";
import { AppLayout } from "@/components/layout/AppLayout";

export const Route = createFileRoute("/pharmacist")({
  component: () => <AppLayout role="PHARMACIST" />,
});
