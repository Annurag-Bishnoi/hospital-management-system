import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Plus, Eye, Pencil, Trash2 } from "lucide-react";
import { DataTable, PageHeader } from "@/components/DataTable";
import { Badge } from "@/components/Badge";
import { Button, Field, Input, Select, Textarea } from "@/components/UI";
import { Modal } from "@/components/Modal";
import { patients as seed, type Patient } from "@/lib/mockData";

export const Route = createFileRoute("/_app/patients")({ component: PatientsPage });

const emptyForm = {
  name: "", age: "", gender: "Male", phone: "", address: "",
  bloodGroup: "O+", emergencyContact: "", symptoms: "",
};

function PatientsPage() {
  const [rows, setRows] = useState<Patient[]>(seed);
  const [open, setOpen] = useState(false);
  const [viewing, setViewing] = useState<Patient | null>(null);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [form, setForm] = useState(emptyForm);

  const openAdd = () => { setEditingId(null); setForm(emptyForm); setOpen(true); };
  const openEdit = (p: Patient) => {
    setEditingId(p.id);
    setForm({
      name: p.name, age: String(p.age), gender: p.gender, phone: p.phone,
      address: p.address ?? "", bloodGroup: p.bloodGroup,
      emergencyContact: p.emergencyContact ?? "", symptoms: p.symptoms ?? "",
    });
    setOpen(true);
  };
  const remove = (id: string) => {
    if (confirm("Delete this patient?")) setRows((r) => r.filter((x) => x.id !== id));
  };

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    const data = {
      ...form, age: Number(form.age) || 0,
    };
    if (editingId) {
      setRows((r) => r.map((p) => (p.id === editingId ? { ...p, ...data } : p)));
    } else {
      const id = `P${String(rows.length + 1).padStart(3, "0")}`;
      setRows((r) => [...r, { id, status: "Active", ...data }]);
    }
    setOpen(false);
  };

  return (
    <div>
      <PageHeader
        title="Patients"
        description="Manage hospital patient records"
        action={<Button onClick={openAdd}><Plus className="size-4" /> Add Patient</Button>}
      />
      <DataTable<Patient>
        rows={rows}
        columns={[
          { key: "id", label: "ID" },
          { key: "name", label: "Name" },
          { key: "age", label: "Age" },
          { key: "gender", label: "Gender" },
          { key: "phone", label: "Phone" },
          { key: "bloodGroup", label: "Blood" },
          { key: "status", label: "Status", render: (r) => <Badge>{r.status}</Badge> },
          {
            key: "actions", label: "Actions",
            render: (r) => (
              <div className="flex gap-1">
                <Button size="sm" variant="ghost" onClick={() => setViewing(r)}><Eye className="size-4" /></Button>
                <Button size="sm" variant="ghost" onClick={() => openEdit(r)}><Pencil className="size-4" /></Button>
                <Button size="sm" variant="ghost" onClick={() => remove(r.id)}><Trash2 className="size-4 text-destructive" /></Button>
              </div>
            ),
          },
        ]}
      />

      <Modal open={open} onClose={() => setOpen(false)} title={editingId ? "Edit Patient" : "Add Patient"} size="lg">
        <form onSubmit={submit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Field label="Full Name"><Input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required /></Field>
          <Field label="Age"><Input type="number" value={form.age} onChange={(e) => setForm({ ...form, age: e.target.value })} required /></Field>
          <Field label="Gender">
            <Select value={form.gender} onChange={(e) => setForm({ ...form, gender: e.target.value })}>
              <option>Male</option><option>Female</option><option>Other</option>
            </Select>
          </Field>
          <Field label="Phone"><Input value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} required /></Field>
          <Field label="Blood Group">
            <Select value={form.bloodGroup} onChange={(e) => setForm({ ...form, bloodGroup: e.target.value })}>
              {["O+","O-","A+","A-","B+","B-","AB+","AB-"].map((b) => <option key={b}>{b}</option>)}
            </Select>
          </Field>
          <Field label="Emergency Contact"><Input value={form.emergencyContact} onChange={(e) => setForm({ ...form, emergencyContact: e.target.value })} /></Field>
          <div className="md:col-span-2">
            <Field label="Address"><Input value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} /></Field>
          </div>
          <div className="md:col-span-2">
            <Field label="Symptoms / Reason for Visit"><Textarea value={form.symptoms} onChange={(e) => setForm({ ...form, symptoms: e.target.value })} /></Field>
          </div>
          <div className="md:col-span-2 flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancel</Button>
            <Button type="submit">{editingId ? "Save Changes" : "Add Patient"}</Button>
          </div>
        </form>
      </Modal>

      <Modal open={!!viewing} onClose={() => setViewing(null)} title="Patient Details">
        {viewing && (
          <div className="space-y-2 text-sm">
            {[
              ["ID", viewing.id], ["Name", viewing.name], ["Age", viewing.age],
              ["Gender", viewing.gender], ["Phone", viewing.phone], ["Blood Group", viewing.bloodGroup],
              ["Status", viewing.status], ["Address", viewing.address ?? "—"],
              ["Emergency", viewing.emergencyContact ?? "—"], ["Symptoms", viewing.symptoms ?? "—"],
            ].map(([k, v]) => (
              <div key={String(k)} className="flex justify-between gap-4 border-b border-border py-2">
                <span className="text-muted-foreground">{k}</span>
                <span className="font-medium text-right">{v}</span>
              </div>
            ))}
          </div>
        )}
      </Modal>
    </div>
  );
}
