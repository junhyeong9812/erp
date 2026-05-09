import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { POINTS } from "@/lib/mock/crm";
import { lookupCustomer } from "@/lib/mock";
import { fmtN, fmtDateTime } from "@/lib/format";

export async function PointsView() {
  const t = await getTranslations("Crm.points");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.earnedAt")}</th>
            <th className="px-4 py-2.5">{t("col.customer")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.amount")}</th>
            <th className="px-4 py-2.5">{t("col.source")}</th>
            <th className="px-4 py-2.5">{t("col.expireOn")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {POINTS.map((p) => {
            const cust = lookupCustomer(p.customerId);
            return (
              <tr
                key={p.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {fmtDateTime(p.earnedAt)}
                </td>
                <td className="px-4 py-3 font-medium text-text">
                  {cust.name}
                </td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {fmtN(p.amount)} P
                </td>
                <td className="px-4 py-3 text-text-3">{p.source}</td>
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {p.expireOn}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      p.status === "ACTIVE"
                        ? "ok"
                        : p.status === "USED"
                          ? "info"
                          : "neutral"
                    }
                  >
                    {t(`status.${p.status}`)}
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
