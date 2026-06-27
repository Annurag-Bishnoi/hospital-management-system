import api from "./api";
import type { Patient, PatientRequest } from "@/types";

const normalizePatient = (patient: Patient): Patient => ({
  ...patient,
  id: patient.id ?? patient.patientId ?? (patient as any)._id,
});

export async function getPatients(): Promise<Patient[]> {
  const { data } = await api.get("/api/patients");
  const items = Array.isArray(data)
    ? data
    : data?.value ?? data?.data ?? data?.patients ?? [];
  return (items as Patient[]).map(normalizePatient);
}

export async function searchPatients(keyword: string): Promise<Patient[]> {
  const { data } = await api.get("/api/patients/search", {
    params: { keyword },
  });
  const items = Array.isArray(data)
    ? data
    : data?.value ?? data?.data ?? data?.patients ?? [];
  return (items as Patient[]).map(normalizePatient);
}

export async function getPatientById(id: string | number): Promise<Patient> {
  const { data } = await api.get(`/api/patients/${id}`);
  return normalizePatient(data);
}

export async function registerPatient(
  payload: PatientRequest,
): Promise<Patient> {
  // Strip empty username/password so backend generates default credentials.
  const body: PatientRequest = { ...payload };
  if (!body.username) delete body.username;
  if (!body.password) delete body.password;
  const { data } = await api.post("/api/patients/register", body);
  return normalizePatient(data);
}

export async function updatePatient(
  id: string | number,
  payload: Omit<PatientRequest, "username" | "password">,
): Promise<Patient> {
  const { data } = await api.put(`/api/patients/${id}`, payload);
  return normalizePatient(data);
}

export async function setPatientStatus(
  id: string | number,
  active: boolean,
) {
  const { data } = await api.patch(`/api/patients/${id}/status`, { active });
  return data;
}
