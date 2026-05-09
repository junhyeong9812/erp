import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtKRW } from "@/lib/format";
import { PRODUCTS, lookupSeller } from "@/lib/mock";

export async function ProductsView() {
  const t = await getTranslations("Stocks.products");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.sku")}</th>
            <th className="px-4 py-2.5">{t("col.name")}</th>
            <th className="px-4 py-2.5">{t("col.seller")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.price")}</th>
          </tr>
        </thead>
        <tbody>
          {PRODUCTS.map((p) => {
            const seller = lookupSeller(p.sellerId);
            return (
              <tr
                key={p.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  {p.sku}
                </td>
                <td className="px-4 py-3 font-medium text-text">{p.name}</td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      seller.tier === "ENTERPRISE"
                        ? "accent"
                        : seller.tier === "PREMIUM"
                          ? "info"
                          : "neutral"
                    }
                  >
                    {seller.name}
                  </Badge>
                </td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {fmtKRW(p.price)}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
