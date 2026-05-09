import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtKRW, fmtN, fmtDateTime } from "@/lib/format";
import { REPORTS } from "@/lib/mock";

const KRW_KEYS = [
  "total_payment",
  "refund_amount",
  "gross_profit",
  "revenue",
  "cogs",
  "operating_cost",
  "net_income",
  "bucket_0_30",
  "bucket_31_60",
  "bucket_61_90",
  "bucket_90_plus",
];

export async function ReportsView() {
  const t = await getTranslations("Reports");

  return (
    <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
      {REPORTS.map((r) => {
        const entries = Object.entries(r.metrics);
        return (
          <article
            key={r.id}
            className="rounded-lg border border-border bg-panel p-5"
          >
            <header className="mb-3 flex items-start justify-between">
              <div>
                <div className="font-mono text-[11px] text-text-3">
                  RPT-{r.id}
                </div>
                <h3 className="mt-0.5 text-[14px] font-semibold text-text">
                  {t(`type.${r.reportType}`)}
                </h3>
                <div className="mt-0.5 text-[12px] text-text-3 tabular-nums">
                  {r.targetDate}
                </div>
              </div>
              <Badge tone="info">{r.reportType}</Badge>
            </header>

            <dl className="grid grid-cols-2 gap-x-4 gap-y-2 text-[12.5px]">
              {entries.map(([k, v]) => (
                <div key={k} className="flex flex-col">
                  <dt className="text-[10.5px] uppercase tracking-wider text-text-3">
                    {k}
                  </dt>
                  <dd className="mt-0.5 font-medium tabular-nums text-text">
                    {KRW_KEYS.includes(k) ? fmtKRW(v) : fmtN(v)}
                  </dd>
                </div>
              ))}
            </dl>

            <footer className="mt-4 flex items-center justify-between border-t border-divider pt-3 text-[10.5px] text-text-3">
              <span>{t("generatedAt")}</span>
              <span className="tabular-nums">
                {fmtDateTime(r.generatedAt)}
              </span>
            </footer>
          </article>
        );
      })}
    </div>
  );
}
