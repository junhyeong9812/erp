"use client";

import { useCountUp } from "@/lib/hooks/use-count-up";
import { Sparkline } from "./sparkline";
import { cn } from "@/lib/utils";

interface Props {
  label: string;
  value: number;
  unit?: string;
  delta?: number;
  vs?: string;
  format?: "n" | "krw";
  spark?: number[];
  className?: string;
}

export function Kpi({
  label,
  value,
  unit,
  delta,
  vs,
  format = "n",
  spark,
  className,
}: Props) {
  const animated = useCountUp(value);
  const display =
    format === "krw"
      ? "₩" + Math.round(animated).toLocaleString("ko-KR")
      : Math.round(animated).toLocaleString("ko-KR");

  return (
    <div
      className={cn(
        "relative flex flex-col gap-1.5 rounded-lg border border-border bg-panel p-4 shadow-sm",
        className
      )}
    >
      {spark && (
        <div className="absolute right-3 top-3 opacity-90">
          <Sparkline points={spark} />
        </div>
      )}
      <div className="text-[12px] font-medium text-text-3">{label}</div>
      <div className="flex items-baseline gap-1.5">
        <span className="text-[28px] font-semibold tracking-tight tabular-nums text-text">
          {display}
        </span>
        {unit && <span className="text-[12px] text-text-3">{unit}</span>}
      </div>
      {delta != null && (
        <div className="flex items-center gap-1.5 text-[11.5px]">
          <span
            className={cn(
              "font-medium tabular-nums",
              delta >= 0 ? "text-ok" : "text-danger"
            )}
          >
            {delta >= 0 ? "▲" : "▼"} {Math.abs(delta).toFixed(1)}%
          </span>
          {vs && <span className="text-text-3">{vs}</span>}
        </div>
      )}
    </div>
  );
}
