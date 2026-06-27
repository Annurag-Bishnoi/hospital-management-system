import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { Check, Stethoscope, CalendarClock } from "lucide-react";
import { PageHeader } from "@/components/common/PageHeader";
import { PatientSearch } from "@/components/patients/PatientSearch";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import type { Patient } from "@/types";

export const Route = createFileRoute("/receptionist/book-appointment")({
  component: BookAppointment,
});

const STEPS = ["Select Patient", "Select Doctor", "Confirm Appointment"];

function BookAppointment() {
  const [step, setStep] = useState(0);
  const [patient, setPatient] = useState<Patient | null>(null);

  return (
    <div>
      <PageHeader
        title="Book Appointment"
        description="Three-step appointment booking workflow."
      />

      <div className="mb-6 flex items-center">
        {STEPS.map((label, i) => (
          <div key={label} className="flex flex-1 items-center last:flex-none">
            <div className="flex items-center gap-2">
              <div
                className={cn(
                  "flex h-8 w-8 items-center justify-center rounded-full border text-sm font-medium",
                  i < step
                    ? "border-primary bg-primary text-primary-foreground"
                    : i === step
                      ? "border-primary text-primary"
                      : "border-border text-muted-foreground",
                )}
              >
                {i < step ? <Check className="h-4 w-4" /> : i + 1}
              </div>
              <span
                className={cn(
                  "hidden text-sm sm:inline",
                  i === step ? "font-medium text-foreground" : "text-muted-foreground",
                )}
              >
                {label}
              </span>
            </div>
            {i < STEPS.length - 1 && (
              <div className="mx-3 h-px flex-1 bg-border" />
            )}
          </div>
        ))}
      </div>

      <Card>
        <CardContent className="p-6">
          {step === 0 && (
            <div className="space-y-4">
              {patient && (
                <div className="flex items-center justify-between rounded-lg border bg-accent/40 p-3 text-sm">
                  <span>
                    Selected: <strong>{patient.fullName}</strong>
                    {patient.phone ? ` · ${patient.phone}` : ""}
                  </span>
                  <Button size="sm" onClick={() => setStep(1)}>
                    Continue
                  </Button>
                </div>
              )}
              <PatientSearch
                selectLabel="Select"
                onSelect={(p) => {
                  setPatient(p);
                }}
              />
            </div>
          )}

          {step === 1 && (
            <div className="flex flex-col items-center justify-center py-12 text-center">
              <div className="mb-4 rounded-full bg-accent p-4 text-accent-foreground">
                <Stethoscope className="h-7 w-7" />
              </div>
              <h2 className="text-lg font-semibold">Select Doctor</h2>
              <p className="mt-2 max-w-md text-sm text-muted-foreground">
                Doctor API not available yet. This step is ready to connect to the
                doctor listing API when available.
              </p>
              <div className="mt-6 flex gap-2">
                <Button variant="outline" onClick={() => setStep(0)}>
                  Back
                </Button>
                <Button onClick={() => setStep(2)}>Skip for now</Button>
              </div>
            </div>
          )}

          {step === 2 && (
            <div className="flex flex-col items-center justify-center py-12 text-center">
              <div className="mb-4 rounded-full bg-accent p-4 text-accent-foreground">
                <CalendarClock className="h-7 w-7" />
              </div>
              <h2 className="text-lg font-semibold">Confirm Appointment</h2>
              <p className="mt-2 max-w-md text-sm text-muted-foreground">
                Appointment API not available yet. Booking cannot be submitted
                until the backend appointment module is connected.
              </p>
              <div className="mt-6 flex gap-2">
                <Button variant="outline" onClick={() => setStep(1)}>
                  Back
                </Button>
                <Button disabled>Book Appointment</Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
