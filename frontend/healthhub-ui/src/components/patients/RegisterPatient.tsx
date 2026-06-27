import { useState } from "react";
import { toast } from "sonner";
import { Card, CardContent } from "@/components/ui/card";
import { PatientForm } from "./PatientForm";
import { registerPatient } from "@/services/patientService";
import type { Patient, PatientRequest } from "@/types";

export function RegisterPatient() {
  const [loading, setLoading] = useState(false);
  const [created, setCreated] = useState<Patient | null>(null);

  const handleSubmit = async (data: PatientRequest) => {
    setLoading(true);
    setCreated(null);
    try {
      const res = await registerPatient(data);
      toast.success("Patient registered successfully.");
      setCreated(res ?? null);
    } catch (err) {
      toast.error(err instanceof Error ? err.message : "Registration failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl space-y-4">
      {created && (
        <Card className="border-emerald-200 bg-emerald-50">
          <CardContent className="p-4 text-sm">
            <p className="font-semibold text-emerald-700">
              Patient registered successfully
            </p>
            <p className="mt-1 text-emerald-700/80">
              {created.fullName}
              {created.username ? ` · username: ${created.username}` : ""}
              {created.id ? ` · ID: #${created.id}` : ""}
            </p>
          </CardContent>
        </Card>
      )}
      <Card>
        <CardContent className="p-6">
          <PatientForm mode="create" loading={loading} onSubmit={handleSubmit} />
        </CardContent>
      </Card>
    </div>
  );
}

export default RegisterPatient;
