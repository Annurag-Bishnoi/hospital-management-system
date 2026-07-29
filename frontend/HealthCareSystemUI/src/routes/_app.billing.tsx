import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Plus, Eye, Printer } from "lucide-react";
import { DataTable, PageHeader } from "@/components/DataTable";
import { Badge } from "@/components/Badge";
import { Button, Field, Input, Select } from "@/components/UI";
import { Modal } from "@/components/Modal";
import { bills as seed, patients, billTotal, type Bill } from "@/lib/mockData";

export const Route = createFileRoute("/_app/billing")({ component: BillingPage });

function BillingPage() {
  const [rows, setRows] = useState<Bill[]>(seed);
  const [open, setOpen] = useState(false);
  const [viewing, setViewing] = useState<Bill | null>(null);
  const [form, setForm] = useState({
    patientName: patients[0]?.name ?? "",
    consultation: "500", medicine: "0", room: "0", other: "0",
    status: "Pending" as Bill["status"],
  });

  const total =
    (Number(form.consultation) || 0) +
    (Number(form.medicine) || 0) +
    (Number(form.room) || 0) +
    (Number(form.other) || 0);

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    const id = `BL${String(rows.length + 1).padStart(3, "0")}`;
    setRows((r) => [...r, {
      id,
      patientName: form.patientName,
      consultation: Number(form.consultation) || 0,
      medicine: Number(form.medicine) || 0,
      room: Number(form.room) || 0,
      other: Number(form.other) || 0,
      status: form.status,
    }]);
    setOpen(false);
  };

  return (
    <div>
      <PageHeader
        title="Billing"
        description="Hospital billing ledger"
        action={<Button onClick={() => setOpen(true)}><Plus className="size-4" /> New Bill</Button>}
      />
      <DataTable<Bill>
        rows={rows}
        columns={[
          { key: "id", label: "Bill ID" },
          { key: "patientName", label: "Patient" },
          { key: "consultation", label: "Consult", render: (r) => `₹${r.consultation}` },
          { key: "medicine", label: "Medicine", render: (r) => `₹${r.medicine}` },
          { key: "room", label: "Room", render: (r) => `₹${r.room}` },
          { key: "other", label: "Other", render: (r) => `₹${r.other}` },
          { key: "id", label: "Total", render: (r) => <span className="font-semibold">₹{billTotal(r)}</span> },
          { key: "status", label: "Status", render: (r) => <Badge>{r.status}</Badge> },
          {
            key: "actions", label: "",
            render: (r) => (
              <Button size="sm" variant="outline" onClick={() => setViewing(r)}>
                <Eye className="size-4" /> View
              </Button>
            ),
          },
        ]}
      />

      <Modal open={open} onClose={() => setOpen(false)} title="New Bill" size="lg">
        <form onSubmit={submit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="md:col-span-2">
            <Field label="Patient">
              <Select value={form.patientName} onChange={(e) => setForm({ ...form, patientName: e.target.value })}>
                {patients.map((p) => <option key={p.id}>{p.name}</option>)}
              </Select>
            </Field>
          </div>
          <Field label="Consultation Fee (₹)"><Input type="number" value={form.consultation} onChange={(e) => setForm({ ...form, consultation: e.target.value })} /></Field>
          <Field label="Medicine Charges (₹)"><Input type="number" value={form.medicine} onChange={(e) => setForm({ ...form, medicine: e.target.value })} /></Field>
          <Field label="Room Charges (₹)"><Input type="number" value={form.room} onChange={(e) => setForm({ ...form, room: e.target.value })} /></Field>
          <Field label="Other Charges (₹)"><Input type="number" value={form.other} onChange={(e) => setForm({ ...form, other: e.target.value })} /></Field>
          <Field label="Payment Status">
            <Select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value as Bill["status"] })}>
              <option>Pending</option><option>Paid</option>
            </Select>
          </Field>
          <div className="flex items-end">
            <div className="w-full bg-muted rounded-lg p-3">
              <div className="text-xs text-muted-foreground">Total</div>
              <div className="text-xl font-semibold">₹{total}</div>
            </div>
          </div>
          <div className="md:col-span-2 flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancel</Button>
            <Button type="submit">Save Bill</Button>
          </div>
        </form>
      </Modal>

      <Modal open={!!viewing} onClose={() => setViewing(null)} title="Invoice">
        {viewing && <Invoice bill={viewing} />}
      </Modal>
    </div>
  );
}

function Invoice({ bill }: { bill: Bill }) {
  const total = billTotal(bill);
  return (
    <div>
      <div id="invoice-print" className="bg-card">
        <div className="flex justify-between border-b border-border pb-4">
          <div>
            <div className="text-lg font-semibold">MediCare Hospital</div>
            <div className="text-xs text-muted-foreground">123 Health St, Pune, India</div>
          </div>
          <div className="text-right">
            <div className="text-sm font-medium">Invoice {bill.id}</div>
            <div className="text-xs text-muted-foreground">{new Date().toLocaleDateString()}</div>
          </div>
        </div>
        <div className="py-4">
          <div className="text-sm text-muted-foreground">Billed to</div>
          <div className="font-medium">{bill.patientName}</div>
        </div>
        <table className="w-full text-sm border-t border-border">
          <tbody>
            {[
              ["Consultation Fee", bill.consultation],
              ["Medicine Charges", bill.medicine],
              ["Room Charges", bill.room],
              ["Other Charges", bill.other],
            ].map(([label, val]) => (
              <tr key={String(label)} className="border-b border-border">
                <td className="py-2">{label}</td>
                <td className="py-2 text-right">₹{val}</td>
              </tr>
            ))}
            <tr>
              <td className="py-3 font-semibold">Total</td>
              <td className="py-3 text-right font-semibold">₹{total}</td>
            </tr>
          </tbody>
        </table>
        <div className="flex justify-between items-center mt-2">
          <span className="text-sm text-muted-foreground">Payment Status</span>
          <Badge>{bill.status}</Badge>
        </div>
      </div>
      <div className="mt-6 flex justify-end">
        <Button onClick={() => window.print()}><Printer className="size-4" /> Print</Button>
      </div>
    </div>
  );
}
