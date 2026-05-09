import type { ReactNode } from "react";
import { cn } from "@/lib/utils";

interface Props {
  title: string;
  sub?: string;
  actions?: ReactNode;
  className?: string;
}

export function PageHead({ title, sub, actions, className }: Props) {
  return (
    <div
      className={cn(
        "mb-6 flex items-start justify-between gap-4",
        className
      )}
    >
      <div>
        <h1 className="text-2xl font-semibold tracking-tight text-text">
          {title}
        </h1>
        {sub && <p className="mt-1 text-[13.5px] text-text-2">{sub}</p>}
      </div>
      {actions && <div className="flex items-center gap-2">{actions}</div>}
    </div>
  );
}
