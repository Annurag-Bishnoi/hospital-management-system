import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Plus } from "lucide-react";
import { DataTable, PageHeader } from "@/components/DataTable";
import { Badge } from "@/components/Badge";
import { Button, Field, Input, Select, Textarea } from "@/components/UI";
import { Modal } from "@/components/Modal";
import { appointments as seed, doctors, type Appointment } from "@/lib/mockData";

export const Route = createFileRoute("/_app/my-appointments")({ component: MyAppointmentsPage });

function MyAppointmentsPage() {
  const [rows, setRows] = useState<Appointment[]>(seed.slice(0, 3));
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({
    doctorName: doctors[0]?.name ?? "",
    department: doctors[0]?.department ?? "",
    date: "", time: "", notes: "",
  });
  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    const id = `A${String(rows.length + 100).padStart(3, "0")}`;
    setRows((r) => [...r, { id, patientName: "You", status: "Scheduled", ...form }]);
    setOpen(false);
  };
  return (
    <div>
      <PageHeader
        title="My Appointments"
        description="View and book appointments"
        action={<Button onClick={() => setOpen(true)}><Plus className="size-4" /> Book Appointment</Button>}
      />
      <DataTable<Appointment>
        rows={rows}
        columns={[
          { key: "id", label: "ID" },
          { key: "doctorName", label: "Doctor" },
          { key: "department", label: "Department" },
          { key: "date", label: "Date" },
          { key: "time", label: "Time" },
          { key: "status", label: "Status", render: (r) => <Badge>{r.status}</Badge> },
        ]}
      />
      <Modal open={open} onClose={() => setOpen(false)} title="Book Appointment" size="lg">
        <form onSubmit={submit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Field label="Doctor">
            <Select
              value={form.doctorName}
              onChange={(e) => {
                const d = doctors.find((d) => d.name === e.target.value);
                setForm({ ...form, doctorName: e.target.value, department: d?.department ?? "" });
              }}
            >
              {doctors.map((d) => <option key={d.id}>{d.name}</option>)}
            </Select>
          </Field>
          <Field label="Department"><Input value={form.department} readOnly /></Field>
          <Field label="Date"><Input type="date" value={form.date} onChange={(e) => setForm({ ...form, date: e.target.value })} required /></Field>
          <Field label="Time"><Input type="time" value={form.time} onChange={(e) => setForm({ ...form, time: e.target.value })} required /></Field>
          <div className="md:col-span-2">
            <Field label="Notes"><Textarea value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} /></Field>
          </div>
          <div className="md:col-span-2 flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancel</Button>
            <Button type="submit">Book</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
