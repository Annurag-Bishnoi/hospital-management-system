import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

type Tone = "success" | "danger" | "warning" | "neutral";

const toneClasses: Record<Tone, string> = {
  success:
    "bg-emerald-100 text-emerald-700 border-emerald-200 hover:bg-emerald-100",
  danger: "bg-red-100 text-red-700 border-red-200 hover:bg-red-100",
  warning: "bg-amber-100 text-amber-700 border-amber-200 hover:bg-amber-100",
  neutral: "bg-muted text-muted-foreground border-border hover:bg-muted",
};

export function StatusBadge({
  active,
  trueLabel = "Active",
  falseLabel = "Inactive",
  tone,
  label,
}: {
  active?: boolean;
  trueLabel?: string;
  falseLabel?: string;
  tone?: Tone;
  label?: string;
}) {
  const resolvedTone: Tone = tone ?? (active ? "success" : "danger");
  return (
    <Badge
      variant="outline"
      className={cn("font-medium", toneClasses[resolvedTone])}
    >
      {label ?? (active ? trueLabel : falseLabel)}
    </Badge>
  );
}

export default StatusBadge;
