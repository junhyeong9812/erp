import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtKRW, fmtDate } from "@/lib/format";
import { QUOTES, lookupCustomer, lookupProduct } from "@/lib/mock";

export async function QuotesView() {
  const t = await getTranslations("Quotes");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.id")}</th>
            <th className="px-4 py-2.5">{t("col.customer")}</th>
            <th className="px-4 py-2.5">{t("col.items")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.total")}</th>
            <th className="px-4 py-2.5">{t("col.validUntil")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {QUOTES.map((q) => {
            const cust = lookupCustomer(q.customerId);
            const firstLine = q.lines[0];
            const firstName = firstLine
              ? lookupProduct(firstLine.productId).name
              : "—";
            const moreCount = q.lines.length - 1;
            return (
              <tr
                key={q.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  Q-{q.id}
                </td>
                <td className="px-4 py-3 font-medium text-text">
                  {cust.name}
                </td>
                <td className="px-4 py-3 text-text-2">
                  {firstName}
                  {moreCount > 0 && (
                    <span className="ml-1 text-text-3">
                      외 {moreCount}품목
                    </span>
                  )}
                </td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {fmtKRW(q.total)}
                </td>
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {fmtDate(q.validUntil)}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      q.status === "ACCEPTED"
                        ? "ok"
                        : q.status === "ACTIVE"
                          ? "info"
                          : q.status === "EXPIRED"
                            ? "neutral"
                            : "danger"
                    }
                  >
                    {t(`status.${q.status}`)}
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
