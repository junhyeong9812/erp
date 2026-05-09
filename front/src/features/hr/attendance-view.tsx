import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import {
  ATTENDANCES,
  lookupEmployee,
  lookupDepartment,
} from "@/lib/mock/hr";

export async function AttendanceView() {
  const t = await getTranslations("Hr.attendance");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.date")}</th>
            <th className="px-4 py-2.5">{t("col.employee")}</th>
            <th className="px-4 py-2.5">{t("col.department")}</th>
            <th className="px-4 py-2.5">{t("col.checkIn")}</th>
            <th className="px-4 py-2.5">{t("col.checkOut")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {ATTENDANCES.map((a) => {
            const emp = lookupEmployee(a.employeeId);
            const dept = lookupDepartment(emp.departmentId);
            return (
              <tr
                key={a.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {a.workDate}
                </td>
                <td className="px-4 py-3 font-medium text-text">{emp.name}</td>
                <td className="px-4 py-3 text-text-2">{dept.name}</td>
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {a.checkInTime ?? "—"}
                </td>
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {a.checkOutTime ?? "—"}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      a.status === "NORMAL"
                        ? "ok"
                        : a.status === "LATE" || a.status === "EARLY_LEAVE"
                          ? "warn"
                          : "danger"
                    }
                  >
                    {t(`status.${a.status}`)}
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
