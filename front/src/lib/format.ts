export function fmtKRW(n: number | null | undefined): string {
  if (n == null) return "-";
  return "₩" + Math.round(n).toLocaleString("ko-KR");
}

export function fmtN(n: number | null | undefined): string {
  if (n == null) return "-";
  return Number(n).toLocaleString("ko-KR");
}

export function fmtDate(iso: string | null | undefined): string {
  if (!iso) return "-";
  const d = new Date(iso);
  return `${d.getMonth() + 1}/${d.getDate()}`;
}

export function fmtDateTime(iso: string | null | undefined): string {
  if (!iso) return "-";
  const d = new Date(iso);
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${d.getMonth() + 1}/${d.getDate()} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}

export type RelativeUnit = "now" | "minutes" | "hours" | "days";

export interface RelativeParts {
  unit: RelativeUnit;
  count: number;
}

export function relativeParts(iso: string | null | undefined): RelativeParts | null {
  if (!iso) return null;
  const t = new Date(iso).getTime();
  const diffMin = Math.round((Date.now() - t) / 60000);
  if (diffMin < 1) return { unit: "now", count: 0 };
  if (diffMin < 60) return { unit: "minutes", count: diffMin };
  if (diffMin < 60 * 24) return { unit: "hours", count: Math.round(diffMin / 60) };
  return { unit: "days", count: Math.round(diffMin / 60 / 24) };
}
