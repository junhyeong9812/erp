"use client";

import { Fragment } from "react";
import { useTranslations } from "next-intl";
import type { StatusKey } from "@/lib/types";
import { cn } from "@/lib/utils";

interface Props {
  steps: readonly StatusKey[];
  current: StatusKey;
}

export function Stepper({ steps, current }: Props) {
  const t = useTranslations("Status");
  const idx = steps.indexOf(current);

  return (
    <div className="flex items-center gap-2">
      {steps.map((s, i) => {
        const done = i < idx;
        const now = i === idx;
        return (
          <Fragment key={s}>
            <div className="flex items-center gap-2">
              <span
                className={cn(
                  "flex size-5 items-center justify-center rounded-full text-[10.5px] font-semibold",
                  done && "bg-accent text-white",
                  now && "bg-accent-soft text-accent-ink ring-2 ring-accent",
                  !done && !now && "bg-hover text-text-3"
                )}
              >
                {done ? "✓" : i + 1}
              </span>
              <span
                className={cn(
                  "text-[12px]",
                  done && "text-text-2",
                  now && "font-medium text-text",
                  !done && !now && "text-text-3"
                )}
              >
                {t(s)}
              </span>
            </div>
            {i < steps.length - 1 && (
              <span
                className={cn(
                  "h-px flex-1",
                  done ? "bg-accent" : "bg-border"
                )}
              />
            )}
          </Fragment>
        );
      })}
    </div>
  );
}
