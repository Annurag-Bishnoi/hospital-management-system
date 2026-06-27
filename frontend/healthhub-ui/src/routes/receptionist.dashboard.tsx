import { createFileRoute } from "@tanstack/react-router";
import { Link } from "@tanstack/react-router";
import { UserPlus, Search, Users, CalendarPlus } from "lucide-react";
import { PageHeader } from "@/components/common/PageHeader";
import { Card, CardContent } from "@/components/ui/card";

export const Route = createFileRoute("/receptionist/dashboard")({
  component: ReceptionistDashboard,
});

const ACTIONS = [
  {
    to: "/receptionist/register-patient",
    label: "Register Patient",
    desc: "Add a new patient record",
    icon: UserPlus,
    tone: "bg-primary/10 text-primary",
  },
  {
    to: "/receptionist/search",
    label: "Search Patients",
    desc: "Find existing patients",
    icon: Search,
    tone: "bg-emerald-100 text-emerald-600",
  },
  {
    to: "/receptionist/patients",
    label: "All Patients",
    desc: "Browse and manage patients",
    icon: Users,
    tone: "bg-violet-100 text-violet-600",
  },
  {
    to: "/receptionist/book-appointment",
    label: "Book Appointment",
    desc: "Schedule a patient visit",
    icon: CalendarPlus,
    tone: "bg-amber-100 text-amber-600",
  },
] as const;

function ReceptionistDashboard() {
  return (
    <div>
      <PageHeader
        title="Receptionist Dashboard"
        description="Quick access to your daily front-desk tasks."
      />
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        {ACTIONS.map((a) => (
          <Link key={a.to} to={a.to}>
            <Card className="h-full transition-shadow hover:shadow-md">
              <CardContent className="p-5">
                <div className={`mb-3 inline-flex rounded-lg p-3 ${a.tone}`}>
                  <a.icon className="h-6 w-6" />
                </div>
                <p className="font-semibold text-foreground">{a.label}</p>
                <p className="mt-1 text-sm text-muted-foreground">{a.desc}</p>
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
}
