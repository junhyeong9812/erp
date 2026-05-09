import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { COUPONS } from "@/lib/mock/crm";
import { lookupCustomer } from "@/lib/mock";
import { fmtKRW } from "@/lib/format";

export async function CouponsView() {
  const t = await getTranslations("Crm.coupons");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.code")}</th>
            <th className="px-4 py-2.5">{t("col.target")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.discount")}</th>
            <th className="px-4 py-2.5">{t("col.expireOn")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {COUPONS.map((c) => {
            const target = c.customerId
              ? lookupCustomer(c.customerId).name
              : t("publicTarget");
            const discount =
              c.discountRate != null
                ? `${c.discountRate}%`
                : c.discountAmount != null
                  ? fmtKRW(c.discountAmount)
                  : "—";
            return (
              <tr
                key={c.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-mono text-[12px] font-medium text-text">
                  {c.code}
                </td>
                <td className="px-4 py-3 text-text-2">{target}</td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {discount}
                </td>
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {c.expireOn}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      c.status === "ISSUED"
                        ? "info"
                        : c.status === "USED"
                          ? "ok"
                          : "neutral"
                    }
                  >
                    {t(`status.${c.status}`)}
                  </Badge>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
