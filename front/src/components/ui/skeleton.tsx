import type { HTMLAttributes } from "react";
import { cn } from "@/lib/utils";

export function Skeleton({
  className,
  ...rest
}: HTMLAttributes<HTMLSpanElement>) {
  return (
    <span
      className={cn(
        "inline-block animate-pulse rounded-md bg-hover align-middle",
        className
      )}
      {...rest}
    />
  );
}
