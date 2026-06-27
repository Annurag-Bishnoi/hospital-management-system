import { useEffect, useState } from "react";
import {
  Eye,
  Pencil,
  Power,
  Lock,
  Unlock,
  KeyRound,
  ShieldCheck,
  RefreshCw,
  Plus,
} from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { FormInput } from "@/components/forms/FormInput";
import { Label } from "@/components/ui/label";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { DataTable, type Column } from "@/components/tables/DataTable";
import { StatusBadge } from "@/components/common/StatusBadge";
import { ConfirmDialog } from "@/components/common/ConfirmDialog";
import {
  changeUserRole,
  createUser,
  getRoles,
  getUsers,
  resetUserPassword,
  setUserLock,
  setUserStatus,
  updateUser,
} from "@/services/userService";
import type { AppUser, CreateUserRequest, RoleItem } from "@/types";

function roleCodeOf(r: RoleItem): string {
  return String(r.roleCode ?? r.code ?? r.roleName ?? r.name ?? "");
}
function roleNameOf(r: RoleItem): string {
  return String(r.roleName ?? r.name ?? r.roleCode ?? r.code ?? "");
}
function userRoleOf(u: AppUser): string {
  return String(u.roleCode ?? u.role ?? u.roleName ?? "—");
}
function getUserId(u: AppUser | null | undefined): string | number | undefined {
  return u?.id ?? u?.userId ?? (u as any)?._id;
}

function DetailRow({ label, value }: { label: string; value?: string }) {
  return (
    <div className="flex justify-between gap-4 border-b py-2 text-sm last:border-0">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium text-right">{value || "—"}</span>
    </div>
  );
}

export function UsersManager() {
  const [users, setUsers] = useState<AppUser[]>([]);
  const [roles, setRoles] = useState<RoleItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const [creating, setCreating] = useState(false);
  const [createForm, setCreateForm] = useState<CreateUserRequest>({
    username: "",
    password: "",
    fullName: "",
    email: "",
    phone: "",
    roleCode: "",
  });

  const [viewing, setViewing] = useState<AppUser | null>(null);
  const [editing, setEditing] = useState<AppUser | null>(null);
  const [editForm, setEditForm] = useState({ fullName: "", email: "", phone: "" });
  const [roleTarget, setRoleTarget] = useState<AppUser | null>(null);
  const [newRole, setNewRole] = useState("");
  const [pwdTarget, setPwdTarget] = useState<AppUser | null>(null);
  const [newPwd, setNewPwd] = useState("");
  const [statusTarget, setStatusTarget] = useState<AppUser | null>(null);
  const [lockTarget, setLockTarget] = useState<AppUser | null>(null);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const [u, r] = await Promise.all([getUsers(), getRoles().catch(() => [])]);
      setUsers(u);
      setRoles(r);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load users.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!createForm.roleCode) {
      toast.error("Please select a role.");
      return;
    }
    setBusy(true);
    try {
      await createUser(createForm);
      toast.success("User created successfully.");
      setCreating(false);
      setCreateForm({ username: "", password: "", fullName: "", email: "", phone: "", roleCode: "" });
      load();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Create failed.");
    } finally {
      setBusy(false);
    }
  };

  const handleEdit = async (e: React.FormEvent) => {
    e.preventDefault();
    const id = getUserId(editing);
    if (!id) return;
    setBusy(true);
    try {
      await updateUser(id, editForm);
      toast.success("User updated.");
      setEditing(null);
      load();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Update failed.");
    } finally {
      setBusy(false);
    }
  };

  const handleRole = async () => {
    const id = getUserId(roleTarget);
    if (!id || !newRole) return;
    setBusy(true);
    try {
      await changeUserRole(id, newRole);
      toast.success("Role updated.");
      setRoleTarget(null);
      load();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Role change failed.");
    } finally {
      setBusy(false);
    }
  };

  const handlePwd = async () => {
    const id = getUserId(pwdTarget);
    if (!id || !newPwd) {
      toast.error("Enter a new password.");
      return;
    }
    setBusy(true);
    try {
      await resetUserPassword(id, newPwd);
      toast.success("Password reset.");
      setPwdTarget(null);
      setNewPwd("");
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Reset failed.");
    } finally {
      setBusy(false);
    }
  };

  const handleStatus = async () => {
    const id = getUserId(statusTarget);
    const isActive = statusTarget?.active;
    if (!id) return;
    setBusy(true);
    try {
      await setUserStatus(id, !isActive);
      toast.success(`User ${isActive ? "deactivated" : "activated"}.`);
      setStatusTarget(null);
      load();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Status update failed.");
    } finally {
      setBusy(false);
    }
  };

  const handleLock = async () => {
    const id = getUserId(lockTarget);
    const isLocked = lockTarget?.accountLocked;
    if (!id) return;
    setBusy(true);
    try {
      await setUserLock(id, !isLocked);
      toast.success(`User ${isLocked ? "unlocked" : "locked"}.`);
      setLockTarget(null);
      load();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Lock update failed.");
    } finally {
      setBusy(false);
    }
  };

  const columns: Column<AppUser>[] = [
    {
      key: "id",
      header: "ID",
      className: "w-14",
      render: (u) => <span className="text-muted-foreground">#{String(getUserId(u) ?? "—")}</span>,
    },
    { key: "fullName", header: "Name" },
    { key: "username", header: "Username" },
    { key: "email", header: "Email" },
    {
      key: "role",
      header: "Role",
      render: (u) => (
        <span className="rounded bg-accent px-2 py-0.5 text-xs font-medium text-accent-foreground">
          {userRoleOf(u)}
        </span>
      ),
    },
    {
      key: "active",
      header: "Status",
      render: (u) => (
        <div className="flex gap-1">
          <StatusBadge active={u.active !== false} />
          {u.accountLocked && <StatusBadge tone="warning" label="Locked" />}
        </div>
      ),
    },
    {
      key: "actions",
      header: "Actions",
      className: "text-right",
      render: (u) => (
        <div className="flex justify-end gap-1">
          <Button type="button" variant="ghost" size="icon" onClick={() => setViewing(u)} title="View details">
            <Eye className="h-4 w-4" />
          </Button>
          <Button
            type="button"
            variant="ghost"
            size="icon"
            onClick={() => {
              setEditing(u);
              setEditForm({
                fullName: u.fullName ?? "",
                email: u.email ?? "",
                phone: u.phone ?? "",
              });
            }}
            title="Edit user"
          >
            <Pencil className="h-4 w-4" />
          </Button>
          <Button
            type="button"
            variant="ghost"
            size="icon"
            onClick={() => {
              setRoleTarget(u);
              setNewRole(userRoleOf(u));
            }}
            title="Change role"
          >
            <ShieldCheck className="h-4 w-4" />
          </Button>
          <Button
            type="button"
            variant="ghost"
            size="icon"
            onClick={() => setPwdTarget(u)}
            title="Reset password"
          >
            <KeyRound className="h-4 w-4" />
          </Button>
          <Button
            type="button"
            variant="ghost"
            size="icon"
            onClick={() => setStatusTarget(u)}
            title={u.active !== false ? "Deactivate user" : "Activate user"}
          >
            <Power className={u.active !== false ? "h-4 w-4 text-emerald-600" : "h-4 w-4 text-red-600"} />
          </Button>
          <Button
            type="button"
            variant="ghost"
            size="icon"
            onClick={() => setLockTarget(u)}
            title={u.accountLocked ? "Unlock user" : "Lock user"}
          >
            {u.accountLocked ? (
              <Unlock className="h-4 w-4" />
            ) : (
              <Lock className="h-4 w-4" />
            )}
          </Button>
        </div>
      ),
    },
  ];

  return (
    <>
      <DataTable
        columns={columns}
        data={users}
        loading={loading}
        error={error}
        searchPlaceholder="Search users..."
        searchKeys={["fullName", "username", "email"]}
        emptyTitle="No users found"
        rowKey={(u, i) => String(getUserId(u) ?? i)}
        toolbar={
          <div className="flex gap-2">
            <Button variant="outline" onClick={load}>
              <RefreshCw className="mr-2 h-4 w-4" /> Refresh
            </Button>
            <Button onClick={() => setCreating(true)}>
              <Plus className="mr-2 h-4 w-4" /> Add User
            </Button>
          </div>
        }
      />

      {/* Create */}
      <Dialog open={creating} onOpenChange={setCreating}>
        <DialogContent className="sm:max-w-lg">
          <DialogHeader>
            <DialogTitle>Add New User</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleCreate} className="space-y-4">
            <div className="grid gap-4 sm:grid-cols-2">
              <FormInput
                id="c-username"
                label="Username"
                required
                value={createForm.username}
                onChange={(e) => setCreateForm({ ...createForm, username: e.target.value })}
              />
              <FormInput
                id="c-password"
                label="Password"
                type="password"
                required
                value={createForm.password}
                onChange={(e) => setCreateForm({ ...createForm, password: e.target.value })}
              />
            </div>
            <FormInput
              id="c-fullName"
              label="Full Name"
              required
              value={createForm.fullName}
              onChange={(e) => setCreateForm({ ...createForm, fullName: e.target.value })}
            />
            <div className="grid gap-4 sm:grid-cols-2">
              <FormInput
                id="c-email"
                label="Email"
                type="email"
                value={createForm.email}
                onChange={(e) => setCreateForm({ ...createForm, email: e.target.value })}
              />
              <FormInput
                id="c-phone"
                label="Phone"
                value={createForm.phone}
                onChange={(e) => setCreateForm({ ...createForm, phone: e.target.value })}
              />
            </div>
            <div className="space-y-1.5">
              <Label>Role</Label>
              <Select
                value={createForm.roleCode}
                onValueChange={(v) => setCreateForm({ ...createForm, roleCode: v })}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Select a role" />
                </SelectTrigger>
                <SelectContent>
                  {roles.map((r) => (
                    <SelectItem key={roleCodeOf(r)} value={roleCodeOf(r)}>
                      {roleNameOf(r)}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => setCreating(false)}>
                Cancel
              </Button>
              <Button type="submit" disabled={busy}>
                {busy ? "Creating..." : "Create User"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* View */}
      <Dialog open={!!viewing} onOpenChange={(o) => !o && setViewing(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>User Details</DialogTitle>
          </DialogHeader>
          {viewing && (
            <div className="space-y-1">
              <DetailRow label="ID" value={String(viewing.id ?? "")} />
              <DetailRow label="Full Name" value={viewing.fullName} />
              <DetailRow label="Username" value={viewing.username} />
              <DetailRow label="Email" value={viewing.email} />
              <DetailRow label="Phone" value={viewing.phone} />
              <DetailRow label="Role" value={userRoleOf(viewing)} />
              <DetailRow label="Active" value={viewing.active !== false ? "Yes" : "No"} />
              <DetailRow label="Locked" value={viewing.accountLocked ? "Yes" : "No"} />
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Edit */}
      <Dialog open={!!editing} onOpenChange={(o) => !o && setEditing(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Edit User</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleEdit} className="space-y-4">
            <FormInput
              id="e-fullName"
              label="Full Name"
              required
              value={editForm.fullName}
              onChange={(e) => setEditForm({ ...editForm, fullName: e.target.value })}
            />
            <FormInput
              id="e-email"
              label="Email"
              type="email"
              value={editForm.email}
              onChange={(e) => setEditForm({ ...editForm, email: e.target.value })}
            />
            <FormInput
              id="e-phone"
              label="Phone"
              value={editForm.phone}
              onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })}
            />
            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => setEditing(null)}>
                Cancel
              </Button>
              <Button type="submit" disabled={busy}>
                {busy ? "Saving..." : "Save Changes"}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      {/* Change role */}
      <Dialog open={!!roleTarget} onOpenChange={(o) => !o && setRoleTarget(null)}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Change Role</DialogTitle>
          </DialogHeader>
          <div className="space-y-1.5">
            <Label>Role for {roleTarget?.fullName}</Label>
            <Select value={newRole} onValueChange={setNewRole}>
              <SelectTrigger>
                <SelectValue placeholder="Select a role" />
              </SelectTrigger>
              <SelectContent>
                {roles.map((r) => (
                  <SelectItem key={roleCodeOf(r)} value={roleCodeOf(r)}>
                    {roleNameOf(r)}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setRoleTarget(null)}>
              Cancel
            </Button>
            <Button onClick={handleRole} disabled={busy}>
              {busy ? "Updating..." : "Update Role"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Reset password */}
      <Dialog open={!!pwdTarget} onOpenChange={(o) => !o && setPwdTarget(null)}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Reset Password</DialogTitle>
          </DialogHeader>
          <FormInput
            id="reset-pwd"
            label={`New password for ${pwdTarget?.fullName ?? ""}`}
            type="password"
            value={newPwd}
            onChange={(e) => setNewPwd(e.target.value)}
          />
          <DialogFooter>
            <Button variant="outline" onClick={() => setPwdTarget(null)}>
              Cancel
            </Button>
            <Button onClick={handlePwd} disabled={busy}>
              {busy ? "Resetting..." : "Reset Password"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <ConfirmDialog
        open={!!statusTarget}
        onOpenChange={(o) => !o && setStatusTarget(null)}
        title={statusTarget?.active !== false ? "Deactivate user?" : "Activate user?"}
        description={`This will ${statusTarget?.active !== false ? "deactivate" : "activate"} ${statusTarget?.fullName}.`}
        confirmLabel={statusTarget?.active !== false ? "Deactivate" : "Activate"}
        destructive={statusTarget?.active !== false}
        loading={busy}
        onConfirm={handleStatus}
      />
      <ConfirmDialog
        open={!!lockTarget}
        onOpenChange={(o) => !o && setLockTarget(null)}
        title={lockTarget?.accountLocked ? "Unlock account?" : "Lock account?"}
        description={`This will ${lockTarget?.accountLocked ? "unlock" : "lock"} ${lockTarget?.fullName}.`}
        confirmLabel={lockTarget?.accountLocked ? "Unlock" : "Lock"}
        destructive={!lockTarget?.accountLocked}
        loading={busy}
        onConfirm={handleLock}
      />
    </>
  );
}

export default UsersManager;
