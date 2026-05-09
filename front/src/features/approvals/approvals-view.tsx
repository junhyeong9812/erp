"use client";

import { useTranslations } from "next-intl";
import { Badge } from "@/components/ui/badge";
import { fmtKRW, fmtDateTime } from "@/lib/format";
import { lookupEmployee } from "@/lib/mock/hr";
import { useErpStore } from "@/store/erp-store";

export function ApprovalsView() {
  const t = useTranslations("Approvals");
  const approvals = useErpStore((s) => s.approvals);

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.id")}</th>
            <th className="px-4 py-2.5">{t("col.title")}</th>
            <th className="px-4 py-2.5">{t("col.type")}</th>
            <th className="px-4 py-2.5">{t("col.drafter")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.amount")}</th>
            <th className="px-4 py-2.5">{t("col.progress")}</th>
            <th className="px-4 py-2.5">{t("col.draftedAt")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {approvals.map((a) => {
            const drafter = lookupEmployee(a.drafterId);
            return (
              <tr
                key={a.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  AP-{a.id}
                </td>
                <td className="px-4 py-3 font-medium text-text">{a.title}</td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      a.documentType === "EXPENSE"
                        ? "warn"
                        : a.documentType === "PROCUREMENT"
                          ? "info"
                          : a.documentType === "LEAVE"
                            ? "neutral"
                            : "neutral"
                    }
                  >
                    {t(`type.${a.documentType}`)}
                  </Badge>
                </td>
                <td className="px-4 py-3 text-text-2">{drafter.name}</td>
                <td className="px-4 py-3 text-right tabular-nums text-text">
                  {a.amount != null ? fmtKRW(a.amount) : "—"}
                </td>
                <td className="px-4 py-3 text-text-3 tabular-nums">
                  {a.currentStep}/{a.totalSteps}
                </td>
                <td className="px-4 py-3 text-text-3 tabular-nums">
                  {fmtDateTime(a.draftedAt)}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      a.status === "APPROVED"
                        ? "ok"
                        : a.status === "IN_PROGRESS"
                          ? "warn"
                          : a.status === "REJECTED"
                            ? "danger"
                            : "neutral"
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
