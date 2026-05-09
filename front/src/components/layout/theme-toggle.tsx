"use client";

import { useTheme } from "next-themes";
import { useEffect, useState } from "react";
import { useTranslations } from "next-intl";
import { Icon } from "@/components/icons";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

export function ThemeToggle() {
  const { theme, setTheme, resolvedTheme } = useTheme();
  const t = useTranslations("Topbar");
  const [mounted, setMounted] = useState(false);

  useEffect(() => setMounted(true), []);

  if (!mounted) {
    return (
      <div
        aria-hidden
        className="size-8 rounded-md bg-hover/50"
      />
    );
  }

  const order = ["light", "dark", "system"] as const;
  type Mode = (typeof order)[number];
  const next: Mode =
    order[(order.indexOf((theme as Mode) ?? "system") + 1) % order.length] ??
    "system";

  const labelMap: Record<Mode, string> = {
    light: t("themeLight"),
    dark: t("themeDark"),
    system: t("themeSystem"),
  };

  const ActiveIcon =
    theme === "system"
      ? Icon.System
      : resolvedTheme === "dark"
        ? Icon.Moon
        : Icon.Sun;

  return (
    <Button
      variant="ghost"
      size="icon"
      onClick={() => setTheme(next)}
      title={labelMap[(theme as Mode) ?? "system"]}
      aria-label={labelMap[(theme as Mode) ?? "system"]}
    >
      <ActiveIcon
        className={cn("size-4", theme === "system" && "text-text-3")}
      />
    </Button>
  );
}
