import { Loader2 } from "lucide-react";
import { cn } from "@/lib/utils";

export function LoadingSpinner({
  className,
  label,
}: {
  className?: string;
  label?: string;
}) {
  return (
    <div className="flex flex-col items-center justify-center gap-3 py-12 text-muted-foreground">
      <Loader2 className={cn("h-7 w-7 animate-spin text-primary", className)} />
      {label && <p className="text-sm">{label}</p>}
    </div>
  );
}

export default LoadingSpinner;
