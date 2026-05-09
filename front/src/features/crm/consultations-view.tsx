import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { CONSULTATIONS } from "@/lib/mock/crm";
import { lookupCustomer } from "@/lib/mock";
import { fmtDateTime } from "@/lib/format";

export async function ConsultationsView() {
  const t = await getTranslations("Crm.consultations");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.occurredAt")}</th>
            <th className="px-4 py-2.5">{t("col.customer")}</th>
            <th className="px-4 py-2.5">{t("col.channel")}</th>
            <th className="px-4 py-2.5">{t("col.summary")}</th>
            <th className="px-4 py-2.5">{t("col.agent")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {CONSULTATIONS.map((c) => {
            const cust = lookupCustomer(c.customerId);
            return (
              <tr
                key={c.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {fmtDateTime(c.occurredAt)}
                </td>
                <td className="px-4 py-3 font-medium text-text">
                  {cust.name}
                </td>
                <td className="px-4 py-3">
                  <Badge tone="info">{t(`channel.${c.channel}`)}</Badge>
                </td>
                <td className="px-4 py-3 text-text-2">{c.summary}</td>
                <td className="px-4 py-3 text-text-3">{c.agentName}</td>
                <td className="px-4 py-3">
                  <Badge tone={c.status === "OPEN" ? "warn" : "ok"}>
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
