import { createFileRoute } from "@tanstack/react-router";
import { PageHeader } from "@/components/common/PageHeader";
import { UsersManager } from "@/components/users/UsersManager";

export const Route = createFileRoute("/admin/users")({
  component: () => (
    <div>
      <PageHeader
        title="User Management"
        description="Create, edit and manage system users and their access."
      />
      <UsersManager />
    </div>
  ),
});
