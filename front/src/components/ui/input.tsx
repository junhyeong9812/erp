import type { InputHTMLAttributes, Ref } from "react";
import { cn } from "@/lib/utils";

export interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  ref?: Ref<HTMLInputElement>;
}

export function Input({ className, type = "text", ref, ...rest }: InputProps) {
  return (
    <input
      type={type}
      ref={ref}
      className={cn(
        "h-9 w-full rounded-md border border-border bg-bg-elev px-3 text-[13px] text-text outline-none transition-colors",
        "placeholder:text-text-3",
        "focus-visible:border-accent focus-visible:ring-2 focus-visible:ring-accent/20",
        "disabled:cursor-not-allowed disabled:opacity-50",
        className
      )}
      {...rest}
    />
  );
}
