import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtDateTime } from "@/lib/format";
import { APP_USERS } from "@/lib/mock/auth";
import { lookupEmployee } from "@/lib/mock/hr";

export async function UsersView() {
  const t = await getTranslations("Admin.users");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.id")}</th>
            <th className="px-4 py-2.5">{t("col.username")}</th>
            <th className="px-4 py-2.5">{t("col.employee")}</th>
            <th className="px-4 py-2.5">{t("col.roles")}</th>
            <th className="px-4 py-2.5">{t("col.lastLoginAt")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {APP_USERS.map((u) => {
            const emp = u.employeeId ? lookupEmployee(u.employeeId) : null;
            return (
              <tr
                key={u.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  U-{u.id}
                </td>
                <td className="px-4 py-3 font-medium text-text">
                  {u.username}
                </td>
                <td className="px-4 py-3 text-text-2">
                  {emp ? `${emp.name} (${emp.employeeNumber})` : "—"}
                </td>
                <td className="px-4 py-3">
                  <div className="flex flex-wrap gap-1">
                    {u.roleCodes.map((r) => (
                      <span
                        key={r}
                        className="inline-flex items-center rounded border border-border bg-bg-elev px-1.5 py-0.5 font-mono text-[10.5px] text-text-2"
                      >
                        {r}
                      </span>
                    ))}
                  </div>
                </td>
                <td className="px-4 py-3 text-text-3 tabular-nums">
                  {u.lastLoginAt ? fmtDateTime(u.lastLoginAt) : "—"}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      u.status === "ACTIVE"
                        ? "ok"
                        : u.status === "LOCKED"
                          ? "warn"
                          : "neutral"
                    }
                  >
                    {t(`status.${u.status}`)}
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
