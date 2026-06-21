import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Plus } from "lucide-react";
import { DataTable, PageHeader } from "@/components/DataTable";
import { Badge } from "@/components/Badge";
import { Button, Field, Input, Select } from "@/components/UI";
import { Modal } from "@/components/Modal";
import { medicines as seed, stockStatus, type Medicine } from "@/lib/mockData";

export const Route = createFileRoute("/_app/pharmacy")({ component: PharmacyPage });

function PharmacyPage() {
  const [rows, setRows] = useState<Medicine[]>(seed);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({
    name: "", category: "Tablet", quantity: "0", price: "0", expiry: "", supplier: "",
  });

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    const id = `M${String(rows.length + 1).padStart(3, "0")}`;
    setRows((r) => [...r, {
      id, name: form.name, category: form.category,
      quantity: Number(form.quantity) || 0, price: Number(form.price) || 0,
      expiry: form.expiry, supplier: form.supplier,
    }]);
    setOpen(false);
  };

  return (
    <div>
      <PageHeader
        title="Pharmacy Inventory"
        description="Track medicines and stock levels"
        action={<Button onClick={() => setOpen(true)}><Plus className="size-4" /> Add Medicine</Button>}
      />
      <DataTable<Medicine>
        rows={rows}
        columns={[
          { key: "id", label: "ID" },
          { key: "name", label: "Medicine" },
          { key: "category", label: "Category" },
          { key: "quantity", label: "Qty" },
          { key: "price", label: "Price", render: (r) => `₹${r.price}` },
          { key: "expiry", label: "Expiry" },
          { key: "id", label: "Stock", render: (r) => <Badge>{stockStatus(r)}</Badge> },
        ]}
      />
      <Modal open={open} onClose={() => setOpen(false)} title="Add Medicine" size="lg">
        <form onSubmit={submit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Field label="Medicine Name"><Input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required /></Field>
          <Field label="Category">
            <Select value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })}>
              {["Tablet","Capsule","Syrup","Injection","Ointment","Other"].map((c) => <option key={c}>{c}</option>)}
            </Select>
          </Field>
          <Field label="Quantity"><Input type="number" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} required /></Field>
          <Field label="Price (₹)"><Input type="number" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} required /></Field>
          <Field label="Expiry Date"><Input type="date" value={form.expiry} onChange={(e) => setForm({ ...form, expiry: e.target.value })} required /></Field>
          <Field label="Supplier"><Input value={form.supplier} onChange={(e) => setForm({ ...form, supplier: e.target.value })} /></Field>
          <div className="md:col-span-2 flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>Cancel</Button>
            <Button type="submit">Add Medicine</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
