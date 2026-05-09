"use client";

import { useTranslations } from "next-intl";
import { usePathname } from "@/i18n/routing";
import { Icon } from "@/components/icons";
import { Button } from "@/components/ui/button";
import { ThemeToggle } from "./theme-toggle";
import { LanguageToggle } from "./language-toggle";

const PAGE_SECTION: Record<string, { sectionKey: string; pageKey: string }> = {
  "/seller/dashboard":  { sectionKey: "main",       pageKey: "dashboard" },
  "/seller/products":   { sectionKey: "products",   pageKey: "myProducts" },
  "/seller/orders":     { sectionKey: "products",   pageKey: "myOrders" },
  "/seller/shipments":  { sectionKey: "products",   pageKey: "myShipments" },
  "/seller/settlement": { sectionKey: "operations", pageKey: "mySettlement" },
};

export function SellerTopbar() {
  const pathname = usePathname();
  const tNav = useTranslations("SellerNav");
  const tBar = useTranslations("Topbar");

  const matched =
    PAGE_SECTION[pathname] ?? { sectionKey: "main", pageKey: "dashboard" };

  return (
    <div className="flex h-14 items-center gap-3 border-b border-border bg-panel/80 px-5 backdrop-blur">
      <div className="flex items-center gap-2 text-[13px] text-text-2">
        <span>{tNav(matched.sectionKey)}</span>
        <span className="text-text-3">/</span>
        <span className="font-medium text-text">
          {tNav(matched.pageKey)}
        </span>
      </div>

      <div className="ml-6 flex max-w-md flex-1 items-center gap-2 rounded-md border border-border bg-bg-elev px-3 py-1.5 text-text-3">
        <Icon.Search className="size-3.5" />
        <input
          placeholder={tBar("searchPlaceholder")}
          className="w-full bg-transparent text-[13px] text-text outline-none placeholder:text-text-3"
          aria-label={tBar("searchPlaceholder")}
        />
        <kbd className="rounded border border-border bg-panel px-1.5 py-0.5 text-[10.5px] text-text-3">
          ⌘K
        </kbd>
      </div>

      <div className="ml-auto flex items-center gap-1">
        <LanguageToggle />
        <ThemeToggle />
        <Button variant="ghost" size="icon" aria-label={tBar("openNotifications")}>
          <Icon.Bell className="size-4" />
        </Button>
      </div>
    </div>
  );
}
