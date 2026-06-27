import { useEffect, useState } from "react";
import { Eye, Pencil, RefreshCw, Plus } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { DataTable, type Column } from "@/components/tables/DataTable";
import { StatusBadge } from "@/components/common/StatusBadge";
import { DoctorForm } from "./DoctorForm";
import {
  getDoctors,
  registerDoctor,
  updateDoctor,
} from "@/services/doctorService";
import type { Doctor, DoctorRequest } from "@/types";

function getDoctorId(d: Doctor | null | undefined): string | number | undefined {
  return d?.id ?? d?.doctorId ?? d?.userId ?? (d as any)?._id;
}

function DetailRow({ label, value }: { label: string; value?: string }) {
  return (
    <div className="flex justify-between gap-4 border-b py-2 text-sm last:border-0">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium text-right">{value || "—"}</span>
    </div>
  );
}

export function DoctorsManager() {
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [viewing, setViewing] = useState<Doctor | null>(null);
  const [editing, setEditing] = useState<Doctor | null>(null);
  const [creating, setCreating] = useState(false);
  const [saving, setSaving] = useState(false);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      setDoctors(await getDoctors());
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load doctors.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const handleCreate = async (data: DoctorRequest) => {
    setSaving(true);
    try {
      await registerDoctor(data);
      toast.success("Doctor created successfully.");
      setCreating(false);
      load();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Create failed.");
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = async (data: DoctorRequest) => {
    const id = getDoctorId(editing);
    if (!id) return;
    setSaving(true);
    try {
      const { username, password, ...rest } = data;
      void username;
      void password;
      await updateDoctor(id, rest);
      toast.success("Doctor updated successfully.");
      setEditing(null);
      load();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Update failed.");
    } finally {
      setSaving(false);
    }
  };

  const columns: Column<Doctor>[] = [
    {
      key: "doctorId",
      header: "ID",
      className: "w-16",
      render: (d) => <span className="text-muted-foreground">#{String(getDoctorId(d) ?? "—")}</span>,
    },
    { key: "fullName", header: "Name" },
    { key: "department", header: "Department" },
    { key: "doctorCode", header: "Doctor Code" },
    { key: "phone", header: "Phone" },
    { key: "email", header: "Email" },
    { key: "experience", header: "Experience" },
    {
      key: "active",
      header: "Status",
      render: (d) => <StatusBadge active={d.active !== false} />,
    },
    {
      key: "actions",
      header: "Actions",
      className: "text-right",
      render: (d) => (
        <div className="flex justify-end gap-1">
          <Button variant="ghost" size="icon" onClick={() => setViewing(d)} title="View">
            <Eye className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setEditing(d)}
            title="Edit"
          >
            <Pencil className="h-4 w-4" />
          </Button>
        </div>
      ),
    },
  ];

  return (
    <>
      <DataTable
        columns={columns}
        data={doctors}
        loading={loading}
        error={error}
        searchPlaceholder="Search by name, phone, email..."
        searchKeys={["fullName", "phone", "email", "department", "doctorCode"]}
        emptyTitle="No doctors found"
        emptyDescription="Register a doctor to get started."
        rowKey={(d) => String(getDoctorId(d) ?? "")}
        toolbar={
          <div className="flex gap-2 flex-wrap">
            <Button variant="outline" onClick={load}>
              <RefreshCw className="mr-2 h-4 w-4" />
              Refresh
            </Button>
            <Button onClick={() => setCreating(true)}>
              <Plus className="mr-2 h-4 w-4" /> Add Doctor
            </Button>
          </div>
        }
      />

      <Dialog open={!!viewing} onOpenChange={(o) => !o && setViewing(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Doctor Details</DialogTitle>
          </DialogHeader>
          {viewing && (
            <div className="space-y-1">
              <DetailRow label="Full Name" value={viewing.fullName} />
              <DetailRow label="Doctor Code" value={viewing.doctorCode} />
              <DetailRow label="Department" value={viewing.department} />
              <DetailRow label="Phone" value={viewing.phone} />
              <DetailRow label="Email" value={viewing.email} />
              <DetailRow label="Experience" value={String(viewing.experience ?? "—")} />
              <DetailRow label="Username" value={viewing.username} />
              <DetailRow label="Active" value={viewing.active !== false ? "Yes" : "No"} />
            </div>
          )}
        </DialogContent>
      </Dialog>

      <Dialog open={creating} onOpenChange={setCreating}>
        <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-2xl">
          <DialogHeader>
            <DialogTitle>Add New Doctor</DialogTitle>
          </DialogHeader>
          <DoctorForm
            mode="create"
            loading={saving}
            onSubmit={handleCreate}
            onCancel={() => setCreating(false)}
          />
        </DialogContent>
      </Dialog>

      <Dialog open={!!editing} onOpenChange={(o) => !o && setEditing(null)}>
        <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-2xl">
          <DialogHeader>
            <DialogTitle>Edit Doctor</DialogTitle>
          </DialogHeader>
          {editing && (
            <DoctorForm
              mode="edit"
              initial={editing}
              loading={saving}
              onSubmit={handleEdit}
              onCancel={() => setEditing(null)}
            />
          )}
        </DialogContent>
      </Dialog>

    </>
  );
}
