import { useEffect, useState } from "react";
import { Plus, RefreshCw, Power, Info } from "lucide-react";
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
import { AppointmentForm } from "./AppointmentForm";
import {
  getAppointments,
  getAppointmentById,
  createAppointment,
  cancelAppointment,
} from "@/services/appointmentService";
import { getDoctors } from "@/services/doctorService";
import { getPatients } from "@/services/patientService";
import type { Appointment, AppointmentRequest, Doctor, Patient } from "@/types";

function getAppointmentId(a: Appointment | null | undefined): string | number | undefined {
  return a?.id ?? a?.appointmentId ?? (a as any)?._id;
}

export function AppointmentsManager() {
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [patients, setPatients] = useState<Patient[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [creating, setCreating] = useState(false);
  const [saving, setSaving] = useState(false);
  const [cancelTarget, setCancelTarget] = useState<Appointment | null>(null);
  const [cancelLoading, setCancelLoading] = useState(false);
  const [appointmentDetails, setAppointmentDetails] = useState<Appointment | null>(null);
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [detailsError, setDetailsError] = useState<string | null>(null);
  const [detailsOpen, setDetailsOpen] = useState(false);

  const loadData = async () => {
    setLoading(true);
    setError(null);

    const results = await Promise.allSettled([
      getAppointments(),
      getDoctors(),
      getPatients(),
    ]);

    const [appointmentsResult, doctorsResult, patientsResult] = results;

    if (appointmentsResult.status === "fulfilled") {
      setAppointments(appointmentsResult.value);
    }
    if (doctorsResult.status === "fulfilled") {
      setDoctors(doctorsResult.value);
    }
    if (patientsResult.status === "fulfilled") {
      setPatients(patientsResult.value);
    }

    const rejectedResult = results.find(
      (result): result is PromiseRejectedResult => result.status === "rejected",
    );
    if (rejectedResult) {
      setError(
        rejectedResult.reason instanceof Error
          ? rejectedResult.reason.message
          : "Failed to load appointments data.",
      );
    }

    setLoading(false);
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCreate = async (data: AppointmentRequest) => {
    setSaving(true);
    try {
      await createAppointment(data);
      toast.success("Appointment created successfully.");
      setCreating(false);
      loadData();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Create failed.");
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = async () => {
    if (!cancelTarget) return;
    const appointmentId = getAppointmentId(cancelTarget);
    if (!appointmentId) {
      toast.error("Unable to determine appointment ID.");
      return;
    }

    setCancelLoading(true);
    try {
      await cancelAppointment(appointmentId);
      toast.success("Appointment cancelled successfully.");
      setCancelTarget(null);
      loadData();
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Cancel failed.");
    } finally {
      setCancelLoading(false);
    }
  };

  const handleViewDetails = async (appointment: Appointment) => {
    const appointmentId = getAppointmentId(appointment);
    if (!appointmentId) {
      setDetailsError("Unable to determine appointment ID.");
      return;
    }

    setDetailsLoading(true);
    setDetailsError(null);
    setDetailsOpen(true);

    try {
      const details = await getAppointmentById(appointmentId);
      setAppointmentDetails(details);
    } catch (err) {
      setDetailsError(err instanceof Error ? err.message : "Failed to load appointment details.");
      setAppointmentDetails(null);
    } finally {
      setDetailsLoading(false);
    }
  };

  const columns: Column<Appointment>[] = [
    {
      key: "appointmentId",
      header: "ID",
      className: "w-16",
      render: (appointment) => (
        <span className="text-muted-foreground">#{String(getAppointmentId(appointment) ?? "—")}</span>
      ),
    },
    { key: "appointmentNumber", header: "Appointment #" },
    { key: "tokenNumber", header: "Token" },
    { key: "appointmentDate", header: "Date" },
    { key: "appointmentTime", header: "Time" },
    { key: "patientName", header: "Patient" },
    { key: "doctorName", header: "Doctor" },
    { key: "status", header: "Status", render: (appointment) => <StatusBadge active={appointment.status !== "CANCELLED"} label={appointment.status} /> },
    { key: "reasonForVisit", header: "Reason" },
    {
      key: "actions",
      header: "Actions",
      className: "text-right",
      render: (appointment) => (
        <div className="flex justify-end gap-1">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => handleViewDetails(appointment)}
            title="View details"
          >
            <Info className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setCancelTarget(appointment)}
            title="Cancel"
            disabled={appointment.status === "CANCELLED"}
          >
            <Power className="h-4 w-4" />
          </Button>
        </div>
      ),
    },
  ];

  return (
    <>
      <DataTable
        columns={columns}
        data={appointments}
        loading={loading}
        error={error}
        searchPlaceholder="Search appointments..."
        searchKeys={["appointmentNumber", "patientName", "doctorName", "status", "tokenNumber"]}
        emptyTitle="No appointments found"
        emptyDescription="Create an appointment to get started."
        rowKey={(appointment) => String(getAppointmentId(appointment) ?? "")}
        rowClassName={(appointment) =>
          appointment.status === "CANCELLED"
            ? "bg-red-50 text-red-700"
            : ""
        }
        toolbar={
          <div className="flex gap-2 flex-wrap">
            <Button variant="outline" onClick={loadData}>
              <RefreshCw className="mr-2 h-4 w-4" />
              Refresh
            </Button>
            <Button onClick={() => setCreating(true)}>
              <Plus className="mr-2 h-4 w-4" /> Add Appointment
            </Button>
          </div>
        }
      />

      <Dialog open={creating} onOpenChange={setCreating}>
        <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-2xl">
          <DialogHeader>
            <DialogTitle>Book Appointment</DialogTitle>
          </DialogHeader>
          <AppointmentForm
            doctors={doctors}
            patients={patients}
            loading={saving}
            onSubmit={handleCreate}
            onCancel={() => setCreating(false)}
          />
        </DialogContent>
      </Dialog>

      <Dialog
        open={detailsOpen}
        onOpenChange={(open) => {
          if (!open) {
            setAppointmentDetails(null);
          }
          setDetailsOpen(open);
        }}
      >
        <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-2xl">
          <DialogHeader>
            <DialogTitle>Appointment Details</DialogTitle>
          </DialogHeader>
          {detailsLoading ? (
            <div className="p-6 text-center">Loading appointment details...</div>
          ) : detailsError ? (
            <div className="p-6 text-center text-destructive">{detailsError}</div>
          ) : appointmentDetails ? (
            <div className="grid gap-4 text-sm">
              <div>
                <strong>Appointment #</strong>
                <p>{appointmentDetails.appointmentNumber}</p>
              </div>
              <div>
                <strong>Status</strong>
                <p>{appointmentDetails.status}</p>
              </div>
              <div>
                <strong>Patient</strong>
                <p>{appointmentDetails.patientName}</p>
              </div>
              <div>
                <strong>Doctor</strong>
                <p>{appointmentDetails.doctorName}</p>
              </div>
              <div>
                <strong>Date & Time</strong>
                <p>
                  {appointmentDetails.appointmentDate} at {appointmentDetails.appointmentTime}
                </p>
              </div>
              <div>
                <strong>Reason</strong>
                <p>{appointmentDetails.reasonForVisit}</p>
              </div>
            </div>
          ) : (
            <div className="p-6 text-center text-muted-foreground">No details available.</div>
          )}
        </DialogContent>
      </Dialog>

      <ConfirmDialog
        open={!!cancelTarget}
        onOpenChange={(open) => !open && setCancelTarget(null)}
        title="Cancel appointment?"
        description={`This will cancel appointment ${cancelTarget?.appointmentNumber ?? ""} for ${cancelTarget?.patientName ?? ""}.`}
        confirmLabel="Cancel Appointment"
        destructive
        loading={cancelLoading}
        onConfirm={handleCancel}
      />
    </>
  );
}

export default AppointmentsManager;
