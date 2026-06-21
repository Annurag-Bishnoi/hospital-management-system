import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Plus } from "lucide-react";
import { DataTable, PageHeader } from "@/components/DataTable";
import { Button, Field, Input, Select, Textarea } from "@/components/UI";
import { Modal } from "@/components/Modal";
import { prescriptions as seed, patients, type Prescription } from "@/lib/mockData";

export const Route = createFileRoute("/_app/prescriptions")({ component: PrescriptionsPage });

function PrescriptionsPage() {
  const [rows, setRows] = useState<Prescription[]>(seed);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({
    patientName: patients[0]?.name ?? "",
    diagnosis: "", medicines: "", dosage: "", instructions: "", followUp: "",
  });
  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    const id = `PR${String(rows.length + 1).padStart(3, "0")}`;
    setRows((r) => [...r, { id, ...form }]);
    setOpen(false);
  };
  return (
    <div>
      <PageHeader
        title="Prescriptions"
        description="Write and review patient prescriptions"
        action={<Button onClick={() => setOpen(true)}><Plus className="size-4" /> New Prescription</Button>}
      />
      <DataTable<Prescription>
        rows={rows}
        columns={[
          { key: "id", label: "ID" },
          { key: "patientName", label: "Patient" },
          { key: "diagnosis", label: "Diagnosis" },
          { key: "medicines", label: "Medicines" },
          { key: "dosage", label: "Dosage" },
          { key: "followUp", label: "Follow-up" },
        ]}
      />
      <Modal open={open} onClose={() => setOpen(false)} title="New Prescription" size="lg">
        <form onSubmit={submit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Field label="Patient">
            <Select value={form.patientName} onChange={(e) => setForm({ ...form, patientName: e.target.value })}>
              {patients.map((p) => <option key={p.id}>{p.name}</option>)}
            </Select>
          </Field>
          <Field label="Diagnosis"><Input value={form.diagnosis} onChange={(e) => setForm({ ...form, diagnosis: e.target.value })} required /></Field>
          <Field label="Medicines"><Input value={form.medicines} onChange={(e) => setForm({ ...form, medicines: e.target.value })} required /></Field>
          <Field label="Dosage"><Input placeholder="1-0-1" value={form.dosage} onChange={(e) => setForm({ ...form, dosage: e.target.value })} required /></Field>
          <Field label="Follow-up Date"><Input type="date" value={form.followUp} onChange={(e) => setForm({ ...form, followUp: e.target.value })} /></Field>
          <div className="md:col-span-2">
            <Field label="Instructions"><Textarea value={form.instructions} onChange={(e) => setForm({ ...form, instructions: e.target.value })} /></Field>
          </div>
          <div className="md:col-span-2 flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancel</Button>
            <Button type="submit">Save</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
