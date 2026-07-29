import { createFileRoute, redirect } from "@tanstack/react-router";
import { getUser } from "@/lib/auth";

export const Route = createFileRoute("/")({
  beforeLoad: () => {
    if (typeof window !== "undefined") {
      throw redirect({ to: getUser() ? "/dashboard" : "/login" });
    }
  },
  component: () => null,
});
