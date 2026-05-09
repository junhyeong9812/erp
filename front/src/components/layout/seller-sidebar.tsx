import { getTranslations } from "next-intl/server";
import { Icon, type IconKey } from "@/components/icons";
import { lookupSeller } from "@/lib/mock";
import { DEFAULT_SELLER_ID } from "@/lib/mock";
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
      {
        key: "dashboard",
        href: "/seller/dashboard",
        labelKey: "dashboard",
        icon: "Dashboard",
      },
    ],
  },
  {
    labelKey: "products",
    items: [
      {
        key: "products",
        href: "/seller/products",
        labelKey: "myProducts",
        icon: "Box",
      },
      {
        key: "orders",
        href: "/seller/orders",
        labelKey: "myOrders",
        icon: "Cart",
      },
      {
        key: "shipments",
        href: "/seller/shipments",
        labelKey: "myShipments",
        icon: "Truck",
      },
    ],
  },
  {
    labelKey: "operations",
    items: [
      {
        key: "settlement",
        href: "/seller/settlement",
        labelKey: "mySettlement",
        icon: "Coin",
      },
    ],
  },
];

export async function SellerSidebar() {
  const tBrand = await getTranslations("Brand");
  const tNav = await getTranslations("SellerNav");
  const tSeller = await getTranslations("Seller");

  const seller = lookupSeller(DEFAULT_SELLER_ID);

  return (
    <aside className="flex w-60 flex-col border-r border-border bg-panel">
      <div className="flex items-center gap-2.5 px-4 py-4">
        <div
          aria-hidden
          className="size-8 rounded-lg bg-info shadow-[inset_0_-2px_0_rgba(0,0,0,0.08)]"
        />
        <div className="leading-tight">
          <div className="text-sm font-semibold text-text">
            {tBrand("name")}
          </div>
          <div className="text-[11px] text-info-ink">
            {tSeller("modeBadge")}
          </div>
        </div>
      </div>

      <div className="mx-3 mb-2 rounded-md border border-info-soft bg-info-soft px-3 py-2">
        <div className="text-[10.5px] font-semibold uppercase tracking-wider text-info-ink">
          {tSeller("switcher")}
        </div>
        <div className="mt-0.5 text-[12.5px] font-medium text-info-ink">
          {seller.name}
        </div>
        <div className="text-[11px] text-info-ink/70">
          {seller.code} · {seller.tier}
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

      <div className="border-t border-border p-3">
        <a
          href="/dashboard"
          className="flex items-center justify-center gap-1.5 rounded-md bg-hover px-3 py-2 text-[12px] font-medium text-text-2 transition-colors hover:bg-accent-soft hover:text-accent-ink"
        >
          ← Admin
        </a>
      </div>
    </aside>
  );
}
