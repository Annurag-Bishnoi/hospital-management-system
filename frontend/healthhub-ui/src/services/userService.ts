import api from "./api";
import type {
  AppUser,
  CreateUserRequest,
  DashboardSummary,
  RoleItem,
  UpdateUserRequest,
} from "@/types";

export async function getDashboardSummary(): Promise<DashboardSummary> {
  const { data } = await api.get("/api/admin/dashboard/summary");
  return data;
}

const normalizeUser = (user: AppUser): AppUser => ({
  ...user,
  id: user.id ?? user.userId ?? (user as any)._id,
});

export async function getUsers(): Promise<AppUser[]> {
  const { data } = await api.get("/api/admin/users");
  const items = Array.isArray(data) ? data : (data?.data ?? data?.users ?? []);
  return (items as AppUser[]).map(normalizeUser);
}

export async function getRoles(): Promise<RoleItem[]> {
  const { data } = await api.get("/api/admin/roles");
  return Array.isArray(data) ? data : (data?.data ?? data?.roles ?? []);
}

export async function getUserById(id: string | number): Promise<AppUser> {
  const { data } = await api.get(`/api/admin/users/${id}`);
  return normalizeUser(data);
}

export async function createUser(payload: CreateUserRequest): Promise<AppUser> {
  const { data } = await api.post("/api/admin/users", payload);
  return data;
}

export async function updateUser(
  id: string | number,
  payload: UpdateUserRequest,
): Promise<AppUser> {
  const { data } = await api.put(`/api/admin/users/${id}`, payload);
  return data;
}

export async function setUserStatus(id: string | number, active: boolean) {
  const { data } = await api.patch(`/api/admin/users/${id}/status`, { active });
  return data;
}

export async function setUserLock(
  id: string | number,
  accountLocked: boolean,
) {
  const { data } = await api.patch(`/api/admin/users/${id}/lock`, {
    accountLocked,
  });
  return data;
}

export async function changeUserRole(id: string | number, roleCode: string) {
  const { data } = await api.patch(`/api/admin/users/${id}/role`, { roleCode });
  return data;
}

export async function resetUserPassword(
  id: string | number,
  newPassword: string,
) {
  const { data } = await api.patch(`/api/admin/users/${id}/reset-password`, {
    newPassword,
  });
  return data;
}
