import { getTranslations } from "next-intl/server";
import { fmtKRW } from "@/lib/format";
import { AGING_SNAPSHOTS } from "@/lib/mock/settlement";
import { lookupCustomer } from "@/lib/mock";

export async function AgingView() {
  const t = await getTranslations("Settlement.aging");

  const totals = AGING_SNAPSHOTS.reduce(
    (acc, s) => ({
      total: acc.total + s.invoiceTotal,
      b0: acc.b0 + s.bucket0_30,
      b1: acc.b1 + s.bucket31_60,
      b2: acc.b2 + s.bucket61_90,
      b3: acc.b3 + s.bucket90Plus,
    }),
    { total: 0, b0: 0, b1: 0, b2: 0, b3: 0 }
  );

  return (
    <>
      <div className="mb-4 grid grid-cols-2 gap-3 sm:grid-cols-4">
        <div className="rounded-lg border border-border bg-panel p-3">
          <div className="text-[11px] text-text-3">{t("kpi.b0")}</div>
          <div className="mt-1 text-[18px] font-semibold tabular-nums text-text">
            {fmtKRW(totals.b0)}
          </div>
        </div>
        <div className="rounded-lg border border-border bg-panel p-3">
          <div className="text-[11px] text-warn-ink">{t("kpi.b1")}</div>
          <div className="mt-1 text-[18px] font-semibold tabular-nums text-warn">
            {fmtKRW(totals.b1)}
          </div>
        </div>
        <div className="rounded-lg border border-border bg-panel p-3">
          <div className="text-[11px] text-warn-ink">{t("kpi.b2")}</div>
          <div className="mt-1 text-[18px] font-semibold tabular-nums text-warn">
            {fmtKRW(totals.b2)}
          </div>
        </div>
        <div className="rounded-lg border border-danger-soft bg-danger-soft/30 p-3">
          <div className="text-[11px] text-danger-ink">{t("kpi.b3")}</div>
          <div className="mt-1 text-[18px] font-semibold tabular-nums text-danger">
            {fmtKRW(totals.b3)}
          </div>
        </div>
      </div>

      <div className="overflow-hidden rounded-lg border border-border bg-panel">
        <table className="w-full text-[13px]">
          <thead>
            <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
              <th className="px-4 py-2.5">{t("col.customer")}</th>
              <th className="px-4 py-2.5 text-right">{t("col.total")}</th>
              <th className="px-4 py-2.5 text-right">{t("col.b0")}</th>
              <th className="px-4 py-2.5 text-right">{t("col.b1")}</th>
              <th className="px-4 py-2.5 text-right">{t("col.b2")}</th>
              <th className="px-4 py-2.5 text-right">{t("col.b3")}</th>
              <th className="px-4 py-2.5">{t("col.asOf")}</th>
            </tr>
          </thead>
          <tbody>
            {AGING_SNAPSHOTS.map((a) => {
              const cust = lookupCustomer(a.customerId);
              return (
                <tr
                  key={a.id}
                  className="border-b border-divider transition-colors hover:bg-row-hover"
                >
                  <td className="px-4 py-3 font-medium text-text">
                    {cust.name}
                  </td>
                  <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                    {fmtKRW(a.invoiceTotal)}
                  </td>
                  <td className="px-4 py-3 text-right tabular-nums text-text-2">
                    {a.bucket0_30 > 0 ? fmtKRW(a.bucket0_30) : "—"}
                  </td>
                  <td className="px-4 py-3 text-right tabular-nums text-warn">
                    {a.bucket31_60 > 0 ? fmtKRW(a.bucket31_60) : "—"}
                  </td>
                  <td className="px-4 py-3 text-right tabular-nums text-warn">
                    {a.bucket61_90 > 0 ? fmtKRW(a.bucket61_90) : "—"}
                  </td>
                  <td className="px-4 py-3 text-right tabular-nums text-danger">
                    {a.bucket90Plus > 0 ? fmtKRW(a.bucket90Plus) : "—"}
                  </td>
                  <td className="px-4 py-3 text-text-3 tabular-nums">
                    {a.asOf}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </>
  );
}
