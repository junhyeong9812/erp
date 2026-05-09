"use client";

import { useLocale, useTranslations } from "next-intl";
import { useRouter, usePathname } from "@/i18n/routing";
import { Button } from "@/components/ui/button";
import { Icon } from "@/components/icons";
import type { AppLocale } from "@/i18n/routing";

export function LanguageToggle() {
  const locale = useLocale() as AppLocale;
  const router = useRouter();
  const pathname = usePathname();
  const t = useTranslations("Topbar");

  const next: AppLocale = locale === "ko" ? "en" : "ko";

  return (
    <Button
      variant="ghost"
      size="sm"
      onClick={() => router.replace(pathname, { locale: next })}
      title={t("languageToggle")}
      aria-label={t("languageToggle")}
    >
      <Icon.Globe className="size-3.5" />
      <span className="text-[11px] font-semibold tracking-wide">
        {locale.toUpperCase()}
      </span>
    </Button>
  );
}
