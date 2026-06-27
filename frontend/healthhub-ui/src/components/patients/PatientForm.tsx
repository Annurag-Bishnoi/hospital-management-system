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
import { Textarea } from "@/components/ui/textarea";
import { Separator } from "@/components/ui/separator";
import type { Patient, PatientRequest } from "@/types";

const GENDERS = ["MALE", "FEMALE", "OTHER"];
const BLOOD_GROUPS = ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"];

const empty: PatientRequest = {
  username: "",
  password: "",
  fullName: "",
  dateOfBirth: "",
  gender: "MALE",
  bloodGroup: "B+",
  phone: "",
  email: "",
  address: "",
  emergencyContactName: "",
  emergencyContactPhone: "",
};

function normalizeGender(value?: string): string {
  const raw = String(value ?? "MALE").trim().toUpperCase();
  if (raw === "M" || raw === "MALE") return "MALE";
  if (raw === "F" || raw === "FEMALE") return "FEMALE";
  if (raw === "O" || raw === "OTHER" || raw === "NONBINARY" || raw === "NON-BINARY") return "OTHER";
  return "MALE";
}

function normalizeBloodGroup(value?: string): string {
  const raw = String(value ?? "B+").trim().toUpperCase();
  const normalized = raw
    .replace(/\s+/g, "")
    .replace("POSITIVE", "+")
    .replace("NEGATIVE", "-")
    .replace("PLUS", "+")
    .replace("MINUS", "-");

  if (BLOOD_GROUPS.includes(normalized)) return normalized;
  if (normalized === "AP") return "A+";
  if (normalized === "AM") return "A-";
  if (normalized === "BP") return "B+";
  if (normalized === "BM") return "B-";
  if (normalized === "ABP") return "AB+";
  if (normalized === "ABM") return "AB-";
  if (normalized === "OP") return "O+";
  if (normalized === "OM") return "O-";
  return "B+";
}

function getInitialGender(patient?: Patient): string {
  if (!patient) return empty.gender;
  return normalizeGender(
    patient.gender ?? (patient as any).sex ?? (patient as any).Gender ?? (patient as any).Sex,
  );
}

function getInitialBloodGroup(patient?: Patient): string {
  if (!patient) return empty.bloodGroup;
  return normalizeBloodGroup(
    patient.bloodGroup ?? (patient as any).blood_group ?? (patient as any).bloodgroup ?? (patient as any).BloodGroup,
  );
}

export function PatientForm({
  initial,
  mode,
  loading,
  onSubmit,
  onCancel,
}: {
  initial?: Patient;
  mode: "create" | "edit";
  loading?: boolean;
  onSubmit: (data: PatientRequest) => void;
  onCancel?: () => void;
}) {
  const [form, setForm] = useState<PatientRequest>(empty);

  useEffect(() => {
    if (initial) {
      setForm({
        ...empty,
        fullName: initial.fullName ?? "",
        dateOfBirth: initial.dateOfBirth ?? "",
        gender: getInitialGender(initial),
        bloodGroup: getInitialBloodGroup(initial),
        phone: initial.phone ?? "",
        email: initial.email ?? "",
        address: initial.address ?? "",
        emergencyContactName: initial.emergencyContactName ?? "",
        emergencyContactPhone: initial.emergencyContactPhone ?? "",
      });
    }
  }, [initial]);

  const set = (k: keyof PatientRequest, v: string) =>
    setForm((f) => ({ ...f, [k]: v }));

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(form);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      {mode === "create" && (
        <>
          <div>
            <p className="text-sm font-medium">Login Credentials (optional)</p>
            <p className="text-xs text-muted-foreground">
              Leave blank to let the system generate default credentials.
            </p>
          </div>
          <div className="grid gap-4 sm:grid-cols-2">
            <FormInput
              id="username"
              label="Username"
              value={form.username}
              onChange={(e) => set("username", e.target.value)}
              placeholder="auto-generated if empty"
            />
            <FormInput
              id="password"
              label="Password"
              type="password"
              value={form.password}
              onChange={(e) => set("password", e.target.value)}
              placeholder="auto-generated if empty"
            />
          </div>
          <Separator />
        </>
      )}

      <div className="grid gap-4 sm:grid-cols-2">
        <FormInput
          id="fullName"
          label="Full Name"
          required
          value={form.fullName}
          onChange={(e) => set("fullName", e.target.value)}
        />
        <FormInput
          id="dateOfBirth"
          label="Date of Birth"
          type="date"
          required
          value={form.dateOfBirth}
          onChange={(e) => set("dateOfBirth", e.target.value)}
        />
        <div className="space-y-1.5">
          <Label>Gender</Label>
          <Select value={form.gender} onValueChange={(v) => set("gender", v)}>
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {GENDERS.map((g) => (
                <SelectItem key={g} value={g}>
                  {g}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        <div className="space-y-1.5">
          <Label>Blood Group</Label>
          <Select
            value={form.bloodGroup}
            onValueChange={(v) => set("bloodGroup", v)}
          >
            <SelectTrigger>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              {BLOOD_GROUPS.map((b) => (
                <SelectItem key={b} value={b}>
                  {b}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
        <FormInput
          id="phone"
          label="Phone"
          required
          value={form.phone}
          onChange={(e) => set("phone", e.target.value)}
        />
        <FormInput
          id="email"
          label="Email"
          type="email"
          value={form.email}
          onChange={(e) => set("email", e.target.value)}
        />
      </div>

      <div className="space-y-1.5">
        <Label htmlFor="address">Address</Label>
        <Textarea
          id="address"
          value={form.address}
          onChange={(e) => set("address", e.target.value)}
          rows={2}
        />
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <FormInput
          id="emergencyContactName"
          label="Emergency Contact Name"
          value={form.emergencyContactName}
          onChange={(e) => set("emergencyContactName", e.target.value)}
        />
        <FormInput
          id="emergencyContactPhone"
          label="Emergency Contact Phone"
          value={form.emergencyContactPhone}
          onChange={(e) => set("emergencyContactPhone", e.target.value)}
        />
      </div>

      <div className="flex justify-end gap-2 pt-2">
        {onCancel && (
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancel
          </Button>
        )}
        <Button type="submit" disabled={loading}>
          {loading
            ? "Saving..."
            : mode === "create"
              ? "Register Patient"
              : "Save Changes"}
        </Button>
      </div>
    </form>
  );
}

export default PatientForm;
