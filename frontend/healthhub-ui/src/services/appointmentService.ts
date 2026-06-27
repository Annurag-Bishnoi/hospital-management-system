import api from "./api";
import type { Appointment, AppointmentRequest } from "@/types";

const normalizeAppointment = (appointment: Appointment): Appointment => ({
  ...appointment,
  id: appointment.id ?? appointment.appointmentId ?? (appointment as any)._id,
});

export async function getAppointments(): Promise<Appointment[]> {
  const { data } = await api.get("/api/appointments/get");
  const items = Array.isArray(data)
    ? data
    : data?.value ?? data?.data ?? data?.appointments ?? [];
  return (items as Appointment[]).map(normalizeAppointment);
}

export async function getAppointmentById(id: string | number): Promise<Appointment> {
  const { data } = await api.get(`/api/appointments/${id}`);
  const appointment = data?.value ?? data?.data ?? data;
  return normalizeAppointment(appointment as Appointment);
}

export async function createAppointment(
  payload: AppointmentRequest,
): Promise<Appointment> {
  const { data } = await api.post(
    "/api/appointments/register",
    {
      ...payload,
      patientId: Number(payload.patientId),
      doctorId: Number(payload.doctorId),
    },
  );
  return normalizeAppointment(data);
}

export async function cancelAppointment(
  id: string | number,
): Promise<Appointment> {
  const { data } = await api.put(`/api/appointments/${id}/cancel`);
  return normalizeAppointment(data);
}
