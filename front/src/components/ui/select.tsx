import type { Ref, SelectHTMLAttributes } from "react";
import { cn } from "@/lib/utils";

export interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  ref?: Ref<HTMLSelectElement>;
  invalid?: boolean;
}

export function Select({
  className,
  invalid,
  ref,
  children,
  ...rest
}: SelectProps) {
  return (
    <select
      ref={ref}
      aria-invalid={invalid || undefined}
      className={cn(
        "h-9 w-full appearance-none rounded-md border bg-bg-elev px-3 pr-7 text-[13px] text-text outline-none transition-colors",
        "bg-[url('data:image/svg+xml;utf8,<svg xmlns=%22http://www.w3.org/2000/svg%22 width=%2210%22 height=%226%22 viewBox=%220 0 10 6%22 fill=%22none%22><path d=%22M1 1l4 4 4-4%22 stroke=%22%23999%22 stroke-width=%221.2%22 stroke-linecap=%22round%22 stroke-linejoin=%22round%22/></svg>')] bg-[length:10px_6px] bg-[position:right_10px_center] bg-no-repeat",
        "focus-visible:border-accent focus-visible:ring-2 focus-visible:ring-accent/20",
        "disabled:cursor-not-allowed disabled:opacity-50",
        invalid
          ? "border-danger focus-visible:border-danger focus-visible:ring-danger/20"
          : "border-border",
        className
      )}
      {...rest}
    >
      {children}
    </select>
  );
}
