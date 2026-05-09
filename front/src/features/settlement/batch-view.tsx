import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtN, fmtDateTime } from "@/lib/format";
import { BATCH_LOGS } from "@/lib/mock/settlement";

export async function BatchView() {
  const t = await getTranslations("Settlement.batch");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.id")}</th>
            <th className="px-4 py-2.5">{t("col.jobName")}</th>
            <th className="px-4 py-2.5">{t("col.parameters")}</th>
            <th className="px-4 py-2.5">{t("col.startedAt")}</th>
            <th className="px-4 py-2.5">{t("col.duration")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.read")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.write")}</th>
            <th className="px-4 py-2.5 text-right">{t("col.skip")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {BATCH_LOGS.map((b) => {
            const duration =
              b.endedAt
                ? Math.round(
                    (new Date(b.endedAt).getTime() -
                      new Date(b.startedAt).getTime()) /
                      1000
                  )
                : null;
            return (
              <tr
                key={b.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 font-mono text-[12px] text-text-2">
                  J-{b.id}
                </td>
                <td className="px-4 py-3 font-medium text-text">{b.jobName}</td>
                <td className="px-4 py-3 font-mono text-[11.5px] text-text-3">
                  {b.parameters}
                </td>
                <td className="px-4 py-3 text-text-2 tabular-nums">
                  {fmtDateTime(b.startedAt)}
                </td>
                <td className="px-4 py-3 text-text-3 tabular-nums">
                  {duration != null ? `${duration}s` : "—"}
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-text-2">
                  {fmtN(b.readCount)}
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-text-2">
                  {fmtN(b.writeCount)}
                </td>
                <td className="px-4 py-3 text-right tabular-nums text-text-2">
                  {b.skipCount > 0 ? (
                    <span className="text-warn">{fmtN(b.skipCount)}</span>
                  ) : (
                    fmtN(b.skipCount)
                  )}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      b.status === "COMPLETED"
                        ? "ok"
                        : b.status === "FAILED"
                          ? "danger"
                          : "info"
                    }
                    live={b.status === "RUNNING"}
                  >
                    {t(`status.${b.status}`)}
                  </Badge>
                  {b.failureMessage && (
                    <div
                      className="mt-1 text-[10.5px] text-danger"
                      title={b.failureMessage}
                    >
                      {b.failureMessage.slice(0, 40)}…
                    </div>
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
