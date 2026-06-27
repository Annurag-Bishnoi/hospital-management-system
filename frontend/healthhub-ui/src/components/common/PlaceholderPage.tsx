import { Construction } from "lucide-react";
import { PageHeader } from "./PageHeader";

export function PlaceholderPage({
  title,
  module,
}: {
  title: string;
  module?: string;
}) {
  return (
    <div>
      <PageHeader title={title} />
      <div className="flex flex-col items-center justify-center rounded-xl border border-dashed bg-muted/30 px-6 py-20 text-center">
        <div className="mb-4 rounded-full bg-accent p-4 text-accent-foreground">
          <Construction className="h-7 w-7" />
        </div>
        <h2 className="text-lg font-semibold text-foreground">
          API not available yet
        </h2>
        <p className="mt-2 max-w-md text-sm text-muted-foreground">
          The {module ?? title} module UI is ready. Connect the backend API here
          when it becomes available.
        </p>
      </div>
    </div>
  );
}

export default PlaceholderPage;
