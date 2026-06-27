import type { AuthUser, Role } from "@/types";

const STORAGE_KEY = "hms_user";

export function saveUser(user: AuthUser) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
}

export function getUser(): AuthUser | null {
  if (typeof window === "undefined") return null;
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? (JSON.parse(raw) as AuthUser) : null;
  } catch {
    return null;
  }
}

export function clearUser() {
  localStorage.removeItem(STORAGE_KEY);
}

export function isAuthenticated(): boolean {
  return !!getUser();
}

const ROLE_HOME: Record<string, string> = {
  ADMIN: "/admin/dashboard",
  DOCTOR: "/doctor/dashboard",
  RECEPTIONIST: "/receptionist/dashboard",
  PATIENT: "/patient/dashboard",
  NURSE: "/nurse/dashboard",
  PHARMACIST: "/pharmacist/dashboard",
};

export function homeForRole(role?: string): string {
  if (!role) return "/login";
  return ROLE_HOME[role.toUpperCase()] ?? "/login";
}

export function normalizeRole(role?: string): Role | undefined {
  return role ? (role.toUpperCase() as Role) : undefined;
}
