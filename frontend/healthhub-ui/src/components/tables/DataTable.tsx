import { type ReactNode, useMemo, useState } from "react";
import { Search } from "lucide-react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Input } from "@/components/ui/input";
import { LoadingSpinner } from "@/components/common/LoadingSpinner";
import { EmptyState } from "@/components/common/EmptyState";

export interface Column<T> {
  key: string;
  header: string;
  render?: (row: T) => ReactNode;
  className?: string;
}

export function DataTable<T extends Record<string, unknown>>({
  columns,
  data,
  loading,
  error,
  searchable = true,
  searchPlaceholder = "Search...",
  searchKeys,
  emptyTitle,
  emptyDescription,
  rowKey,
  rowClassName,
  toolbar,
}: {
  columns: Column<T>[];
  data: T[];
  loading?: boolean;
  error?: string | null;
  searchable?: boolean;
  searchPlaceholder?: string;
  searchKeys?: (keyof T)[];
  emptyTitle?: string;
  emptyDescription?: string;
  rowKey?: (row: T, index: number) => string | number;
  rowClassName?: (row: T) => string;
  toolbar?: ReactNode;
}) {
  const [query, setQuery] = useState("");

  const filtered = useMemo(() => {
    if (!query.trim()) return data;
    const q = query.toLowerCase();
    const keys = searchKeys ?? (columns.map((c) => c.key) as (keyof T)[]);
    return data.filter((row) =>
      keys.some((k) => String(row[k] ?? "").toLowerCase().includes(q)),
    );
  }, [data, query, searchKeys, columns]);

  return (
    <div className="space-y-4">
      {(searchable || toolbar) && (
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          {searchable ? (
            <div className="relative w-full sm:max-w-xs">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder={searchPlaceholder}
                className="pl-9"
              />
            </div>
          ) : (
            <div />
          )}
          {toolbar}
        </div>
      )}

      <div className="overflow-hidden rounded-lg border bg-card">
        {loading ? (
          <LoadingSpinner label="Loading data..." />
        ) : error ? (
          <div className="px-6 py-12 text-center">
            <p className="text-sm font-medium text-destructive">
              Failed to load data
            </p>
            <p className="mt-1 text-sm text-muted-foreground">{error}</p>
          </div>
        ) : filtered.length === 0 ? (
          <EmptyState
            title={emptyTitle ?? "No records found"}
            description={
              emptyDescription ??
              (query
                ? "Try adjusting your search."
                : "There is no data to display.")
            }
          />
        ) : (
          <Table>
            <TableHeader>
              <TableRow className="bg-muted/50">
                {columns.map((c) => (
                  <TableHead key={c.key} className={c.className}>
                    {c.header}
                  </TableHead>
                ))}
              </TableRow>
            </TableHeader>
            <TableBody>
              {filtered.map((row, i) => (
                <TableRow
                  key={rowKey ? rowKey(row, i) : i}
                  className={rowClassName?.(row) ?? ""}
                >
                  {columns.map((c) => (
                    <TableCell key={c.key} className={c.className}>
                      {c.render ? c.render(row) : String(row[c.key] ?? "—")}
                    </TableCell>
                  ))}
                </TableRow>
              ))}
            </TableBody>
          </Table>
        )}
      </div>
    </div>
  );
}

export default DataTable;
