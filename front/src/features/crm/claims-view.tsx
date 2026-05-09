import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { CLAIMS } from "@/lib/mock/crm";
import { lookupCustomer } from "@/lib/mock";
import { fmtDateTime } from "@/lib/format";

export async function ClaimsView() {
  const t = await getTranslations("Crm.claims");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.filedAt")}</th>
            <th className="px-4 py-2.5">{t("col.customer")}</th>
            <th className="px-4 py-2.5">{t("col.type")}</th>
            <th className="px-4 py-2.5">{t("col.description")}</th>
            <th className="px-4 py-2.5">{t("col.resolvedAt")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {CLAIMS.map((c) => {
            const cust = lookupCustomer(c.customerId);
            return (
              <tr
                key={c.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {fmtDateTime(c.filedAt)}
                </td>
                <td className="px-4 py-3 font-medium text-text">
                  {cust.name}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      c.type === "REFUND"
                        ? "warn"
                        : c.type === "PRODUCT"
                          ? "info"
                          : c.type === "SHIPPING"
                            ? "danger"
                            : "neutral"
                    }
                  >
                    {t(`type.${c.type}`)}
                  </Badge>
                </td>
                <td className="px-4 py-3 text-text-2">{c.description}</td>
                <td className="px-4 py-3 text-text-3 tabular-nums">
                  {c.resolvedAt ? fmtDateTime(c.resolvedAt) : "—"}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      c.status === "OPEN"
                        ? "danger"
                        : c.status === "IN_PROGRESS"
                          ? "warn"
                          : c.status === "RESOLVED"
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
