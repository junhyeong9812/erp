import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtKRW, fmtDateTime } from "@/lib/format";
import { PAYMENTS, REFUNDS } from "@/lib/mock";

export async function PaymentsView() {
  const t = await getTranslations("Payments");

  return (
    <>
      <div className="overflow-hidden rounded-lg border border-border bg-panel">
        <table className="w-full text-[13px]">
          <thead>
            <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
              <th className="px-4 py-2.5">{t("col.id")}</th>
              <th className="px-4 py-2.5">{t("col.order")}</th>
              <th className="px-4 py-2.5">{t("col.method")}</th>
              <th className="px-4 py-2.5 text-right">{t("col.amount")}</th>
              <th className="px-4 py-2.5">{t("col.processedAt")}</th>
              <th className="px-4 py-2.5">{t("col.pgRef")}</th>
              <th className="px-4 py-2.5">{t("col.status")}</th>
            </tr>
          </thead>
          <tbody>
            {PAYMENTS.map((p) => (
              <tr
                key={p.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  PAY-{p.id}
                </td>
                <td className="px-4 py-3 text-text-2">#{p.orderId}</td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      p.method === "CARD"
                        ? "info"
                        : p.method === "BANK"
                          ? "accent"
                          : "neutral"
                    }
                  >
                    {t(`method.${p.method}`)}
                  </Badge>
                </td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {fmtKRW(p.amount)}
                </td>
                <td className="px-4 py-3 text-text-3 tabular-nums">
                  {p.processedAt ? fmtDateTime(p.processedAt) : "—"}
                </td>
                <td className="px-4 py-3 font-mono text-[11.5px] text-text-3">
                  {p.pgReference ?? "—"}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      p.status === "COMPLETED"
                        ? "ok"
                        : p.status === "PENDING"
                          ? "warn"
                          : p.status === "FAILED"
                            ? "danger"
                            : "neutral"
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

      <section className="mt-6">
        <h2 className="mb-3 text-[13px] font-semibold text-text">
          {t("refunds")}
        </h2>
        <div className="overflow-hidden rounded-lg border border-border bg-panel">
          <table className="w-full text-[13px]">
            <thead>
              <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
                <th className="px-4 py-2.5">{t("refundCol.id")}</th>
                <th className="px-4 py-2.5">{t("refundCol.payment")}</th>
                <th className="px-4 py-2.5 text-right">
                  {t("refundCol.amount")}
                </th>
                <th className="px-4 py-2.5">{t("refundCol.reason")}</th>
                <th className="px-4 py-2.5">{t("refundCol.processedAt")}</th>
              </tr>
            </thead>
            <tbody>
              {REFUNDS.map((r) => (
                <tr
                  key={r.id}
                  className="border-b border-divider transition-colors hover:bg-row-hover"
                >
                  <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                    RFD-{r.id}
                  </td>
                  <td className="px-4 py-3 text-text-3">PAY-{r.paymentId}</td>
                  <td className="px-4 py-3 text-right font-medium tabular-nums text-danger">
                    −{fmtKRW(r.amount)}
                  </td>
                  <td className="px-4 py-3 text-text-2">{r.reason}</td>
                  <td className="px-4 py-3 text-text-3 tabular-nums">
                    {fmtDateTime(r.processedAt)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>
    </>
  );
}
