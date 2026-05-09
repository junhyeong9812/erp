import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtN } from "@/lib/format";
import { WAREHOUSES, initialStocks } from "@/lib/mock";

export async function WarehousesView() {
  const t = await getTranslations("Stocks.warehouses");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.name")}</th>
            <th className="px-4 py-2.5">{t("col.location")}</th>
            <th className="px-4 py-2.5">{t("col.type")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.skuCount")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.totalUnits")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.reservedUnits")}</th>
          </tr>
        </thead>
        <tbody>
          {WAREHOUSES.map((w) => {
            const wstocks = initialStocks.filter(
              (s) => s.warehouseId === w.id
            );
            const skuCount = wstocks.length;
            const totalUnits = wstocks.reduce((sum, s) => sum + s.total, 0);
            const reservedUnits = wstocks.reduce(
              (sum, s) => sum + s.reserved,
              0
            );
            return (
              <tr
                key={w.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-medium text-text">{w.name}</td>
                <td className="px-4 py-3 text-text-2">{w.location}</td>
                <td className="px-4 py-3">
                  <Badge tone={w.type === "MAIN" ? "accent" : "neutral"}>
                    {t(`type.${w.type}`)}
                  </Badge>
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-text-2">
                  {fmtN(skuCount)}
                </td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {fmtN(totalUnits)}
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-warn">
                  {fmtN(reservedUnits)}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
