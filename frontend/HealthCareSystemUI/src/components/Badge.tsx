const TONES: Record<string, string> = {
  // statuses
  Active: "bg-success/15 text-success",
  Scheduled: "bg-primary/10 text-primary",
  Completed: "bg-success/15 text-success",
  Cancelled: "bg-destructive/10 text-destructive",
  Discharged: "bg-muted text-muted-foreground",
  Available: "bg-success/15 text-success",
  Occupied: "bg-warning/20 text-warning-foreground",
  Maintenance: "bg-muted text-muted-foreground",
  Paid: "bg-success/15 text-success",
  Pending: "bg-warning/20 text-warning-foreground",
  "Low Stock": "bg-warning/20 text-warning-foreground",
  "Out of Stock": "bg-destructive/10 text-destructive",
};

export function Badge({ children }: { children: string }) {
  const tone = TONES[children] ?? "bg-muted text-muted-foreground";
  return (
    <span className={`inline-flex items-center px-2 py-0.5 rounded-md text-xs font-medium ${tone}`}>
      {children}
    </span>
  );
}
