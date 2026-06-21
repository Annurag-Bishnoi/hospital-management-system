import { createFileRoute } from "@tanstack/react-router";
import {
  ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip,
  LineChart, Line,
} from "recharts";
import { StatCard } from "@/components/StatCard";
import { PageHeader } from "@/components/DataTable";
import { IndianRupee, CalendarDays, Users, AlertTriangle, BedDouble } from "lucide-react";
import {
  monthlyRevenue, monthlyAppointments, patients, beds, medicines,
  stockStatus, bills, billTotal,
} from "@/lib/mockData";

export const Route = createFileRoute("/_app/analytics")({ component: AnalyticsPage });

function AnalyticsPage() {
  const revenue = bills.filter((b) => b.status === "Paid").reduce((s, b) => s + billTotal(b), 0);
  const occupied = beds.filter((b) => b.status === "Occupied").length;
  const occupancy = Math.round((occupied / beds.length) * 100);
  const alerts = medicines.filter((m) => stockStatus(m) !== "Available").length;

  return (
    <div>
      <PageHeader title="Analytics" description="Hospital performance overview" />
      <div className="grid gap-4 grid-cols-1 sm:grid-cols-2 lg:grid-cols-5">
        <StatCard label="Monthly Revenue" value={`₹${revenue.toLocaleString()}`} icon={IndianRupee} tone="success" />
        <StatCard label="Appointments" value={monthlyAppointments.at(-1)?.count ?? 0} icon={CalendarDays} />
        <StatCard label="Patients" value={patients.length} icon={Users} tone="primary" />
        <StatCard label="Stock Alerts" value={alerts} icon={AlertTriangle} tone="warning" />
        <StatCard label="Bed Occupancy" value={`${occupancy}%`} icon={BedDouble} tone="success" />
      </div>

      <div className="grid gap-6 lg:grid-cols-2 mt-8">
        <div className="bg-card border border-border rounded-xl p-5">
          <h3 className="font-semibold mb-4">Monthly Revenue</h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={monthlyRevenue}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                <XAxis dataKey="month" stroke="var(--muted-foreground)" fontSize={12} />
                <YAxis stroke="var(--muted-foreground)" fontSize={12} />
                <Tooltip contentStyle={{ background: "var(--card)", border: "1px solid var(--border)", borderRadius: 8 }} />
                <Bar dataKey="revenue" fill="var(--primary)" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
        <div className="bg-card border border-border rounded-xl p-5">
          <h3 className="font-semibold mb-4">Appointments Trend</h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={monthlyAppointments}>
                <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
                <XAxis dataKey="month" stroke="var(--muted-foreground)" fontSize={12} />
                <YAxis stroke="var(--muted-foreground)" fontSize={12} />
                <Tooltip contentStyle={{ background: "var(--card)", border: "1px solid var(--border)", borderRadius: 8 }} />
                <Line type="monotone" dataKey="count" stroke="var(--primary)" strokeWidth={2} dot={{ r: 4 }} />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
}
