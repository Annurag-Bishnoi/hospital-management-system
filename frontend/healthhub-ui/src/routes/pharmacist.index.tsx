import { createFileRoute, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/pharmacist/")({
  beforeLoad: () => {
    throw redirect({ to: "/pharmacist/dashboard" });
  },
});
