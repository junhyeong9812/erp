"use client";

import { Link, usePathname } from "@/i18n/routing";
import { cn } from "@/lib/utils";
import type { ReactNode } from "react";

interface Props {
  href: string;
  icon: ReactNode;
  label: string;
  badge?: number;
}

export function SidebarNavItem({ href, icon, label, badge }: Props) {
  const pathname = usePathname();
  const active = pathname === href || pathname.startsWith(href + "/");

  return (
    <Link
      href={href}
      className={cn(
        "group flex items-center gap-2.5 rounded-md px-3 py-2 text-[13.5px] transition-colors",
        "text-text-2 hover:bg-hover hover:text-text",
        active && "bg-accent-soft text-accent-ink font-medium"
      )}
    >
      <span
        className={cn(
          "size-4 shrink-0 text-text-3 group-hover:text-text-2",
          active && "text-accent-ink"
        )}
      >
        {icon}
      </span>
      <span className="flex-1 truncate">{label}</span>
      {badge != null && badge > 0 && (
        <span className="ml-auto rounded-full bg-accent/90 px-1.5 text-[11px] tabular-nums text-white min-w-5 text-center">
          {badge}
        </span>
      )}
    </Link>
  );
}
