import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtKRW } from "@/lib/format";
import { PAYROLLS, lookupEmployee } from "@/lib/mock/hr";

export async function PayrollView() {
  const t = await getTranslations("Hr.payroll");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.period")}</th>
            <th className="px-4 py-2.5">{t("col.employee")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.baseSalary")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.allowance")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.insurance")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.netSalary")}</th>
            <th className="px-4 py-2.5">{t("col.paid")}</th>
          </tr>
        </thead>
        <tbody>
          {PAYROLLS.map((p) => {
            const emp = lookupEmployee(p.employeeId);
            return (
              <tr
                key={p.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {p.year}-{String(p.month).padStart(2, "0")}
                </td>
                <td className="px-4 py-3 font-medium text-text">{emp.name}</td>
                <td className="px-4 py-3 text-right tabular-nums text-text-2">
                  {fmtKRW(p.baseSalary)}
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-text-2">
                  {fmtKRW(p.allowance)}
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-text-3">
                  −{fmtKRW(p.insurance)}
                </td>
                <td className="px-4 py-3 text-right font-medium tabular-nums text-text">
                  {fmtKRW(p.netSalary)}
                </td>
                <td className="px-4 py-3">
                  <Badge tone={p.paidAt ? "ok" : "warn"}>
                    {p.paidAt ? t("paid") : t("unpaid")}
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
