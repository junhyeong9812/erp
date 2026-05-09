import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { CUSTOMERS } from "@/lib/mock";
import { POINTS } from "@/lib/mock/crm";
import { fmtN } from "@/lib/format";

export async function CustomersView() {
  const t = await getTranslations("Crm.customers");

  const pointSum = new Map<number, number>();
  for (const p of POINTS) {
    if (p.status !== "ACTIVE") continue;
    pointSum.set(p.customerId, (pointSum.get(p.customerId) ?? 0) + p.amount);
  }

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.code")}</th>
            <th className="px-4 py-2.5">{t("col.name")}</th>
            <th className="px-4 py-2.5">{t("col.grade")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.points")}</th>
          </tr>
        </thead>
        <tbody>
          {CUSTOMERS.map((c) => {
            const sumPts = pointSum.get(c.id) ?? 0;
            return (
              <tr
                key={c.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  {c.code}
                </td>
                <td className="px-4 py-3 font-medium text-text">{c.name}</td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      c.grade === "VIP"
                        ? "accent"
                        : c.grade === "GOLD"
                          ? "warn"
                          : c.grade === "SILVER"
                            ? "info"
                            : "neutral"
                    }
                  >
                    {c.grade}
                  </Badge>
                </td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {fmtN(sumPts)} P
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
