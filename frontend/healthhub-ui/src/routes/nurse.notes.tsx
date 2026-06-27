import { createFileRoute } from "@tanstack/react-router";
import { PlaceholderPage } from "@/components/common/PlaceholderPage";

export const Route = createFileRoute("/nurse/notes")({
  component: () => <PlaceholderPage title="Notes" module="Nursing Notes" />,
});
