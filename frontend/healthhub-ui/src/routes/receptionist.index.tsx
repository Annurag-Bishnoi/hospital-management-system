import { createFileRoute, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/receptionist/")({
  beforeLoad: () => {
    throw redirect({ to: "/receptionist/dashboard" });
  },
});
