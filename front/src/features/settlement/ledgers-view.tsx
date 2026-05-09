import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtKRW, fmtDateTime } from "@/lib/format";
import { LEDGERS } from "@/lib/mock/settlement";

export async function LedgersView() {
  const t = await getTranslations("Settlement.ledgers");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.occurredAt")}</th>
            <th className="px-4 py-2.5">{t("col.period")}</th>
            <th className="px-4 py-2.5">{t("col.type")}</th>
            <th className="px-4 py-2.5">{t("col.description")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.debit")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.credit")}</th>
            <th className="px-4 py-2.5">{t("col.reversed")}</th>
          </tr>
        </thead>
        <tbody>
          {LEDGERS.map((l) => (
            <tr
              key={l.id}
              className="border-b border-divider transition-colors hover:bg-row-hover"
            >
              <td className="px-4 py-3 text-text-2 tabular-nums">
                {fmtDateTime(l.occurredAt)}
              </td>
              <td className="px-4 py-3 text-text-3">P-{l.periodId}</td>
              <td className="px-4 py-3">
                <Badge
                  tone={
                    l.type === "SALES"
                      ? "ok"
                      : l.type === "REFUND"
                        ? "danger"
                        : l.type === "FEE"
                          ? "warn"
                          : l.type === "REVERSAL"
                            ? "neutral"
                            : "info"
                  }
                >
                  {t(`type.${l.type}`)}
                </Badge>
              </td>
              <td className="px-4 py-3 text-text-2">{l.description}</td>
              <td className="px-4 py-3 text-right tabular-nums text-danger">
                {l.debit > 0 ? fmtKRW(l.debit) : "—"}
              </td>
              <td className="px-4 py-3 text-right tabular-nums text-ok">
                {l.credit > 0 ? fmtKRW(l.credit) : "—"}
              </td>
              <td className="px-4 py-3 text-text-3">
                {l.reversedBy ? `→ #${l.reversedBy}` : "—"}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
