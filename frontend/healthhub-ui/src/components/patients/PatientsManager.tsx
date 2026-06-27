import { useEffect, useState } from "react";
import { Eye, Pencil, Power, RefreshCw, Plus } from "lucide-react";
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
import { ConfirmDialog } from "@/components/common/ConfirmDialog";
import { PatientForm } from "./PatientForm";
import {
  getPatients,
  getPatientById,
  registerPatient,
  setPatientStatus,
  updatePatient,
} from "@/services/patientService";
import type { Patient, PatientRequest } from "@/types";

function getPatientId(p: Patient | null | undefined): string | number | undefined {
  return p?.id ?? p?.patientId ?? (p as any)?._id;
}

function DetailRow({ label, value }: { label: string; value?: string }) {
  return (
    <div className="flex justify-between gap-4 border-b py-2 text-sm last:border-0">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium text-right">{value || "—"}</span>
    </div>
  );
}

export function PatientsManager() {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [viewing, setViewing] = useState<Patient | null>(null);
  const [editing, setEditing] = useState<Patient | null>(null);
  const [creating, setCreating] = useState(false);
  const [saving, setSaving] = useState(false);
  const [loadingEdit, setLoadingEdit] = useState(false);
  const [statusTarget, setStatusTarget] = useState<Patient | null>(null);
  const [statusLoading, setStatusLoading] = useState(false);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      setPatients(await getPatients());
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load patients.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const handleCreate = async (data: PatientRequest) => {
    setSaving(true);
    try {
      await registerPatient(data);
      toast.success("Patient created successfully.");
      setCreating(false);
      setSaving(false);
      load();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Create failed.");
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = async (data: PatientRequest) => {
    const id = getPatientId(editing);
    if (!id) return;
    setSaving(true);
    try {
      const { username, password, ...rest } = data;
      void username;
      void password;
      await updatePatient(id, rest);
      toast.success("Patient updated successfully.");
      setEditing(null);
      load();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Update failed.");
    } finally {
      setSaving(false);
    }
  };

  const handleEditClick = async (patient: Patient) => {
    const id = getPatientId(patient);
    if (!id) return;
    setLoadingEdit(true);
    try {
      const fullPatient = await getPatientById(id);
      setEditing(fullPatient);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Could not load patient details.");
    } finally {
      setLoadingEdit(false);
    }
  };

  const handleStatus = async () => {
    const id = getPatientId(statusTarget);
    const active = statusTarget?.active;
    if (!id) return;
    setStatusLoading(true);
    try {
      await setPatientStatus(id, !active);
      toast.success(`Patient ${active ? "deactivated" : "activated"}.`);
      setStatusTarget(null);
      load();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Status update failed.");
    } finally {
      setStatusLoading(false);
    }
  };

  const columns: Column<Patient>[] = [
    {
      key: "id",
      header: "ID",
      className: "w-16",
      render: (p) => <span className="text-muted-foreground">#{String(getPatientId(p) ?? "—")}</span>,
    },
    { key: "fullName", header: "Name" },
    { key: "phone", header: "Phone" },
    {
      key: "gender",
      header: "Gender",
      render: (p) => <span className="capitalize">{String(p.gender ?? "—").toLowerCase()}</span>,
    },
    { key: "bloodGroup", header: "Blood" },
    {
      key: "active",
      header: "Status",
      render: (p) => <StatusBadge active={p.active !== false} />,
    },
    {
      key: "actions",
      header: "Actions",
      className: "text-right",
      render: (p) => (
        <div className="flex justify-end gap-1">
          <Button variant="ghost" size="icon" onClick={() => setViewing(p)} title="View">
            <Eye className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            onClick={() => handleEditClick(p)}
            title="Edit"
            disabled={loadingEdit}
          >
            <Pencil className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setStatusTarget(p)}
            title={p.active !== false ? "Deactivate" : "Activate"}
          >
            <Power
              className={p.active !== false ? "h-4 w-4 text-emerald-600" : "h-4 w-4 text-red-600"}
            />
          </Button>
        </div>
      ),
    },
  ];

  return (
    <>
      <DataTable
        columns={columns}
        data={patients}
        loading={loading}
        error={error}
        searchPlaceholder="Search by name, phone..."
        searchKeys={["fullName", "phone", "email"]}
        emptyTitle="No patients found"
        emptyDescription="Register a patient to get started."
        rowKey={(p) => String(getPatientId(p) ?? "")}
        toolbar={
          <div className="flex gap-2 flex-wrap">
            <Button variant="outline" onClick={load}>
              <RefreshCw className="mr-2 h-4 w-4" />
              Refresh
            </Button>
            <Button onClick={() => setCreating(true)}>
              <Plus className="mr-2 h-4 w-4" /> Add Patient
            </Button>
          </div>
        }
      />

      {/* View */}
      <Dialog open={!!viewing} onOpenChange={(o) => !o && setViewing(null)}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Patient Details</DialogTitle>
          </DialogHeader>
          {viewing && (
            <div className="space-y-1">
              <DetailRow label="Full Name" value={viewing.fullName} />
              <DetailRow label="Username" value={viewing.username} />
              <DetailRow label="Date of Birth" value={viewing.dateOfBirth} />
              <DetailRow label="Gender" value={viewing.gender} />
              <DetailRow label="Blood Group" value={viewing.bloodGroup} />
              <DetailRow label="Phone" value={viewing.phone} />
              <DetailRow label="Email" value={viewing.email} />
              <DetailRow label="Address" value={viewing.address} />
              <DetailRow label="Emergency Contact" value={viewing.emergencyContactName} />
              <DetailRow label="Emergency Phone" value={viewing.emergencyContactPhone} />
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Create */}
      <Dialog open={creating} onOpenChange={setCreating}>
        <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-2xl">
          <DialogHeader>
            <DialogTitle>Add New Patient</DialogTitle>
          </DialogHeader>
          <PatientForm
            mode="create"
            loading={saving}
            onSubmit={handleCreate}
            onCancel={() => setCreating(false)}
          />
        </DialogContent>
      </Dialog>

      {/* Edit */}
      <Dialog open={!!editing} onOpenChange={(o) => !o && setEditing(null)}>
        <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-2xl">
          <DialogHeader>
            <DialogTitle>Edit Patient</DialogTitle>
          </DialogHeader>
          {editing && (
            <PatientForm
              mode="edit"
              initial={editing}
              loading={saving}
              onSubmit={handleEdit}
              onCancel={() => setEditing(null)}
            />
          )}
        </DialogContent>
      </Dialog>

      <ConfirmDialog
        open={!!statusTarget}
        onOpenChange={(o) => !o && setStatusTarget(null)}
        title={
          statusTarget?.active !== false ? "Deactivate patient?" : "Activate patient?"
        }
        description={`This will ${statusTarget?.active !== false ? "deactivate" : "activate"} ${statusTarget?.fullName}.`}
        confirmLabel={statusTarget?.active !== false ? "Deactivate" : "Activate"}
        destructive={statusTarget?.active !== false}
        loading={statusLoading}
        onConfirm={handleStatus}
      />
    </>
  );
}

export default PatientsManager;
