import { useEffect } from "react";
import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { getUser, homeForRole } from "@/utils/auth";
import { LoadingSpinner } from "@/components/common/LoadingSpinner";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "HMS — Hospital Management System" },
      {
        name: "description",
        content:
          "Hospital Management System for administrators, doctors, receptionists, nurses, pharmacists and patients.",
      },
    ],
  }),
  component: Index,
});

function Index() {
  const navigate = useNavigate();
  useEffect(() => {
    const user = getUser();
    navigate({
      to: user ? homeForRole(String(user.role)) : "/login",
      replace: true,
    });
  }, [navigate]);

  return (
    <div className="flex min-h-screen items-center justify-center bg-background">
      <LoadingSpinner label="Loading HMS..." />
    </div>
  );
}
