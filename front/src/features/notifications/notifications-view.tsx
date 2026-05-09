import { getTranslations } from "next-intl/server";
import { Badge } from "@/components/ui/badge";
import { fmtDateTime } from "@/lib/format";
import { NOTIFICATIONS } from "@/lib/mock";
import { lookupAppUser } from "@/lib/mock/auth";

export async function NotificationsView() {
  const t = await getTranslations("Notifications");

  return (
    <div className="overflow-hidden rounded-lg border border-border bg-panel">
      <table className="w-full text-[13px]">
        <thead>
          <tr className="border-b border-border bg-panel-2 text-left text-[11.5px] uppercase tracking-wider text-text-3">
            <th className="px-4 py-2.5">{t("col.sentAt")}</th>
            <th className="px-4 py-2.5">{t("col.recipient")}</th>
            <th className="px-4 py-2.5">{t("col.channel")}</th>
            <th className="px-4 py-2.5">{t("col.title")}</th>
            <th className="px-4 py-2.5">{t("col.body")}</th>
            <th className="px-4 py-2.5">{t("col.status")}</th>
          </tr>
        </thead>
        <tbody>
          {NOTIFICATIONS.map((n) => {
            const recipient = lookupAppUser(n.recipientId);
            return (
              <tr
                key={n.id}
                className="border-b border-divider transition-colors hover:bg-row-hover"
              >
                <td className="px-4 py-3 text-text-3 tabular-nums">
                  {n.sentAt ? fmtDateTime(n.sentAt) : "—"}
                </td>
                <td className="px-4 py-3 font-medium text-text">
                  {recipient.username}
                </td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      n.channel === "EMAIL"
                        ? "info"
                        : n.channel === "SMS"
                          ? "warn"
                          : n.channel === "PUSH"
                            ? "accent"
                            : "neutral"
                    }
                  >
                    {t(`channel.${n.channel}`)}
                  </Badge>
                </td>
                <td className="px-4 py-3 font-medium text-text">{n.title}</td>
                <td className="px-4 py-3 text-text-2">{n.body}</td>
                <td className="px-4 py-3">
                  <Badge
                    tone={
                      n.status === "SENT"
                        ? "ok"
                        : n.status === "PENDING"
                          ? "warn"
                          : "danger"
                    }
                  >
                    {t(`status.${n.status}`)}
                  </Badge>
                  {n.failureReason && (
                    <div className="mt-1 text-[10.5px] text-danger">
                      {n.failureReason}
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
