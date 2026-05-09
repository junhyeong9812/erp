import { getTranslations } from "next-intl/server";
import { Icon, type IconKey } from "@/components/icons";
import { SidebarNavItem } from "./sidebar-nav-item";

interface NavItemDef {
  key: string;
  href: string;
  labelKey: string;
  icon: IconKey;
}

interface NavSectionDef {
  labelKey: string;
  items: NavItemDef[];
}

const NAV: NavSectionDef[] = [
  {
    labelKey: "main",
    items: [
      { key: "dashboard", href: "/dashboard", labelKey: "dashboard", icon: "Dashboard" },
    ],
  },
  {
    labelKey: "sales",
    items: [
      { key: "quotes",   href: "/quotes",   labelKey: "quotes",   icon: "Doc" },
      { key: "orders",   href: "/orders",   labelKey: "orders",   icon: "Cart" },
      { key: "payments", href: "/payments", labelKey: "payments", icon: "Card" },
      { key: "crm",      href: "/crm",      labelKey: "crm",      icon: "Users" },
    ],
  },
  {
    labelKey: "logistics",
    items: [
      { key: "shipments",  href: "/shipments",  labelKey: "shipments",  icon: "Truck" },
      { key: "deliveries", href: "/deliveries", labelKey: "deliveries", icon: "Route" },
    ],
  },
  {
    labelKey: "supply",
    items: [
      { key: "stocks",     href: "/stocks",     labelKey: "stocks",     icon: "Box" },
      { key: "purchase",   href: "/purchase",   labelKey: "purchase",   icon: "DocCheck" },
      { key: "suppliers",  href: "/suppliers",  labelKey: "suppliers",  icon: "Building" },
      { key: "production", href: "/production", labelKey: "production", icon: "Factory" },
    ],
  },
  {
    labelKey: "finance",
    items: [
      { key: "settlement", href: "/settlement", labelKey: "settlement", icon: "Coin" },
    ],
  },
  {
    labelKey: "operations",
    items: [
      { key: "approvals",     href: "/approvals",     labelKey: "approvals",     icon: "Check" },
      { key: "notifications", href: "/notifications", labelKey: "notifications", icon: "Bell" },
      { key: "reports",       href: "/reports",       labelKey: "reports",       icon: "Chart" },
      { key: "hr",            href: "/hr",            labelKey: "hr",            icon: "User" },
    ],
  },
  {
    labelKey: "admin",
    items: [
      { key: "admin", href: "/admin", labelKey: "admin", icon: "Settings" },
    ],
  },
];

export async function Sidebar() {
  const tBrand = await getTranslations("Brand");
  const tNav = await getTranslations("Nav");
  const tUser = await getTranslations("User");

  return (
    <aside className="flex w-60 flex-col border-r border-border bg-panel">
      <div className="flex items-center gap-2.5 px-4 py-4">
        <div
          aria-hidden
          className="size-8 rounded-lg bg-accent shadow-[inset_0_-2px_0_rgba(0,0,0,0.08)]"
        />
        <div className="leading-tight">
          <div className="text-sm font-semibold text-text">{tBrand("name")}</div>
          <div className="text-[11px] text-text-3">{tBrand("subtitle")}</div>
        </div>
      </div>

      <nav className="flex-1 overflow-y-auto px-2 pb-4">
        {NAV.map((section) => (
          <div key={section.labelKey} className="mt-3">
            <div className="px-3 pb-1 text-[10.5px] font-semibold uppercase tracking-wider text-text-3">
              {tNav(section.labelKey)}
            </div>
            <div className="flex flex-col gap-0.5">
              {section.items.map((it) => {
                const IconCmp = Icon[it.icon];
                return (
                  <SidebarNavItem
                    key={it.key}
                    href={it.href}
                    icon={<IconCmp />}
                    label={tNav(it.labelKey)}
                  />
                );
              })}
            </div>
          </div>
        ))}
      </nav>

      <div className="border-t border-border px-3 pt-3">
        <a
          href="/seller/dashboard"
          className="flex items-center justify-center gap-1.5 rounded-md bg-info-soft px-3 py-2 text-[12px] font-medium text-info-ink transition-colors hover:bg-info-soft/80"
        >
          Seller →
        </a>
      </div>

      <div className="flex items-center gap-2.5 border-t border-border px-4 py-3">
        <div
          aria-hidden
          className="flex size-8 items-center justify-center rounded-full bg-accent-soft text-[11px] font-semibold text-accent-ink"
        >
          JS
        </div>
        <div className="leading-tight">
          <div className="text-[12.5px] font-medium text-text">
            {tUser("name")}
          </div>
          <div className="text-[11px] text-text-3">{tUser("role")}</div>
        </div>
      </div>
    </aside>
  );
}
