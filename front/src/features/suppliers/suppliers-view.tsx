import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { SUPPLIERS } from "@/lib/mock";

export async function SuppliersView() {
  const t = await getTranslations("Suppliers");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.code")}</th>
            <th className="px-4 py-2.5">{t("col.name")}</th>
            <th className="px-4 py-2.5">{t("col.category")}</th>
            <th className="px-4 py-2.5">{t("col.contact")}</th>
            <th className="px-4 py-2.5">{t("col.email")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.rating")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {SUPPLIERS.map((s) => (
            <tr
              key={s.id}
              className="border-b border-divider transition-colors hover:bg-row-hover"
            >
              <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                {s.code}
              </td>
              <td className="px-4 py-3 font-medium text-text">{s.name}</td>
              <td className="px-4 py-3">
                <Badge tone="info">{s.category}</Badge>
              </td>
              <td className="px-4 py-3 font-mono text-[11.5px] text-text-3">
                {s.contact}
              </td>
              <td className="px-4 py-3 text-text-3">{s.email}</td>
              <td className="px-4 py-3 text-right">
                <span className="font-medium tabular-nums text-text">
                  ★ {s.rating.toFixed(1)}
                </span>
              </td>
              <td className="px-4 py-3">
                <Badge tone={s.status === "ACTIVE" ? "ok" : "neutral"}>
                  {t(`status.${s.status}`)}
                </Badge>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
