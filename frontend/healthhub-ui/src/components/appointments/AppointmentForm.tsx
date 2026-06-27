import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { FormInput } from "@/components/forms/FormInput";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import type { AppointmentRequest, Doctor, Patient } from "@/types";

export function AppointmentForm({
  initial,
  doctors,
  patients,
  loading,
  onSubmit,
  onCancel,
}: {
  initial?: AppointmentRequest;
  doctors: Doctor[];
  patients: Patient[];
  loading?: boolean;
  onSubmit: (data: AppointmentRequest) => void;
  onCancel: () => void;
}) {
  const [form, setForm] = useState<AppointmentRequest>(
    initial ?? {
      patientId: "",
      doctorId: "",
      appointmentDate: "",
      appointmentTime: "",
      reasonForVisit: "",
    },
  );

  useEffect(() => {
    if (initial) {
      setForm(initial);
    }
  }, [initial]);

  const set = (key: keyof AppointmentRequest, value: string) =>
    setForm((prev) => ({ ...prev, [key]: value }));

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(form);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <div className="grid gap-4 sm:grid-cols-2">
        <div className="space-y-1.5">
          <Label>Patient</Label>
          <Select
            value={String(form.patientId)}
            onValueChange={(value) => set("patientId", value)}
          >
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {patients.map((patient) => {
                const id = String(patient.id ?? patient.patientId ?? "");
                return (
                  <SelectItem key={id} value={id}>
                    {patient.fullName ?? `Patient ${id}`}
                  </SelectItem>
                );
              })}
            </SelectContent>
          </Select>
        </div>

        <div className="space-y-1.5">
          <Label>Doctor</Label>
          <Select
            value={String(form.doctorId)}
            onValueChange={(value) => set("doctorId", value)}
          >
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {doctors.map((doctor) => {
                const id = String(doctor.id ?? doctor.doctorId ?? "");
                return (
                  <SelectItem key={id} value={id}>
                    {doctor.fullName ?? `Doctor ${id}`}
                  </SelectItem>
                );
              })}
            </SelectContent>
          </Select>
        </div>

        <FormInput
          id="appointmentDate"
          label="Appointment Date"
          type="date"
          required
          value={form.appointmentDate}
          onChange={(e) => set("appointmentDate", e.target.value)}
        />
        <FormInput
          id="appointmentTime"
          label="Appointment Time"
          type="time"
          required
          value={form.appointmentTime}
          onChange={(e) => set("appointmentTime", e.target.value)}
        />
      </div>

      <div>
        <Label htmlFor="reasonForVisit">Reason for Visit</Label>
        <textarea
          id="reasonForVisit"
          value={form.reasonForVisit}
          onChange={(e) => set("reasonForVisit", e.target.value)}
          className="w-full rounded-lg border p-3 text-sm"
          rows={4}
        />
      </div>

      <div className="flex justify-end gap-2 pt-2">
        <Button type="button" variant="outline" onClick={onCancel} disabled={loading}>
          Cancel
        </Button>
        <Button type="submit" disabled={loading}>
          {loading ? "Saving..." : "Create Appointment"}
        </Button>
      </div>
    </form>
  );
}
