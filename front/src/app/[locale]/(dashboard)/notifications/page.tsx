import { getTranslations } from "next-intl/server";
import { PageHead } from "@/components/erp/page-head";
import { NotificationsView } from "@/features/notifications/notifications-view";
import { NOTIFICATIONS } from "@/lib/mock";

export default async function NotificationsPage() {
  const tPage = await getTranslations("Pages.notifications");
  const tNotif = await getTranslations("Notifications");
  const failed = NOTIFICATIONS.filter((n) => n.status === "FAILED").length;
  return (
    <div className="px-6 py-6">
      <PageHead
        title={tPage("title")}
        sub={tNotif("subtitleCount", {
          count: NOTIFICATIONS.length,
          failed,
        })}
      />
      <NotificationsView />
    </div>
  );
}
