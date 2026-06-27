import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/patient/profile")({
  component: () => <PlaceholderPage title="My Profile" module="Profile" />,
});
