import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/doctor/profile")({
  component: () => <PlaceholderPage title="Profile" module="Profile" />,
});
