import type { Role } from "./mockData";

const KEY = "hms_user";

export interface SessionUser {
  email: string;
  role: Role;
  name: string;
}

export const getUser = (): SessionUser | null => {
  if (typeof window === "undefined") return null;
  try {
    const raw = localStorage.getItem(KEY);
    return raw ? (JSON.parse(raw) as SessionUser) : null;
  } catch {
    return null;
  }
};

export const setUser = (u: SessionUser) => {
  localStorage.setItem(KEY, JSON.stringify(u));
};

export const clearUser = () => {
  localStorage.removeItem(KEY);
};
