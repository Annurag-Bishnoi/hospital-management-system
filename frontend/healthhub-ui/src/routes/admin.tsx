import { createFileRoute } from "@tanstack/react-router";
import { AppLayout } from "@/components/layout/AppLayout";

export const Route = createFileRoute("/admin")({
  component: () => <AppLayout role="ADMIN" />,
});
