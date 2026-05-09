import { getTranslations } from "next-intl/server";
import { PERMISSIONS, ROLES } from "@/lib/mock/auth";

export async function PermissionsView() {
  const t = await getTranslations("Admin.permissions");

  const roleMap = new Map<string, string[]>();
  for (const r of ROLES) {
    for (const pCode of r.permissionCodes) {
      const list = roleMap.get(pCode) ?? [];
      list.push(r.code);
      roleMap.set(pCode, list);
    }
  }

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.code")}</th>
            <th className="px-4 py-2.5">{t("col.name")}</th>
            <th className="px-4 py-2.5">{t("col.description")}</th>
            <th className="px-4 py-2.5">{t("col.assignedRoles")}</th>
          </tr>
        </thead>
        <tbody>
          {PERMISSIONS.map((p) => {
            const roles = roleMap.get(p.code) ?? [];
            return (
              <tr
                key={p.code}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-mono text-[12px] font-medium text-text">
                  {p.code}
                </td>
                <td className="px-4 py-3 text-text">{p.name}</td>
                <td className="px-4 py-3 text-text-3">{p.description}</td>
                <td className="px-4 py-3">
                  <div className="flex flex-wrap gap-1">
                    {roles.map((r) => (
                      <span
                        key={r}
                        className="inline-flex items-center rounded border border-border bg-bg-elev px-1.5 py-0.5 font-mono text-[10.5px] text-text-2"
                      >
                        {r}
                      </span>
                    ))}
                    {roles.length === 0 && (
                      <span className="text-[11px] text-text-3">
                        {t("noRoles")}
                      </span>
                    )}
                  </div>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
