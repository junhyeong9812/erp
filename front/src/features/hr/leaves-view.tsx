import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtDateTime } from "@/lib/format";
import { LEAVE_REQUESTS, lookupEmployee } from "@/lib/mock/hr";

export async function LeavesView() {
  const t = await getTranslations("Hr.leaves");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.employee")}</th>
            <th className="px-4 py-2.5">{t("col.period")}</th>
            <th className="px-4 py-2.5">{t("col.reason")}</th>
            <th className="px-4 py-2.5">{t("col.requestedAt")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {LEAVE_REQUESTS.map((l) => {
            const emp = lookupEmployee(l.employeeId);
            return (
              <tr
                key={l.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-medium text-text">{emp.name}</td>
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {l.startDate} → {l.endDate}
                </td>
                <td className="px-4 py-3 text-text-2">{l.reason}</td>
                <td className="px-4 py-3 text-text-3 tabular-nums">
                  {fmtDateTime(l.requestedAt)}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      l.status === "APPROVED"
                        ? "ok"
                        : l.status === "PENDING"
                          ? "warn"
                          : "danger"
                    }
                  >
                    {t(`status.${l.status}`)}
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
