import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import {
  Users, CalendarDays, BedDouble, Receipt, Pill, IndianRupee, Activity,
  Stethoscope, ClipboardList, FileText, AlertTriangle,
} from "lucide-react";
import { StatCard } from "@/components/StatCard";
import { DataTable, PageHeader } from "@/components/DataTable";
import { Badge } from "@/components/Badge";
import { getUser, type SessionUser } from "@/lib/auth";
import {
  patients, appointments, beds, bills, medicines, billTotal, stockStatus,
  prescriptions, type Appointment,
} from "@/lib/mockData";

export const Route = createFileRoute("/_app/dashboard")({
  component: DashboardPage,
});

function DashboardPage() {
  const navigate = useNavigate();
  const [user, setUser] = useState<SessionUser | null | undefined>(undefined);

  useEffect(() => {
    const sessionUser = getUser();
    setUser(sessionUser);

    if (!sessionUser) {
      navigate({ to: "/login", replace: true });
    }
  }, [navigate]);

  if (user === undefined) {
    return <div className="text-sm text-muted-foreground">Loading dashboard...</div>;
  }

  if (!user) return null;

  const totalRevenue = bills.filter((b) => b.status === "Paid").reduce((s, b) => s + billTotal(b), 0);
  const availableBeds = beds.filter((b) => b.status === "Available").length;
  const lowStock = medicines.filter((m) => stockStatus(m) !== "Available").length;
  const todayAppts = appointments.filter((a) => a.status === "Scheduled").length;
  const pendingBills = bills.filter((b) => b.status === "Pending").length;

  const greeting = `Welcome back, ${user.name}`;

  if (user.role === "admin") {
    return (
      <div>
        <PageHeader title={greeting} description="Hospital overview at a glance" />
        <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-3">
          <StatCard label="Total Patients" value={patients.length} icon={Users} tone="primary" />
          <StatCard label="Today Appointments" value={todayAppts} icon={CalendarDays} tone="primary" />
          <StatCard label="Available Beds" value={availableBeds} icon={BedDouble} tone="success" />
          <StatCard label="Pending Bills" value={pendingBills} icon={Receipt} tone="warning" />
          <StatCard label="Pharmacy Alerts" value={lowStock} icon={AlertTriangle} tone="destructive" />
          <StatCard label="Total Revenue" value={`₹${totalRevenue.toLocaleString()}`} icon={IndianRupee} tone="success" />
        </div>
        <div className="mt-8">
          <h3 className="font-semibold mb-3">Recent Appointments</h3>
          <AppointmentsMini rows={appointments.slice(0, 5)} />
        </div>
      </div>
    );
  }

  if (user.role === "doctor") {
    return (
      <div>
        <PageHeader title={greeting} description="Your consultations today" />
        <div className="grid gap-4 grid-cols-1 sm:grid-cols-3">
          <StatCard label="Today Appointments" value={appointments.filter((a) => a.status === "Scheduled").length} icon={CalendarDays} />
          <StatCard label="Pending Consultations" value={appointments.filter((a) => a.status === "Scheduled").length} icon={Stethoscope} tone="warning" />
          <StatCard label="Completed Consultations" value={appointments.filter((a) => a.status === "Completed").length} icon={FileText} tone="success" />
        </div>
        <div className="mt-8">
          <h3 className="font-semibold mb-3">Upcoming Appointments</h3>
          <AppointmentsMini rows={appointments.filter((a) => a.status === "Scheduled")} />
        </div>
      </div>
    );
  }

  if (user.role === "nurse") {
    return (
      <div>
        <PageHeader title={greeting} description="Ward overview" />
        <div className="grid gap-4 grid-cols-1 sm:grid-cols-3">
          <StatCard label="Assigned Patients" value={patients.filter((p) => p.status === "Active").length} icon={Users} />
          <StatCard label="Available Beds" value={availableBeds} icon={BedDouble} tone="success" />
          <StatCard label="Medicine Schedule" value={medicines.length} icon={Pill} tone="primary" />
        </div>
      </div>
    );
  }

  if (user.role === "pharmacist") {
    return (
      <div>
        <PageHeader title={greeting} description="Pharmacy inventory snapshot" />
        <div className="grid gap-4 grid-cols-1 sm:grid-cols-3">
          <StatCard label="Total Medicines" value={medicines.length} icon={Pill} />
          <StatCard label="Low Stock Items" value={medicines.filter((m) => stockStatus(m) === "Low Stock").length} icon={AlertTriangle} tone="warning" />
          <StatCard label="Out of Stock" value={medicines.filter((m) => stockStatus(m) === "Out of Stock").length} icon={AlertTriangle} tone="destructive" />
        </div>
      </div>
    );
  }

  if (user.role === "receptionist") {
    return (
      <div>
        <PageHeader title={greeting} description="Front desk overview" />
        <div className="grid gap-4 grid-cols-1 sm:grid-cols-3">
          <StatCard label="Total Patients" value={patients.length} icon={Users} />
          <StatCard label="Today Appointments" value={todayAppts} icon={CalendarDays} tone="primary" />
          <StatCard label="Pending Bills" value={pendingBills} icon={Receipt} tone="warning" />
        </div>
      </div>
    );
  }

  // patient
  return (
    <div>
      <PageHeader title={greeting} description="Your health summary" />
      <div className="grid gap-4 grid-cols-1 sm:grid-cols-3">
        <StatCard label="My Appointments" value={appointments.length} icon={CalendarDays} />
        <StatCard label="My Prescriptions" value={prescriptions.length} icon={ClipboardList} tone="success" />
        <StatCard label="My Bills" value={bills.length} icon={Receipt} tone="warning" />
      </div>
      <div className="mt-8 bg-card border border-border rounded-xl p-6">
        <div className="flex items-center gap-3">
          <Activity className="size-5 text-primary" />
          <div>
            <div className="font-medium">Stay healthy, {user.name}</div>
            <div className="text-sm text-muted-foreground">Book your next check-up from the sidebar.</div>
          </div>
        </div>
      </div>
    </div>
  );
}

function AppointmentsMini({ rows }: { rows: Appointment[] }) {
  return (
    <DataTable<Appointment>
      rows={rows}
      columns={[
        { key: "id", label: "ID" },
        { key: "patientName", label: "Patient" },
        { key: "doctorName", label: "Doctor" },
        { key: "date", label: "Date" },
        { key: "time", label: "Time" },
        { key: "status", label: "Status", render: (r) => <Badge>{r.status}</Badge> },
      ]}
    />
  );
}
