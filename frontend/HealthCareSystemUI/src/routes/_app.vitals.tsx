import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Plus } from "lucide-react";
import { DataTable, PageHeader } from "@/components/DataTable";
import { Button, Field, Input, Select, Textarea } from "@/components/UI";
import { Modal } from "@/components/Modal";
import { vitals as seed, patients, type Vital } from "@/lib/mockData";

export const Route = createFileRoute("/_app/vitals")({ component: VitalsPage });

function VitalsPage() {
  const [rows, setRows] = useState<Vital[]>(seed);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({
    patientName: patients[0]?.name ?? "", temperature: "", bp: "", pulse: "", notes: "",
  });
  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    const id = `V${String(rows.length + 1).padStart(3, "0")}`;
    setRows((r) => [...r, { id, date: new Date().toISOString().slice(0, 10), ...form }]);
    setOpen(false);
  };
  return (
    <div>
      <PageHeader
        title="Patient Vitals"
        description="Record vital signs for assigned patients"
        action={<Button onClick={() => setOpen(true)}><Plus className="size-4" /> Record Vitals</Button>}
      />
      <DataTable<Vital>
        rows={rows}
        columns={[
          { key: "id", label: "ID" },
          { key: "patientName", label: "Patient" },
          { key: "temperature", label: "Temp (°F)" },
          { key: "bp", label: "BP" },
          { key: "pulse", label: "Pulse" },
          { key: "date", label: "Date" },
          { key: "notes", label: "Notes" },
        ]}
      />
      <Modal open={open} onClose={() => setOpen(false)} title="Record Vitals">
        <form onSubmit={submit} className="space-y-4">
          <Field label="Patient">
            <Select value={form.patientName} onChange={(e) => setForm({ ...form, patientName: e.target.value })}>
              {patients.map((p) => <option key={p.id}>{p.name}</option>)}
            </Select>
          </Field>
          <Field label="Temperature (°F)"><Input value={form.temperature} onChange={(e) => setForm({ ...form, temperature: e.target.value })} required /></Field>
          <Field label="Blood Pressure"><Input placeholder="120/80" value={form.bp} onChange={(e) => setForm({ ...form, bp: e.target.value })} required /></Field>
          <Field label="Pulse"><Input value={form.pulse} onChange={(e) => setForm({ ...form, pulse: e.target.value })} required /></Field>
          <Field label="Notes"><Textarea value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} /></Field>
          <div className="flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancel</Button>
            <Button type="submit">Save</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
