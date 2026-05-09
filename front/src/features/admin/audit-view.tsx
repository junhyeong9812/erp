import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtDateTime } from "@/lib/format";
import { AUDIT_LOGS, lookupAppUser } from "@/lib/mock/auth";

export async function AuditView() {
  const t = await getTranslations("Admin.audit");

  const sorted = [...AUDIT_LOGS].sort((a, b) =>
    a.occurredAt < b.occurredAt ? 1 : -1
  );

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.occurredAt")}</th>
            <th className="px-4 py-2.5">{t("col.user")}</th>
            <th className="px-4 py-2.5">{t("col.action")}</th>
            <th className="px-4 py-2.5">{t("col.resource")}</th>
            <th className="px-4 py-2.5">{t("col.ip")}</th>
          </tr>
        </thead>
        <tbody>
          {sorted.map((a) => {
            const user = lookupAppUser(a.userId);
            const isSecuritySensitive =
              a.action === "USER_LOCK" ||
              a.action === "ROLE_ASSIGN" ||
              a.action === "PERIOD_CLOSE";
            return (
              <tr
                key={a.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {fmtDateTime(a.occurredAt)}
                </td>
                <td className="px-4 py-3 font-medium text-text">
                  {user.username}
                </td>
                <td className="px-4 py-3">
                  <Badge tone={isSecuritySensitive ? "warn" : "info"}>
                    {a.action}
                  </Badge>
                </td>
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  {a.resource}
                  {a.resourceId && (
                    <span className="text-text-3"> · {a.resourceId}</span>
                  )}
                </td>
                <td className="px-4 py-3 font-mono text-[11.5px] text-text-3">
                  {a.ip}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
