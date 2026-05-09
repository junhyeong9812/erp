"use client";

import { Link, usePathname } from "@/i18n/routing";
import { cn } from "@/lib/utils";

export interface TabItem {
  key: string;
  href: string;
  label: string;
  count?: number;
}

interface Props {
  items: readonly TabItem[];
  className?: string;
}

export function TabsNav({ items, className }: Props) {
  const pathname = usePathname();

  return (
    <nav
      className={cn(
        "flex gap-1 border-b border-border",
        className
      )}
    >
      {items.map((it) => {
        const active = pathname === it.href || pathname.startsWith(it.href + "/");
        return (
          <Link
            key={it.key}
            href={it.href}
            className={cn(
              "relative inline-flex items-center gap-1.5 px-4 py-2.5 text-[13px] transition-colors",
              active
                ? "font-semibold text-text"
                : "text-text-3 hover:text-text-2"
            )}
          >
            <span>{it.label}</span>
            {it.count != null && (
              <span
                className={cn(
                  "rounded-full px-1.5 text-[10.5px] tabular-nums",
                  active
                    ? "bg-accent-soft text-accent-ink"
                    : "bg-hover text-text-3"
                )}
              >
                {it.count}
              </span>
            )}
            {active && (
              <span
                aria-hidden
                className="absolute inset-x-0 -bottom-px h-0.5 bg-accent"
              />
            )}
          </Link>
        );
      })}
    </nav>
  );
}
