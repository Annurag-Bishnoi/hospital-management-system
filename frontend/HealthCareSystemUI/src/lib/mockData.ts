export type Role = "admin" | "doctor" | "nurse" | "pharmacist" | "receptionist" | "patient";

export const ROLES: Role[] = ["admin", "doctor", "nurse", "pharmacist", "receptionist", "patient"];

export const roleLabel = (r: Role) => r.charAt(0).toUpperCase() + r.slice(1);

export interface Patient {
  id: string;
  name: string;
  age: number;
  gender: string;
  phone: string;
  bloodGroup: string;
  status: "Active" | "Discharged";
  address?: string;
  emergencyContact?: string;
  symptoms?: string;
}

export interface Doctor {
  id: string;
  name: string;
  department: string;
  phone: string;
  experience: number;
}

export interface Appointment {
  id: string;
  patientName: string;
  doctorName: string;
  department: string;
  date: string;
  time: string;
  status: "Scheduled" | "Completed" | "Cancelled";
  notes?: string;
}

export interface Bed {
  id: string;
  number: string;
  ward: "General" | "ICU" | "Emergency" | "Private";
  status: "Available" | "Occupied" | "Maintenance";
  patient?: string;
}

export interface Medicine {
  id: string;
  name: string;
  category: string;
  quantity: number;
  price: number;
  expiry: string;
  supplier?: string;
}

export interface Bill {
  id: string;
  patientName: string;
  consultation: number;
  medicine: number;
  room: number;
  other: number;
  status: "Paid" | "Pending";
}

export interface Prescription {
  id: string;
  patientName: string;
  diagnosis: string;
  medicines: string;
  dosage: string;
  instructions: string;
  followUp: string;
}

export interface Vital {
  id: string;
  patientName: string;
  temperature: string;
  bp: string;
  pulse: string;
  notes: string;
  date: string;
}

export const patients: Patient[] = [
  { id: "P001", name: "Ravi Kumar", age: 34, gender: "Male", phone: "9876543210", bloodGroup: "O+", status: "Active" },
  { id: "P002", name: "Anita Sharma", age: 28, gender: "Female", phone: "9876501234", bloodGroup: "A+", status: "Active" },
  { id: "P003", name: "Suresh Patel", age: 52, gender: "Male", phone: "9988776655", bloodGroup: "B+", status: "Discharged" },
  { id: "P004", name: "Priya Nair", age: 41, gender: "Female", phone: "9123456780", bloodGroup: "AB+", status: "Active" },
  { id: "P005", name: "Mohit Verma", age: 19, gender: "Male", phone: "9090909090", bloodGroup: "O-", status: "Active" },
];

export const doctors: Doctor[] = [
  { id: "D001", name: "Dr. Mehta", department: "Cardiology", phone: "9000000001", experience: 12 },
  { id: "D002", name: "Dr. Reddy", department: "Orthopedics", phone: "9000000002", experience: 8 },
  { id: "D003", name: "Dr. Iyer", department: "General Medicine", phone: "9000000003", experience: 15 },
  { id: "D004", name: "Dr. Kapoor", department: "Pediatrics", phone: "9000000004", experience: 6 },
];

export const appointments: Appointment[] = [
  { id: "A001", patientName: "Ravi Kumar", doctorName: "Dr. Mehta", department: "Cardiology", date: "2026-05-25", time: "10:00", status: "Scheduled" },
  { id: "A002", patientName: "Anita Sharma", doctorName: "Dr. Iyer", department: "General Medicine", date: "2026-05-25", time: "11:30", status: "Completed" },
  { id: "A003", patientName: "Priya Nair", doctorName: "Dr. Reddy", department: "Orthopedics", date: "2026-05-26", time: "09:00", status: "Scheduled" },
  { id: "A004", patientName: "Mohit Verma", doctorName: "Dr. Kapoor", department: "Pediatrics", date: "2026-05-24", time: "14:00", status: "Cancelled" },
];

export const beds: Bed[] = [
  { id: "B001", number: "G-101", ward: "General", status: "Occupied", patient: "Ravi Kumar" },
  { id: "B002", number: "G-102", ward: "General", status: "Available" },
  { id: "B003", number: "ICU-01", ward: "ICU", status: "Occupied", patient: "Suresh Patel" },
  { id: "B004", number: "ICU-02", ward: "ICU", status: "Available" },
  { id: "B005", number: "E-01", ward: "Emergency", status: "Available" },
  { id: "B006", number: "P-201", ward: "Private", status: "Maintenance" },
  { id: "B007", number: "P-202", ward: "Private", status: "Occupied", patient: "Priya Nair" },
  { id: "B008", number: "G-103", ward: "General", status: "Available" },
];

export const medicines: Medicine[] = [
  { id: "M001", name: "Paracetamol 500mg", category: "Tablet", quantity: 250, price: 2, expiry: "2027-06-01" },
  { id: "M002", name: "Amoxicillin 250mg", category: "Capsule", quantity: 15, price: 8, expiry: "2026-09-15" },
  { id: "M003", name: "Cough Syrup", category: "Syrup", quantity: 0, price: 65, expiry: "2026-12-10" },
  { id: "M004", name: "Insulin", category: "Injection", quantity: 30, price: 450, expiry: "2026-07-20" },
  { id: "M005", name: "Vitamin D3", category: "Tablet", quantity: 120, price: 15, expiry: "2028-01-01" },
];

export const bills: Bill[] = [
  { id: "BL001", patientName: "Ravi Kumar", consultation: 500, medicine: 350, room: 1500, other: 100, status: "Paid" },
  { id: "BL002", patientName: "Anita Sharma", consultation: 500, medicine: 120, room: 0, other: 0, status: "Pending" },
  { id: "BL003", patientName: "Suresh Patel", consultation: 800, medicine: 1200, room: 4500, other: 300, status: "Pending" },
  { id: "BL004", patientName: "Priya Nair", consultation: 500, medicine: 250, room: 2000, other: 50, status: "Paid" },
];

export const prescriptions: Prescription[] = [
  { id: "PR001", patientName: "Ravi Kumar", diagnosis: "Hypertension", medicines: "Amlodipine 5mg", dosage: "1-0-1", instructions: "After food", followUp: "2026-06-10" },
  { id: "PR002", patientName: "Anita Sharma", diagnosis: "Common Cold", medicines: "Paracetamol, Cetirizine", dosage: "1-1-1", instructions: "Plenty of fluids", followUp: "2026-06-01" },
];

export const vitals: Vital[] = [
  { id: "V001", patientName: "Ravi Kumar", temperature: "98.6", bp: "120/80", pulse: "76", notes: "Stable", date: "2026-05-25" },
  { id: "V002", patientName: "Suresh Patel", temperature: "99.4", bp: "140/90", pulse: "88", notes: "Monitor BP", date: "2026-05-25" },
];

export const billTotal = (b: Bill) => b.consultation + b.medicine + b.room + b.other;

export const stockStatus = (m: Medicine): "Available" | "Low Stock" | "Out of Stock" => {
  if (m.quantity === 0) return "Out of Stock";
  if (m.quantity < 20) return "Low Stock";
  return "Available";
};

export const monthlyRevenue = [
  { month: "Jan", revenue: 42000 },
  { month: "Feb", revenue: 51000 },
  { month: "Mar", revenue: 48500 },
  { month: "Apr", revenue: 60200 },
  { month: "May", revenue: 72000 },
];

export const monthlyAppointments = [
  { month: "Jan", count: 120 },
  { month: "Feb", count: 145 },
  { month: "Mar", count: 132 },
  { month: "Apr", count: 168 },
  { month: "May", count: 190 },
];
