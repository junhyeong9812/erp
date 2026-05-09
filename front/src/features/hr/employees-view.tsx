import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtKRW } from "@/lib/format";
import { EMPLOYEES, lookupDepartment } from "@/lib/mock/hr";

export async function EmployeesView() {
  const t = await getTranslations("Hr.employees");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.no")}</th>
            <th className="px-4 py-2.5">{t("col.name")}</th>
            <th className="px-4 py-2.5">{t("col.department")}</th>
            <th className="px-4 py-2.5">{t("col.hiredAt")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.baseSalary")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {EMPLOYEES.map((e) => {
            const dept = lookupDepartment(e.departmentId);
            return (
              <tr
                key={e.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  {e.employeeNumber}
                </td>
                <td className="px-4 py-3 font-medium text-text">{e.name}</td>
                <td className="px-4 py-3 text-text-2">{dept.name}</td>
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {e.hiredAt}
                </td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {fmtKRW(e.baseSalary)}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      e.status === "ACTIVE"
                        ? "ok"
                        : e.status === "ON_LEAVE"
                          ? "warn"
                          : "neutral"
                    }
                  >
                    {t(`status.${e.status}`)}
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
