import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtDateTime } from "@/lib/format";
import { SETTLEMENT_PERIODS } from "@/lib/mock/settlement";

export async function PeriodsView() {
  const t = await getTranslations("Settlement.periods");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.id")}</th>
            <th className="px-4 py-2.5">{t("col.range")}</th>
            <th className="px-4 py-2.5">{t("col.closedAt")}</th>
            <th className="px-4 py-2.5">{t("col.settledAt")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {SETTLEMENT_PERIODS.map((p) => (
            <tr
              key={p.id}
              className="border-b border-divider transition-colors hover:bg-row-hover"
            >
              <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                P-{p.id}
              </td>
              <td className="px-4 py-3 text-text tabular-nums">
                {p.startDate} → {p.endDate}
              </td>
              <td className="px-4 py-3 text-text-2 tabular-nums">
                {p.closedAt ? fmtDateTime(p.closedAt) : "—"}
              </td>
              <td className="px-4 py-3 text-text-2 tabular-nums">
                {p.settledAt ? fmtDateTime(p.settledAt) : "—"}
              </td>
              <td className="px-4 py-3">
                <Badge
                  tone={
                    p.status === "OPEN"
                      ? "info"
                      : p.status === "CLOSED"
                        ? "warn"
                        : "ok"
                  }
                >
                  {t(`status.${p.status}`)}
                </Badge>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
