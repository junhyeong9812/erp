import { getTranslations } from "next-intl/server";
import { ROLES } from "@/lib/mock/auth";

export async function RolesView() {
  const t = await getTranslations("Admin.roles");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.code")}</th>
            <th className="px-4 py-2.5">{t("col.name")}</th>
            <th className="px-4 py-2.5">{t("col.description")}</th>
            <th className="px-4 py-2.5">{t("col.permissions")}</th>
          </tr>
        </thead>
        <tbody>
          {ROLES.map((r) => (
            <tr
              key={r.code}
              className="border-b border-divider transition-colors hover:bg-row-hover"
            >
              <td className="px-4 py-3 font-mono text-[12px] font-medium text-text">
                {r.code}
              </td>
              <td className="px-4 py-3 text-text-2">{r.name}</td>
              <td className="px-4 py-3 text-text-3">{r.description}</td>
              <td className="px-4 py-3">
                <div className="flex flex-wrap gap-1">
                  {r.permissionCodes.map((p) => (
                    <span
                      key={p}
                      className="inline-flex items-center rounded bg-accent-soft px-1.5 py-0.5 font-mono text-[10.5px] text-accent-ink"
                    >
                      {p}
                    </span>
                  ))}
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
