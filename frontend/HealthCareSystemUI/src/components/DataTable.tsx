export function DataTable<T extends { id: string }>({
  columns,
  rows,
  empty = "No records found",
}: {
  columns: { key: keyof T | "actions"; label: string; render?: (row: T) => React.ReactNode }[];
  rows: T[];
  empty?: string;
}) {
  return (
    <div className="overflow-x-auto bg-card border border-border rounded-xl">
      <table className="w-full text-sm">
        <thead className="bg-muted/50 text-muted-foreground">
          <tr>
            {columns.map((c) => (
              <th key={String(c.key)} className="text-left font-medium px-4 py-3 whitespace-nowrap">
                {c.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="px-4 py-8 text-center text-muted-foreground">
                {empty}
              </td>
            </tr>
          )}
          {rows.map((row) => (
            <tr key={row.id} className="border-t border-border hover:bg-muted/30">
              {columns.map((c) => (
                <td key={String(c.key)} className="px-4 py-3 whitespace-nowrap">
                  {c.render ? c.render(row) : (row[c.key as keyof T] as React.ReactNode)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export function PageHeader({
  title,
  description,
  action,
}: {
  title: string;
  description?: string;
  action?: React.ReactNode;
}) {
  return (
    <div className="flex items-start justify-between gap-4 mb-6">
      <div>
        <h2 className="text-2xl font-semibold text-foreground">{title}</h2>
        {description && <p className="text-sm text-muted-foreground mt-1">{description}</p>}
      </div>
      {action}
    </div>
  );
}
