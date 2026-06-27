import {
  LayoutDashboard,
  Users,
  Shield,
  UserPlus,
  Search,
  CalendarPlus,
  Calendar,
  Stethoscope,
  ClipboardList,
  Receipt,
  Pill,
  BedDouble,
  BarChart3,
  Settings,
  FileText,
  User,
  NotebookPen,
  Boxes,
  ShoppingCart,
  type LucideIcon,
} from "lucide-react";
import type { Role } from "@/types";

export interface NavItem {
  label: string;
  to: string;
  icon: LucideIcon;
  placeholder?: boolean;
}

export const ROLE_LABELS: Record<string, string> = {
  ADMIN: "Administrator",
  DOCTOR: "Doctor",
  RECEPTIONIST: "Receptionist",
  PATIENT: "Patient",
  NURSE: "Nurse",
  PHARMACIST: "Pharmacist",
};

export const NAV_CONFIG: Record<Role, NavItem[]> = {
  ADMIN: [
    { label: "Dashboard", to: "/admin/dashboard", icon: LayoutDashboard },
    { label: "Users", to: "/admin/users", icon: Users },
    { label: "Roles", to: "/admin/roles", icon: Shield },
    { label: "Patients", to: "/admin/patients", icon: UserPlus },
    { label: "Appointments", to: "/admin/appointments", icon: Calendar },
    { label: "Doctors", to: "/admin/doctors", icon: Stethoscope },
    { label: "Receptionists", to: "/admin/receptionists", icon: ClipboardList, placeholder: true },
    { label: "Billing", to: "/admin/billing", icon: Receipt, placeholder: true },
    { label: "Pharmacy", to: "/admin/pharmacy", icon: Pill, placeholder: true },
    { label: "Bed Management", to: "/admin/beds", icon: BedDouble, placeholder: true },
    { label: "Reports", to: "/admin/reports", icon: BarChart3, placeholder: true },
    { label: "Settings", to: "/admin/settings", icon: Settings, placeholder: true },
  ],
  RECEPTIONIST: [
    { label: "Dashboard", to: "/receptionist/dashboard", icon: LayoutDashboard },
    { label: "Register Patient", to: "/receptionist/register-patient", icon: UserPlus },
    { label: "Search Patients", to: "/receptionist/search", icon: Search },
    { label: "All Patients", to: "/receptionist/patients", icon: Users },
    { label: "Book Appointment", to: "/receptionist/book-appointment", icon: CalendarPlus },
    { label: "Appointments", to: "/receptionist/appointments", icon: Calendar, placeholder: true },
    { label: "Billing", to: "/receptionist/billing", icon: Receipt, placeholder: true },
    { label: "Reports", to: "/receptionist/reports", icon: BarChart3, placeholder: true },
  ],
  DOCTOR: [
    { label: "Dashboard", to: "/doctor/dashboard", icon: LayoutDashboard },
    { label: "Today Appointments", to: "/doctor/appointments", icon: Calendar, placeholder: true },
    { label: "Patients", to: "/doctor/patients", icon: Users },
    { label: "Prescriptions", to: "/doctor/prescriptions", icon: FileText, placeholder: true },
    { label: "Medical Records", to: "/doctor/records", icon: ClipboardList, placeholder: true },
    { label: "Profile", to: "/doctor/profile", icon: User, placeholder: true },
  ],
  PATIENT: [
    { label: "Dashboard", to: "/patient/dashboard", icon: LayoutDashboard },
    { label: "My Appointments", to: "/patient/appointments", icon: Calendar, placeholder: true },
    { label: "My Prescriptions", to: "/patient/prescriptions", icon: FileText, placeholder: true },
    { label: "My Bills", to: "/patient/bills", icon: Receipt, placeholder: true },
    { label: "My Profile", to: "/patient/profile", icon: User, placeholder: true },
  ],
  NURSE: [
    { label: "Dashboard", to: "/nurse/dashboard", icon: LayoutDashboard },
    { label: "Assigned Patients", to: "/nurse/patients", icon: Users, placeholder: true },
    { label: "Bed Status", to: "/nurse/beds", icon: BedDouble, placeholder: true },
    { label: "Notes", to: "/nurse/notes", icon: NotebookPen, placeholder: true },
  ],
  PHARMACIST: [
    { label: "Dashboard", to: "/pharmacist/dashboard", icon: LayoutDashboard },
    { label: "Medicine Inventory", to: "/pharmacist/inventory", icon: Boxes, placeholder: true },
    { label: "Prescriptions", to: "/pharmacist/prescriptions", icon: FileText, placeholder: true },
    { label: "Sales / Billing", to: "/pharmacist/sales", icon: ShoppingCart, placeholder: true },
  ],
};
