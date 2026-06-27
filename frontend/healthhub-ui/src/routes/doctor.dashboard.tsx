import { createFileRoute } from "@tanstack/react-router";
import { Users, Calendar, FileText, ClipboardList } from "lucide-react";
import { PageHeader } from "@/components/common/PageHeader";
import { StatCard } from "@/components/common/StatCard";
import { Card, CardContent } from "@/components/ui/card";

export const Route = createFileRoute("/doctor/dashboard")({
  component: DoctorDashboard,
});

function DoctorDashboard() {
  return (
    <div>
      <PageHeader
        title="Doctor Dashboard"
        description="Your clinical overview. Some modules connect once their APIs are available."
      />
      <div className="mb-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <StatCard label="Today's Appointments" value="—" icon={Calendar} tone="primary" />
        <StatCard label="My Patients" value="—" icon={Users} tone="emerald" />
        <StatCard label="Prescriptions" value="—" icon={FileText} tone="violet" />
        <StatCard label="Medical Records" value="—" icon={ClipboardList} tone="amber" />
      </div>
      <Card>
        <CardContent className="p-6 text-sm text-muted-foreground">
          You can search patients and view patient details using the Patients
          section. Appointment, prescription and medical record modules will be
          enabled once their APIs are available.
        </CardContent>
      </Card>
    </div>
  );
}
