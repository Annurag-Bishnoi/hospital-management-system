import { createFileRoute } from "@tanstack/react-router";
import { PageHeader } from "@/components/DataTable";
import { Button, Field, Input } from "@/components/UI";

export const Route = createFileRoute("/_app/settings")({ component: SettingsPage });

function SettingsPage() {
  return (
    <div>
      <PageHeader title="Settings" description="Hospital configuration" />
      <div className="bg-card border border-border rounded-xl p-6 max-w-2xl space-y-4">
        <Field label="Hospital Name"><Input defaultValue="MediCare Hospital" /></Field>
        <Field label="Address"><Input defaultValue="123 Health St, Pune, India" /></Field>
        <Field label="Contact"><Input defaultValue="+91 9000000000" /></Field>
        <Field label="Email"><Input defaultValue="contact@medicare.com" /></Field>
        <div className="flex justify-end"><Button>Save</Button></div>
      </div>
    </div>
  );
}
