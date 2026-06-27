import api from "./api";
import type { AuthUser, LoginRequest } from "@/types";

export async function login(payload: LoginRequest): Promise<AuthUser> {
  const { data } = await api.post<AuthUser>("/api/auth/login", payload);
  return data;
}
