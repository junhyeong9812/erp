import { cva, type VariantProps } from "class-variance-authority";
import type { HTMLAttributes } from "react";
import { cn } from "@/lib/utils";

const badgeVariants = cva(
  "inline-flex items-center gap-1 rounded-md px-2 py-0.5 text-[11.5px] font-medium",
  {
    variants: {
      tone: {
        ok: "bg-ok-soft text-ok-ink",
        warn: "bg-warn-soft text-warn-ink",
        info: "bg-info-soft text-info-ink",
        danger: "bg-danger-soft text-danger-ink",
        neutral: "bg-neutral-soft text-neutral-ink",
        accent: "bg-accent-soft text-accent-ink",
      },
      live: {
        true: "before:size-1.5 before:rounded-full before:bg-current before:animate-pulse",
        false: "",
      },
    },
    defaultVariants: { tone: "neutral", live: false },
  }
);

export interface BadgeProps
  extends HTMLAttributes<HTMLSpanElement>,
    VariantProps<typeof badgeVariants> {}

export function Badge({ className, tone, live, ...rest }: BadgeProps) {
  return (
    <span className={cn(badgeVariants({ tone, live }), className)} {...rest} />
  );
}

export { badgeVariants };
