import api from "./api";
import type { Doctor, DoctorRequest, DoctorCreateResponse } from "@/types";

const normalizeDoctor = (doctor: Doctor): Doctor => ({
  ...doctor,
  id: doctor.id ?? doctor.doctorId ?? doctor.userId ?? (doctor as any)._id,
});

export async function getDoctors(): Promise<Doctor[]> {
  const { data } = await api.get("/api/doctors");
  const items = Array.isArray(data)
    ? data
    : data?.value ?? data?.data ?? data?.doctors ?? [];
  return (items as Doctor[]).map(normalizeDoctor);
}

export async function searchDoctors(keyword: string): Promise<Doctor[]> {
  const { data } = await api.get("/api/doctors/search", {
    params: { keyword },
  });
  const items = Array.isArray(data)
    ? data
    : data?.value ?? data?.data ?? data?.doctors ?? [];
  return (items as Doctor[]).map(normalizeDoctor);
}

export async function getDoctorById(id: string | number): Promise<Doctor> {
  const { data } = await api.get(`/api/doctors/${id}`);
  return normalizeDoctor(data);
}

export async function registerDoctor(
  payload: DoctorRequest,
): Promise<Doctor> {
  const { data } = await api.post("/api/doctors/register", payload);
  return normalizeDoctor((data as DoctorCreateResponse).doctor ?? data);
}

export async function updateDoctor(
  id: string | number,
  payload: Omit<DoctorRequest, "username" | "password">,
): Promise<Doctor> {
  const { data } = await api.put(`/api/doctors/${id}`, payload);
  return normalizeDoctor(data);
}

